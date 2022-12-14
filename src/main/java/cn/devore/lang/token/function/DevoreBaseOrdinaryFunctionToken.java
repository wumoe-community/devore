package cn.devore.lang.token.function;

import cn.devore.lang.Ast;
import cn.devore.lang.Env;
import cn.devore.lang.Evaluator;
import cn.devore.lang.Token;
import cn.devore.lang.token.KeywordToken;
import cn.devore.lang.token.list.VariableListToken;

import java.util.ArrayList;
import java.util.List;

public class DevoreBaseOrdinaryFunctionToken extends OrdinaryFunctionToken {
    private final List<Ast> _asts;
    private final Env _env;
    private final List<String> _parameters;

    private DevoreBaseOrdinaryFunctionToken(List<Ast> asts, List<String> parameters, String[] types, boolean variadic, Env env) {
        super(types, variadic);
        this._asts = new ArrayList<>();
        for (Ast ast : asts)
            _asts.add(ast.copy());
        this._parameters = parameters;
        this._env = env;
    }

    public static DevoreBaseOrdinaryFunctionToken make(List<Ast> asts, List<String> parameters, String[] types, boolean variadic, Env env) {
        return new DevoreBaseOrdinaryFunctionToken(asts, parameters, types, variadic, env);
    }

    @Override
    public Token deepcopy() {
        List<Ast> newAsts = new ArrayList<>();
        for (Ast ast : _asts)
            newAsts.add(ast.copy());
        return make(newAsts, _parameters, _types, _variadic, _env);
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
        Env functionEnv = _env.createChild();
        if (_variadic) {
            for (int i = 0; i < _parameters.size() - 1; ++i)
                functionEnv.put(_parameters.get(i), args.get(i));
            VariableListToken list = new VariableListToken();
            for (int i = _parameters.size() - 1; i < args.size(); ++i)
                list.add(args.get(i));
            functionEnv.put(_parameters.get(_parameters.size() - 1), list.toImmutable());
        } else
            for (int i = 0; i < _parameters.size(); ++i)
                functionEnv.put(_parameters.get(i), args.get(i));
        Token result = KeywordToken.KEYWORD_NIL;
        for (Ast ast : _asts)
            result = Evaluator.eval(ast.copy(), functionEnv);
        return result;
    }
}
