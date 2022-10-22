package cn.devore.lang.token;

import cn.devore.lang.Token;

public class BoolToken extends Token {
    public static final BoolToken TRUE = new BoolToken(true);
    public static final BoolToken FALSE = new BoolToken(false);
    private final boolean _bool;

    private BoolToken(boolean bool) {
        this._bool = bool;
    }

    public static BoolToken valueOf(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    @Override
    public String type() {
        return "bool";
    }

    @Override
    public Token deepcopy() {
        return new BoolToken(_bool);
    }

    @Override
    public boolean bool() {
        return _bool;
    }

    @Override
    public boolean equiv(Token o) {
        return o instanceof BoolToken && ((BoolToken) o)._bool == _bool;
    }

    @Override
    public String toString() {
        return _bool ? "true" : "false";
    }
}
