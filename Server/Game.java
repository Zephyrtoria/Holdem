package Server;

import java.util.ArrayList;
import java.util.Collections;

import Data.Player;
import Data.Robot;
import Data.Pot;
import Data.Table;
import Data.Cards;
import View.Desk;
import View.Menu;

import static Data.Table.*;

public class Game {
    /*游戏初始化，要求输入游戏人数，设置玩家位置、大小盲、初始筹码，实例化对应人数的Robot并存放到allPlayer中
    * 负责人：kxuan
    * 参数：void
       返回值：void*/
    public static void gameInit() {//初始化筹码和筹码池
        Pot.initChips = 1000L; // 设置玩家初始筹码
        Pot.blindChips = 20L;
        Pot.totalChipsPot = 0L; // 游戏开始时总筹码池为0
        Pot.thisRoundChipsPot = 0L; // 新回合开始时当前回合筹码池为0
        Table.times = 1;

        int n;//选择游戏人数
        do {
            System.out.println("请输入添加机器人个数：（3 < n < 7）");
            n = GetInput.getInt();
        } while (!(3 < n && n < 7));
        n++;
        Table.playerNumber = n;
        //玩家对象存储

        allPlayers = new ArrayList<>();
        //添加人机玩家
        //初始id为2的玩家为小盲
        Player robot1 = new Robot();
        allPlayers.add(robot1);
        robot1.setId(2);
        robot1.setPosition(0);  //小盲，第一个操作
        robot1.setSmallBlind(true);

        //初始id为3的玩家为大盲
        Player robot2 = new Robot();
        allPlayers.add(robot2);
        robot2.setId(3);
        robot2.setPosition(1);
        robot2.setBigBlind(true);

        //添加剩余n-3个人机玩家
        for (int i = 0; i < n - 3; i++) {
            Player robot = new Robot();
            allPlayers.add(robot);
            robot.setId(4 + i);
            robot.setPosition(2 + i);
        }

        //添加主机玩家
        //主机玩家初始为庄家
        Player player = new Player();
        allPlayers.add(player);  //n - 1
        player.setId(1);//主机玩家编号设为1
        player.setPosition(n - 1);  //玩家作为庄家，最后一个操作
        player.setDealer(true);
    }

    /*开始一局游戏，重置玩家状态（不重置筹码），分发每局的奖励筹码，并完成发牌
    负责人：像一只猪 Soyo 阿楷 冈崎朋也 kxuan 苏纳蓝 随性 初雪 程煜迅 徒然
    参数：void
    返回值：void*/
    public static void gameStart() {
        Table.clearPublicCards();
        Cards.initCards();  //初始化牌
        Pot.allocateChips(allPlayers);  //分发奖励筹码
        System.out.println("奖励筹码1000 已发放");

        existPlayerNumber = playerNumber;
        for (int i = (times - 1) % playerNumber, cnt = 0; cnt < playerNumber; cnt++, i = (i + 1) % playerNumber) {
            Player each = allPlayers.get(i);
            each.setStatus(true);
            each.setChecked(false);
            each.setHoleCards(Cards.dealHoleCards());
        }
    }

    /*游戏进程。完成每名玩家轮流操作，进行多轮，可以完成一局游戏
    负责人：像一只猪 Soyo 阿楷 冈崎朋也 kxuan 苏纳蓝 随性 初雪 程煜迅 徒然
    参数：void
    返回值：void*/
    public static void gameProcess() throws InterruptedException {
        gameStart();

        for (round = 1; round <= 4; round++) {
            flag = (times - 2 + playerNumber) % playerNumber;  //从庄家后一位玩家开始操作
            //虽然会有弃牌，但是除了第一回合外，flag会在下面的nextPlayer()中走到下一个合法的位置

            if (round == 1) {
                flag = (flag + 1) % playerNumber;  //第一轮跳过大小盲
                allPlayers.get(flag).bet(Pot.blindChips / 2);
                flag = (flag + 1) % playerNumber;  //第一轮跳过大小盲
                allPlayers.get(flag).bet(Pot.blindChips);
            }

            System.out.println("正在进入新回合………………");
            Thread.sleep(2000);
            Desk.showDesk();

            //每个玩家进行操作
            while (nextPlayer() && existPlayerNumber > 1) {
                Player curPlayer = allPlayers.get(flag);
                curPlayer.action();
            }

            Pot.addPot();
            if (existPlayerNumber == 1) {
                break;
            }

            //发牌
            Cards.dealPublicCards(publicCards);

            //清空下注筹码和重置过牌状态
            for (Player each : allPlayers) {
                each.setChips(0);
                each.setChecked(false);
            }
        }

        //查找剩余玩家
        ArrayList<Player> existPlayers = new ArrayList<>();
        for (Player each : allPlayers) {
            if (each.isStatus()) {
                existPlayers.add(each);
            }
        }

        System.out.println("本局游戏结束，正在判断胜者………………");
        Thread.sleep(2000);
        if (existPlayerNumber == 1) {
            Game.gameOver(existPlayers);
        } else {
            ArrayList<Player> winners = Game.winnerJudge(existPlayers);
            Game.gameOver(winners);
        }
    }


