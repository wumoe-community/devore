package cn.devore.lang.token.function;

import cn.devore.lang.Ast;
import cn.devore.lang.Env;
import cn.devore.lang.Evaluator;
import cn.devore.lang.Token;
import cn.devore.lang.token.KeywordToken;
import cn.devore.lang.token.list.ImmutableListToken;
import cn.devore.lang.token.list.ListToken;

import java.util.ArrayList;
import java.util.List;

public class DevoreBaseOrdinaryFunctionToken extends OrdinaryFunctionToken {
    private final List<Ast> _asts;
    private final List<String> _parameters;

    private DevoreBaseOrdinaryFunctionToken(List<Ast> asts, List<String> parameters, String[] types, boolean variadic) {
        super(types, variadic);
        this._asts = new ArrayList<>();
        for (Ast ast : asts)
            _asts.add(ast.copy());
        this._parameters = parameters;
    }

    public static DevoreBaseOrdinaryFunctionToken make(List<Ast> asts, List<String> parameters, String[] types, boolean variadic) {
        return new DevoreBaseOrdinaryFunctionToken(asts, parameters, types, variadic);
    }

    @Override
    public Token deepcopy() {
        List<Ast> newAsts = new ArrayList<>();
        for (Ast ast : _asts)
            newAsts.add(ast.copy());
        return make(newAsts, _parameters, _types, _variadic);
    }

    @Override
    public boolean bool() {
        return _asts != null && !_asts.isEmpty();
    }

    @Override
    public boolean equiv(Token o) {
        return o instanceof DevoreBaseOrdinaryFunctionToken
                && ((DevoreBaseOrdinaryFunctionToken) o)._asts.equals(_asts)
                && ((DevoreBaseOrdinaryFunctionToken) o)._parameters == _parameters;
    }

    @Override
    public Token call(List<Token> args, Env env) {
        Env functionEnv = env.createChild();
        if (_variadic) {
            for (int i = 0; i < _parameters.size() - 1; ++i)
                functionEnv.put(_parameters.get(i), args.get(i));
            ListToken list = new ImmutableListToken();
            for (int i = _parameters.size() - 1; i < args.size(); ++i)
                list.add(args.get(i));
            functionEnv.put(_parameters.get(_parameters.size() - 1), list);
        } else {
            for (int i = 0; i < _parameters.size(); ++i)
                functionEnv.put(_parameters.get(i), args.get(i));
        }
        Token result = KeywordToken.KEYWORD_NIL;
        for (Ast ast : _asts)
            result = Evaluator.eval(ast.copy(), functionEnv);
        return result;
    }
}
