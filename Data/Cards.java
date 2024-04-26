package Data;

import java.util.Arrays;
import java.util.Comparator;

import Data.Table;

/*存放卡牌、以及进行对卡牌的操作*/
public class Cards {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    /*花色 %4可以得到花色*/
    public static String[] suits = new String[]{"♠", ANSI_RED + "♥" + ANSI_RESET, ANSI_RED + "♦" + ANSI_RESET, "♣"};

    /*点数 /4可以得到点数*/
    public static String[] ranks = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    /*true表示已经发过的牌，false表示没有发出的牌*/
    public static boolean[] isDealt = new boolean[52];

    /*卡牌初始化，每局游戏开始前进行
    * 负责人：SC郑
    * 参数：void
       返回值：void*/
    public static void initCards() {
        for (Player each : Table.allPlayers) {
            each.clearHoleCards();
        }

        for (int i = 0; i < 52; i++) {
            isDealt[i] = false;
        }
    }


    /*发出一张牌，返回值在0~51之间
     * 通过随机数实现，保证不会发出重复的牌
     * 负责人：SC郑
    * 参数：void
       返回值：int*/
    public static int dealCard() {
        int temp = (int) (Math.random() * 52);
        while (isDealt[temp]) { // 应该是当当前牌已经发过时，再次随机选择
            temp = (int) (Math.random() * 52);
        }
        isDealt[temp] = true; // 发出这张牌并标记为已发出
        return temp;

    }

    /*给玩家发牌，通过调用2次dealCard实现
    * 负责人：SC郑
    * 参数：void
       返回值：int[]*/
    public static int[] dealHoleCards() {
        int temp[] = new int[2];
        for (int i = 0; i < 2; i++) {
            temp[i] = dealCard();
        }
        return temp;
    }

    /*发出公共牌，根据当前的publicCardsNumber来决定调用多少次dealCard
    * 负责人：SC郑
    * 参数：int[], int
       返回值：void*/
    public static void dealPublicCards(int[] publicCards) {
        if (Table.publicCardsNumber == 0) {
            // 发翻牌（Flop）
            for (int i = 0; i < 3; i++) {
                publicCards[i] = dealCard();
            }
            Table.publicCardsNumber = 3;
        } else if (Table.publicCardsNumber == 3 || Table.publicCardsNumber == 4) {
            // 发转牌（Turn）或河牌（River）
            publicCards[Table.publicCardsNumber] = dealCard();
            Table.publicCardsNumber++;
        }
        // 如果publicCardsNumber不是0、3或4，没有对应的操作

    }

    /*比较两张牌的大小，返回值代表两张牌的差值
    * 负责人：SC郑
    * 参数：int, int
       返回值：int*/
    public static int compareCards(int card1, int card2) {
        return card1 / 4 - card2 / 4;

    }


    /*判断两张牌花色是否相同，相同则返回true；不同则返回false
    * 负责人：SC郑
    * 参数：int, int
       返回值：boolean*/
    public static boolean isSuitSame(int card1, int card2) {
        return card1 % 4 == card2 % 4;
    }


    /*传入卡牌编号，转换成相应图形和数字并返回
    * 负责人：SC郑
    * 参数：int
       返回值：String*/
    public static String cardNumberToPattern(int cardNumber) {
        return suits[cardNumber % 4] + ranks[cardNumber / 4];
    }


    /*计算牌力值，对于同花顺,设定为9000000,然后加上他顺子最大或者最小的牌的值(这里需要明确是哪一个,具体的比较我不清楚)
    以顺子大小举例,例如34567  这里取3   对于10 J Q K A 这里取10
    即一个牌力值为903,一个牌力值为910,于是大小就可以比较,不会出现判断不了的情况
    以上是横向的比较
    纵向比较的比较清晰,因为三位数第三位大小可以直接比较
    四条为800+,以此类推
    负责人：SC郑
    * 参数：int[]
       返回值：int*/
    public static int calculatePower(int[] list) {
        int score = 0;
        int[] t = {0};
        if (isStraightFlush(list, t)) {
            score = 9000000 + t[0];
            t[0] = 0;
        } else if (isFourOfAKind(list, t)) {
            score = 8000000 + t[0];
            t[0] = 0;
        } else if (isFullHouse(list, t)) {
            score = 7000000 + t[0];
            t[0] = 0;
        } else if (isFlush(list, t)) {
            score = 6000000 + t[0];
            t[0] = 0;
        } else if (isStraight(list, t)) {
            score = 5000000 + t[0];
            t[0] = 0;
        } else if (isThreeOfAKind(list, t)) {
            score = 4000000 + t[0];
            t[0] = 0;
        } else if (isTwoPair(list, t)) {
            score = 3000000 + t[0];
            t[0] = 0;
        } else if (isOnePair(list, t)) {
            score = 2000000 + t[0];
            t[0] = 0;
        } else {
            int[] temp = new int[list.length];
            for (int i = 0; i < list.length; i++) {
                temp[i] = list[i] / 4;
            }
            Arrays.sort(temp);
            //找到最大的牌
            t[0] = temp[temp.length - 1];
            score = 1000000 + t[0];
        }


        return score;
    }


