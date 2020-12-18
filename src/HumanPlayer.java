import java.util.ArrayList;
import java.util.Scanner;

public class HumanPlayer extends Player{

    HumanPlayer(ArrayList<Card> c){
        cards = c;
        name = "you";
    }

    public boolean terminate(){
        if (cards.size() == 0) {
            System.out.println("congratulations! You win!");
            return true;
        }
        return false;
    }

    private int[] legalTypes(int type){
        switch (type){
            case 1: return new int[]{0, 1, 4};
            case 2: return new int[]{0, 2, 4};
            case 3: return new int[]{0, 3, 4};
            case 4: return new int[]{0, 4};
        }
        return null;
    }

    public Combo think(Combo last) {
        System.out.print("It's your turn.\nYou have those cards:");
        displayCards();

        while (true){
            Scanner scanner = new Scanner(System.in);
            int type = -1;
            ArrayList<Combo> steps = null;

            if (last == null || last.type == 0){
                System.out.println("What type do you want to play?(1:singal, 2:a pair, 3:straight, 4:bomb)");

            }else {
                int[] lt = legalTypes(last.type);
                System.out.print("You can choose");
                for (int i : lt){
                    System.out.print(" "+i+ ":"+Combo.typeNames[i]);
                }
                System.out.println("\nWhat type do you want to play?");
            }
            type = scanner.nextInt();

            switch (type) {
                case 0 -> {
                    return Combo.createSkip();
                }
                case 1 -> {
                    if (last == null || last.type == 0 || last.len != 1)
                        steps = getAllSingle(cards);
                    else
                        steps = getLegalSingle(last.cards[0], cards);
                }
                case 2 -> {
                    steps = getLegalPair(last, cards);
                }
                case 3 -> {
                    steps = getLegalStraight(last, cards);
                }
                case 4 -> {
                    if (last == null || last.type == 0)
                        steps = getAllBomb(cards);
                    else
                        steps = getLegalBomb(last, cards);
                }
            }

            if(steps == null || steps.size() == 0){
                System.out.println("Impossible type. Please think again.");
            }else {

                for (int i = 0; i < steps.size(); i++) {
                    System.out.print(i + ":" + steps.get(i).name()+" ");
                    if ((i+1) % 20 == 0)
                        System.out.println();
                }
                System.out.println();

                System.out.println("Please input the index of cards that you want to play.\n" +
                        "If you want to go back to the previous step, please input - 1.");
                int index = scanner.nextInt();
                if (index >= 0) {
                    return steps.get(index);
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int type = scanner.nextInt();
        System.out.println(type);
        String str = scanner.nextLine();
        System.out.println(str);
//        String[] arr = str.split(" ");
    }
}