    /*当前该局游戏结束后，进行结算操作（胜利显示（调用Menu.showWinner()）、筹码分配（调用Pot.winChips()）等）
    * 负责人：像一只猪 Soyo 阿楷 冈崎朋也 kxuan 苏纳蓝 随性 初雪 程煜迅 徒然
    * 参数：ArrayList<Data.Player>
       返回值：void*/
    public static void gameOver(ArrayList<Player> list) {
        Menu.showWinner(list);
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
        Pot.winChips(list);
    }

    /*将剩余的玩家输入，通过Cards.calculatePower()判断胜负，最终返回胜利的玩家（可能会有平局情况出现）
* 负责人：kxuan
* 参数：ArrayList<Data.Player>
   返回值：ArrayList<Data.Player>*/
    public static ArrayList<Player> winnerJudge(ArrayList<Player> existPlayers) {
        int maxPower = 0;
        int[] tempCards = new int[7];
        System.arraycopy(publicCards, 0, tempCards, 0, publicCardsNumber);
        for (Player each : existPlayers) {
            System.arraycopy(each.getHoleCards(), 0, tempCards, publicCardsNumber, 2);
            each.setPower(Cards.calculatePower(tempCards));
        }
        ArrayList<Player> winners = new ArrayList<>();
        // 遍历所有玩家，找到最高牌型值及其对应的玩家
        for (Player player : existPlayers) {
            if (player.getPower() > maxPower) {
                // 如果发现更高的牌型值，则重置胜利玩家列表
                maxPower = player.getPower();
                winners.clear();
                winners.add(player);
            } else if (player.getPower() == maxPower) {
                // 如果牌型值与当前最高牌型值相同，则加入胜利玩家列表
                winners.add(player);
            }
        }
        return winners;
    }

    /*退出游戏。调用System.exit()
    * 负责人：程煜迅
    * 参数：void
       返回值：void*/
    public static void gameExit() {
        System.out.println("感谢您的游玩，欢迎您下次的到来！");
        System.exit(0);
    }

    /*下一个玩家
    * 需要判断是否满足一轮结束的条件，满足则返回false，不满足返回true
    * 负责人：初雪
    * 参数：void
       返回值：boolean*/
    public static boolean nextPlayer() {
        //判断每个人的下注数是否相同(注意check())
        do {
            flag = (flag + 1) % playerNumber;
        } while (!allPlayers.get(flag).isStatus());  //找到下一个玩家

        long maxNumber = -1;
        for (Player each : allPlayers) {
            if (each.isStatus()) {
                if (!(maxNumber == -1 || each.getChips() == maxNumber)) {
                    return true;
                }
                maxNumber = Math.max(maxNumber, each.getChips());
            }
        }

        //还没有下注，而且不是check的情况
        if (maxNumber == 0) {
            for (Player each : allPlayers) {
                if (each.isStatus()) {
                    if (!each.isChecked()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*更新玩家信息（大小盲、position等），一局游戏结束后调用，并询问是否进行下一局，是则返回true，否则返回false
    * 负责人：程煜迅
    * 参数：void
       返回值：boolean*/
    public static boolean nextGame() {

        // 询问用户是否进行下一局游戏
        System.out.println("是否进行下一局游戏？输入任意字符进行下一局;输入no返回主界面:");
        String input = GetInput.getString().toLowerCase(); // 读取用户输入并转换为小写
        // 根据用户输入返回对应的结果
        if ("no".equals(input)) {
            return false; // 用户不想进行下一局
        } else {
            return true;
        }
    }
}
