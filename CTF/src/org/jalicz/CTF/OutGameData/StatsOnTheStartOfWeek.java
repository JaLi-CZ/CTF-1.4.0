package org.jalicz.CTF.OutGameData;

import java.util.HashMap;

public class StatsOnTheStartOfWeek {

    public static final HashMap<String, StatsOnTheStartOfWeek> values = new HashMap<>();
    public final int GAMES_PLAYED, WINS, LOSSES, SCORE, COLLECTED_POWERUPS, FROZE_ENEMIES, UNFROZE_TEAMMATES, WROTE_MESSAGES;


    public StatsOnTheStartOfWeek(int[] values) {
        GAMES_PLAYED = values[0];
        WINS = values[1];
        LOSSES = values[2];
        SCORE = values[3];
        COLLECTED_POWERUPS = values[4];
        FROZE_ENEMIES = values[5];
        UNFROZE_TEAMMATES = values[6];
        WROTE_MESSAGES = values[7];
    }

    public int getByInt(int statistics) {
        switch (statistics) {
            case 0: return GAMES_PLAYED;
            case 1: return WINS;
            case 2: return LOSSES;
            case 3: return SCORE;
            case 4: return COLLECTED_POWERUPS;
            case 5: return FROZE_ENEMIES;
            case 6: return UNFROZE_TEAMMATES;
            case 7: return WROTE_MESSAGES;
            default: return 0;
        }
    }
}