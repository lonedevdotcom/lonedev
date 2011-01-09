package com.lonedev.gtroot.shared;

import java.text.DecimalFormat;
import java.text.Format;

public class ClientMessageType {
    public static final int JOIN_TABLE = 1;
    private static Format numberFormat = new DecimalFormat("00000");

    public static String createJoinTableMessage() {
        return numberFormat.format(JOIN_TABLE) + "ARGS";
    }
}
