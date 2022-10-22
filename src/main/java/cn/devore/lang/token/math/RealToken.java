package cn.devore.lang.token.math;

import ch.obermuhlner.math.big.BigDecimalMath;
import cn.devore.Devore;
import cn.devore.lang.Token;
import cn.devore.lang.token.BoolToken;
import cn.devore.lang.token.ComparableToken;
import cn.devore.util.MathUtils;
import cn.devore.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class RealToken extends NumberToken {
    public static final BigDecimal BD_NEG_ONE = new BigDecimal(-1);
    public static final RealToken ZERO = RealToken.valueOf(0);
    public static final RealToken ONE = RealToken.valueOf(1);
    public static final RealToken NEG_ONE = RealToken.valueOf(-1);

    private final BigDecimal _val;

    public RealToken(int n) {
        _val = new BigDecimal(n, Devore.MATH_CONTEXT);
    }

    public RealToken(double d) {
        _val = new BigDecimal(d, Devore.MATH_CONTEXT);
    }

    public RealToken(String s) {
        _val = new BigDecimal(s);
    }

    public RealToken(BigDecimal n) {
        _val = n;
    }

    public RealToken(BigInteger val) {
        _val = new BigDecimal(val);
    }

    public static RealToken valueOf(int n) {
        return new RealToken(new BigDecimal(n, Devore.MATH_CONTEXT));
    }

    public static RealToken valueOf(double d) {
        return new RealToken(new BigDecimal(d, Devore.MATH_CONTEXT));
    }

    public static RealToken valueOf(String s) {
        return new RealToken(new BigDecimal(s));
    }

    public static RealToken valueOf(BigDecimal n) {
        return new RealToken(n);
    }

    public static RealToken valueOf(BigInteger val) {
        return new RealToken(new BigDecimal(val));
    }

    private static BigDecimal ceil(BigDecimal val) {
        return val.setScale(0, RoundingMode.CEILING);
    }

    private static BigDecimal floor(BigDecimal val) {
        return val.setScale(0, RoundingMode.FLOOR);
    }

    @Override
    public int toInt() {
        return _val.intValue();
    }

    @Override
    public long toLong() {
        return _val.longValue();
    }

    @Override
    public float toFloat() {
        return _val.floatValue();
    }

    @Override
    public double toDouble() {
        return _val.doubleValue();
    }

    @Override
    public BigDecimal toBigDecimal() {
        return _val;
    }

    @Override
    public BigInteger toBigInteger() {
        return _val.toBigInteger();
    }

    @Override
    public NumberToken add(ArithmeticToken other) {
        return this.add((RealToken) this.convert((NumberToken) other));
    }

    public RealToken add(RealToken other) {
        return RealToken.valueOf(_val.add(other._val));
    }

    @Override
    public NumberToken sub(ArithmeticToken other) {
        return this.sub((RealToken) this.convert((NumberToken) other));
    }

    public RealToken sub(RealToken other) {
        return RealToken.valueOf(_val.subtract(other._val));
    }

    @Override
    public NumberToken mul(ArithmeticToken other) {
        return this.mul((RealToken) this.convert((NumberToken) other));
    }

    public RealToken mul(RealToken other) {
        return RealToken.valueOf(_val.multiply(other._val));
    }

    @Override
    public NumberToken div(ArithmeticToken other) {
        return this.div((RealToken) this.convert((NumberToken) other));
    }

    public RealToken div(RealToken other) {
        return RealToken.valueOf(_val.divide(other._val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken idiv(NumberToken other) {
        return this.idiv((RealToken) this.convert(other));
    }

    public RealToken idiv(RealToken other) {
        return RealToken.valueOf(floor(_val.divide(other._val, Devore.MATH_CONTEXT)));
    }

    @Override
    public NumberToken mod(NumberToken other) {
        return this.mod((RealToken) this.convert(other));
    }

    public RealToken mod(RealToken other) {
        return RealToken.valueOf(_val.toBigInteger().mod(other._val.toBigInteger()));
    }

    @Override
    public NumberToken pow(NumberToken other) {
        return this.pow((RealToken) this.convert(other));
    }

    public RealToken pow(RealToken other) {
        return RealToken.valueOf(BigDecimalMath.pow(_val, other._val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken gcd(NumberToken other) {
        return this.gcd((RealToken) this.convert(other));
    }

    public RealToken gcd(RealToken other) {
        return RealToken.valueOf(MathUtils.gcd(_val, other._val));
    }

    @Override
    public NumberToken lcm(NumberToken other) {
        return this.lcm((RealToken) this.convert(other));
    }

    public RealToken lcm(RealToken other) {
        return RealToken.valueOf(MathUtils.lcm(_val, other._val));
    }

    @Override
    public NumberToken negate() {
        return RealToken.valueOf(_val.negate());
    }

    @Override
    public NumberToken inc() {
        return RealToken.valueOf(_val.add(BigDecimal.ONE));
    }

    @Override
    public NumberToken dec() {
        return RealToken.valueOf(_val.subtract(BigDecimal.ONE));
    }

    @Override
    public NumberToken signnum() {
        return RealToken.valueOf(_val.signum());
    }

    @Override
    public NumberToken factorial() {
        return RealToken.valueOf(MathUtils.factorial(_val));
    }

    @Override
    public NumberToken abs() {
        return RealToken.valueOf(_val.multiply(_val.signum() >= 0 ? BigDecimal.ONE : BD_NEG_ONE));
    }

    @Override
    public NumberToken exp() {
        return RealToken.valueOf(BigDecimalMath.exp(_val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken sin() {
        return RealToken.valueOf(BigDecimalMath.sin(_val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken cos() {
        return RealToken.valueOf(BigDecimalMath.cos(_val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken tan() {
        return RealToken.valueOf(BigDecimalMath.tan(_val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken asin() {
        return RealToken.valueOf(BigDecimalMath.asin(_val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken acos() {
        return RealToken.valueOf(BigDecimalMath.acos(_val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken atan() {
        return RealToken.valueOf(BigDecimalMath.atan(_val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken log() {
        return RealToken.valueOf(BigDecimalMath.log10(_val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken ln() {
        return RealToken.valueOf(BigDecimalMath.log2(_val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken sqrt() {
        return RealToken.valueOf(BigDecimalMath.sqrt(_val, Devore.MATH_CONTEXT));
    }

    @Override
    public NumberToken ceil() {
        return RealToken.valueOf(ceil(_val));
    }

    @Override
    public NumberToken floor() {
        return RealToken.valueOf(floor(_val));
    }

    @Override
    public BoolToken isPrime() {
        return BoolToken.valueOf(MathUtils.isPrime(_val.toBigInteger()));
    }

    @Override
    public String type() {
        return (_val.signum() == 0 || _val.scale() <= 0 || _val.stripTrailingZeros().scale() <= 0) ? "int" : "real";
    }

    @Override
    public boolean bool() {
        return !(_val.compareTo(BigDecimal.ZERO) == 0);
    }

    @Override
    public boolean equiv(Token o) {
        return o instanceof NumberToken && ((NumberToken) o).toBigDecimal().compareTo(_val) == 0;
    }

    @Override
    public int compareTo(ComparableToken n) {
        if (n instanceof NumberToken)
            return _val.compareTo(((NumberToken) n).toBigDecimal());
        return 0;
    }

    @Override
    public RealToken one() {
        return ONE;
    }

    @Override
    public RealToken zero() {
        return ZERO;
    }

    @Override
    public RealToken negOne() {
        return NEG_ONE;
    }

    @Override
    protected NumberToken convert(NumberToken promote) {
        return RealToken.valueOf(promote.toBigDecimal());
    }

    @Override
    public String toString() {
        return StringUtils.bigDecimalToString(_val);
    }
}
