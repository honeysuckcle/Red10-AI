import java.util.ArrayList;

public class AIPlayer extends Player{

    ArrayList<Card> cardsOfEnemy;

    //AI knows two people's cards
    AIPlayer(ArrayList<Card> c, ArrayList<Card> e){
        cards = c;
        cardsOfEnemy = e;
        name = "AI";
    }

    //Think about the best steps
    public Combo think(Combo last){
        System.out.println("AI has those cards:");
        displayCards();

        System.out.println("Waiting for the opponent to play...");

        Combo bestStep = null;
        int bestVal = 999999999;

        //Find the node of the first layer
        ArrayList<Combo> allLegalActions = getLegalSteps(last, cards);

        //Find the way to make your opponent score the lowest
        for (Combo combo: allLegalActions){
            int val = minimax(move(combo, cards),cardsOfEnemy, combo, false,0);
            if (val < bestVal){
                bestVal = val;
                bestStep = combo;
            }
        }
        return bestStep;

    }

    /*
    minmax algorithm:
    function minimax(node, depth, maximizingPlayer)
        if depth = 0 or node is a terminal node then
          return the heuristic value of node
        if maximizingPlayer then
        value := −∞
                for each child of node do
        value := max(value, minimax(child, depth − 1, FALSE))
                return value
        else (* minimizing player *)
        value := +∞
                for each child of node do
        value := min(value, minimax(child, depth − 1, TRUE))
                return value

     */
    public int minimax(ArrayList<Card> cardsInHand, ArrayList<Card> cardsOfEnemy, Combo last, boolean maxi, int depth){

        //game end
        if (cardsInHand.size() == 0){
            if (cardsOfEnemy.size() == 0)
                return 0;
            else
                return 5000 - depth;
        }else if (cardsOfEnemy.size() == 0)
            return -5000 + depth;

        //deepest depth, number of steps predicted
        if (depth == 5){
            return getScoreOfCards(cardsInHand, cardsOfEnemy);
        }

        int bestVal = -999999999;
        if (maxi) {
            ArrayList<Combo> allLegalActions = getLegalSteps(last, cardsInHand);
            for (Combo myNextAction : allLegalActions) {
                ArrayList<Card> newCardsInHand = move(myNextAction, cardsInHand);
                int val = minimax(newCardsInHand, cardsOfEnemy, myNextAction, false, depth + 1);//swap roles
                if (val > bestVal)
                    bestVal = val;

            }
        }else{
            bestVal *= -1;
            ArrayList<Combo> allLegalActions = getLegalSteps(last, cardsInHand);
            for (Combo myNextAction : allLegalActions) {
                ArrayList<Card> newCardsInHand = move(myNextAction, cardsOfEnemy);
                int val = minimax(cardsInHand, newCardsInHand, myNextAction, true, depth + 1);//swap roles
                if (val < bestVal)
                    bestVal = val;

            }
        }
        return bestVal;

    }

    public boolean terminate(){
        if (cards.size() == 0) {
            System.out.println("game over. The opponent wins.");
            return true;
        }
        return false;
    }

    public void updateCardOfEnemy(ArrayList<Card> c){
        cardsOfEnemy = c;
    }
}
