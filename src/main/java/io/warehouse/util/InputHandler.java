package io.warehouse.util;

import java.time.LocalDate;
import java.util.Scanner;

public class InputHandler {

    private static Scanner input = new Scanner(System.in);

    public static String getStringInput() {
        return input.nextLine();
    }

    public static int getIntegerInput() {
        return Integer.parseInt(input.nextLine());
    }

    public static double getDoubleInput() {
        return Double.parseDouble(input.nextLine());
    }

    public static LocalDate getDateInput() {
        return LocalDate.parse(input.nextLine());
    }

    public static boolean getBooleanInput() {
        return Boolean.parseBoolean(input.nextLine());
    }

    public static void closeInput() {
        input.close();
    }
}