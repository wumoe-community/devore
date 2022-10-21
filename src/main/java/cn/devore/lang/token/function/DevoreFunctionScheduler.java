package cn.devore.lang.token.function;

import cn.devore.lang.Env;
import cn.devore.lang.Token;
import cn.devore.lang.token.KeywordToken;
import cn.devore.lang.DevoreType;

import java.util.ArrayList;
import java.util.List;

public class DevoreFunctionScheduler extends Token {
    private final List<OrdinaryFunctionToken> _functions;

    public DevoreFunctionScheduler(List<OrdinaryFunctionToken> functions) {
        this._functions = functions;
    }

    public DevoreFunctionScheduler() {
        this(new ArrayList<>());
    }

    public DevoreFunctionScheduler addFunction(OrdinaryFunctionToken function) {
        this._functions.add(function);
        return this;
    }

    public Token call(List<Token> args, Env env) {
        int diff = Integer.MAX_VALUE;
        OrdinaryFunctionToken diffFunc = null;
        funcScheduler: for (OrdinaryFunctionToken function : _functions) {
            if ((function._types.length == args.size())
                    || ((function._variadic) && (args.size() >= function._types.length - 1))) {
                int diffing = 0;
                if (function._variadic) {
                    if (args.size() >= function._types.length)
                        for (int i = function._types.length - 1; i < args.size(); ++i)
                            if (DevoreType.path(args.get(i).type(), function._types[function._types.length - 1]) == Integer.MAX_VALUE)
                                continue funcScheduler;
                    for (int i = 0; i < function._types.length - 1; ++i)
                        diffing += DevoreType.path(args.get(i).type(), function._types[i]);
                    for (int i = function._types.length - 1; i < args.size(); ++i)
                        diffing += DevoreType.path(args.get(i).type(), function._types[function._types.length - 1]);
                } else
                    for (int i = 0; i < args.size(); ++i)
                        diffing += DevoreType.path(args.get(i).type(), function._types[i]);
                if (diffing < diff) {
                    diff = diffing;
                    diffFunc = function;
                }
            }
        }
        if (diff == Integer.MAX_VALUE)
            return KeywordToken.KEYWORD_NIL;
        return diffFunc.call(args, env);
    }

    @Override
    public String type() {
        return "function";
    }

    @Override
    public Token deepcopy() {
        return new DevoreFunctionScheduler(_functions);
    }

    @Override
    public boolean bool() {
        return false;
    }

    @Override
    public boolean equiv(Token o) {
        return o instanceof DevoreFunctionScheduler && ((DevoreFunctionScheduler) o)._functions == _functions;
    }
}
