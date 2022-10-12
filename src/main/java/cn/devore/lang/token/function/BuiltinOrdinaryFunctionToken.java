package cn.devore.lang.token.function;

import cn.devore.lang.Env;
import cn.devore.lang.Token;

import java.util.List;
import java.util.function.BiFunction;

public class BuiltinOrdinaryFunctionToken extends OrdinaryFunctionToken {
    private final BiFunction<List<Token>, Env, Token> _func;

    private BuiltinOrdinaryFunctionToken(BiFunction<List<Token>, Env, Token> func) {
        this._func = func;
    }

    public static BuiltinOrdinaryFunctionToken make(BiFunction<List<Token>, Env, Token> func) {
        return new BuiltinOrdinaryFunctionToken(func);
    }

    @Override
    public Token deepcopy() {
        return make(_func);
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
