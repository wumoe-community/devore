package cn.devore.lang.token.function;

import cn.devore.lang.Env;
import cn.devore.lang.Token;

import java.util.List;

public abstract class OrdinaryFunctionToken extends Token {
    public final String[] _types;
    public final boolean _variadic;

    public OrdinaryFunctionToken(String[] types, boolean variadic) {
        this._types = types;
        this._variadic = variadic;
    }

    @Override
    public String type() {
        return "function";
    }

    public abstract Token call(List<Token> args, Env env);
}
