package Data;

import java.util.ArrayList;

/*表示筹码池以及筹码池的各种操作*/
public class Pot {

    /*设置的初始筹码*/
    public static long initChips;

    /*设置盲注*/
    public static long blindChips;

    /*该局游戏中的总筹码数*/
    public static long totalChipsPot;

    /*该回合的筹码数*/
    public static long thisRoundChipsPot;

    /*将thisRoundChipsPot加入totalChipsPot，并清零thisRoundChipsPot
    * 负责人：徒然
* 参数：void
   返回值：void*/
    public static void addPot() {
        Pot.totalChipsPot += Pot.thisRoundChipsPot;
        Pot.thisRoundChipsPot = 0;
    }

    /*清零totalChipsPot
    * 负责人：冈崎朋也
* 参数：void
   返回值：void*/
    public static void clearTotalChipsPot() {
        Pot.totalChipsPot = 0;
    }

    /*判断当前回合筹码池是否为空，用来判断check()
    * 负责人：苏纳蓝
* 参数：void
   返回值：boolean*/
    public static boolean isThisRoundChipsPotEmpty() {
        return Pot.thisRoundChipsPot == 0;
    }

    /*给胜利的玩家添加筹码
    * 负责人：Soyo
* 参数：ArrayList<Player>
   返回值：void*/
    public static void winChips(ArrayList<Player> winPlayers) {
        if (winPlayers.size() == 1) {
            Player winner = winPlayers.get(0);
            winner.setRemainchips(winner.getRemainchips() + Pot.totalChipsPot);
        } else {
            long tempChipsPot = Pot.totalChipsPot / winPlayers.size();
            for (Player each : winPlayers) {
                each.setRemainchips(each.getRemainchips() + tempChipsPot);
            }
        }
        totalChipsPot = 0;
    }

    /*在每局游戏开始前为每位玩家添加初始金额
    * 负责人：Soyo
* 参数：ArrayList<Player>
   返回值：void*/
    public static void allocateChips(ArrayList<Player> allPlayers) {
        for (Player each : allPlayers) {
            each.setRemainchips(each.getRemainchips() + Pot.initChips);
        }
    }
}
