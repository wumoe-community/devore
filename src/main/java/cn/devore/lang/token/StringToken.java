package cn.devore.lang.token;

import cn.devore.lang.Token;
import cn.devore.lang.token.list.ImmutableListToken;
import cn.devore.lang.token.list.ListToken;
import cn.devore.lang.token.math.NumberToken;
import cn.devore.lang.token.math.RealToken;

import java.util.Objects;

public class StringToken extends Token {
    private final String _str;

    public StringToken(String str) {
        this._str = str;
    }

    public String value() {
        return _str;
    }

    @Override
    public String type() {
        return "string";
    }

    @Override
    public Token deepcopy() {
        return new StringToken(value());
    }

    @Override
    public boolean bool() {
        return value().length() > 0;
    }

    public StringToken substring(NumberToken start, NumberToken end) {
        return new StringToken(_str.substring(start.toInt(), end.toInt()));
    }

    public ListToken split(StringToken regex) {
        String[] strings = _str.split(regex._str);
        ListToken result = new ImmutableListToken();
        for (String s : strings)
            result.add(new StringToken(s));
        return result;
    }

    public NumberToken indexOf(StringToken s) {
        return RealToken.valueOf(_str.indexOf(s._str));
    }

    public NumberToken lastIndexOf(StringToken s) {
        return RealToken.valueOf(_str.lastIndexOf(s._str));
    }

    public StringToken replace(StringToken regex, StringToken replacement) {
        return new StringToken(_str.replaceAll(regex._str, replacement._str));
    }

    @Override
    public boolean equiv(Token o) {
        return o instanceof StringToken && ((StringToken) o).value().equals(value());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_str);
    }

    @Override
    public String toString() {
        return _str;
    }
}
