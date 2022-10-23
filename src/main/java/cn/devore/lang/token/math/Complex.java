package cn.devore.lang.token.math;

import cn.devore.exception.DevoreAssert;

public class Complex {
    public double r;
    public double i;

    public Complex(final double r, final double i) {
        this.r = r;
        this.i = i;
    }

    public Complex() {
        this.r = 0.0;
        this.i = 0.0;
    }

    public static Complex add(final Complex a, final Complex b) {
        final double real = a.r + b.r;
        final double imag = a.i + b.i;
        return new Complex(real, imag);
    }

    public static Complex add(final double real, final Complex c) {
        return new Complex(c.r + real, c.i);
    }

    public static Complex sub(final Complex a, final Complex b) {
        final double real = a.r - b.r;
        final double imag = a.i - b.i;
        return new Complex(real, imag);
    }

    public static Complex mul(final Complex a, final Complex b) {
        final double real = (a.r * b.r) - (a.i * b.i);
        final double imag = (a.i * b.r) + (a.r * b.i);
        return new Complex(real, imag);
    }

    public static Complex div(final Complex a, final Complex b) {
        DevoreAssert.runtimeAssert((b.r != 0) || (b.i != 0), "The complex number b is 0");
        final double c = Math.pow(b.r, 2);
        final double d = Math.pow(b.i, 2);
        double real;
        double imag;
        real = (a.r * b.r) + (a.i * b.i);
        real /= (c + d);
        imag = (a.i * b.r) - (a.r * b.i);
        imag /= (c + d);

        return new Complex(real, imag);
    }

    public static double abs(final Complex z) {
        double x, y, ans, temp;
        x = Math.abs(z.r);
        y = Math.abs(z.i);
        if (x == 0.0) {
            ans = y;
        } else if (y == 0.0) {
            ans = x;
        } else if (x > y) {
            temp = y / x;
            ans = x * Math.sqrt(1.0 + (temp * temp));
        } else {
            temp = x / y;
            ans = y * Math.sqrt(1.0 + (temp * temp));
        }
        return ans;
    }

    public static Complex div(final double x, final Complex c) {
        DevoreAssert.runtimeAssert(x != 0, "scalar is 0");
        final Complex result = new Complex();
        result.r = c.r / x;
        result.i = c.i / x;
        return result;
    }

    public Complex negate() {
        return new Complex(-this.r, -this.i);
    }

    public Complex exp() {
        final double exp_x = Math.exp(this.r);
        return new Complex(exp_x * Math.cos(this.i), exp_x * Math.sin(this.i));
    }

    public Complex ln() {
        final double rpart = Math.sqrt((this.r * this.r) + (this.i * this.i));
        double ipart = Math.atan2(this.i, this.r);
        if (ipart > Math.PI) {
            ipart = ipart - (2.0 * Math.PI);
        }
        return new Complex(Math.log(rpart), ipart);
    }

    public Complex log() {
        final double rpart = Math.sqrt((this.r * this.r) + (this.i * this.i));
        double ipart = Math.atan2(this.i, this.r);
        if (ipart > Math.PI) {
            ipart = ipart - (2.0 * Math.PI);
        }
        return new Complex(Math.log10(rpart), (1 / Math.log(10)) * ipart);
    }

    public Complex sqrt() {
        final double r = Math.sqrt((this.r * this.r) + (this.i * this.i));
        final double rpart = Math.sqrt(0.5 * (r + this.r));
        double ipart = Math.sqrt(0.5 * (r - this.r));
        if (this.i < 0.0) {
            ipart = -ipart;
        }
        return new Complex(rpart, ipart);
    }

    public Complex sin() {
        return new Complex(Math.sin(this.r) * Math.cosh(this.i), Math.cos(this.r) * Math.sinh(this.i));
    }

    public Complex cos() {
        return new Complex(Math.cos(this.r) * Math.cosh(this.i), -StrictMath.sin(this.r) * Math.sinh(this.i));
    }

    public Complex tan() {
        return div(this.sin(), this.cos());
    }

    public Complex asin() {
        final Complex IM = new Complex(0.0, -1.0);
        final Complex ZP = mul(this, IM);
        final Complex ZM = add((sub(new Complex(1.0, 0.0), mul(this, this))).sqrt(), ZP);
        return mul(ZM.log(), new Complex(0.0, 1.0));
    }

    public Complex acos() {
        final Complex IM = new Complex(0.0, -1.0);
        final Complex ZM = add(mul((sub(new Complex(1.0, 0.0), mul(this, this))).sqrt(), IM), this);
        return mul(ZM.log(), new Complex(0.0, 1.0));
    }

    public Complex atan() {
        final Complex IM = new Complex(0.0, -1.0);
        final Complex ZP = new Complex(this.r, this.i - 1.0);
        final Complex ZM = new Complex(-this.r, -this.i - 1.0);
        return div(2.0, mul(IM, (div(ZP, ZM).log())));
    }
}
