package cn.devore.lang.token.math;

import cn.devore.lang.token.ComparableToken;

public abstract class ArithmeticToken extends ComparableToken {
    public abstract ArithmeticToken add(ArithmeticToken other);

    public abstract ArithmeticToken sub(ArithmeticToken other);

    public abstract ArithmeticToken mul(ArithmeticToken other);

    public abstract ArithmeticToken div(ArithmeticToken other);
}
