package cn.devore.parse;

import cn.devore.lang.Token;
import cn.devore.lang.token.IdToken;
import cn.devore.lang.token.KeywordToken;
import cn.devore.lang.token.StringToken;
import cn.devore.lang.token.math.RealToken;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    public static String preprocessor(String code) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        char[] chars = code.replaceAll("(\n|\r\n)$\\s*", "").toCharArray();
        var i = 0;
        while (i < chars.length) {
            char c = chars[i];
            if (index >= 1 && c == '\"') {
                builder.append('\"');
                int j = i;
                boolean skip = false;
                while (true) {
                    char peek = chars[++j];
                    if ((chars[j + 1] == '\\' || chars[j + 1] == '\"') && peek == '\\') {
                        skip = !skip;
                    } else if (peek == '\"') {
                        if (!skip)
                            break;
                        skip = false;
                    }
                    builder.append(peek);
                    ++i;
                }
                builder.append("\"");
                ++i;
            } else if (c == ';' || c == '\r') {
                do {
                    if (++i == chars.length)
                        return builder.toString();
                } while (chars[i] != '\n');
            } else if (c == '(') {
                ++index;
                builder.append(c);
            } else if (c == ')') {
                --index;
                builder.append(c);
            } else if (index >= 1 && (c == ' ' || c == '\n' || c == '\t')) {
                var j = i + 1;
                while (chars[j] == ' ' || chars[j] == '\n' || chars[j] == '\t') {
                    ++i;
                    ++j;
                }
                builder.append(' ');
            } else
                builder.append(c);
            ++i;
        }
        return builder.toString();
    }

    public static List<String> splitCode(String code) {
        List<String> expressions = new ArrayList<>();
        int index = 0;
        while (index < code.length()) {
            int flag = 0;
            StringBuilder builder = new StringBuilder();
            while (code.charAt(index) != '(')
                ++index;
            do {
                if (code.charAt(index) == '\"') {
                    builder.append("\"");
                    StringBuilder value = new StringBuilder();
                    boolean skip = false;
                    while (true) {
                        ++index;
                        if (index < code.length() - 1 && code.charAt(index) == '\\') {
                            if (skip) {
                                skip = false;
                                value.append("\\\\");
                            } else
                                skip = true;
                            continue;
                        } else if (index >= code.length() - 1 || code.charAt(index) == '\"') {
                            if (skip) {
                                skip = false;
                                value.append("\\\"");
                                continue;
                            } else
                                break;
                        } else if (skip) {
                            value.append("\\").append(code.charAt(index));
                            skip = false;
                            continue;
                        }
                        value.append(code.charAt(index));
                    }
                    builder.append(value.append("\""));
                    ++index;
                    continue;
                }
                if (code.charAt(index) == '(')
                    ++flag;
                else if (code.charAt(index) == ')')
                    --flag;
                builder.append(code.charAt(index++));
            } while (flag > 0);
            expressions.add(builder.toString());
        }
        return expressions;
    }

    public static List<Token> lexer(String expression) {
        List<Token> tokens = new ArrayList<>();
        int index = -1;
        while (++index < expression.length()) {
            switch (expression.charAt(index)) {
                case '(' -> {
                    tokens.add(KeywordToken.KEYWORD_LB);
                    continue;
                }
                case ')' -> {
                    tokens.add(KeywordToken.KEYWORD_RB);
                    continue;
                }
                case ' ' -> {
                    continue;
                }
            }
            boolean negative = false;
            if (expression.charAt(index) == '-' && index < expression.length() - 1 && Character.isDigit(expression.charAt(index + 1))) {
                negative = true;
                ++index;
            }
            if (Character.isDigit(expression.charAt(index))) {
                RealToken v = RealToken.ZERO;
                while (true) {
                    if (index >= expression.length() - 1 || !Character.isDigit(expression.charAt(index))) {
                        --index;
                        break;
                    }
                    v = v.mul(RealToken.valueOf(10)).add(RealToken.valueOf(expression.charAt(index) - '0'));
                    ++index;
                }
                if (expression.charAt(index + 1) != '.') {
                    tokens.add(negative ? v.sub(v.mul(RealToken.valueOf(2))) : v);
                    continue;
                }
                RealToken x = RealToken.valueOf(v.toString());
                RealToken d = RealToken.valueOf(10);
                ++index;
                while (true) {
                    ++index;
                    if (index >= expression.length() - 1 || !Character.isDigit(expression.charAt(index))) {
                        --index;
                        break;
                    }
                    x = x.add(RealToken.valueOf(expression.charAt(index) - '0').div(d));
                    d = d.mul(RealToken.valueOf(10));
                }
                tokens.add(negative ? x.sub(x.mul(RealToken.valueOf(2))) : x);
                continue;
            }
            if (expression.charAt(index) == '\"') {
                StringBuilder builder = new StringBuilder();
                boolean skip = false;
                while (true) {
                    ++index;
                    if (index < expression.length() - 1 && expression.charAt(index) == '\\') {
                        if (skip) {
                            skip = false;
                            builder.append("\\");
                        } else
                            skip = true;
                        continue;
                    } else if (index >= expression.length() - 1 || expression.charAt(index) == '\"') {
                        if (skip) {
                            skip = false;
                            builder.append("\"");
                            continue;
                        } else
                            break;
                    }
                    if (skip) {
                        skip = false;
                        builder.append(
                                switch (expression.charAt(index)) {
                                    case 'n' -> "\n";
                                    case 'r' -> "\r";
                                    case 't' -> "\t";
                                    case 'b' -> "\b";
                                    default -> expression.charAt(index);
                                }
                        );
                    } else
                        builder.append(expression.charAt(index));
                }
                tokens.add(new StringToken(builder.toString()));
                continue;
            }
            if (expression.charAt(index) != ' ' && expression.charAt(index) != '(' && expression.charAt(index) != ')') {
                StringBuilder builder = new StringBuilder();
                while (true) {
                    if (index >= expression.length() - 1 || expression.charAt(index) == ' ' || expression.charAt(index) == ')') {
                        --index;
                        break;
                    }
                    builder.append(expression.charAt(index));
                    ++index;
                }
                tokens.add(new IdToken(builder.toString()));
            }
        }
        return tokens;
    }
}
