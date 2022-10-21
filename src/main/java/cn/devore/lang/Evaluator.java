package cn.devore.lang;

import cn.devore.lang.token.IdToken;
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
        if ((temp instanceof SpecialFunctionToken || temp instanceof OrdinaryFunctionToken) && ast.isEmpty())
            ast.setType(Ast.AstType.FUNCTION);
        ast.setOp(temp);
        if (ast.isEmpty() && ast.type() != Ast.AstType.FUNCTION)
            return ast.op();
        if (ast.op() instanceof SpecialFunctionToken) {
            ast.setOp(((SpecialFunctionToken) ast.op()).call(ast, env));
            ast.clear();
        } else if (ast.op() instanceof DevoreFunctionScheduler) {
            for (int i = 0; i < ast.size(); ++i)
                ast.get(i).setOp(eval(ast.get(i).copy(), env));
            List<Token> args = new ArrayList<>();
            for (int i = 0; i < ast.size(); ++i)
                args.add(ast.get(i).op());
            ast.setOp(((DevoreFunctionScheduler) ast.op()).call(args, env));
            ast.clear();
        }
        return ast.op();
    }
}
