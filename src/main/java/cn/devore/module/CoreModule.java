package cn.devore.module;

import cn.devore.Devore;
import cn.devore.lang.Ast;
import cn.devore.lang.Env;
import cn.devore.lang.Evaluator;
import cn.devore.lang.Token;
import cn.devore.lang.token.*;
import cn.devore.lang.token.table.ImmutableTableToken;
import cn.devore.lang.token.table.TableToken;
import cn.devore.lang.token.table.VariableTableToken;
import cn.devore.lang.token.function.*;
import cn.devore.lang.token.list.ImmutableListToken;
import cn.devore.lang.token.list.ListToken;
import cn.devore.lang.token.list.VariableListToken;
import cn.devore.lang.token.math.NumberToken;
import cn.devore.lang.token.math.RealToken;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoreModule extends Module {
    @Override
    public void init(Env _env) {
        _env.put("true", BoolToken.TRUE);
        _env.put("false", BoolToken.FALSE);
        _env.put("nil", KeywordToken.KEYWORD_NIL);
        _env.put("cond", BuiltinSpecialFunctionToken.make(((ast, env) -> {
            for (int i = 0; i < ast.size(); ++i) {
                Env tempEnv = env.createChild();
                Ast child = ast.get(i);
                if ("else".equals(child.op().toString())) {
                    Token result = Evaluator.eval(child.get(0), tempEnv);
                    for (int j = 1; j < child.size(); ++j)
                        result = Evaluator.eval(child.get(j), tempEnv);
                    return result;
                } else if ("apply".equals(child.op().toString())
                        && Evaluator.eval(child.get(0), tempEnv).bool()) {
                    Token result = Evaluator.eval(child.get(1), tempEnv);
                    for (int j = 2; j < child.size(); ++j)
                        result = Evaluator.eval(child.get(j), tempEnv);
                    return result;
                } else if (Evaluator.eval(new Ast(child.op()), tempEnv).bool()) {
                    Token result = Evaluator.eval(child.get(0), tempEnv);
                    for (int j = 1; j < child.size(); ++j)
                        result = Evaluator.eval(child.get(j), tempEnv);
                    return result;
                }
            }
            return KeywordToken.KEYWORD_NIL;
        })));
        _env.put("range", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            List<Token> list = new ArrayList<>();
            switch (args.size()) {
                case 1 -> {
                    NumberToken end = (NumberToken) args.get(0);
                    for(NumberToken i = RealToken.ZERO; i.compareTo(end) < 0; i = i.add(RealToken.ONE))
                        list.add(i);
                }
                case 2 -> {
                    NumberToken start = (NumberToken) args.get(0);
                    NumberToken end = (NumberToken) args.get(1);
                    for(NumberToken i = start; i.compareTo(end) < 0; i = i.add(RealToken.ONE))
                        list.add(i);
                }
                default -> {
                    NumberToken start = (NumberToken) args.get(0);
                    NumberToken end = (NumberToken) args.get(1);
                    NumberToken step = (NumberToken) args.get(2);
                    NumberToken size = end.sub(start).div(step);
                    for(NumberToken i = RealToken.ZERO; i.compareTo(size) < 0; i = i.add(RealToken.ONE))
                        list.add(start.add(i.mul(step)));
                }
            }
            return new ImmutableListToken(list);
        }));
        _env.put("random", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            BigInteger rangeStart = ((NumberToken) args.get(0)).toBigInteger();
            BigInteger rangeEnd = ((NumberToken) args.get(1)).toBigInteger();
            Random rand = new Random();
            int scale = rangeEnd.toString().length();
            StringBuilder generated = new StringBuilder();
            for(int i = 0; i < rangeEnd.toString().length(); ++i)
                generated.append(rand.nextInt(10));
            BigDecimal inputRangeStart = new BigDecimal("0").setScale(scale, RoundingMode.FLOOR);
            BigDecimal inputRangeEnd = new BigDecimal(String.format("%0" + (rangeEnd.toString().length()) +  "d", 0)
                    .replace('0', '9')).setScale(scale, RoundingMode.FLOOR);
            BigDecimal outputRangeStart = new BigDecimal(rangeStart).setScale(scale, RoundingMode.FLOOR);
            BigDecimal outputRangeEnd = new BigDecimal(rangeEnd).add(new BigDecimal("1")).setScale(scale, RoundingMode.FLOOR);
            BigDecimal bd1 = new BigDecimal(new BigInteger(generated.toString()))
                    .setScale(scale, RoundingMode.FLOOR).subtract(inputRangeStart);
            BigDecimal bd2 = inputRangeEnd.subtract(inputRangeStart);
            BigDecimal bd3 = bd1.divide(bd2, RoundingMode.FLOOR);
            BigDecimal bd4 = outputRangeEnd.subtract(outputRangeStart);
            BigDecimal bd5 = bd3.multiply(bd4);
            BigDecimal bd6 = bd5.add(outputRangeStart);
            BigInteger returnInteger = bd6.setScale(0, RoundingMode.FLOOR).toBigInteger();
            returnInteger = (returnInteger.compareTo(rangeEnd) > 0 ? rangeEnd : returnInteger);
            return RealToken.valueOf(returnInteger);
        }));
        _env.put("println", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            StringBuilder builder = new StringBuilder();
            args.forEach(builder::append);
            Devore.print.println(builder);
            return KeywordToken.KEYWORD_NIL;
        })));
        _env.put("print", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            StringBuilder builder = new StringBuilder();
            args.forEach(builder::append);
            Devore.print.print(builder);
            return KeywordToken.KEYWORD_NIL;
        })));
        _env.put("+", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            NumberToken number = (NumberToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                number = number.add((NumberToken) args.get(i));
            return number;
        })));
        _env.put("-", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            NumberToken number = (NumberToken) args.get(0);
            if (args.size() == 1)
                return number.mul(number.negOne());
            for (int i = 1; i < args.size(); ++i)
                number = number.sub((NumberToken) args.get(i));
            return number;
        })));
        _env.put("*", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            NumberToken number = (NumberToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                number = number.mul((NumberToken) args.get(i));
            return number;
        })));
        _env.put("/", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            NumberToken number = (NumberToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                number = number.div((NumberToken) args.get(i));
            return number;
        })));
        _env.put("undef", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            env.remove(args.get(0).toString());
            return KeywordToken.KEYWORD_NIL;
        }));
        _env.put("def-symbol", BuiltinSpecialFunctionToken.make((ast, env) -> {
            List<String> parameters = new ArrayList<>();
            for (Ast child : ast.get(0).children())
                parameters.add(child.op().toString());
            List<Ast> asts = new ArrayList<>();
            for (int i = 1; i < ast.size(); ++i)
                asts.add(ast.get(i).copy());
            env.put(ast.get(0).op().toString(), BuiltinSpecialFunctionToken.make((inlineAst, inlineEnv) -> {
                if (inlineAst.type() == Ast.AstType.FUNCTION) {
                    Token result = KeywordToken.KEYWORD_NIL;
                    Env newEnv = inlineEnv.createChild();
                    for (Ast child : inlineAst.children())
                        result = Evaluator.eval(new Ast(Evaluator.eval(child, newEnv)), newEnv);
                    return result;
                }
                List<Ast> newAsts = new ArrayList<>();
                for (Ast child : asts) {
                    Ast newAst = child.copy();
                    for (int j = 0; j < parameters.size(); ++j)
                        defSymbolRsc(newAst, parameters.get(j), inlineAst.get(j));
                    newAsts.add(newAst);
                }
                Env newEnv = inlineEnv.createChild();
                Token result = KeywordToken.KEYWORD_NIL;
                for (Ast child : newAsts)
                    result = Evaluator.eval(child.copy(), newEnv);
                return result;
            }));
            return KeywordToken.KEYWORD_NIL;
        }));
        _env.put("def", BuiltinSpecialFunctionToken.make((ast, env) -> {
            String key = ast.get(0).op().toString();
            if (!env.contains(key)) {
                Token value = KeywordToken.KEYWORD_NIL;
                if (ast.get(0).isEmpty() && ast.get(0).type() != Ast.AstType.FUNCTION) {
                    Env tempEnv = env.createChild();
                    for (int i = 1; i < ast.size(); ++i)
                        value = Evaluator.eval(ast.get(i), tempEnv);
                } else {
                    List<String> parameters = new ArrayList<>();
                    List<Ast> asts = new ArrayList<>();
                    for (int i = 0; i < ast.get(0).size(); ++i)
                        parameters.add(ast.get(0).get(i).op().toString());
                    for (int i = 1; i < ast.size(); ++i)
                        asts.add(ast.get(i).copy());
                    value = DevoreBaseOrdinaryFunctionToken.make(asts, parameters);
                }
                _env.put(key, value);
            }
            return KeywordToken.KEYWORD_NIL;
        }));
        _env.put("set!", BuiltinSpecialFunctionToken.make((ast, env) -> {
            String key = ast.get(0).op().toString();
            Token value = KeywordToken.KEYWORD_NIL;
            if (ast.get(0).isEmpty()) {
                Env tempEnv = env.createChild();
                for (int i = 1; i < ast.size(); ++i)
                    value = Evaluator.eval(ast.get(i), tempEnv);
            } else {
                List<String> parameters = new ArrayList<>();
                List<Ast> asts = new ArrayList<>();
                for (int i = 0; i < ast.get(0).size(); ++i)
                    parameters.add(ast.get(0).get(i).op().toString());
                for (int i = 1; i < ast.size(); ++i)
                    asts.add(ast.get(i).copy());
                value = DevoreBaseOrdinaryFunctionToken.make(asts, parameters);
            }
            _env.set(key, value);
            return KeywordToken.KEYWORD_NIL;
        }));
        _env.put("apply", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            List<Token> tokens = new ArrayList<>();
            for (int i = 1; i < args.size(); ++i)
                tokens.add(args.get(i));
            Ast asts = new Ast();
            if (args.get(0) instanceof OrdinaryFunctionToken ordinaryFunc)
                return ordinaryFunc.call(tokens, env);
            for (Token token : tokens)
                asts.add(new Ast(token));
            if (args.get(0) instanceof SpecialFunctionToken specialFunc)
                return specialFunc.call(asts, env);
            return args.get(0);
        }));
        _env.put("require", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            String path = args.get(0).toString();
            if (Devore._module.containsKey(path)) {
                env.load(path);
                return BoolToken.TRUE;
            }
            Path filePath = Path.of(Devore._path + "/" + path.replace(".", "/") + ".devore");
            if (Files.exists(filePath)) {
                try {
                    Devore.call(Files.readString(filePath), env);
                } catch (IOException e) {
                    return BoolToken.FALSE;
                }
                return BoolToken.TRUE;
            }
            filePath = Path.of("./module/" + path.replace(".", "/") + ".devore");
            if (Files.exists(filePath)) {
                try {
                    Devore.call(Files.readString(filePath), env);
                } catch (IOException e) {
                    return BoolToken.FALSE;
                }
                return BoolToken.TRUE;
            }
            return BoolToken.FALSE;
        }));
        _env.put("lambda", BuiltinSpecialFunctionToken.make((ast, env) -> {
            List<String> parameters = new ArrayList<>();
            List<Ast> asts = new ArrayList<>();
            Token parameter = ast.get(0).op();
            if (!KeywordToken.KEYWORD_EMPTY.equiv(parameter))
                parameters.add(parameter.toString());
            for (int i = 0; i < ast.get(0).size(); ++i) {
                parameter = ast.get(0).get(i).op();
                if (!KeywordToken.KEYWORD_EMPTY.equiv(parameter))
                    parameters.add(parameter.toString());
            }
            for (int i = 1; i < ast.size(); ++i)
                asts.add(ast.get(i).copy());
            return DevoreBaseOrdinaryFunctionToken.make(asts, parameters);
        }));
        _env.put("if", BuiltinSpecialFunctionToken.make((ast, env) -> {
            Env tempEnv = env.createChild();
            if (Evaluator.eval(ast.get(0), tempEnv).bool())
                return Evaluator.eval(ast.get(1), tempEnv);
            else {
                Token result = Evaluator.eval(ast.get(2), tempEnv);
                for (int i = 3; i < ast.size(); ++i)
                    result = Evaluator.eval(ast.get(i), tempEnv);
                return result;
            }
        }));
        _env.put(">", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) < 0
                        || temp.compareTo((ComparableToken) args.get(i)) == 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }));
        _env.put("<", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) > 0
                        || temp.compareTo((ComparableToken) args.get(i)) == 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }));
        _env.put(">=", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) < 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }));
        _env.put("<=", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) > 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }));
        _env.put("=", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) != 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }));
        _env.put("/=", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) == 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }));
        _env.put("begin", BuiltinSpecialFunctionToken.make((ast, env) -> {
            Env tempEnv = env.createChild();
            Token result = Evaluator.eval(ast.get(0), tempEnv);
            for (int i = 1; i < ast.size(); ++i)
                result = Evaluator.eval(ast.get(i), tempEnv);
            return result;
        }));
        _env.put("sin", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).sin()));
        _env.put("cos", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).cos()));
        _env.put("tan", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).tan()));
        _env.put("asin", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).asin()));
        _env.put("acos", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).acos()));
        _env.put("atan", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).atan()));
        _env.put("log", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).log()));
        _env.put("ln", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).ln()));
        _env.put("sqrt", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).sqrt()));
        _env.put("ceil", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).ceil()));
        _env.put("floor", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).floor()));
        _env.put("prime?", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).isPrime()));
        _env.put("negate", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).negate()));
        _env.put("inc", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).inc()));
        _env.put("dec", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).dec()));
        _env.put("signnum", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).signnum()));
        _env.put("abs", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).abs()));
        _env.put("exp", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).exp()));
        _env.put("factorial", BuiltinOrdinaryFunctionToken.make((args, env) -> ((NumberToken) args.get(0)).factorial()));
        _env.put("list", BuiltinOrdinaryFunctionToken.make(((args, env) -> new ImmutableListToken(args))));
        _env.put("variable-list!", BuiltinOrdinaryFunctionToken.make(((args, env) -> new VariableListToken(args))));
        _env.put("list-ref", BuiltinOrdinaryFunctionToken.make(((args, env) ->
                ((ListToken) args.get(0)).get(((NumberToken) args.get(1)).toInt()))));
        _env.put("list-add", BuiltinOrdinaryFunctionToken.make(((args, env) -> ((ListToken) args.get(0)).add(args.get(1)))));
        _env.put("list-set", BuiltinOrdinaryFunctionToken.make(((args, env) ->
                ((ListToken) args.get(0)).set(((NumberToken) args.get(1)).toInt(), args.get(2)))));
        _env.put("list-contains", BuiltinOrdinaryFunctionToken.make(((args, env) -> ((ListToken) args.get(0)).contains(args.get(1)))));
        _env.put("list-remove", BuiltinOrdinaryFunctionToken.make(((args, env) -> ((ListToken) args.get(0)).remove(args.get(1)))));
        _env.put("list-remove-index", BuiltinOrdinaryFunctionToken.make(((args, env) ->
                ((ListToken) args.get(0)).remove(((NumberToken) args.get(1)).toInt()))));
        _env.put("sort", BuiltinOrdinaryFunctionToken.make(((args, env) -> ((ListToken) args.get(0)).sort())));
        _env.put("reverse", BuiltinOrdinaryFunctionToken.make(((args, env) -> ((ListToken) args.get(0)).reverse())));
        _env.put("head", BuiltinOrdinaryFunctionToken.make(((args, env) -> ((ListToken) args.get(0)).head())));
        _env.put("init", BuiltinOrdinaryFunctionToken.make(((args, env) -> ((ListToken) args.get(0)).init())));
        _env.put("last", BuiltinOrdinaryFunctionToken.make(((args, env) -> ((ListToken) args.get(0)).last())));
        _env.put("tail", BuiltinOrdinaryFunctionToken.make(((args, env) -> ((ListToken) args.get(0)).tail())));
        _env.put("table", BuiltinOrdinaryFunctionToken.make((args, env) -> new ImmutableTableToken()));
        _env.put("variable-table!", BuiltinOrdinaryFunctionToken.make((args, env) -> new VariableTableToken()));
        _env.put("table-put", BuiltinOrdinaryFunctionToken.make((args, env) -> ((TableToken) args.get(0)).put(args.get(1), args.get(2))));
        _env.put("table-get", BuiltinOrdinaryFunctionToken.make((args, env) -> ((TableToken) args.get(0)).get(args.get(1))));
        _env.put("table-remove", BuiltinOrdinaryFunctionToken.make((args, env) -> ((TableToken) args.get(0)).remove(args.get(1))));
        _env.put("table-keys", BuiltinOrdinaryFunctionToken.make((args, env) -> ((TableToken) args.get(0)).keys()));
        _env.put("table-values", BuiltinOrdinaryFunctionToken.make((args, env) -> ((TableToken) args.get(0)).values()));
        _env.put("table-contains-key", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((TableToken) args.get(0)).containsKey(args.get(1))));
        _env.put("table-contains-value", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((TableToken) args.get(0)).containsValue(args.get(1))));
        _env.put("string-split", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((StringToken) args.get(0)).split((StringToken) args.get(1))));
        _env.put("string-index", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((StringToken) args.get(0)).indexOf((StringToken) args.get(1))));
        _env.put("string-last-index", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((StringToken) args.get(0)).lastIndexOf((StringToken) args.get(1))));
        _env.put("string-replace", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((StringToken) args.get(0)).replace((StringToken) args.get(1), (StringToken) args.get(2))));
        _env.put("string-substring", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((StringToken) args.get(0)).substring((NumberToken) args.get(1), (NumberToken) args.get(2))));
        _env.put("->string", BuiltinOrdinaryFunctionToken.make((args, env) -> new StringToken(args.get(0).toString())));
        _env.put("->bool", BuiltinOrdinaryFunctionToken.make((args, env) -> BoolToken.valueOf(args.get(0).bool())));
        _env.put("->real", BuiltinOrdinaryFunctionToken.make((args, env) -> RealToken.valueOf(args.get(0).toString())));
    }

    private static void defSymbolRsc(Ast ast, String id, Ast value) {
        if (ast.op() instanceof IdToken && id.equals(ast.op().toString())) {
            ast.setOp(value.op());
            ast.setChildren(value.children());
        }
        for (Ast child : ast.children())
            defSymbolRsc(child, id, value);
    }
}
