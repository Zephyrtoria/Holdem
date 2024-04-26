package Data;

import Server.GetInput;
import View.Desk;
import View.Menu;

import java.util.ArrayList;

import static Data.Table.*;

/*玩家类，包含玩家信息*/
public class Player {
    /*玩家id，1表示主机，其他数字表示人机。通过id来判断是主机进行操控还是人机进行操控*/
    private int id;

    /*玩家的状态，false表示弃牌，true表示仍在游戏*/
    private boolean status = true;

    /*玩家的位置，从0开始*/
    private int position;

    /*表示玩家是否为庄家，true为是*/
    private boolean isDealer = false;

    /*表示玩家是否为小盲，true为是*/
    private boolean isSmallBlind = false;

    /*表示玩家是否为大盲，true为是*/
    private boolean isBigBlind = false;

    /*表示玩家的手牌*/
    private int[] holeCards;

    /*表示玩家当前回合下注的筹码*/
    private long chips = 0;

    /*表示玩家手中剩余的筹码*/
    private long remainchips = 0;

    /*表示玩家手上的牌与桌面上的公共牌所能组成的最大牌型值*/
    private int power = 0;

    private boolean isChecked = false;

    private final String BET_ACTION = "1";
    private final String RAISE_ACTION = "2";
    private final String CALL_ACTION = "3";
    private final String CHECK_ACTION = "4";
    private final String FOLD_ACTION = "5";

    /*空参构造方式，所有数据都通过set方式获取*/
    public Player() {
    }

    /*通过控制台输入相应数字，选择操作。
    1.下注
    2.加注
    3.跟注
    4.过牌
    5.弃牌
     * 如果选择了下注、加注操作需要进行筹码的输入
     负责人：Soyo
    * 参数：void
       返回值：void*/
    public void action() throws InterruptedException {
        Desk.showDesk();
        boolean[] validAction = checkValidAction();
        Menu.showAction(this);
        System.out.print("\n轮到您操作了！ ");
        boolean judgeStop = true;
        while (judgeStop) {
            System.out.print("\n请输入对应操作: ");
            String choice = GetInput.getString();
            switch (choice) {
                case BET_ACTION -> {
                    if (validAction[0]) {
                        while (true) {
                            System.out.print("请输入下注金额: ");
                            long number = GetInput.getLong();
                            if (bet(number)) {
                                judgeStop = false;
                                break;
                            }
                        }
                    } else {
                        System.out.println("该操作不可进行！请重新选择");
                        Menu.showAction(this);
                    }
                }
                case RAISE_ACTION -> {
                    if (validAction[1]) {
                        while (true) {
                            System.out.print("请输入加注金额: ");
                            long number = GetInput.getLong();
                            if (raise(number)) {
                                judgeStop = false;
                                break;
                            }
                        }
                    } else {
                        System.out.println("该操作不可进行！请重新选择");
                        Menu.showAction(this);
                    }
                }
                case CALL_ACTION -> {
                    if (validAction[2]) {
                        judgeStop = false;
                        call();
                    } else {
                        System.out.println("该操作不可进行！请重新选择");
                        Menu.showAction(this);
                    }
                }
                case CHECK_ACTION -> {
                    if (validAction[3]) {
                        judgeStop = false;
                        check();
                    } else {
                        System.out.println("该操作不可进行！请重新选择");
                        Menu.showAction(this);
                    }
                }
                case FOLD_ACTION -> {
                    fold();
                    judgeStop = false;
                    break;
                }
            }
        }
        System.out.println("操作中……");
        Thread.sleep(2000);
        Desk.showDesk();
    }


    /*传入下注金额，进行下注。
     * 先判断下注金额是否合法。不合法则返回错误信息，重新输入金额
     * 若金额合法，则remainchips减去numebr，chips加上number，Pot.chipPot加上number
     * 负责人：初雪
    * 参数：long
       返回值：boolean*/
    public boolean bet(long number) {
        if (number + this.chips >= this.remainchips)

            if (number <= 0) {
                System.out.println("下注金额必须大于0");
                return false;
            }
        if (number >= this.remainchips) {
            System.out.println("下注金额不能大于等于剩余筹码（PS.All In功能绝赞开发中）");
            return false;
        }

        Player previousPlayer = allPlayers.get(findPreviousPlayer(flag));        //判断筹码是否合法
        if (number + this.chips < previousPlayer.getChips()) {
            System.out.println("加注金额必须大于等于前一个玩家的下注金额");
            return false;
        }

        System.out.println("下注成功!");
        this.remainchips -= number;
        this.chips += number;
        Pot.thisRoundChipsPot += number;
        return true;
    }

