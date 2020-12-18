public class Game {
    Desk desk;
    AIPlayer AI;
    HumanPlayer human;
    Player now;

    Game(){
        desk = new Desk();
        desk.DealCards();

        AI = new AIPlayer(desk.AIPlayer, desk.humanPlayer);
        human = new HumanPlayer(desk.humanPlayer);
        desk.displayCards("AI");
        desk.displayCards("human");
    }

    private void swapPlayer(){
        //update data of enemy
        if (now == human) {
            AI.updateCardOfEnemy(now.cards);
            now = AI;
        }
        else
            now = human;
    }

    public void run(){
        System.out.println("Game begins!");
        if (desk.firstTurn == 0){
            now = AI;
        }else{
            now = human;
        }
        Combo last = Combo.createSkip();
        while (true) {
            Combo combo = now.think(last);
            last = combo;
            now.play(combo);//output
            if (now.terminate()){
                break;
            }
            swapPlayer();
        }

        //summery
        now.printHistory();
        swapPlayer();
        now.printHistory();
    }


    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }
}
