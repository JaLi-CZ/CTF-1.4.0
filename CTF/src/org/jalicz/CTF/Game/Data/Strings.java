package org.jalicz.CTF.Game.Data;

import org.jalicz.CTF.Game.Visual.C;

public class Strings {

    public static final String
            KIT =           C.DARK_GRAY +  "[" + C.GREEN + "Kit" +           C.DARK_GRAY +               "] ",
            TEAM =          C.DARK_GRAY +  "[" + C.PINK +  "Team" +          C.DARK_GRAY +               "] ",
            GAME =          C.DARK_GRAY +  "[" + C.AQUA +  "CTF" +           C.DARK_GRAY +               "] ",
            COMMAND =       C.DARK_GRAY +  "[" + C.RED +   "CMD" +           C.DARK_GRAY +               "] ",
            ADMIN_CHAT =    C.YELLOW +     "[" + C.GOLD +  "Admin" +         C.RED + "Chat" + C.YELLOW + "] ",
            DATA_MANAGER =  C.DARK_RED +   "[" + C.RED + "CTF-DataManager" + C.DARK_RED +                "] ",
            PUNISHMENTS =   C.GOLD + "[" + C.RED + "Punishments" + C.GOLD + "] ",

            joinInfoMessage = "[\"\",{\"text\":\"--\",\"color\":\"black\"},{\"text\":\"--\",\"color\":\"dark_blue\"},{\"text\":\"--\",\"color\":\"blue\"},{\"text\":\"--\",\"color\":" +
                    "\"dark_aqua\"},{\"text\":\"-- \",\"color\":\"aqua\"},{\"text\":\"CTF \"},{\"text\":\"--\",\"color\":\"aqua\"},{\"text\":\"--\",\"color\":\"dark_aqua\"},{\"text\":" +
                    "\"--\",\"color\":\"blue\"},{\"text\":\"--\",\"color\":\"dark_blue\"},{\"text\":\"--\",\"color\":\"black\"},{\"text\":\"\\n\"},{\"text\":\"Tip of the day: \",\"" +
                    "color\":\"dark_purple\"},{\"text\":\"" + "--TIP--" + "\",\"color\":\"light_purple\"},{\"text\":\"\\n\\n\"},{\"text\":\"Do you \",\"color\":\"gray\"}," +
                    "{\"text\":\"have an idea\",\"bold\":true,\"color\":\"green\"},{\"text\":\", do you want to\",\"color\":\"gray\"},{\"text\":\" \\n\"},{\"text\":\"report a hacker\"," +
                    "\"bold\":true,\"color\":\"red\"},{\"text\":\" or did you \",\"color\":\"gray\"},{\"text\":\"find a bug\",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"?\",\"color\"" +
                    ":\"gray\"},{\"text\":\"\\n\"},{\"text\":\"You can fill out \",\"color\":\"dark_aqua\"},{\"text\":\"this form\",\"italic\":true,\"underlined\":true,\"color\":\"aqua\"," +
                    "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://forms.gle/b1u8nQt2pUQz6v1aA\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + C.GOLD + "Click" +
                    " here to open!\"}},{\"text\":\"!\",\"color\":\"dark_aqua\"},{\"text\":\"\\n\"},{\"text\":\"--\",\"color\":\"black\"},{\"text\":\"--\",\"color\":\"dark_blue\"}," +
                    "{\"text\":\"--\",\"color\":\"blue\"},{\"text\":\"--\",\"color\":\"dark_aqua\"},{\"text\":\"--\",\"color\":\"aqua\"},{\"text\":\"-----\",\"color\":\"white\"}," +
                    "{\"text\":\"--\",\"color\":\"aqua\"},{\"text\":\"--\",\"color\":\"dark_aqua\"},{\"text\":\"--\",\"color\":\"blue\"},{\"text\":\"--\",\"color\":\"dark_blue\"}," +
                    "{\"text\":\"--\",\"color\":\"black\"}]",

            commandHelpMessage =
                    Strings.COMMAND + C.DARK_GREEN + "All CTF commands: " + C.GREEN +
                    "\n/ctf help" +
                    "\n/ctf admin list" +
                    "\n/ctf admin <add:remove> <player>" +
                    "\n/ctf admin <message> <msg>" +
                    "\n/ctf broadcast <message>" +
                    "\n/ctf build-mode <allow:deny>" +
                    "\n/ctf chat-history <get:delete> <player>" +
                    "\n/ctf config <location:read:reload:save>" +
                    "\n/ctf console-output-readers <add:remove:list:clear> [player]" +
                    "\n/ctf default-kit <player> [kit]" +
                    "\n/ctf execute <command>" +
                    "\n/ctf fly [player]" +
                    "\n/ctf invsee <player>" +
                    "\n/ctf ip <player>" +
                    "\n/ctf punishment <mute:ban> <player> <duration-value> <seconds:minutes:hours:days:weeks:months:years> [reason]" +
                    "\n/ctf punishment <kick:unmute:unban> <player> [reason]" +
                    "\n/ctf punishment <ban-list:mute-list>" +
                    "\n/ctf local-time" +
                    "\n/ctf memory-usage" +
                    "\n/ctf operation-system" +
                    "\n/ctf players <min:max:list:offline-list> [value]" +
                    "\n/ctf powerup <knockball:booster:bomb:prison> [amount] [player]" +
                    "\n/ctf restart" +
                    "\n/ctf stats <get:set:delete> <player> [statistic] [value]" +
                    "\n/ctf sudo <player> <msg>" +
                    "\n/ctf time <lobby:game> [value]" +
                    "\n/ctf whitelist <on:off:list:reload>" +
                    "\n/ctf whitelist <add:remove> <player>" +
                    "\n-------------------------------";
}