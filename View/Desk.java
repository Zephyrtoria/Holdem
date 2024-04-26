package View;

import Data.Cards;
import Data.Player;
import Data.Pot;
import Data.Table;
import View.Menu;

import java.util.ArrayList;

/*在游戏过程中显示玩家信息、下注筹码、展示牌面等等*/
public class Desk {

    /*显示游戏过程中的所有信息
     * 包括其他玩家（编号、状态、下注金额、是否为大小盲）、主机（状态、下注金额、是否为大小盲、手牌）、
     * 操作日志、公共牌面、筹码池等
     * 负责人：Soyo
    * 参数：void
       返回值：void*/
    public static void showDesk() {
        Menu.clearScreen();
        //打印回合数、筹码池
        System.out.printf("%20s:%d%9s:%d\n", "当前回合", Table.round, "筹码池", Pot.totalChipsPot);
        System.out.println("");

        //输出玩家以及编号和状态
        for (Player each : Table.allPlayers) {
            if (each.getId() != 1) {
                if (each.equals(Table.allPlayers.get(Table.flag))) {
                    System.out.printf("          \u001B[31m%s%d\u001B[30m", "玩家", each.getId());
                } else {
                    System.out.printf("          %s%d", "玩家", each.getId());
                }
            }
        }
        System.out.println();

        //输出状态或下注金额
        for (Player each : Table.allPlayers) {
            if (each.getId() != 1) {
                if (each.isStatus()) {
                    System.out.printf("      %s:%d", "已下注", each.getChips());
                } else {
                    System.out.print("          （弃牌）");
                }
            }
        }
        System.out.println();

        //输出公共牌
        System.out.println("\t\t\t\t+-----------------------------------+");
        System.out.println("\t\t\t\t|\t\t\t\t公共牌\t\t\t\t|");
        System.out.println("\t\t\t\t|\t\t\t\t\t\t\t\t\t|");
        System.out.println("\t\t\t\t|\t\t\t\t\t\t\t\t\t|");
        System.out.print("\t\t\t\t\t\t");
        for (int i = 0; i < Table.publicCardsNumber; i++) {
            System.out.printf("\t%s", Cards.cardNumberToPattern(Table.publicCards[i]));
        }
        System.out.println();
        System.out.println("\t\t\t\t|\t\t\t\t\t\t\t\t\t|");
        System.out.println("\t\t\t\t|\t\t\t\t\t\t\t\t\t|");
        System.out.println("\t\t\t\t+-----------------------------------+");
        System.out.println();
        System.out.println();

        Player you = Table.allPlayers.get(Table.playerNumber - 1);
        System.out.print("您:");
        if (you.isStatus()) {
            int[] yourCards = you.getHoleCards();
            System.out.printf("%10s%10s\n", Cards.cardNumberToPattern(yourCards[0]), Cards.cardNumberToPattern(yourCards[1]));
        } else {
            System.out.printf("%20s\n", "您已弃牌");
        }
        System.out.printf("当前筹码:%d\t\t\t已下注:%d\n", you.getRemainchips(), you.getChips());
    }

    /*更新状态、筹码，在轮到玩家之后调用
    * 主要是调用Desk中的方法来更新界面
    * 负责人：苏纳蓝
    * 参数：void
       返回值：void*/
    public static void update() {
        Desk.showDesk();
    }
}
