package Data;

import Server.GetInput;
import View.Desk;
import View.Menu;

import java.util.Random;
import java.util.Scanner;

import static Data.Table.*;

/*表示机器人类，继承自Player类*/
public class Robot extends Player {

    /*通过产生随机值，进行随机操作。当随机数大于90，则进行加注；
    当随机数大于65则进行跟注或下注（下注金额由randomBet()/randomRaise()产生）；
    其他则弃牌
    负责人：像一只猪
* 参数：void
   返回值：void*/
    @Override
    public void action() throws InterruptedException {
        Desk.update();
        Random random = new Random();
        // 生成随机数
        int randomNum = random.nextInt(100);
        String output = "玩家" + this.getId();
        System.out.print(output + "正在进行操作 ---> ");
        // 显示玩家可进行的操作
        boolean[] validAction = checkValidAction();
        Thread.sleep(1000);
        if (randomNum > 90 || !validAction[0]) {
            fold();
            System.out.println(output + "弃牌了");
        } else if (randomNum > 60 && validAction[3]) {
            check();
            System.out.println(output + "进行了过牌操作");
        } else if (randomNum > 10 && validAction[1]) {
            call();
            System.out.println(output + "进行了跟注操作");
        } else {
            Player previousPlayer = allPlayers.get(findPreviousPlayer(flag));
            long lastBetAmount = previousPlayer.getChips(); // 获取上一个玩家的下注金额
            long number = randomBet();
            if (number < previousPlayer.getChips()) {
                fold();
                System.out.println(output + "弃牌了");
            } else if (number == 0) {
                check();
                System.out.println(output + "进行了过牌操作");
            } else {
                bet(number);
                System.out.println(output + "进行了下注操作");
            }
        }
        Thread.sleep(1500);

        //04.15修改
        Scanner scanner = new Scanner(System.in);

        System.out.println("输入任意字符后，按回车键继续...");

        // 读取一个字符串，这里我们使用nextLine()方法，它会等待用户按下回车键
        String input = scanner.nextLine();

        // 当读取到输入后，可以在这里继续你的程序
        System.out.println("继续执行程序。");
        Desk.update();
    }


    /*当随机操作到下注时调用，返回一个合法的下注值
    * 负责人：随性
    * 参数：void
   返回值：long*/
    public long randomBet() {
        Player previousPlayer = allPlayers.get(findPreviousPlayer(flag));
        long lastBetAmount = previousPlayer.getChips(); // 获取上一个玩家的下注金额
        Random random = new Random();

        long raiseAmount;
        //第一轮必然会走else，其他轮如果走了if，那么由于第一轮的大盲和小盲的强制下注，totalChipsPot > 0恒成立
        if (lastBetAmount == 0) {
            raiseAmount = Math.abs(random.nextLong()) % (Pot.totalChipsPot / 3) % this.getRemainchips();
        } else {
            raiseAmount = (long) (Math.abs(random.nextLong()) % (lastBetAmount - this.getChips()) * 1.2 + lastBetAmount - this.getChips()) % this.getRemainchips(); //随机生成的加注金额
        }

        return raiseAmount;
    }

    @Override
    public boolean bet(long number) {
        this.setRemainchips(this.getRemainchips() - number);
        this.setChips(this.getChips() + number);
        Pot.thisRoundChipsPot += number;
        return true;
    }

    /*当随机操作到加注时调用，随机数范围在(1,3]，随机数乘前一个玩家下注的筹码并强制转换为long
    作为返回值返回
    负责人：随性
    * 参数：void
   返回值：long*//*
    public long randomRaise() {

        // 访问前一个玩家
        Player previousPlayer = allPlayers.get(findPreviousPlayer(flag));
        long lastBetAmount = previousPlayer.getChips(); // 获取上一个玩家的下注金额
        Random random = new Random();

        // 生成一个(1,3]的随机浮点数
        double multiplier = 1.0;
        do {
            multiplier = random.nextDouble() * 2.0 + 1.0;
        } while (multiplier == 1.0);

        long raiseAmount = (long) (multiplier * lastBetAmount);  //随机生成的加注金额

        // 确保加注金额不超过玩家的剩余筹码
        long maxRaiseAmount = getRemainchips() - lastBetAmount;
        if (raiseAmount > maxRaiseAmount) {
            raiseAmount = maxRaiseAmount; // 若加注金额超过剩余筹码，将加注金额设置为剩余筹码
        }
        return raiseAmount;
    }

    @Override
    public boolean raise(long number) {
        //获取前一个玩家的位置
        Player previousPlayer = allPlayers.get(findPreviousPlayer(flag));        //判断筹码是否合法
        if (number + this.getChips() <= previousPlayer.getChips()) {
            return false;
        }
        bet(number);
        return true;
    }*/


}
