package org.jalicz.CTF.OutGameData;

import org.jalicz.CTF.Game.Visual.C;

public class RecordData {

    public final String PLAYER, SCORE, PLACE;

    public RecordData(String player, String score, int place) {
        PLAYER = player;
        SCORE = score;
        PLACE = getPlace(place);
    }

    private static String getPlace(int place) {
        switch (place) {
            case 1: return C.YELLOW + "1st";
            case 2: return C.YELLOW + "2nd";
            case 3: return C.YELLOW + "3rd";
            case 4: return C.YELLOW + "4th";
            case 5: return C.YELLOW + "5th";
            default: return C.YELLOW + "?th";
        }
    }

    public String toString() {
        return PLAYER + ", " + SCORE + ", " + PLACE;
    }
}