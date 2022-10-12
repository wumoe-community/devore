package cn.devore.lang.token.function;

import cn.devore.lang.Ast;
import cn.devore.lang.Env;
import cn.devore.lang.Token;

public abstract class SpecialFunctionToken extends Token {
    public abstract Token call(Ast ast, Env env);
}
