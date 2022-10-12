package cn.devore.lang.token.function;

import cn.devore.lang.Ast;
import cn.devore.lang.Env;
import cn.devore.lang.Token;

import java.util.function.BiFunction;

public class BuiltinSpecialFunctionToken extends SpecialFunctionToken {
    private final BiFunction<Ast, Env, Token> _func;

    private BuiltinSpecialFunctionToken(BiFunction<Ast, Env, Token> func) {
        this._func = func;
    }

    public static BuiltinSpecialFunctionToken make(BiFunction<Ast, Env, Token> func) {
        return new BuiltinSpecialFunctionToken(func);
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
        return o instanceof BuiltinSpecialFunctionToken && ((BuiltinSpecialFunctionToken) o)._func.equals(_func);
    }

    @Override
    public Token call(Ast ast, Env env) {
        return _func.apply(ast, env);
    }
}
