package cn.devore.lang.token.math;

import cn.devore.lang.token.ComparableToken;

public abstract class ArithmeticToken extends ComparableToken {
    public abstract NumberToken add(NumberToken other);

    public abstract NumberToken sub(NumberToken other);

    public abstract NumberToken mul(NumberToken other);

    public abstract NumberToken div(NumberToken other);
}
