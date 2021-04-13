![image-20201206121305975](.\pic\image-20201206121305975.png)

# Game

游戏类，统管整个游戏，包含一个Desk和两个Player

构造函数完成游戏的初始化，包括发牌

run函数包含了游戏进行的过程，包括出牌、交换玩家、记录出牌历史



# Desk

桌面，主要实现发牌的任务，发完的牌按照大小排序（"3","4","5","6","7","8","9","10","J","Q","K","A","2","Joker"），大小一样的按花色"H","D","S","C","Grey","Red"顺序排。



# Card

一个card对象表示一张牌，包含花色和数字两个值

```
static String[] nums = {"3","4","5","6","7","8","9","10","J","Q","K","A","2","Joker"} ;
static String[] colors = {"H","D","S","C","Grey","Red"};
int myNum;
int myColor;
```



# Combo

一个combo对象表示一组牌型组合，算上skip有5种，储存在type里

```
int type;//1-4
static String typeNames[] = {"skip", "single", "pair", "straight", "bomb"};
```



# Player

* AIPlayer
* humanPlayer

这两个类都继承了player这个基类，都有思考和出牌的能力



两者都拥有一系列相同的基类函数，包括

* ```
  public Combo think(Combo last)//思考
  ```

* ```
  public void play(Combo combo)//删掉手上的这几张牌，然后把他放到历史记录里
  ```

* ```
  public boolean terminate()//判断自己有没有出完牌
  ```

* ```
  public void printHistory()//游戏结束的时候用，展示历史
  ```

* ```
  public int getScoreOfOne(ArrayList<Card> cardsInHand)//给当前牌型打分，具体打分策略如下
  //red 10 : +16
          //pair : +2
          //straight : +length
          //bomb : +16 * length
          //double joker : +100
          //double red 10 : +150
          
  //Scoring the cards in your hand, which determines AI's intelligence
  这个函数决定了ai的智慧程度
      //shortages:当然也有一些没有考虑进去的情况
      // 1. straight start with Ace is ignored now
      // 2. double red 10 as well as double joker repeated bonus points
      // 3. May be in the last card dilemma
  ```

* ```
  public int getScoreOfCards(ArrayList<Card> cardsInHand, ArrayList<Card> cardsOfEnemy)//把上面的函数算的值做差
  ```

* ```
  public ArrayList<Combo> getLegalSteps(Combo last, ArrayList<Card> myCards)
  找到当前合法的出牌组合，要保证在游戏规则上比上一个人出的牌大。
  myCards是手里现有的牌
  这个函数由以下几个函数支撑，last表示的是上一个人出的牌，last可能是skip。
  ```

  * single

    ```
    protected ArrayList<Combo> getLegalSingle(Card last, ArrayList<Card> myCards)
    ```

  * a pair

    ```
    protected ArrayList<Combo> getLegalPair(Combo last, ArrayList<Card> myCards)
    ```

  * straight

    ```
    protected ArrayList<Combo> getLegalStraight(Combo last, ArrayList<Card> myCards)
    ```

  * bomb

    ```
    protected ArrayList<Combo> getLegalBomb(Combo last, ArrayList<Card> myCards)
    ```



同时，他们也有不同的地方

在think的时候，human主要做的操作是人机交互，而ai主要是通过minimax算法来寻找最优解

关于minimax算法：

参考资料：https://zhuanlan.zhihu.com/p/114857835

```java
//函数本身的意义是，预判之后可能的情况，返回在当前状态下，双方都认为打出了最好的牌的情况下的得分
public int minimax(ArrayList<Card> cardsInHand, ArrayList<Card> cardsOfEnemy, Combo last, int depth){

        //game end, 如果游戏结束，结束递归，返回一个值，这里的值是我自己定义的
    //这个值的绝对值是很大的，为了给出完牌，即获得胜利增加筹码
    //如果我先出完，返回正值；如果对方先出完，返回负值
        if (cardsInHand.size() == 0){
            if (cardsOfEnemy.size() == 0)
                return 0;
            else
                return 5000 - depth;
        }else if (cardsOfEnemy.size() == 0)
            return -5000 + depth;

        //deepest depth, number of steps predicted
    //提前预判5步，把这里作为最深的预测深度，就假设到了叶子节点
        if (depth == 5){
            return getScoreOfCards(cardsInHand, cardsOfEnemy);
        }

    //为bestval设置负无穷的初始值，它要用来存储当前节点的子节点，能达到的分数的最大值
        int bestVal = -999999999;
    //找到所有可能的出牌情况
        ArrayList<Combo> allLegalActions = getLegalSteps(last, cardsInHand);
        for( Combo myNextAction : allLegalActions){
            //如果把这组牌打出去，我手里的牌就变成了newCardsInHand
            ArrayList<Card> newCardsInHand = move(myNextAction, cardsInHand);
            //这里很关键，这里要交换手里的牌，因为双方都希望自己出的是最好的牌
            //此处的myNextAction作为上一方出的牌，同时depth加一，递归
            int val = minimax(cardsOfEnemy, newCardsInHand, myNextAction, depth + 1);//swap roles
            //只记录最大值
            if (val > bestVal)
                bestVal = val;

        }
    //返回遍历完当前分支，及考虑所有可能情况后，自身牌型得到的最大分值
        return bestVal;

    }
```



ai的思考过程：

```java
public Combo think(Combo last){
    System.out.println("AI has those cards:");
    displayCards();

    System.out.println("Waiting for the opponent to play...");

    Combo bestStep = null;
    int bestVal = 999999999;
    
    //我不光想要知道得分，我还想知道当前这一步我应该走什么，所以这里多了一个for循环，找到能使对方先手的minimax的值最小的解是我想要的

    //Find the node of the first layer
    ArrayList<Combo> allLegalActions = getLegalSteps(last, cards);

    //Find the way to make your opponent score the lowest
    for (Combo combo: allLegalActions){
        int val = minimax(cardsOfEnemy, move(combo, cards), combo, 0);
        if (val < bestVal){
            bestVal = val;
            bestStep = combo;
        }
    }
    return bestStep;

}
```