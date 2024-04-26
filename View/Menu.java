package View;

import Data.Cards;
import Data.Player;
import Data.Table;
import Server.Game;
import Server.GetInput;

import java.util.ArrayList;

/*显示游戏主页面以及规则*/
public class Menu {
    private static final String GAME_START = "1";
    private static final String GAME_RULE = "2";
    private static final String GAME_EXIT = "3";
    private static final String[] ACTION_SHOW = {"1.下注", "2.加注", "3.跟注", "4.过牌", "5.弃牌"};

    private static final String CLEAR_STRING = new String(new char[50]).replace("\0", "\r\n");

    /*显示游戏主页面调用Menu，获取输入进行对应操作。
     * 1.开始游戏，调用Game.gameInit()和 do {Game.gameStart();} while(Game.nextGame());
     * 2.游戏规则，调用Menu.showRule()
     * 3.退出游戏，调用Game.gameExit()
    负责人：苏纳蓝
    * 参数：void
       返回值：void*/
    public static void showMenu() throws InterruptedException {
        Menu.clearScreen();
        String mainMenu =
                "+---------------------+\n"
                        + "|     游戏主页面       |\n"
                        + "|   1.开始游戏         |\n"
                        + "|   2.游戏说明         |\n"
                        + "|   3.退出游戏         |\n"
                        + "+---------------------+";

        System.out.println(mainMenu);
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        String choice = GetInput.getString();
        switch (choice) {
            case GAME_START -> {
                Game.gameInit();
                do {
                    Game.gameProcess();
                    Table.times++;
                } while (Game.nextGame());
            }

            case GAME_RULE -> {
                Menu.showRule();
                System.out.println("输入任意字符后按回车返回");
                GetInput.getString();
            }

            case GAME_EXIT -> {
                Game.gameExit();
            }

            default -> {
                System.out.println("输入错误，请重新输入!");
                Thread.sleep(3000);
            }
        }
    }

    /*显示游戏规则
    负责人：像一只猪
    * 参数：void
       返回值：void*/
    public static void showRule() {
        Menu.clearScreen();
        System.out.println("------------------------游戏说明------------------------");
        System.out.println("德州扑克是一种非常流行的扑克牌游戏，以下是其简单的游戏流程说明：\n" +
                "\n" +
                "1. 起始：\n" +
                "   游戏开始时，玩家们各自拿到两张手牌（被称为“口袋牌”）。\n" +
                "   庄家由一张名为“底牌”的牌代表。\n" +
                "\n" +
                "2. 第一轮下注：\n" +
                "   根据口袋牌的情况，玩家轮流决定是否下注、跟注、加注或放弃。\n" +
                "   如果有加注，其他玩家需要再决定是否跟注或放弃。\n" +
                "\n" +
                "3. 翻牌：\n" +
                "   第一轮下注结束后，庄家会翻开三张公共牌，这些牌被称为“翻牌”。\n" +
                "\n" +
                "4. 第二轮下注：\n" +
                "   玩家再次根据手中的牌以及与公共牌的组合情况，进行下注、跟注、加注或放弃。\n" +
                "   同样，有加注的情况下，其他玩家需要再次做出决定。\n" +
                "\n" +
                "5. 转牌：\n" +
                "   第二轮下注结束后，庄家会再翻开一张公共牌，称为“转牌”。\n" +
                "\n" +
                "6. 第三轮下注：\n" +
                "   玩家再次根据自己的两张口袋牌以及与公共牌的组合情况，进行下注、跟注、加注或放弃。\n" +
                "   若有加注，其他玩家需要再次决定跟注或放弃。\n" +
                "\n" +
                "7. 河牌：\n" +
                "   第三轮下注结束后，庄家会再翻开一张公共牌，称为“河牌”。\n" +
                "\n" +
                "8. 最后一轮下注：\n" +
                "   玩家根据手中的牌以及与公共牌的组合情况，进行最后一轮的下注、跟注、加注或放弃。\n" +
                "\n" +
                "9. 摊牌：\n" +
                "   所有玩家（如果还有未放弃的）将自己的两张口袋牌与公共牌进行组合，以形成最好的五张牌组合。\n" +
                "   胜出的玩家将赢得底池中的筹码。\n" +
                "\n" +
                "10. 结算：\n" +
                "    最后，庄家将筹码分配给获胜者，并重新开始新的一轮游戏。\n" +
                "\n" +
                "德州扑克的关键在于判断手中的牌与公共牌的组合情况，以及在合适的时机下注或放弃，以获取最大的胜算。\n" +
                "\n");
    }

    /*打印胜利信息（胜利玩家编号）
    * 负责人：kxuan
    * 参数：ArrayList<Data.Player>
       返回值：void*/
    public static void showWinner(ArrayList<Player> winPlayers) {
        Menu.clearScreen();
        if (winPlayers.size() == 1) {
            Player winner = winPlayers.get(0);
            if (winner.getId() == 1) {
                System.out.println("恭喜你获得胜利");
            } else {
                System.out.println("获胜玩家为玩家" + winner.getId());
            }
        } else {
            for (Player winner : winPlayers) {
                System.out.println("获胜玩家为玩家" + winner.getId());
            }
        }
        System.out.println();
        System.out.println("展示每名玩家的手牌：");
        for (Player each : Table.allPlayers) {
            System.out.print("玩家" + each.getId() + "的手牌是：" + Cards.cardNumberToPattern(each.getHoleCards()[0]) + "  " + Cards.cardNumberToPattern(each.getHoleCards()[1]));
            if (each.isStatus()) {
                System.out.println();
            } else {
                System.out.println("  （弃牌）");
            }
        }

        //04.15修改
        System.out.println("展示最终公牌：");
        for (int i = 0; i < Table.publicCardsNumber; i++) {
            System.out.printf("\t%s", Cards.cardNumberToPattern(Table.publicCards[i]));
        }
    }

    /*显示玩家可进行的操作
     * 加上对过牌、加注、下注、跟注是否满足条件的判断，不满足则直接不显示
     * 下注：remainchip > 0
     * 跟注：remainchip > 0 且 前一位玩家有下注
     * 过牌：前一位玩家没有下注
     负责人：Soyo
     * 参数：Player
       返回值：void*/
    public static void showAction(Player curPlayer) {
        boolean[] output = curPlayer.checkValidAction();
        System.out.println("当前可执行操作：");
        for (int i = 0; i < 5; i++) {
            if (output[i]) {
                System.out.print("\t" + ACTION_SHOW[i]);
            }
        }
    }

    /*清空屏幕*/
    public static void clearScreen() {
        System.out.println(CLEAR_STRING);
    }
}
