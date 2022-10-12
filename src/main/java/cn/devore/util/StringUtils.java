package cn.devore.util;

import java.math.BigDecimal;

public class StringUtils {
    public static String bigDecimalToString(BigDecimal val) {
        return trimZeros(val.toString());
    }

    private static String trimZeros(String s) {
        if (!s.contains("."))
            return s;
        int dsi = s.length() - 1;
        while (s.charAt(dsi) == '0')
            dsi--;
        if (s.charAt(dsi) == '.')
            dsi++;
        return s.substring(0, dsi + 1);
    }
}
