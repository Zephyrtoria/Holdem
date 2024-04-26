package Data;

import java.util.ArrayList;

/*表示桌面以及有关桌面、牌的操作*/
public class Table {
    /*存放玩家人数*/
    public static int playerNumber;

    /*剩余在场上的玩家人数*/
    public static int existPlayerNumber;

    /*存放所有玩家，作为existPlayer的副本*/
    public static ArrayList<Player> allPlayers;
    /*已经发出来的公共牌的数量*/
    public static int publicCardsNumber;

    /*公共牌池*/
    public static int[] publicCards = new int[5];

    /*表示当前轮到操作的玩家编号*/
    public static int flag;

    /*表示当前该局游戏进行轮次*/
    public static int round;

    /*表示游戏进行局数*/
    public static int times = 1;

    /*清空公共牌池，publicCardsNumber清零，publicCards清空
* 负责人：像一只猪
* 参数：void
   返回值：void*/
    public static void clearPublicCards() {
        publicCardsNumber = 0;
        for (int i = 0; i < 5; i++) {
            publicCards[i] = -1;
        }
    }
}
