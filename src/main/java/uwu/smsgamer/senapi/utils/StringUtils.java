package uwu.smsgamer.senapi.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import uwu.smsgamer.senapi.ConsolePlayer;

import java.util.*;

/**
 * A utility class for strings.
 */
public class StringUtils {
    private static final List<String> MARKDOWN_CHECKS;
    private static final HashMap<String, Character> MARKDOWN_REPLACEMENTS = new HashMap<>();

    static {
        MARKDOWN_REPLACEMENTS.put("**", 'o');
        MARKDOWN_REPLACEMENTS.put("__", 'n');
        MARKDOWN_REPLACEMENTS.put("--", 'm');
        MARKDOWN_REPLACEMENTS.put("##", 'l');
        MARKDOWN_CHECKS = new ArrayList<>(MARKDOWN_REPLACEMENTS.keySet());
    }

    /**
     * Converts a string input to a formatted string based off of my interpretation of "markdown".
     * <p>
     * Replaces {@code **Italic**} with {@code \u00A7oItalic\u00A7r}.<br>
     * Replaces {@code __Underscore__} with {@code \u00A7nUnderscore\u00A7r}.<br>
     * Replaces {@code --Strikethrough--} with {@code \u00A7mStrikethrough\u00A7r}.<br>
     * Replaces {@code ##Bold##} with {@code \u00A7lBold\u00A7r}.
     *
     * @param input The string to format.
     * @return a "markdown" formatted string.
     */
    // Might be shit idk
    public static String markdownToMinecraft(final String input) {
        return markdownToMinecraft(input, 'r');
    }


    /**
     * Converts a string input to a formatted string based off of my interpretation of "markdown".<br>
     * In the following examples, replaces "r" with the reset character.
     * <p>
     * Replaces {@code **Italic**} with {@code \u00A7oItalic\u00A7r}.<br>
     * Replaces {@code __Underscore__} with {@code \u00A7nUnderscore\u00A7r}.<br>
     * Replaces {@code --Strikethrough--} with {@code \u00A7mStrikethrough\u00A7r}.<br>
     * Replaces {@code ##Bold##} with {@code \u00A7lBold\u00A7r}.
     *
     * @param input The string to format.
     * @param reset The reset character.
     * @return a "markdown" formatted string.
     */
    public static String markdownToMinecraft(final String input, final char reset) {
        final String first = "\u00A7";
        final String after = "\u00A7" + reset;
        StringBuilder output = new StringBuilder(input);
        List<String> checks = new ArrayList<>(MARKDOWN_CHECKS);
        int add = 0;
        boolean skip = false;
        for (int i = 0; i < output.length(); i++) {
            if (skip) {
                skip = false;
                continue;
            }
            if (output.charAt(i) == '\\') {
                add--;
                output.deleteCharAt(i);
                if (output.length() - i > 1)
                    skip = output.charAt(i + 1) == '\\';
                continue;
            }
            List<String> strings = new ArrayList<>(checks);
            strings.addAll(MARKDOWN_CHECKS);
            for (String s : strings) {
                if (input.length() + 1 - i > s.length()) {
                    final String substring = input.substring(i, i + s.length());
                    if (substring.startsWith(s)) {
                        output.delete(i + add, i + s.length() + add);
                        if (checks.size() > 0 && checks.get(0).equals(s)) {
                            output.insert(i + add, after);
                            checks.remove(0);
                        } else {
                            output.insert(i + add, first + MARKDOWN_REPLACEMENTS.get(s));
                            checks.add(0, s);
                        }
                        i += s.length() - 1;
                        add += 2 - s.length();
                        break;
                    }
                }
            }
        }
        return output.toString();
    }

    /**
     * If PlaceholderAPI is enabled, it uses PlaceholderAPI for placeholder replacement.
     * If not, it simply replaces {@code %player_name%} with the player's name.
     *
     * @param player The player for replacements.
     * @param string The string with the placeholders.
     * @return A string with placeholders replaced.
     */
    public static String replacePlaceholders(final OfflinePlayer player, final String string) {
        if (PluginUtils.isPlaceholderAPIEnabled()) {
            if (player instanceof ConsolePlayer)
                Bukkit.getLogger().warning(
                  "If an error occurs, please do not report Sms_Gamer or a placeholder's author. " +
                    "The player parameter was a ConsolePlayer.");
            return PlaceholderAPI.setPlaceholders(player, string);
        } else return string.replace("%player_name%", player.getName());
    }

