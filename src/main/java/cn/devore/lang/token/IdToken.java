package cn.devore.lang.token;

import cn.devore.lang.Token;

import java.util.Objects;

public class IdToken extends Token {
    public final String _type;
    private final String _id;

    public IdToken(String id, String type) {
        this._id = id;
        this._type = type;
    }

    public IdToken(String id) {
        this(id, "any");
    }

    public String value() {
        return _id;
    }

    @Override
    public String type() {
        return "id";
    }

    @Override
    public Token deepcopy() {
        return new IdToken(value(), _type);
    }

    @Override
    public boolean bool() {
        return false;
    }

    @Override
    public boolean equiv(Token o) {
        return o instanceof IdToken && ((IdToken) o).value().equals(value());
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }

    @Override
    public String toString() {
        return _id;
    }
}
