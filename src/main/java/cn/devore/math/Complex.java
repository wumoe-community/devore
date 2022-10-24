package cn.devore.math;

import cn.devore.exception.DevoreAssert;

public class Complex {
    public double _r;
    public double _i;

    public Complex(double r, double i) {
        this._r = r;
        this._i = i;
    }

    public Complex() {
        this._r = 0.0;
        this._i = 0.0;
    }

    public static Complex add(Complex a, Complex b) {
        double real = a._r + b._r;
        double imag = a._i + b._i;
        return new Complex(real, imag);
    }

    public static Complex add(double real, Complex c) {
        return new Complex(c._r + real, c._i);
    }

    public static Complex sub(Complex a, Complex b) {
        double real = a._r - b._r;
        double imag = a._i - b._i;
        return new Complex(real, imag);
    }

    public static Complex mul(Complex a, Complex b) {
        double real = (a._r * b._r) - (a._i * b._i);
        double imag = (a._i * b._r) + (a._r * b._i);
        return new Complex(real, imag);
    }

    public static Complex div(Complex a, Complex b) {
        DevoreAssert.runtimeAssert((b._r != 0) || (b._i != 0), "The complex number b is 0");
        double c = Math.pow(b._r, 2);
        double d = Math.pow(b._i, 2);
        double real;
        double imag;
        real = (a._r * b._r) + (a._i * b._i);
        real /= (c + d);
        imag = (a._i * b._r) - (a._r * b._i);
        imag /= (c + d);
        return new Complex(real, imag);
    }

    public static double abs(Complex z) {
        double x, y, ans, temp;
        x = Math.abs(z._r);
        y = Math.abs(z._i);
        if (x == 0.0)
            ans = y;
        else if (y == 0.0)
            ans = x;
        else if (x > y) {
            temp = y / x;
            ans = x * Math.sqrt(1.0 + (temp * temp));
        } else {
            temp = x / y;
            ans = y * Math.sqrt(1.0 + (temp * temp));
        }
        return ans;
    }

    public static Complex div(double x, Complex c) {
        DevoreAssert.runtimeAssert(x != 0, "scalar is 0");
        Complex result = new Complex();
        result._r = c._r / x;
        result._i = c._i / x;
        return result;
    }

    public Complex negate() {
        return new Complex(-this._r, -this._i);
    }

    public Complex exp() {
        double exp_x = Math.exp(this._r);
        return new Complex(exp_x * Math.cos(this._i), exp_x * Math.sin(this._i));
    }

    public Complex ln() {
        double rpart = Math.sqrt((this._r * this._r) + (this._i * this._i));
        double ipart = Math.atan2(this._i, this._r);
        if (ipart > Math.PI)
            ipart = ipart - (2.0 * Math.PI);
        return new Complex(Math.log(rpart), ipart);
    }

    public Complex log() {
        double rpart = Math.sqrt((this._r * this._r) + (this._i * this._i));
        double ipart = Math.atan2(this._i, this._r);
        if (ipart > Math.PI)
            ipart = ipart - (2.0 * Math.PI);
        return new Complex(Math.log10(rpart), (1 / Math.log(10)) * ipart);
    }

    public Complex sqrt() {
        double r = Math.sqrt((this._r * this._r) + (this._i * this._i));
        double rpart = Math.sqrt(0.5 * (r + this._r));
        double ipart = Math.sqrt(0.5 * (r - this._r));
        if (this._i < 0.0)
            ipart = -ipart;
        return new Complex(rpart, ipart);
    }

    public Complex sin() {
        return new Complex(Math.sin(this._r) * Math.cosh(this._i), Math.cos(this._r) * Math.sinh(this._i));
    }

    public Complex cos() {
        return new Complex(Math.cos(this._r) * Math.cosh(this._i), -StrictMath.sin(this._r) * Math.sinh(this._i));
    }

    public Complex tan() {
        return div(this.sin(), this.cos());
    }

    public Complex asin() {
        Complex IM = new Complex(0.0, -1.0);
        Complex ZP = mul(this, IM);
        Complex ZM = add((sub(new Complex(1.0, 0.0), mul(this, this))).sqrt(), ZP);
        return mul(ZM.log(), new Complex(0.0, 1.0));
    }

    public Complex acos() {
        Complex IM = new Complex(0.0, -1.0);
        Complex ZM = add(mul((sub(new Complex(1.0, 0.0), mul(this, this))).sqrt(), IM), this);
        return mul(ZM.log(), new Complex(0.0, 1.0));
    }

    public Complex atan() {
        Complex IM = new Complex(0.0, -1.0);
        Complex ZP = new Complex(this._r, this._i - 1.0);
        Complex ZM = new Complex(-this._r, -this._i - 1.0);
        return div(2.0, mul(IM, (div(ZP, ZM).log())));
    }
}
