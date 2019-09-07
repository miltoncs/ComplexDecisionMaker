package thesis.util;

import java.util.Collections;

public class StringUtils
{
    public static String repeat(String c, int count)
    {
        return String.join("", Collections.nCopies(count, c));
    }
}
