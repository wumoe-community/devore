package cn.devore.util;

import ch.obermuhlner.math.big.BigDecimalMath;
import cn.devore.Devore;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MathUtils {
    private static final BigInteger BI_ZERO = BigInteger.ZERO;
    private static final BigInteger BI_ONE = BigInteger.ONE;
    private static final BigInteger BI_TWO = BigInteger.valueOf(2);
    private static final BigInteger BI_THREE = BigInteger.valueOf(3);
    private static final BigInteger BI_SIX = BigInteger.valueOf(6);

    public static boolean isPrime(BigInteger n) {
        if (!n.isProbablePrime(64))
            return false;
        if (n.compareTo(BI_TWO) < 0)
            return false;
        if (n.equals(BI_TWO) || n.equals(BI_THREE))
            return true;
        if (n.mod(BI_TWO).equals(BI_ZERO) || n.mod(BI_THREE).equals(BI_ZERO))
            return false;
        BigInteger sqrtN = BigDecimalMath.sqrt(new BigDecimal(n), Devore.MATH_CONTEXT).toBigInteger();
        for (BigInteger i = BI_SIX; i.compareTo(sqrtN) <= 0; i = i.add(BI_SIX))
            if (n.mod(i.subtract(BI_ONE)).equals(BI_ZERO) || n.mod(i.add(BI_ONE)).equals(BI_ZERO))
                return false;
        return true;
    }

    public static BigDecimal factorial(BigDecimal x) {
        return new BigDecimal(factorial(x.toBigInteger()));
    }

    public static BigInteger factorial(BigInteger x) {
        BigInteger out = BI_ONE;
        int max = x.intValue();
        for (int i = 2; i <= max; i++)
            out = out.multiply(BigInteger.valueOf(i));
        return out;
    }

    public static BigDecimal gcd(BigDecimal a, BigDecimal b) {
        return new BigDecimal(a.toBigInteger().gcd(b.toBigInteger()));
    }

    public static BigDecimal lcm(BigDecimal a, BigDecimal b) {
        return a.multiply(b).divide(gcd(a, b), Devore.MATH_CONTEXT);
    }
}
