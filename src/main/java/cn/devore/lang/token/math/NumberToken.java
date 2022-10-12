package cn.devore.lang.token.math;

import cn.devore.lang.token.BoolToken;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class NumberToken extends ArithmeticToken {
    protected abstract NumberToken convert(NumberToken promote);

    public abstract int toInt();

    public abstract long toLong();

    public abstract float toFloat();

    public abstract double toDouble();

    public abstract BigDecimal toBigDecimal();

    public abstract BigInteger toBigInteger();

    public byte toByte() {
        return (byte) toInt();
    }

    public abstract NumberToken idiv(NumberToken other);

    public abstract NumberToken mod(NumberToken other);

    public abstract NumberToken pow(NumberToken other);

    public abstract NumberToken gcd(NumberToken other);

    public abstract NumberToken lcm(NumberToken other);

    public abstract NumberToken negate();

    public abstract NumberToken inc();

    public abstract NumberToken dec();

    public abstract NumberToken signnum();

    public abstract NumberToken factorial();

    public abstract NumberToken abs();

    public abstract NumberToken exp();

    public abstract NumberToken sin();

    public abstract NumberToken cos();

    public abstract NumberToken tan();

    public abstract NumberToken asin();

    public abstract NumberToken acos();

    public abstract NumberToken atan();

    public abstract NumberToken log();

    public abstract NumberToken ln();

    public abstract NumberToken sqrt();

    public abstract NumberToken ceil();

    public abstract NumberToken floor();

    public abstract BoolToken isPrime();

    @Override
    public NumberToken deepcopy() {
        return this;
    }

    public abstract NumberToken one();

    public abstract NumberToken zero();

    public abstract NumberToken negOne();
}