    /*判断一对
    导入7个整数,(/4之后)是否有两个数相等
    负责人：SC郑
    * 参数：int[]
       返回值：boolean*/
    public static boolean isOnePair(int[] list, int[] t) {
//        HashMap<Integer, Integer> map = new HashMap<>();
//        for (int num : list) {
//            int value = num / 4;
//            if (map.containsKey(value)) {
//                return true; // 找到了一对相等的数
//            } else {
//                map.put(value, 1);
//            }
//        }
//        return false; // 没有找到相等的数
        t[0] = 0;
        int[] temp = new int[13];
        for (int i = 0; i < list.length; i++) {
            temp[list[i] / 4]++;
        }
        boolean flag = false;
        int k = 0;
        for (int i = 0; i < temp.length; i++) {
            //找到一对
            if (temp[i] > 1) {
                t[0] = i * 100;
                flag = true;
            }
            //加权最大的牌
            if (temp[i] == 1) {
                k = i;
            }
        }
        t[0] += k;
        if (flag) {

            return true;
        }
        return false;

    }

    /*判断两对
    导入7个整数,(/4之后)是否有两组两个数相等
    负责人：SC郑
    * 参数：int[]
       返回值：boolean*/
    public static boolean isTwoPair(int[] list, int[] t) {
//        if (!isOnePair(list))return false;
//        HashMap<Integer, Integer> counts = new HashMap<>();
//        int pairs = 0;
//
//        for (int num : list) {
//            int key = num / 4;
//            counts.put(key, counts.getOrDefault(key, 0) + 1);
//        }
//
//        for (int count : counts.values()) {
//            if (count >= 2) {
//                pairs++;
//                if (pairs == 2) { // 找到两对
//                    return true;
//                }
//            }
//        }
//        return false; // 没有找到两对
        if (!isOnePair(list, t)) return false;
        t[0] = 0;
        int[] temp = new int[13];//记录每个数字出现的次数
        for (int i = 0; i < list.length; i++) {
            temp[list[i] / 4]++;
        }
        int pair = 0;
        int k = 0;
        for (int i = 12; i > -1; i--) {

            //找到两对,(可能出现三对的情况),从大到小遍历
            if (temp[i] > 1 && pair == 0) {
                t[0] += 10000 * i;
                pair++;

            }
            //
            else if (temp[i] > 1 && pair == 1) {
                t[0] += 100 * i;
                pair++;
            }

        }
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == 1) {
                k = i;
            }
        }
        t[0] += k;
        if (pair == 2)
            return true;
        else
            return false;


    }

    /*判断三条
    导入7个整数,(/4之后)是否有三个数相等（注：先进行isOnePair判断）
    负责人：SC郑
    * 参数：int[]
       返回值：boolean*/
    public static boolean isThreeOfAKind(int[] list, int[] t) {

//        HashMap<Integer, Integer> counts = new HashMap<>();
//
//
//        for (int num : list) {
//            int key = num / 4;
//            counts.put(key, counts.getOrDefault(key, 0) + 1);
//        }
//
//        for (int count : counts.values()) {
//            if (count >= 3) {
//
//
//                    return true;
//
//            }
//        }
//        return false; // 没有找到三条
        if (!isOnePair(list, t)) return false;
        t[0] = 0;
        int[] temp = new int[13];//记录每个数字出现的次数
        for (int i = 0; i < list.length; i++) {
            temp[list[i] / 4]++;
        }
        boolean flag = false;//为了避免没找到最大的一只牌(除了三条的牌)就return
        for (int i = temp.length - 1; i > -1; i--) {
            //迭代一支牌的值,加权

            if (temp[i] >= 3) {
                t[0] = i * 100;
                flag = true;
                break;
            }
        }
        int k = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == 1) {
                k = i;
            }
        }
        t[0] += k;
        if (flag) {
            return true;
        }
        return false;


    }

    /*判断顺子
    导入7个整数,(/4之后)排序
    负责人：SC郑
    * 参数：int[]
       返回值：boolean*/
    public static boolean isStraight(int[] list, int[] t) {
        int[] temp = new int[list.length];
        t[0] = 0;
        for (int i = 0; i < list.length; i++) {
            temp[i] = list[i] / 4;
        }
        Arrays.sort(temp);
        int tip = 1;
        for (int i = 0; i < list.length - 1; i++) {
            //如果是连续的,tip++,并且迭代最大的那个
            if (temp[i] + 1 == temp[i + 1]) {
                tip++;
                t[0] = temp[i + 1];
            } else if (temp[i] == temp[i + 1]) {
                continue;
            } else
                tip = 1;
        }
        if (tip >= 5)//对于牌力值,最好选择大的那个,因为顺子可能是七张
            return true;
        return false;


    }

    /*判断同花
    导入7个整数,(%4之后)是否有5个数相等
    负责人：SC郑
    * 参数：int[]
       返回值：boolean*/
    public static boolean isFlush(int[] list, int[] t) {
        t[0] = 0;
        int[] temp = new int[4];
        int[] temp1 = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            temp1[i] = list[i];
        }
        Arrays.sort(temp1);
        boolean flag = false;
        for (int i = 0; i < list.length; i++) {
            temp[temp1[i] % 4]++;
            //在这里判断这个花色是否已经大于五个,开始迭代最大的那张牌
            if (temp[temp1[i] % 4] >= 5) {
                t[0] = temp1[i] / 4;
                flag = true;
            }
        }
        if (flag)
            return true;

        return false;


    }

    /*判断葫芦
    调用istwoPair和isThreeOfAKind（注：要排除已经判断过的牌的干扰）
    负责人：SC郑
    * 参数：int[]
       返回值：boolean*/
    public static boolean isFullHouse(int[] list, int[] t) {
        if (isThreeOfAKind(list, t) && isTwoPair(list, t)) {
            t[0] = 0;
            int[] temp = new int[13];
            //哈希表看出现次数
            for (int i = 0; i < 7; i++) {
                temp[list[i] / 4]++;

            }
            //找到最大的单只牌
            for (int i = 0; i < temp.length; i++) {
                if (temp[i] == 1) {
                    t[0] = i;

                }

            }
            //因为可能出现两队三条,所以要迭代最大的
            int length = temp.length - 1;
            for (; length >= 0; length--) {
                if (temp[length] >= 3) {
                    t[0] += 10000 * length;
                    break;
                }
            }
            for (; length >= 0; length--) {
                if (temp[length] >= 2) {
                    t[0] += 100 * length;
                    break;
                }
            }
            return true;

        }
        return false;

    }

    /*判断四条
    导入7个整数,(/4之后)是否有四个数相等
    负责人：SC郑
    * 参数：int[]
       返回值：boolean*/
    public static boolean isFourOfAKind(int[] list, int[] t) {
        if (!isThreeOfAKind(list, t))
            return false;
        t[0] = 0;
        int[] temp = new int[13];
        //哈希表看出现次数
        for (int i = 0; i < list.length; i++) {
            temp[list[i] / 4]++;
        }
        boolean flag = false;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] >= 4) {
                //加权
                t[0] += i * 100;
                flag = true;

            }
            //迭代最大的dan只牌
            if (temp[i] == 1) {
                t[0] += i;
            }

        }
        if (flag)
            return true;
        return false;

    }

    /*判断同花顺调用isStraight和isFlush先进行判断
      然后将同花的放进数组里面,再判断是否有顺子
      负责人：SC郑
    * 参数：int[]
       返回值：boolean*/
    public static boolean isStraightFlush(int[] list, int[] t) {
        if (!isFlush(list, t)) return false;
        if (!isStraight(list, t)) return false;
        t[0] = 0;
        //牌的大小花色放进去
        int[][] temp = new int[list.length][2];
        for (int i = 0; i < list.length; i++) {
            temp[i][0] = list[i] / 4;
            temp[i][1] = list[i] % 4;
        }
        //按照牌的大小排序
        Arrays.sort(temp, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return Integer.compare(o1[0], o2[0]);
            }
        });
        int tip = 1;
        for (int i = 0; i < temp.length - 1; i++) {
            if (temp[i][0] + 1 == temp[i + 1][0] && temp[i][1] == temp[i + 1][1]) {
                tip++;
                //逐步找出最大的那个(迭代)
                t[0] = temp[i + 1][0];
            }
            //可能出现牌的大小相同的情况
            else if (temp[i][0] == temp[i + 1][0])
                continue;
                //不是顺子,重置
            else
                tip = 1;
        }
        if (tip >= 5)
            return true;
        return false;


    }

}
