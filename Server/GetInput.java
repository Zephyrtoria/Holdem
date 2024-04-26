package Server;

import java.util.Scanner;

public final class GetInput {
    public static Scanner sc = new Scanner(System.in);

    public static long getLong() {
        long res = -1L;
        while (res == -1L) {
            if (sc.hasNextLong()) {
                res = sc.nextLong();
                sc.nextLine();
            } else {
                sc.nextLine();
                System.out.println("请重新输入数字：");
            }
        }
        return res;
    }

    public static int getInt() {
        int res = -1;
        while (res == -1) {
            if (sc.hasNextInt()) {
                res = sc.nextInt();
                sc.nextLine();
            } else {
                sc.nextLine();
                System.out.println("请重新输入数字：");
            }
        }
        return res;
    }

    public static String getString() {
        String res = null;
        while (res == null) {
            if (sc.hasNextLine()) {
                res = sc.nextLine();
            } else {
                System.out.println("请重新输入：");
            }
        }
        return res;
    }
}
