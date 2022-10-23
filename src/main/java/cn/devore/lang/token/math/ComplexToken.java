package cn.devore.lang.token.math;

import cn.devore.lang.Token;
import cn.devore.lang.token.BoolToken;
import cn.devore.lang.token.ComparableToken;
import cn.devore.math.Complex;
import cn.devore.util.MathUtils;
import cn.devore.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ComplexToken extends NumberToken {
    public static final Complex COMPLEX_ONE = new Complex(1.0, 0.0);
    public static final Complex COMPLEX_ZERO = new Complex(0.0, 0.0);
    public static final ComplexToken ZERO = new ComplexToken(COMPLEX_ZERO);
    public static final ComplexToken ONE = new ComplexToken(COMPLEX_ONE);
    public static final ComplexToken NEG_ONE = new ComplexToken(-1);

    private final Complex _val;

    private ComplexToken(Complex c) {
        _val = c;
    }

    public ComplexToken(int n) {
        _val = new Complex(n, 0);
    }

    public ComplexToken(double d) {
        _val = new Complex(d, 0);
    }

    public ComplexToken(BigDecimal n) {
        _val = new Complex(n.doubleValue(), 0);
    }

    public ComplexToken(double r, double i) {
        _val = new Complex(r, i);
    }

    private static Complex ceil(Complex complex) {
        return new Complex(Math.ceil(complex.r), complex.i);
    }

    private static Complex floor(Complex complex) {
        return new Complex(Math.floor(complex.r), complex.i);
    }

    public double i() {
        return _val.i;
    }

    public double r() {
        return _val.r;
    }

    @Override
    public int toInt() {
        return (int) _val.r;
    }

    @Override
    public long toLong() {
        return (long) _val.r;
    }

    @Override
    public float toFloat() {
        return (float) _val.r;
    }

    @Override
    public double toDouble() {
        return _val.r;
    }

    @Override
    public BigDecimal toBigDecimal() {
        return new BigDecimal(_val.r);
    }

    @Override
    public BigInteger toBigInteger() {
        return toBigDecimal().toBigInteger();
    }

    @Override
    public ArithmeticToken add(ArithmeticToken other) {
        return this.add((ComplexToken) other);
    }

    public ComplexToken add(ComplexToken other) {
        return new ComplexToken(Complex.add(_val, other._val));
    }

    @Override
    public ArithmeticToken sub(ArithmeticToken other) {
        return this.sub((ComplexToken) other);
    }

    public ComplexToken sub(ComplexToken other) {
        return new ComplexToken(Complex.sub(_val, other._val));
    }

    @Override
    public ArithmeticToken mul(ArithmeticToken other) {
        return this.mul((ComplexToken) other);
    }

    public ComplexToken mul(ComplexToken other) {
        return new ComplexToken(Complex.mul(_val, other._val));
    }

    @Override
    public ArithmeticToken div(ArithmeticToken other) {
        return this.div((ComplexToken) other);
    }

    public ComplexToken div(ComplexToken other) {
        return new ComplexToken(Complex.div(_val, other._val));
    }

    @Override
    public NumberToken mod(NumberToken other) {
        return this.mod((ComplexToken) other);
    }

    public ComplexToken mod(ComplexToken other) {
        return new ComplexToken(new Complex(((long) _val.r) % ((long) other._val.r), 0.0));
    }

    @Override
    public NumberToken pow(NumberToken other) {
        return this.pow((ComplexToken) other);
    }

    public ComplexToken pow(ComplexToken other) {
        Complex a = Complex.mul(other._val, _val.ln());
        a = a.exp();
        return new ComplexToken(a);
    }

    @Override
    public NumberToken gcd(NumberToken other) {
        return this.gcd((ComplexToken) other);
    }

    public ComplexToken gcd(ComplexToken other) {
        return new ComplexToken(MathUtils.gcd(new BigDecimal(_val.r), new BigDecimal(other._val.r)));
    }

    @Override
    public NumberToken lcm(NumberToken other) {
        return this.lcm((ComplexToken) other);
    }

    public ComplexToken lcm(ComplexToken other) {
        return new ComplexToken(MathUtils.lcm(new BigDecimal(_val.r), new BigDecimal(other._val.r)));
    }

    @Override
    public NumberToken negate() {
        return new ComplexToken(_val.negate());
    }

    @Override
    public NumberToken inc() {
        return new ComplexToken(Complex.add(_val, COMPLEX_ONE));
    }

    @Override
    public NumberToken dec() {
        return new ComplexToken(Complex.sub(_val, COMPLEX_ONE));
    }

    @Override
    public NumberToken signnum() {
        return new RealToken(new BigDecimal(_val.r).signum());
    }

    @Override
    public NumberToken factorial() {
        return new RealToken(MathUtils.factorial(new BigDecimal(_val.r)));
    }

    @Override
    public NumberToken abs() {
        return new RealToken(Complex.abs(_val));
    }

    @Override
    public NumberToken exp() {
        return new ComplexToken(_val.exp());
    }

    @Override
    public NumberToken sin() {
        return new ComplexToken(_val.sin());
    }

    @Override
    public NumberToken cos() {
        return new ComplexToken(_val.cos());
    }

    @Override
    public NumberToken tan() {
        return new ComplexToken(_val.tan());
    }

    @Override
    public NumberToken asin() {
        return new ComplexToken(_val.asin());
    }

    @Override
    public NumberToken acos() {
        return new ComplexToken(_val.acos());
    }

    @Override
    public NumberToken atan() {
        return new ComplexToken(_val.atan());
    }

    @Override
    public NumberToken log() {
        return new ComplexToken(_val.log());
    }

    @Override
    public NumberToken ln() {
        return new ComplexToken(_val.ln());
    }

    @Override
    public NumberToken sqrt() {
        return new ComplexToken(_val.sqrt());
    }

    @Override
    public NumberToken ceil() {
        return new ComplexToken(ceil(_val));
    }

    @Override
    public NumberToken floor() {
        return new ComplexToken(floor(_val));
    }

    @Override
    public BoolToken isPrime() {
        return BoolToken.valueOf(MathUtils.isPrime(new BigInteger(String.valueOf((long) _val.r))));
    }

    @Override
    public boolean bool() {
        return _val.r != 0.0;
    }

    @Override
    public boolean equiv(Token o) {
        if (o instanceof ComplexToken c)
            return c._val.i == _val.i && c._val.r == _val.r;
        else if (o instanceof NumberToken && _val.i == 0.0)
            return ((NumberToken) o).toDouble() == _val.r;
        else
            return false;
    }

    @Override
    public String type() {
        return "complex";
    }

    @Override
    public int compareTo(ComparableToken n) {
        if (n instanceof NumberToken)
            return Double.compare(_val.r, ((NumberToken) n).toDouble());
        return 0;
    }

    @Override
    public ComplexToken one() {
        return ONE;
    }

    @Override
    public ComplexToken zero() {
        return ZERO;
    }

    @Override
    public ComplexToken negOne() {
        return NEG_ONE;
    }

    @Override
    protected NumberToken convert(NumberToken promote) {
        return new ComplexToken(promote.toDouble(), 0);
    }

    @Override
    public String toString() {
        if (_val.r != 0 && _val.i != 0)
            return StringUtils.doubleToString(_val.r) + "+" + StringUtils.doubleToString(_val.i) + "i";
        if (_val.r == 0 && _val.i != 0)
            return StringUtils.doubleToString(_val.i) + "i";
        if (_val.r != 0)
            return String.valueOf(StringUtils.doubleToString(_val.r));
        return "0";
    }
}
