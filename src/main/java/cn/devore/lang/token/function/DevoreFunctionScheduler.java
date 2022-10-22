package cn.devore.lang.token.function;

import cn.devore.exception.DevoreAssert;
import cn.devore.lang.DevoreType;
import cn.devore.lang.Env;
import cn.devore.lang.Token;

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

    private static int valueAdd(int v1, int v2) {
        if (v1 == Integer.MAX_VALUE || v2 == Integer.MIN_VALUE)
            return Integer.MAX_VALUE;
        return v1 + v2;
    }

    public DevoreFunctionScheduler addFunction(OrdinaryFunctionToken function) {
        this._functions.add(function);
        return this;
    }

    public Token call(List<Token> args, Env env) {
        int diff = Integer.MAX_VALUE;
        OrdinaryFunctionToken diffFunc = null;
        funcScheduler:
        for (OrdinaryFunctionToken function : _functions) {
            if ((function._types.length == args.size())
                    || ((function._variadic) && (args.size() >= function._types.length - 1))) {
                int diffing = 0;
                if (function._variadic) {
                    if (args.size() >= function._types.length)
                        for (int i = function._types.length - 1; i < args.size(); ++i)
                            if (DevoreType.check(args.get(i).type(), function._types[function._types.length - 1]) == Integer.MAX_VALUE)
                                continue funcScheduler;
                    for (int i = 0; i < function._types.length - 1; ++i)
                        diffing = valueAdd(diffing, DevoreType.check(args.get(i).type(), function._types[i]));
                    for (int i = function._types.length - 1; i < args.size(); ++i)
                        diffing = valueAdd(diffing, DevoreType.check(args.get(i).type(), function._types[function._types.length - 1]));
                } else
                    for (int i = 0; i < args.size(); ++i)
                        diffing = valueAdd(diffing, DevoreType.check(args.get(i).type(), function._types[i]));
                if (diffing <= diff) {
                    diff = diffing;
                    diffFunc = function;
                }
            }
        }
        DevoreAssert.runtimeAssert(diff != Integer.MAX_VALUE, "No matching function found.");
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
