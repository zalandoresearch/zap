package zalando.analytics.helpers;

import java.util.List;

/**
 * Collection of String helper methods.
 * <p>
 * Created by Alan Akbik on 8/28/17.
 */
public class StringHelper {

    /**
     * Returns the length of the longest String in list.
     *
     * @param stringList List of Strings
     * @return length of the longest String in list
     */
    public static int lengthOfLongestInList(List<String> stringList) {
        int longest = 0;
        for (String text : stringList) {
            if (text.length() > longest) longest = text.length();
        }
        return longest;
    }

    /**
     * Returns the length of the longest Integer in list.
     *
     * @param intList List of Integer
     * @return length of the longest Integer in list
     */
    public static int lengthOfLongestInt(List<Integer> intList) {
        int longest = 0;
        for (Integer i : intList) {
            if (i.toString().length() > longest) longest = i.toString().length();
        }
        return longest;
    }

    /**
     * Pads string to a certain length by adding whitespaces both left and right, effectively centering the string.
     *
     * @param string         String that is centered.
     * @param expandToLength Length to which is padded.
     * @return Padded and centered string.
     */
    public static String centerInWhitespaces(String string, int expandToLength) {
        int add = expandToLength - string.length();
        for (int i = 0; i < add; i++) {
            if (i % 2 == 0)
                string += " ";
            else string = " " + string;
        }
        return string;
    }

    /**
     * Pads string to a certain length by adding whitespaces right.
     *
     * @param string         String that is padded.
     * @param expandToLength Length to which is padded.
     * @return Padded string.
     */
    public static String addWhitespaces(String string, int expandToLength) {
        int add = expandToLength - string.length();
        for (int i = 0; i < add; i++) {
            string += " ";
        }
        return string;
    }

    public static String capitalizeFirst(String string) {
        if (string.length() < 1) return string.toUpperCase();
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
