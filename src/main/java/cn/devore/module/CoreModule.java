package cn.devore.module;

import cn.devore.Devore;
import cn.devore.exception.DevoreAssert;
import cn.devore.lang.*;
import cn.devore.lang.token.*;
import cn.devore.lang.token.function.*;
import cn.devore.lang.token.list.ImmutableListToken;
import cn.devore.lang.token.list.ListToken;
import cn.devore.lang.token.list.VariableListToken;
import cn.devore.lang.token.math.ArithmeticToken;
import cn.devore.lang.token.math.NumberToken;
import cn.devore.lang.token.math.RealToken;
import cn.devore.lang.token.table.ImmutableTableToken;
import cn.devore.lang.token.table.TableToken;
import cn.devore.lang.token.table.VariableTableToken;

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
    private static void defSymbolRsc(Ast ast, String id, Ast value) {
        if (ast.op() instanceof IdToken && id.equals(ast.op().toString())) {
            ast.setOp(value.op());
            ast.setChildren(value.children());
        }
        for (Ast child : ast.children())
            defSymbolRsc(child, id, value);
    }

    @Override
    public void init(Env _env) {
        _env.put("true", BoolToken.TRUE);
        _env.put("false", BoolToken.FALSE);
        _env.put("nil", KeywordToken.KEYWORD_NIL);
        _env.put("let", BuiltinSpecialFunctionToken.make(((ast, env) -> {
            Env newEnv = env.createChild();
            Token result = KeywordToken.KEYWORD_NIL;
            for (Ast node : ast.get(0).children()) {
                if ("apply".equals(node.op().toString())) {
                    List<String> parameters = new ArrayList<>();
                    String[] paramTypes = new String[node.get(0).size()];
                    for (int i = 0; i < node.get(0).size(); ++i) {
                        parameters.add(node.get(0).get(i).op().toString());
                        paramTypes[i] = ((IdToken) node.get(0).get(i).op())._type;
                    }
                    List<Ast> asts = new ArrayList<>();
                    for (int i = 1; i < node.size(); ++i)
                        asts.add(node.get(i).copy());
                    newEnv.put(node.get(0).op().toString(),
                            DevoreBaseOrdinaryFunctionToken.make(asts, parameters, paramTypes, false, env));
                } else {
                    Token value = KeywordToken.KEYWORD_NIL;
                    for (Ast e : node.children())
                        value = Evaluator.eval(e.copy(), env.createChild());
                    String type = ((IdToken) node.op())._type;
                    DevoreAssert.typeAssert(
                            DevoreType.check(value.type(), type) != Integer.MAX_VALUE,
                            value.type() + " not is " + type);
                    newEnv.put(node.op().toString(), value);
                }
            }
            for (int i = 1; i < ast.size(); ++i)
                result = Evaluator.eval(ast.get(i).copy(), newEnv.createChild());
            return result;
        })));
        _env.put("let*", BuiltinSpecialFunctionToken.make(((ast, env) -> {
            Env newEnv = env.createChild();
            Token result = KeywordToken.KEYWORD_NIL;
            for (Ast node : ast.get(0).children()) {
                if ("apply".equals(node.op().toString())) {
                    List<String> parameters = new ArrayList<>();
                    String[] paramTypes = new String[node.get(0).size()];
                    for (int i = 0; i < node.get(0).size(); ++i) {
                        parameters.add(node.get(0).get(i).op().toString());
                        paramTypes[i] = ((IdToken) node.get(0).get(i).op())._type;
                    }
                    List<Ast> asts = new ArrayList<>();
                    for (int i = 1; i < node.size(); ++i)
                        asts.add(node.get(i).copy());
                    newEnv.put(node.get(0).op().toString(),
                            DevoreBaseOrdinaryFunctionToken.make(asts, parameters, paramTypes, false, env));
                } else {
                    Token value = KeywordToken.KEYWORD_NIL;
                    for (Ast e : node.children())
                        value = Evaluator.eval(e.copy(), newEnv.createChild());
                    String type = ((IdToken) node.op())._type;
                    DevoreAssert.typeAssert(
                            DevoreType.check(value.type(), type) != Integer.MAX_VALUE,
                            value + " not is " + type);
                    newEnv.put(node.op().toString(), value);
                }
            }
            for (int i = 1; i < ast.size(); ++i)
                result = Evaluator.eval(ast.get(i).copy(), newEnv.createChild());
            return result;
        })));
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
            ArithmeticToken end = (ArithmeticToken) args.get(0);
            for (ArithmeticToken i = RealToken.ZERO; i.compareTo(end) < 0; i = i.add(RealToken.ONE))
                list.add(i);
            return new ImmutableListToken(list);
        }, new String[]{"int"}, false));
        _env.put("range", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            List<Token> list = new ArrayList<>();
            ArithmeticToken start = (ArithmeticToken) args.get(0);
            ArithmeticToken end = (ArithmeticToken) args.get(1);
            for (ArithmeticToken i = start; i.compareTo(end) < 0; i = i.add(RealToken.ONE))
                list.add(i);
            return new ImmutableListToken(list);
        }, new String[]{"int", "int"}, false));
        _env.put("range", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            List<Token> list = new ArrayList<>();
            ArithmeticToken start = (ArithmeticToken) args.get(0);
            ArithmeticToken end = (ArithmeticToken) args.get(1);
            ArithmeticToken step = (ArithmeticToken) args.get(2);
            ArithmeticToken size = end.sub(start).div(step);
            for (ArithmeticToken i = RealToken.ZERO; i.compareTo(size) < 0; i = i.add(RealToken.ONE))
                list.add(start.add(i.mul(step)));
            return new ImmutableListToken(list);
        }, new String[]{"int", "int", "int"}, false));
        _env.put("random", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            BigInteger rangeStart = BigInteger.ZERO;
            BigInteger rangeEnd = ((NumberToken) args.get(0)).toBigInteger();
            Random rand = new Random();
            int scale = rangeEnd.toString().length();
            StringBuilder generated = new StringBuilder();
            for (int i = 0; i < rangeEnd.toString().length(); ++i)
                generated.append(rand.nextInt(10));
            BigDecimal inputRangeStart = new BigDecimal("0").setScale(scale, RoundingMode.FLOOR);
            BigDecimal inputRangeEnd = new BigDecimal(String.format("%0" + (rangeEnd.toString().length()) + "d", 0)
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
        }, new String[]{"int"}, false));
        _env.put("random", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            BigInteger rangeStart = ((NumberToken) args.get(0)).toBigInteger();
            BigInteger rangeEnd = ((NumberToken) args.get(1)).toBigInteger();
            Random rand = new Random();
            int scale = rangeEnd.toString().length();
            StringBuilder generated = new StringBuilder();
            for (int i = 0; i < rangeEnd.toString().length(); ++i)
                generated.append(rand.nextInt(10));
            BigDecimal inputRangeStart = new BigDecimal("0").setScale(scale, RoundingMode.FLOOR);
            BigDecimal inputRangeEnd = new BigDecimal(String.format("%0" + (rangeEnd.toString().length()) + "d", 0)
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
        }, new String[]{"int", "int"}, false));
        _env.put("println", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            StringBuilder builder = new StringBuilder();
            args.forEach(builder::append);
            Devore.print.println(builder);
            return KeywordToken.KEYWORD_NIL;
        }), new String[]{"any"}, true));
        _env.put("print", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            StringBuilder builder = new StringBuilder();
            args.forEach(builder::append);
            Devore.print.print(builder);
            return KeywordToken.KEYWORD_NIL;
        }), new String[]{"any"}, true));
        _env.put("+", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            ArithmeticToken arithmetic = (ArithmeticToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                arithmetic = arithmetic.add((ArithmeticToken) args.get(i));
            return arithmetic;
        }), new String[]{"arithmetic", "arithmetic"}, true));
        _env.put("-", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            ArithmeticToken arithmetic = (ArithmeticToken) args.get(0);
            if (arithmetic instanceof NumberToken && args.size() == 1)
                return ((NumberToken) arithmetic).negOne();
            for (int i = 1; i < args.size(); ++i)
                arithmetic = arithmetic.sub((ArithmeticToken) args.get(i));
            return arithmetic;
        }), new String[]{"arithmetic", "arithmetic"}, true));
        _env.put("*", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            ArithmeticToken arithmetic = (ArithmeticToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                arithmetic = arithmetic.mul((ArithmeticToken) args.get(i));
            return arithmetic;
        }), new String[]{"arithmetic", "arithmetic"}, true));
        _env.put("/", BuiltinOrdinaryFunctionToken.make(((args, env) -> {
            ArithmeticToken arithmetic = (ArithmeticToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                arithmetic = arithmetic.div((ArithmeticToken) args.get(i));
            return arithmetic;
        }), new String[]{"arithmetic", "arithmetic"}, true));
        _env.put("undef", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            for (Token token : args)
                env.remove(token.toString());
            return KeywordToken.KEYWORD_NIL;
        }, new String[]{"id", "any"}, true));
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
            Token value = KeywordToken.KEYWORD_NIL;
            if (ast.get(0).isEmpty() && ast.get(0).type() != Ast.AstType.FUNCTION) {
                Env tempEnv = env.createChild();
                for (int i = 1; i < ast.size(); ++i)
                    value = Evaluator.eval(ast.get(i), tempEnv);
            } else {
                List<String> parameters = new ArrayList<>();
                String[] types = new String[ast.get(0).size()];
                List<Ast> asts = new ArrayList<>();
                for (int i = 0; i < ast.get(0).size(); ++i) {
                    parameters.add(ast.get(0).get(i).op().toString());
                    types[i] = ((IdToken) ast.get(0).get(i).op())._type;
                }
                for (int i = 1; i < ast.size(); ++i)
                    asts.add(ast.get(i).copy());
                value = DevoreBaseOrdinaryFunctionToken.make(asts, parameters, types, false, env);
            }
            _env.put(key, value);
            return KeywordToken.KEYWORD_NIL;
        }));
        _env.put("def-variadic", BuiltinSpecialFunctionToken.make((ast, env) -> {
            String key = ast.get(0).op().toString();
            List<String> parameters = new ArrayList<>();
            String[] types = new String[ast.get(0).size()];
            List<Ast> asts = new ArrayList<>();
            for (int i = 0; i < ast.get(0).size(); ++i) {
                parameters.add(ast.get(0).get(i).op().toString());
                types[i] = ((IdToken) ast.get(0).get(i).op())._type;
            }
            for (int i = 1; i < ast.size(); ++i)
                asts.add(ast.get(i).copy());
            _env.put(key, DevoreBaseOrdinaryFunctionToken.make(asts, parameters, types, true, env));
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
                String[] types = new String[ast.get(0).size()];
                List<Ast> asts = new ArrayList<>();
                for (int i = 0; i < ast.get(0).size(); ++i) {
                    parameters.add(ast.get(0).get(i).op().toString());
                    types[i] = ((IdToken) ast.get(0).get(i).op())._type;
                }
                for (int i = 1; i < ast.size(); ++i)
                    asts.add(ast.get(i).copy());
                value = DevoreBaseOrdinaryFunctionToken.make(asts, parameters, types, false, env);
            }
            _env.set(key, value);
            return KeywordToken.KEYWORD_NIL;
        }));
        _env.put("set-variadic!", BuiltinSpecialFunctionToken.make((ast, env) -> {
            String key = ast.get(0).op().toString();
            List<String> parameters = new ArrayList<>();
            String[] types = new String[ast.get(0).size()];
            List<Ast> asts = new ArrayList<>();
            for (int i = 0; i < ast.get(0).size(); ++i) {
                parameters.add(ast.get(0).get(i).op().toString());
                types[i] = ((IdToken) ast.get(0).get(i).op())._type;
            }
            for (int i = 1; i < ast.size(); ++i)
                asts.add(ast.get(i).copy());
            _env.set(key, DevoreBaseOrdinaryFunctionToken.make(asts, parameters, types, true, env));
            return KeywordToken.KEYWORD_NIL;
        }));
        _env.put("while", BuiltinSpecialFunctionToken.make(((ast, env) -> {
            Env newEnv = env.createChild();
            Token result = KeywordToken.KEYWORD_NIL;
            Token condition = Evaluator.eval(ast.get(0).copy(), newEnv);
            while (condition.bool()) {
                for (int i = 1; i < ast.size(); ++i)
                    result = Evaluator.eval(ast.get(i).copy(), newEnv);
                condition = Evaluator.eval(ast.get(0).copy(), newEnv);
            }
            return result;
        })));
        _env.put("type?", BuiltinOrdinaryFunctionToken.make((args, env) ->
                new StringToken(args.get(0).type()), new String[]{"any"}, false));
        _env.put("apply", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            Ast ast = new Ast(args.get(0));
            ast.setType(Ast.AstType.FUNCTION);
            for (int i = 1; i < args.size(); ++i)
                ast.add(new Ast(args.get(i)));
            return Evaluator.eval(ast, env);
        }, new String[]{"function", "any"}, true));
        _env.put("apply-list", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ListToken list = (ListToken) args.get(1);
            Ast ast = new Ast(args.get(0));
            ast.setType(Ast.AstType.FUNCTION);
            for (int i = 0; i < list.size(); ++i)
                ast.add(new Ast(list.get(i)));
            return Evaluator.eval(ast, env);
        }, new String[]{"function", "list"}, false));
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
        }, new String[]{"string"}, false));
        _env.put("lambda", BuiltinSpecialFunctionToken.make((ast, env) -> {
            List<String> parameters = new ArrayList<>();
            String[] types = new String[ast.get(0).size() + 1];
            List<Ast> asts = new ArrayList<>();
            Token parameter = ast.get(0).op();
            if (!KeywordToken.KEYWORD_EMPTY.equiv(parameter)) {
                types[parameters.size()] = ((IdToken) parameter)._type;
                parameters.add(parameter.toString());
            }
            for (int i = 0; i < ast.get(0).size(); ++i) {
                parameter = ast.get(0).get(i).op();
                if (!KeywordToken.KEYWORD_EMPTY.equiv(parameter)) {
                    types[parameters.size()] = ((IdToken) parameter)._type;
                    parameters.add(parameter.toString());
                }
            }
            for (int i = 1; i < ast.size(); ++i)
                asts.add(ast.get(i).copy());
            return DevoreBaseOrdinaryFunctionToken.make(asts, parameters, types, false, env);
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
        _env.put("not", BuiltinOrdinaryFunctionToken.make((args, env) ->
                args.get(0).bool() ? BoolToken.FALSE : BoolToken.TRUE, new String[]{"bool"}, false));
        _env.put("and", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            for (Token arg : args)
                if (!arg.bool())
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }, new String[]{"bool"}, true));
        _env.put("or", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            for (Token arg : args)
                if (arg.bool())
                    return BoolToken.TRUE;
            return BoolToken.FALSE;
        }, new String[]{"bool"}, true));
        _env.put(">", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) < 0
                        || temp.compareTo((ComparableToken) args.get(i)) == 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }, new String[]{"comparable"}, true));
        _env.put("<", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) > 0
                        || temp.compareTo((ComparableToken) args.get(i)) == 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }, new String[]{"comparable"}, true));
        _env.put(">=", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) < 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }, new String[]{"comparable"}, true));
        _env.put("<=", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) > 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }, new String[]{"comparable"}, true));
        _env.put("=", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) != 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }, new String[]{"comparable"}, true));
        _env.put("/=", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ComparableToken temp = (ComparableToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                if (temp.compareTo((ComparableToken) args.get(i)) == 0)
                    return BoolToken.FALSE;
            return BoolToken.TRUE;
        }, new String[]{"comparable"}, true));
        _env.put("begin", BuiltinSpecialFunctionToken.make((ast, env) -> {
            Env tempEnv = env.createChild();
            Token result = Evaluator.eval(ast.get(0), tempEnv);
            for (int i = 1; i < ast.size(); ++i)
                result = Evaluator.eval(ast.get(i), tempEnv);
            return result;
        }));
        _env.put("++", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            boolean flag = false;
            for (Token arg : args)
                if (DevoreType.check(arg.type(), "list") != Integer.MAX_VALUE) {
                    flag = true;
                    break;
                }
            if (flag) {
                VariableListToken list = new VariableListToken();
                for (Token arg : args) {
                    if (DevoreType.check(arg.type(), "list") != Integer.MAX_VALUE)
                        list.add((ListToken) arg);
                    else
                        list.add(arg);
                }
                return list.toImmutable();
            } else {
                StringBuilder builder = new StringBuilder();
                for (Token arg : args)
                    builder.append(arg.toString());
                return new StringToken(builder.toString());
            }
        }, new String[]{"any"}, true));
        _env.put("map", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            VariableListToken result = new VariableListToken();
            ListToken tokens = (ListToken) args.get(1);
            for (int i = 0; i < tokens.size(); ++i) {
                List<Token> parameters = new ArrayList<>();
                parameters.add(tokens.get(i));
                for (int j = 2; j < args.size(); ++j)
                    parameters.add(((ListToken) args.get(j)).get(i));
                Ast ast = new Ast(args.get(0));
                for (Token parameter : parameters)
                    ast.add(new Ast(parameter));
                result.add(Evaluator.eval(ast, env.createChild()));
            }
            return result.toImmutable();
        }, new String[]{"function", "list", "list"}, true));
        _env.put("for-each", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ListToken tokens = (ListToken) args.get(1);
            for (int i = 0; i < tokens.size(); ++i) {
                List<Token> parameters = new ArrayList<>();
                parameters.add(tokens.get(i));
                for (int j = 2; j < args.size(); ++j)
                    parameters.add(((ListToken) args.get(j)).get(i));
                Ast ast = new Ast(args.get(0));
                for (Token parameter : parameters)
                    ast.add(new Ast(parameter));
                Evaluator.eval(ast, env.createChild());
            }
            return KeywordToken.KEYWORD_NIL;
        }, new String[]{"function", "list", "list"}, true));
        _env.put("foldr", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            Token result = args.get(1);
            ListToken tokens = (ListToken) args.get(2);
            for (int i = tokens.size() - 1; i >= 0; --i) {
                Ast ast = new Ast(args.get(0));
                ast.add(new Ast(tokens.get(i)));
                ast.add(new Ast(result));
                result = Evaluator.eval(ast, env.createChild());
            }
            return result;
        }, new String[]{"function", "any", "list"}, false));
        _env.put("foldl", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            Token result = args.get(1);
            ListToken list = (ListToken) args.get(2);
            for (int i = list.size() - 1; i >= 0; --i) {
                Ast ast = new Ast(args.get(0));
                ast.add(new Ast(result));
                ast.add(new Ast(list.get(i)));
                result = Evaluator.eval(ast, env.createChild());
            }
            return result;
        }, new String[]{"function", "any", "list"}, false));
        _env.put("filter", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            VariableListToken result = new VariableListToken();
            ListToken tokens = (ListToken) args.get(1);
            for (int i = 0; i < tokens.size(); ++i) {
                Ast ast = new Ast(args.get(0));
                ast.add(new Ast(tokens.get(i)));
                if (Evaluator.eval(ast, env.createChild()).bool())
                    result.add(tokens.get(i));
            }
            return result.toImmutable();
        }, new String[]{"function", "list"}, false));
        _env.put("sin", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).sin(), new String[]{"num"}, false));
        _env.put("cos", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).cos(), new String[]{"num"}, false));
        _env.put("tan", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).tan(), new String[]{"num"}, false));
        _env.put("asin", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).asin(), new String[]{"num"}, false));
        _env.put("acos", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).acos(), new String[]{"num"}, false));
        _env.put("atan", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).atan(), new String[]{"num"}, false));
        _env.put("log", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).log(), new String[]{"num"}, false));
        _env.put("ln", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).ln(), new String[]{"num"}, false));
        _env.put("sqrt", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).sqrt(), new String[]{"num"}, false));
        _env.put("ceil", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).ceil(), new String[]{"num"}, false));
        _env.put("floor", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).floor(), new String[]{"num"}, false));
        _env.put("prime?", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).isPrime(), new String[]{"num"}, false));
        _env.put("negate", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).negate(), new String[]{"num"}, false));
        _env.put("inc", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).inc(), new String[]{"num"}, false));
        _env.put("dec", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).dec(), new String[]{"num"}, false));
        _env.put("signnum", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).signnum(), new String[]{"num"}, false));
        _env.put("abs", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).abs(), new String[]{"num"}, false));
        _env.put("exp", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).exp(), new String[]{"num"}, false));
        _env.put("factorial", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((NumberToken) args.get(0)).factorial(), new String[]{"num"}, false));
        _env.put("list", BuiltinOrdinaryFunctionToken.make(((args, env) ->
                new ImmutableListToken(args)), new String[]{"any"}, true));
        _env.put("variable-list!", BuiltinOrdinaryFunctionToken.make(((args, env) ->
                new VariableListToken(args)), new String[]{"any"}, true));
        _env.put("list-ref", BuiltinOrdinaryFunctionToken.make(((args, env) ->
                ((ListToken) args.get(0)).get(((NumberToken) args.get(1)).toInt())), new String[]{"list", "int"}, false));
        _env.put("list-add", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ListToken list = (ListToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                list = list.add(args.get(i));
            return list;
        }, new String[]{"list", "any"}, true));
        _env.put("list-set", BuiltinOrdinaryFunctionToken.make(((args, env) ->
                ((ListToken) args.get(0)).set(((NumberToken) args.get(1)).toInt(), args.get(2))), new String[]{"list", "int", "any"}, false));
        _env.put("list-contains", BuiltinOrdinaryFunctionToken.make(((args, env) ->
                ((ListToken) args.get(0)).contains(args.get(1))), new String[]{"list", "any"}, false));
        _env.put("list-remove", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ListToken list = (ListToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                list = list.remove(args.get(i));
            return list;
        }, new String[]{"list", "any"}, true));
        _env.put("list-remove-index", BuiltinOrdinaryFunctionToken.make((args, env) -> {
            ListToken list = (ListToken) args.get(0);
            for (int i = 1; i < args.size(); ++i)
                list = list.remove(((NumberToken) args.get(i)).toInt());
            return list;
        }, new String[]{"list", "any"}, true));
        _env.put("sort", BuiltinOrdinaryFunctionToken.make((args, env) -> ((ListToken) args.get(0)).sort(), new String[]{"list"}, false));
        _env.put("reverse", BuiltinOrdinaryFunctionToken.make((args, env) -> ((ListToken) args.get(0)).reverse(), new String[]{"list"}, false));
        _env.put("head", BuiltinOrdinaryFunctionToken.make((args, env) -> ((ListToken) args.get(0)).head(), new String[]{"list"}, false));
        _env.put("init", BuiltinOrdinaryFunctionToken.make((args, env) -> ((ListToken) args.get(0)).init(), new String[]{"list"}, false));
        _env.put("last", BuiltinOrdinaryFunctionToken.make((args, env) -> ((ListToken) args.get(0)).last(), new String[]{"list"}, false));
        _env.put("tail", BuiltinOrdinaryFunctionToken.make((args, env) -> ((ListToken) args.get(0)).tail(), new String[]{"list"}, false));
        _env.put("table", BuiltinOrdinaryFunctionToken.make((args, env) -> new ImmutableTableToken(), new String[]{}, false));
        _env.put("variable-table!", BuiltinOrdinaryFunctionToken.make((args, env) -> new VariableTableToken(), new String[]{}, false));
        _env.put("table-put", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((TableToken) args.get(0)).put(args.get(1), args.get(2)), new String[]{"table", "any", "any"}, false));
        _env.put("table-get", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((TableToken) args.get(0)).get(args.get(1)), new String[]{"table", "any"}, false));
        _env.put("table-remove", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((TableToken) args.get(0)).remove(args.get(1)), new String[]{"table", "any"}, false));
        _env.put("table-keys", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((TableToken) args.get(0)).keys(), new String[]{"table"}, false));
        _env.put("table-values", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((TableToken) args.get(0)).values(), new String[]{"table"}, false));
        _env.put("table-contains-key", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((TableToken) args.get(0)).containsKey(args.get(1)), new String[]{"table", "any"}, false));
        _env.put("table-contains-value", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((TableToken) args.get(0)).containsValue(args.get(1)), new String[]{"table", "any"}, false));
        _env.put("string-split", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((StringToken) args.get(0)).split((StringToken) args.get(1)), new String[]{"string", "string"}, false));
        _env.put("string-index", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((StringToken) args.get(0)).indexOf((StringToken) args.get(1)), new String[]{"string", "string"}, false));
        _env.put("string-last-index", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((StringToken) args.get(0)).lastIndexOf((StringToken) args.get(1)), new String[]{"string", "string"}, false));
        _env.put("string-replace", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((StringToken) args.get(0)).replace((StringToken) args.get(1), (StringToken) args.get(2)), new String[]{"string", "string", "string"}, false));
        _env.put("string-substring", BuiltinOrdinaryFunctionToken.make((args, env) ->
                ((StringToken) args.get(0)).substring((NumberToken) args.get(1), (NumberToken) args.get(2)), new String[]{"string", "int", "int"}, false));
        _env.put("->string", BuiltinOrdinaryFunctionToken.make((args, env) ->
                new StringToken(args.get(0).toString()), new String[]{"any"}, false));
        _env.put("->bool", BuiltinOrdinaryFunctionToken.make((args, env) ->
                BoolToken.valueOf(args.get(0).bool()), new String[]{"any"}, false));
        _env.put("->real", BuiltinOrdinaryFunctionToken.make((args, env) ->
                RealToken.valueOf(args.get(0).toString()), new String[]{"any"}, false));
        _env.put("->int", BuiltinOrdinaryFunctionToken.make((args, env) ->
                RealToken.valueOf(RealToken.valueOf(args.get(0).toString()).toInt()), new String[]{"any"}, false));
    }
}