    /**
     * Colorizes said string. Basically replaces {@code &} with {@code \u00A7}.
     *
     * @param s Said string to replace.
     * @return a colorized string.
     */
    public static String colorize(final String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /**
     * Colorizes said string with only specified colours. Basically replaces {@code &} with {@code \u00A7} if the
     * next character is part of the allowed characters.
     *
     * @param s Said string to replace.
     * @param onlyAllowed The allowed colours.
     * @return a colorized string.
     */
    public static String colorizeLimited(final String s, char[] onlyAllowed) {
        return colorizeLimited(s, new String(onlyAllowed), 'r');
    }

    /**
     * Colorizes said string with only specified colours. Basically replaces {@code &} with {@code \u00A7} if the
     * next character is part of the allowed characters.
     *
     * @param s Said string to replace.
     * @param onlyAllowed The allowed colours.
     * @return a colorized string.
     */
    public static String colorizeLimited(final String s, String onlyAllowed) {
        return colorizeLimited(s, onlyAllowed, 'r');
    }

    /**
     * Colorizes said string with only specified colours and replaces {@code r} with the reset character.
     * Basically replaces {@code &} with {@code \u00A7} if the next character is part of the allowed characters.
     *
     * @param s Said string to replace.
     * @param onlyAllowed The allowed colours.
     * @return a colorized string.
     */
    public static String colorizeLimited(final String s, char[] onlyAllowed, char reset) {
        return colorizeLimited(s, new String(onlyAllowed), reset);
    }

    /**
     * Colorizes said string with only specified colours and replaces {@code r} with the reset character.
     * Basically replaces {@code &} with {@code \u00A7} if the next character is part of the allowed characters.
     *
     * @param s Said string to replace.
     * @param onlyAllowed The allowed colours.
     * @return a colorized string.
     */
    public static String colorizeLimited(final String s, String onlyAllowed, char reset) {
        char[] output = s.toCharArray();
        for (int i = 0; i < output.length; i++) {
            if (output[i] == '\u00A7') output[i] = '&';
            if (output[i] == '&') {
                char c = Character.toLowerCase(s.charAt(i + 1));
                if ("1234567890abcdefklmnor".indexOf(c) != -1) {
                    if (onlyAllowed.indexOf(c) != -1) {
                        output[i] = '\u00A7';
                        if (c == 'r') {
                            output[i + 1] = reset;
                        }
                    }
                }
            }
        }
        return String.valueOf(output);
    }

    /**
     * Replaces arg placeholders with args. Best to explain in an example:<p>
     * Let's say we have the string {@code %0% %1-5% %2+%} and the args {@code ["a", "b", "c", "d", "e"]}.<br>
     * It replaces {@code %0%} by the element at index 0 in the array. In this case, {@code "a"}. If that
     * element doesn't exist, i.e. the element is higher or equal to the array length,
     * then {@code %0%} is replaced with nothing.<br>
     * It then replaces {@code %1-5%} by every element starting at index 1 and ending index 5 with
     * a space as separator. If the element doesn't exist, i.e. the element is higher or equal to the array length,
     * then it stops. In this case, {@code %1-5%} will get replaced by the string {@code b c d e}.<br>
     * Finally, it replaces {@code %2+%} with every element of the array starting at index 2 with a space as
     * separator. In this case, it will be replaced by {@code b c d e}.
     *
     * @param string The string with the placeholder.
     * @param args The args.
     * @return The string with placeholders replaced.
     */
    public static String replaceArgsPlaceholders(final String string, final String[] args) {
        return replaceArgsPlaceholders(string, " ", args);
    }

    /**
     * Same explanation as above except that the spacing is replaced with the parameter "spacing".
     *
     * @param string The string with the placeholder.
     * @param spacing The spacing to use.
     * @param args The args.
     * @return The string with placeholders replaced.
     */
    public static String replaceArgsPlaceholders(final String string, final String spacing, final String[] args) {
        StringBuilder sb = new StringBuilder();
        int at = 0;
        while (string.length() > at) {
            int current = at;
            boolean append = true;
            if (string.charAt(at) == '%') {
                int i = 0;
                while (string.charAt(++at) >= '0' && string.charAt(at) <= '9') i += string.charAt(at) - '0';
                switch (string.charAt(at)) {
                    case '-':
                        int j = 0;
                        while (Character.isDigit(string.charAt(++at))) j += string.charAt(at) - '0';
                        j = Math.min(j, args.length);
                        if (string.charAt(at) == '%') {
                            append = false;
                            for (; i < j; i++) sb.append(args[i]).append(i == j - 1 ? "" : spacing);
                        }
                        break;
                    case '+':
                        if (string.charAt(++at) == '%') {
                            append = false;
                            for (; i < args.length; i++) sb.append(args[i]).append(i == args.length - 1 ? "" : spacing);
                        }
                        break;
                    case '%':
                        append = false;
                        if (i < args.length) sb.append(args[i]);
                }
            }
            if (append) sb.append(string, current, at + 1);
            at++;
        }
        return sb.toString();
    }

    /**
     * Returns an escaped string (a json friendly string). thx google
     *
     * @param s The string that will be escaped.
     * @return An escaped string (a json friendly string).
     */
    public static String escape(String s) {
        assert s != null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if (ch <= '\u001F' || ch >= '\u007F' && ch <= '\u009F' || ch >= '\u2000' && ch <= '\u20FF') {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }
}
