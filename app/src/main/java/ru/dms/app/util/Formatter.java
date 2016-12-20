package ru.dms.app.util;

/**
 * Created by RinesThaix on 20.12.16.
 */

public class Formatter {

    public static String formatMachineName(String name) {
        return name.replace("dmsds", "ds");
    }

    public static boolean isServerAFake(String name) {
        return name.startsWith("@");
    }

}