    /*传入加注金额，进行加注。
    *先判断输入金额是否大于前一位玩家的chips。否则要求重新输入
    *是则直接调用bet(number)
    * 负责人：徒然
    * 参数：long
    返回值：boolean*/
    public boolean raise(long number) {
        //获取前一个玩家的位置
        Player previousPlayer = allPlayers.get(findPreviousPlayer(flag));        //判断筹码是否合法
        if (number + this.chips <= previousPlayer.getChips()) {
            System.out.println("加注金额必须比前一个玩家的下注金额大");
            return false;
        }
        bet(number);
        return true;
    }

    /*不传入参数
     * 通过自动比较前一位玩家的chips和当前玩家的remainChips判断是否合法。
     * 不合法则返回false并要求重新选择操作
     * 合法则调用bet()，传入参数为前一位玩家的chips减去当前玩家的chips，返回true
     * 负责人：随性
    * 参数：void
       返回值：boolean*/
    public boolean call() {
        Player previousPlayer = allPlayers.get(findPreviousPlayer(flag));
        if (previousPlayer.getChips() <= this.remainchips + this.chips) {  //差值
            bet(previousPlayer.getChips() - this.chips);
            //this.remainchips -= previousPlayer.getChips() - this.chips;
            return true;
        } else {
            return false;
        }
    }

    /*进行过牌操作
     * 注意过牌只能在当前场上没有人下注的情况下才能使用，否则要求重新输入操作
     * 负责人：冈崎朋也
    * 参数：void
       返回值：boolean*/
    public boolean check() {
        boolean a = Pot.isThisRoundChipsPotEmpty();  //调用记录本回合筹码池的方法    round表示回合数(详见table文档)
        if (a) {
            isChecked = true;
            return true;
        } else {
            return false;
        }
    }

    /*玩家选择弃牌
     * 更改玩家status，并删除Table.existPlayer中对应的编号
     * 负责人：初雪
    * 参数：void
       返回值：void*/
    public void fold() {
        if (!(this instanceof Robot)) {
            System.out.println("您已弃牌！");
        }
        this.status = false;
        existPlayerNumber--;
    }

    /*返回合法的操作
     * 负责人:Soyo
     * 参数:void
     * 返回值:boolean[]*/
    public boolean[] checkValidAction() {
        Player prePlayer = allPlayers.get(findPreviousPlayer(flag));
        boolean[] output = new boolean[5];  //0下注 1跟注 2加注 3过牌 4弃牌
        if (this.getRemainchips() > 1) {
            if (Pot.isThisRoundChipsPotEmpty()) {
                output[0] = true;
                output[1] = false;
                output[2] = false;
                output[3] = true;
            } else if (this.remainchips + this.chips >= prePlayer.chips) {
                output[0] = true;
                output[1] = true;
                if (this.remainchips + this.chips > prePlayer.chips) {
                    output[2] = true;
                } else {
                    output[2] = false;
                }
                output[3] = false;
            }
        } else {
            if (Pot.isThisRoundChipsPotEmpty()) {
                output[0] = false;
                output[1] = false;
                output[2] = false;
                output[3] = true;
            } else {
                output[0] = false;
                output[1] = false;
                output[2] = false;
                output[3] = false;
            }
        }
        output[4] = true;
        return output;
    }

    /*查询前一名操作的玩家*/
    public int findPreviousPlayer(int x) {
        do {
            x = (x - 1 + playerNumber) % playerNumber;
        } while (!allPlayers.get(x).isStatus());
        return x;
    }

    /*清空手牌，holeCards = null;
    * 负责人：徒然
    * 参数：void
       返回值：void*/
    public void clearHoleCards() {
        holeCards = null;
    }

    /*以下为JavaBean类的set/get方法，不需要另外修改*/
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isDealer() {
        return isDealer;
    }

    public void setDealer(boolean dealer) {
        isDealer = dealer;
    }

    public boolean isSmallBlind() {
        return isSmallBlind;
    }

    public void setSmallBlind(boolean smallBlind) {
        isSmallBlind = smallBlind;
    }

    public boolean isBigBlind() {
        return isBigBlind;
    }

    public void setBigBlind(boolean bigBlind) {
        isBigBlind = bigBlind;
    }

    public int[] getHoleCards() {
        return holeCards;
    }

    public void setHoleCards(int[] holeCards) {
        this.holeCards = holeCards;
    }

    public long getChips() {
        return chips;
    }

    public void setChips(long chips) {
        this.chips = chips;
    }

    public long getRemainchips() {
        return remainchips;
    }

    public void setRemainchips(long remainchips) {
        this.remainchips = remainchips;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
