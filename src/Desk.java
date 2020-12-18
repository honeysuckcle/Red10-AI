import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Desk {
    int firstTurn;//who play first, 0 is AI, 1 is human
//    int turn = 0;//who play now
    ArrayList<Card> AIPlayer = new ArrayList<>();//player0
    ArrayList<Card> humanPlayer = new ArrayList<>();//player1
    int[][] players = new int[2][27];
    //Preserving history
    ArrayList<Combo> AIHistory = new ArrayList<>();
    ArrayList<Combo> humanHistory = new ArrayList<>();

    //deal cards randomly

    // The numbering rules of the following algorithm are as follows:　　　
    // The order of heart from small to large is: 1-13
    // The order of diamond from small to large is: 14-26　　
    // The order of spades from small to large is: 27-39
    // The order of club blossom from small to large is: 40-52
    // 53 for Grey Joker and 54 for Red Joker
    public void DealCards() {
        System.out.println("Players begin to take turns taking cards...");
        Random r = new Random();
        int[] total = new int[54];
        int leftNum = 54;
        int ranNumber;
        for (int i = 0; i < total.length; i++) {
            total[i] = (i + 1) % 54;
            if (total[i] == 0) {
                total[i] = 54;
            }
        }
        for (int i = 0; i < 27; i++) {
            for (int j = 0; j < players.length; j++) {
                ranNumber = r.nextInt(leftNum);
                players[j][i] = total[ranNumber];
                total[ranNumber] = total[leftNum - 1];
                leftNum--;
            }
        }
        for (int i = 0; i < players.length; i++) {
            for (int j = 0; j < players[i].length; j++) {
                int n = players[i][j];
                if (n == 1)//red heart 3
                    firstTurn = i;
                if (i == 0){
                    AIPlayer.add(new Card(n));
                }
                if (i == 1){
                    humanPlayer.add(new Card(n));
                }
            }
        }

        Comparator<Card> comp = new Comparator<Card>(){
            @Override
            public int compare(Card arg0 , Card arg1 ) {
                //Sort the cards in your hand according to the number
                if (arg0.myNum == arg1.myNum)
                    return arg0.myColor - arg1.myColor;
                return arg0.myNum - arg1.myNum;
            }
        };

        AIPlayer.sort(comp);
        humanPlayer.sort(comp);
        System.out.println("End of card taking.");
    }


    public void displayCards(String identity){
        System.out.print(identity + " has those cards:");
        if (identity.equals("AI")){
            for (int j = 0; j < AIPlayer.size(); j++){
                System.out.print("　" + AIPlayer.get(j).name());
            }
            System.out.println();
        }
        if (identity.equals("human")){
            for (int j = 0; j < humanPlayer.size(); j++){
                System.out.print("　" + humanPlayer.get(j).name());
            }
            System.out.println();
        }
    }

    public void printHisory(){
        if (firstTurn == 0){
            System.out.println("AI play first.");
            System.out.println("AI \t human");
            for (int i = 0; i< AIHistory.size(); i++){
                System.out.print(AIHistory.get(i).name()+"\t"+humanHistory.get(i).name());
            }
        }
    }

    public static void main(String[] args) {
        Desk game = new Desk();
        game.DealCards();
        for (int i = 0; i < game.players.length; i++) {
            System.out.print("Player"+i+":");
            for (int j = 0; j < game.players[i].length; j++) {
                System.out.print("　" + game.players[i][j]);
            }
            System.out.println();
        }
        System.out.print("AI player:");
        for (int j = 0; j < game.AIPlayer.size(); j++){
            System.out.print("　" + game.AIPlayer.get(j).name());
        }
        System.out.println();
        System.out.print("human player:");
        for (int j = 0; j < game.humanPlayer.size(); j++){
            System.out.print("　" + game.humanPlayer.get(j).name());
        }
        System.out.println();

    }
}
