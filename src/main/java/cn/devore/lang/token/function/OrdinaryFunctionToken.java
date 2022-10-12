package cn.devore.lang.token.function;

import cn.devore.lang.Env;
import cn.devore.lang.Token;

import java.util.List;

public abstract class OrdinaryFunctionToken extends Token {
    public abstract Token call(List<Token> args, Env env);
}
