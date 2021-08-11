package org.jalicz.CTF.Game.Data;

import org.jalicz.CTF.Game.Visual.C;

public class GameScore {

    public static int RED = 0, BLUE = 0;

    public static String getString() {
        return C.BLACK + "(" + C.RED + GameScore.RED + C.BLACK + "/" + C.AQUA + GameScore.BLUE + C.BLACK + ")";
    }

    public static void reset() {
        RED = 0;
        BLUE = 0;
    }
}