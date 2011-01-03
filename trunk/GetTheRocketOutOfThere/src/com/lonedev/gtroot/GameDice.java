package com.lonedev.gtroot;

import java.util.Random;

/**
 * 1: Green/Blue
 * 2: Blue/Yellow
 * 3: Attack
 * 4: Orange/Green
 * 5: Yellow/Orange
 * 6: Attack
 *
 * @author Richard Hawkes
 */
public class GameDice {
    private static Random r = new Random();

    public static int rollDice() {
        return (r.nextInt(6) + 1);
    }
}
