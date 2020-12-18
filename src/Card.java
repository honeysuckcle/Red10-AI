import org.jetbrains.annotations.NotNull;

public class Card {
    static String[] nums = {"3","4","5","6","7","8","9","10","J","Q","K","A","2","Joker"} ;
    static String[] colors = {"H","D","S","C","Grey","Red"};
    int myNum;
    int myColor;
    Card(int num, int color){
        myNum = num;
        myColor = color;
    }
    // The numbering rules of the following algorithm are as follows:　　
    // The order of heart from small to large is: 1-13
    // The order of diamond from small to large is: 14-26　　
    // The order of spades from small to large is: 27-39
    // The order of club blossom from small to large is: 40-52
    // 53 for Grey Joker and 54 for Red Joker
    Card(int num){
        if (num == 53 || num == 54){
            myNum = 13;
            myColor = num - 49;
        }else {
            myNum = (num - 1) % 13;
            if (num >= 1 && num <= 13) {
                myColor = 0;
            }
            if (num >= 14 && num <= 26) {
                myColor = 1;
            }
            if (num >= 27 && num <= 39) {
                myColor = 2;
            }
            if (num >= 40 && num <= 52) {
                myColor = 3;
            }
        }

    }

    public int compareTo(@NotNull Card c){
        if (c.isRed10()){
            //when c is red 10
            if (this.isRed10()){
                return 0;
            }else{
                return -1;
            }
        }else if (isRed10()){
            return 1;
        }else if(c.myNum == 13 && myNum == 13){
            return myColor - c.myColor;
        }else{
            return myNum - c.myNum;
        }
    }

    public boolean isRed10(){
        if (myNum == 7 && myColor < 2){
            return true;
        }
        return false;
    }

    public boolean isJoker(){
        if (myNum == 13){
            return true;
        }
        return false;
    }


    public String name(){
        if (myNum == 13)
            return colors[myColor] +" "+nums[myNum];
        else
            return  nums[myNum]+colors[myColor];
    }

    public boolean equals(Card c) {
        if (myNum == c.myNum && myColor == c.myColor){
            return true;
        }
        return false;
    }

    public boolean equals(int num){
        if (myNum == num){
            return true;
        }
        return false;
    }
}
