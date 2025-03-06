package com.fastcode.ecommerce.utils.validation;

public class PagingUtil {
    public static Integer validatePage(String page) {
        try {
            int parsedPage = Integer.parseInt(page);
            return Math.max(parsedPage, 0);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Integer validateSize(String size) {
        try {
            int parsedSize = Integer.parseInt(size);
            return parsedSize <= 0 ? 10 : parsedSize;
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    public static String validateDirection(String direction) {
        return direction.equalsIgnoreCase("ASC") || direction.equalsIgnoreCase("DESC") ? direction : "ASC";
    }
}
