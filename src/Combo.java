import java.lang.reflect.Array;
import java.util.ArrayList;

public class Combo {
    Card[] cards;
    int len;
    int type;//1-4
    static String typeNames[] = {"skip", "single", "pair", "straight", "bomb"};
    Combo(Card[] c, int t){
        cards = c;
        len = cards.length;
        type = t;
    }

    Combo(){
        cards = null;
        len = 0;
        type = 0;
    }

    static Combo createSkip(){
        return new Combo();
    }

    public void addCard(Card c){
        len++;
        Card[] temp = new Card[cards.length + 1];
        System.arraycopy(cards, 0, temp, 0, cards.length);
        temp[cards.length] = c;

    }

    public String name(){
        String s ="";
        if (type == 0)
            s+= typeNames[type];
        for (int i = 0; i<len; i++){
            if (i != 0)
                s+="-";
            s += cards[i].name();
        }
        return s;
    }

    public boolean isDoubleRed10(){
        return len == 2 && cards[0].isRed10() && cards[1].isRed10();
    }

    public boolean containDoubleRed10(){
        return cards[0].isRed10() && cards[1].isRed10();
    }

    public boolean isDoubleJoker(){
        return cards[0].isJoker() && cards[1].isJoker();
    }

    //if combo legal
    public boolean isLegal(){
        if (type == 1){
            if (cards.length != 1)
                return false;
        }
        if (type == 2){
            if (cards.length != 2 || cards[0].myNum != cards[1].myNum)
                return false;
        }
        if (type == 3){
            if (cards.length <3)
                return false;
            int n = cards[0].myNum;
            for (int i = 1; i<len; i++){
                if (cards[i].myNum - n++ != 0)
                    return false;
            }
        }
        if (type == 4){
            if (len < 3){
                if (cards[0].isRed10() && cards[1].isRed10())
                    return true;
                return cards[0].isJoker() && cards[1].isJoker();
            }else{
                int n = cards[0].myNum;
                for (int i = 1; i<len; i++){
                    if (cards[i].myNum - n != 0)
                        return false;
                }
            }
        }
        return true;
    }

//    public int compareTo(Combo c){
//        int lenNew = c.cards.length;
//        //a pair red 10
//        if (len == 2 && cards[0].myNum == 10 && (cards[0].myColor < 2)
//                && cards[1].myNum == 10 && (cards[1].myColor < 2)){
//            return 1;
//        }
//        if (lenNew == 2 && c.cards[0].myNum == 10 && (c.cards[0].myColor < 2)
//                && c.cards[1].myNum == 10 && (c.cards[1].myColor < 2)){
//            return -1;
//        }
//        //Single, A Pair, Straight
//        if (len == lenNew){
//            if (len == 1){//single
//                return cards[0].compareTo(c.cards[0]);
//            }
//            if (len == 2){//pair
//                if (cards[0].myNum == 13 && cards[1].myNum == 13)
//                    return 1;
//                if (c.cards[0].myNum == 13 && c.cards[1].myNum == 13)
//                    return -1;
//                return (cards[0].myNum + 12)%14 - (c.cards[0].myNum+12)%14;
//            }
//            if (len >= 3){//bomb or straight
//                return (cards[0].myNum + 12)%14 - (c.cards[0].myNum+12)%14;//todo
//            }
//        } else {
//            //Bomb
//            if (len < lenNew) {
//                //todo
//            }else{//double jokers,
//
//            }
//        }
//        return -1;
//    }
}
