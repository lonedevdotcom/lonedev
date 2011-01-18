package com.lonedev.gtroot.shared;

import java.text.DecimalFormat;
import java.text.Format;

public class ClientServerMessageInteractor {
    public static final int JOIN_TABLE = 1;
    public static final int PLAYER_JOINED_TABLE = 2;
    public static final int TABLE_STATUS_REQUEST = 3;
    
    private static Format numberFormat = new DecimalFormat("00000");

    public static String createJoinTableRequestMessage() {
        return numberFormat.format(JOIN_TABLE) + "ARGS";
    }

    public static String createTableJoinSuccessMessage(String playerName, int playerPosition) {
        return numberFormat.format(PLAYER_JOINED_TABLE) + ":" + playerName + ":" + playerPosition;
    }
}
