package cn.devore;

import cn.devore.lang.Ast;
import cn.devore.lang.Env;
import cn.devore.lang.Evaluator;
import cn.devore.lang.Token;
import cn.devore.module.CoreModule;
import cn.devore.module.Module;
import cn.devore.parse.Lexer;
import cn.devore.parse.Parser;

import java.io.InputStream;
import java.io.PrintStream;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Devore {
    public static String _path = System.getProperty("user.dir");
    public static PrintStream print = System.out;
    public static InputStream input = System.in;
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    public static final Ast AST_EMPTY = new Ast();
    public static final Map<String, Module> _module = new HashMap<>();

    static {
        _module.put("core", new CoreModule());
    }

    public static Env newDefaultEnv() {
        return new Env().load("core");
    }
    public static Token call(String code) {
        return call(code, newDefaultEnv());
    }

    public static Token call(String code, Env env) {
        List<String> expressions = Lexer.splitCode(Lexer.preprocessor(code));
        Token result = null;
        for (String expression : expressions)
            result = Evaluator.eval(Parser.parser(Lexer.lexer(expression)), env);
        return result;
    }
}
