package cn.devore.lang;

import cn.devore.lang.token.IdToken;
import cn.devore.lang.token.function.DevoreBaseOrdinaryFunctionToken;
import cn.devore.lang.token.function.DevoreFunctionScheduler;
import cn.devore.lang.token.function.OrdinaryFunctionToken;
import cn.devore.lang.token.function.SpecialFunctionToken;

import java.util.ArrayList;
import java.util.List;

public class Evaluator {
    public static Token eval(Ast ast, Env env) {
        Token temp = ast.op();
        while (true) {
            String op = temp.toString();
            if (!(temp instanceof IdToken && env.contains(op)))
                break;
            temp = env.get(op);
        }
        if (temp instanceof DevoreFunctionScheduler scheduler && ast.isEmpty()) {
            for (OrdinaryFunctionToken func : scheduler.getFunctions())
                if (func._types.length == 0 || (func._variadic && func._types.length == 1))
                    ast.setType(Ast.AstType.FUNCTION);
        }
        ast.setOp(temp);
        if (ast.isEmpty() && ast.type() != Ast.AstType.FUNCTION) {
            ast.clear();
            return ast.op();
        }
        if (ast.op() instanceof SpecialFunctionToken) {
            ast.setOp(((SpecialFunctionToken) ast.op()).call(ast, env));
            ast.clear();
        } else if (ast.op() instanceof DevoreFunctionScheduler) {
            for (int i = 0; i < ast.size(); ++i)
                if (!ast.get(i).isEmpty() || "id".equals(ast.get(i).op().type()))
                    eval(ast.get(i).copy(), env);
            List<Token> args = new ArrayList<>();
            for (int i = 0; i < ast.size(); ++i)
                args.add(ast.get(i).op());
            ast.setOp(((DevoreFunctionScheduler) ast.op()).call(args, env));
            ast.clear();
        } else if (ast.op() instanceof DevoreBaseOrdinaryFunctionToken) {
            for (int i = 0; i < ast.size(); ++i)
                if (!ast.get(i).isEmpty() || "id".equals(ast.get(i).op().type()))
                    eval(ast.get(i), env);
            List<Token> args = new ArrayList<>();
            for (int i = 0; i < ast.size(); ++i)
                args.add(ast.get(i).op());
            ast.setOp(((DevoreBaseOrdinaryFunctionToken) ast.op()).call(args, env));
            ast.clear();
        }
        return ast.op();
    }
}
