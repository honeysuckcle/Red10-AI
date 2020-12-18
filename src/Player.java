import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Player {
    ArrayList<Card> cards;
    ArrayList<Combo> history = new ArrayList<>();
    String name;

    //思考，得出下哪步棋
    public Combo think(Combo last){
        return null;
    }

    //出牌
    public void play(Combo combo){
        System.out.println(name + " play: "+combo.name());
        history.add(combo);
        move(combo);
    }

    public void move(Combo combo){
        cards = move(combo, cards);
    }

    public ArrayList<Card> move(Combo combo, ArrayList<Card> myCards){
        if (combo.type == 0)
            return myCards;
        ArrayList<Card> newCardsInHand = new ArrayList<>();
        int[] deleteList = new int[combo.len];
        int j = 0;
        for (int i=0; i<myCards.size(); i++){
            for (Card cc: combo.cards){
                if (cc.equals(myCards.get(i)))
                    deleteList[j++] = i;
            }
        }
        for (int i=0; i<myCards.size(); i++){
            if (!isInTheList(i, deleteList)){
                newCardsInHand.add(myCards.get(i));
            }
        }
        return newCardsInHand;

    }

    private boolean isInTheList(int index, int[] list){
        for (int i : list){
            if (i == index){
                return true;
            }
        }
        return false;
    }

    public boolean terminate(){
        if (cards.size() == 0)
            return true;
        return false;
    }

    public void printHistory(){
        System.out.print(name+":\t");
        for (int i = 0; i< history.size(); i++){
            System.out.print(history.get(i).name()+"\t");
        }
        System.out.println();
    }

    //    public void AIMove(int[] arr){
//
//        //remove cards
//        Card[]
//        for (int i : arr){
//            combo.add(AIPlayer.get(i));
//            AIPlayer.remove(i);
//        }
//        AIHistory.add(combo);
//    }


    //Scoring the cards in your hand, which determines AI's intelligence
    //shortages:
    // 1. straight start with Ace is ignored now
    // 2. double red 10 as well as double joker repeated bonus points
    // 3. May be in the last card dilemma
    public int getScoreOfOne(ArrayList<Card> cardsInHand){
        //red 10 : +16
        //pair : +2
        //straight : +length
        //bomb : +16 * length
        //double joker : +100
        //double red 10 : +150
        int sum = 0, red10 = 0, joker = 0;
        int straightNum = -1, straightLength = 0;
        int bombNum = -1, bombLength = 0;
        for (Card c: cardsInHand){
            sum += c.myNum;

            //red 10 and double red 10
            if(c.isRed10()){
                sum += 16;
                red10++;
            }
            if (red10 == 2){
                sum += 150;
                red10 = 0;
            }

            //double jokers
            if (c.isJoker()){
                joker++;
            }
            if (joker == 2){
                sum += 100;
                joker = 0;
            }

            //pair and bomb
            if (bombNum == -1){
                bombNum = c.myNum;
            }else if (c.myNum == bombNum){
                bombLength++;
                continue;//cannot be straight
            }else {
                if (bombLength == 2){
                    sum += 2;
                }
                if (bombLength > 2){
                    sum += 16 * bombLength;
                }
                bombNum = -1;
                bombLength = 0;
            }

            //straight
            if (straightNum == -1){
                straightNum = c.myNum;
            }else{
                int dis = c.myNum - straightNum;
                if (dis == 1){
                    straightNum++;
                    straightLength++;
                }else{
                    if (straightLength > 2){
                        sum += straightLength;
                    }
                    straightLength = 0;
                    straightNum = -1;
                }
            }

        }
        //The fewer cards in hand, the better
        //avoid n/0
        return sum - cardsInHand.size() * cardsInHand.size();
    }

    //Score the cards in hand
    public int getScoreOfCards(ArrayList<Card> cardsInHand, ArrayList<Card> cardsOfEnemy){

        int sum1 = getScoreOfOne(cardsInHand);
        int sum2 = getScoreOfOne(cardsOfEnemy);
        if (cardsInHand.size() < 5){
            sum1 += 1000 * (5-cardsInHand.size());
        }
        if (cardsInHand.size() < 5){
            sum2 += 1000 * (5-cardsInHand.size());
        }
        return sum1 - sum2;

        //improve: if I have a combo that my opponent can't play
    }

    protected ArrayList<Combo> getAllSingle(ArrayList<Card> myCards){
        ArrayList<Combo> collection = new ArrayList<>();
        int n = -1;
        for (Card c: myCards){
            if (c.myNum != n || c.isRed10()) {
                Combo combo = new Combo(new Card[]{c}, 1);
                collection.add(combo);
            }
        }
        return collection;
    }

    protected ArrayList<Combo> getLegalSingle(Card last, ArrayList<Card> myCards){
        if (last.isRed10())
            return null;
        ArrayList<Combo> collection = new ArrayList<>();
        int num = -1;
        for (Card c: myCards){
            if (c.compareTo(last) > 0 && (c.myNum != num || c.isRed10()) ){
                Combo combo = new Combo(new Card[]{c}, 1);
                collection.add(combo);
                num = c.myNum;
            }
        }
        return collection;
    }

    //ok
    protected ArrayList<Combo> getLegalPair(Combo last, ArrayList<Card> myCards){
        int num;
        if (last == null || last.type == 0){
            num = -1;
        }else if (last.type != 2) {
            return null;
        }else {
            num = last.cards[0].myNum;
        }

        ArrayList<Combo> collection = new ArrayList<>();

        int findNum = -1;
        for (int i = 1; i<myCards.size(); i++){
            Card c1 = myCards.get(i-1);
            Card c2 = myCards.get(i);
//            Combo combo = null;
            if (c1.myNum != findNum && c1.myNum == c2.myNum && c1.myNum > num) {
                Combo combo = new Combo(new Card[]{c1, c2}, 2);
                if (!combo.isDoubleRed10() && !combo.isDoubleJoker()) {
                    collection.add(combo);
                    findNum = c1.myNum;
                }
            }
        }
        return collection;
    }

    private int compareStraight(int m, int y){
        if (y > 10)
            y -= 20;
        if (m > 10)
            m -= 20;
        return m - y;
    }

    protected ArrayList<Combo> getLegalStraight(Combo last, ArrayList<Card> myCards){
        int num;

        if (last == null || last.type == 0){
            num = -1;
        }else if (last.type != 3) {
            return null;
        }else {
            num = last.cards[0].myNum;
        }

        ArrayList<Combo> collection = new ArrayList<>();

        int left = -5;
        ArrayList<Card> list = new ArrayList<>();

        for (int i = 0; i < myCards.size(); i++) {
            Card c = myCards.get(i);
            if (c.myNum == left){
                //skip
                Card cc = list.get(list.size() -1 );
                if (cc.isRed10() && !c.isRed10()) {
                    list.remove(list.size() - 1);
                    list.add(c);
                }
            }
            else if (compareStraight(c.myNum, num) > 0 &&(left < 0 ||  c.myNum - left == 1)) {
                //straight start with Ace
                if (c.myNum == 0){
                    int ace = -1, two = -1;
                    for (int j = myCards.size()-1; myCards.get(j).myNum >= 11 ;j--){
                        if (myCards.get(j).myNum == 12 && two < 0)
                            two = j;
                        if (myCards.get(j).myNum == 11 && ace < 0)
                            ace = j;
                    }
                    if (two > 0){
                        if (ace > 0){
                            list.add(myCards.get(ace));
                        }
                        list.add(myCards.get(two));
                    }
                }
                list.add(c);
                left = c.myNum;
            } else if (c.myNum != left || c.myNum > 11) {//Discontinuous or exceeding ace
                 if (list.size() >= last.len) {
                     if (last.type == 0 || last.len == 0){
                         for (int k = 3; k< list.size(); k++){
                             for (int j = 0; j <= list.size() - k; j++) {
                                 Card[] co = parseToArr(list.subList(j, j+ k));
                                 collection.add(new Combo(co, 3));
                             }
                         }
                     }else
                         for (int j = 0; j <= list.size() - last.len; j++) {
                             Card[] co = parseToArr(list.subList(j, j+ last.len));
                             collection.add(new Combo(co, 3));
                         }
                }

                list.clear();
                left = -5;
//                list.add(c);
            }

        }
        return collection;
    }

    private Card[] parseToArr(List<Card> list){
        Card[] cc = new Card[list.size()];
        for (int i = 0; i<list.size(); i++){
            cc[i] = list.get(i);
        }
        return cc;
    }

    protected ArrayList<Combo> getLegalBomb(Combo last, ArrayList<Card> myCards){

        if (last.isDoubleRed10())
            return null;

        ArrayList<Combo> collection = getAllBomb(myCards);
        if (last.type != 4)
            return collection;

        Collection<Combo> deleteList = new ArrayList<>();

        for (Combo current : collection) {
            if (current.len == last.len && current.cards[0].myNum <= last.cards[0].myNum) {
                deleteList.add(current);
            }
            if (current.len < last.len){
                deleteList.add(current);
            }
        }
        collection.removeAll(deleteList);

        return collection;
    }

    protected ArrayList<Combo> getAllBomb(ArrayList<Card> myCards){
        ArrayList<Combo> collection = new ArrayList<>();
        int bombNum = -1, bombLength = 0;
        ArrayList<Card> list = new ArrayList<>();
        for (Card c: myCards){
            if (bombNum == -1){
                bombNum = c.myNum;
                list.add(c);
                bombLength++;
            }else if (c.myNum == bombNum){
                bombLength++;
                list.add(c);
            }else {
                Card[] cc = parseToArr(list);
                Combo combo = new Combo(cc, 4);
                if (bombLength == 2 && combo.isDoubleJoker()){
                        collection.add(combo);
                }
                if (bombLength > 2){
                    collection.add(combo);
                    if (bombLength == 4){
                        cc = parseToArr(list.subList(0,3));
                        combo = new Combo(cc, 4);
                        collection.add(combo);
                    }
                    if (combo.containDoubleRed10()) {
                        cc = parseToArr(list.subList(0, 2));
                        combo = new Combo(cc, 4);
                        collection.add(combo);
                    }
                }

                bombNum = c.myNum;
                bombLength = 1;
                list.clear();
                list.add(c);
            }
        }

        return collection;

    }

    //important, find all legal actions
    //Find a collection of certain types of cards in my hand
    //special: straight start with Ace
    public ArrayList<Combo> getLegalSteps(Combo last, ArrayList<Card> myCards){
        ArrayList<Combo> collection = new ArrayList<>();
        //You can always choose skip

        if (last == null || last.type == 0 ){
            collection.addAll(getAllSingle(myCards));
            collection.addAll(getLegalPair(last, myCards));
            collection.addAll(getLegalStraight(last, myCards));
            collection.addAll(getAllBomb(myCards));
        }else {
            collection.add(Combo.createSkip());
        }

        Collection<Combo> s = null;
        if (last.type == 1){
            s = getLegalSingle(last.cards[0],myCards);
            if (s != null)
                collection.addAll(s);
            s = getAllBomb(myCards);
            if (s != null)
                collection.addAll(s);
        }
        if (last.type == 2){
            s = getLegalPair(last, myCards);
            if (s != null)
                collection.addAll(s);
            s = getAllBomb(myCards);
            if (s != null)
                collection.addAll(s);
        }
        if (last.type == 3){
            s = getLegalStraight(last, myCards);
            if (s != null)
                collection.addAll(s);
            s = getAllBomb(myCards);
            if (s != null)
                collection.addAll(s);
        }
        if (last.type == 4){
            s = getLegalBomb(last, myCards);
            if (s != null)
                collection.addAll(s);
        }

        return collection;

    }

    public void displayCards(){
        for (int j = 0; j < cards.size(); j++){
            System.out.print("　"+j+":" + cards.get(j).name());
            if ((j+1) % 15 == 0)
                System.out.println();
        }
        System.out.println();
    }

    public boolean ifLegalStep(Card[] last, Card[] now){
        int len = last.length;
        int lenNew = now.length;
        //a pair red 10
        if (len == 2 && now[0].myNum == 10 && (now[0].myColor < 2)
                && now[1].myNum == 10 && (now[1].myColor < 2)){
            return true;
        }
        //Single, A Pair, Straight
        if (len == lenNew){
            if (len == 1){
                return now[0].compareTo(last[0]) > 0;
            }
            if (len == 2){
                if (last[0].myNum == last[1].myNum && now[0].myNum == now[1].myNum){
                    if (now[0].myNum == 13)
                        return true;
                    else return (now[0].myNum + 12) % 14 > (last[0].myNum + 12) % 14;//todo
                }
            }
            if (len >= 3){//bomb or straight

            }
        } else {
            //Bomb
            if (len < lenNew) {

            }else{

            }
        }
        return false;
    }
}
