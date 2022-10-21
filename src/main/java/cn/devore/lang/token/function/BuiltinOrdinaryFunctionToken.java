package cn.devore.lang.token.function;

import cn.devore.lang.Env;
import cn.devore.lang.Token;

import java.util.List;
import java.util.function.BiFunction;

public class BuiltinOrdinaryFunctionToken extends OrdinaryFunctionToken {
    private final BiFunction<List<Token>, Env, Token> _func;

    private BuiltinOrdinaryFunctionToken(BiFunction<List<Token>, Env, Token> func, String[] types, boolean variadic) {
        super(types, variadic);
        this._func = func;
    }

    public static BuiltinOrdinaryFunctionToken make(BiFunction<List<Token>, Env, Token> func, String[] types, boolean variadic) {
        return new BuiltinOrdinaryFunctionToken(func, types, variadic);
    }

    @Override
    public Token deepcopy() {
        return make(_func, _types, _variadic);
    }

    @Override
    public boolean bool() {
        return _func != null;
    }

    @Override
    public boolean equiv(Token o) {
        return o instanceof BuiltinOrdinaryFunctionToken && ((BuiltinOrdinaryFunctionToken) o)._func.equals(_func);
    }

    @Override
    public Token call(List<Token> args, Env env) {
        return _func.apply(args, env);
    }
}
