package cn.devore.lang.token;

import cn.devore.lang.Token;

import java.util.Objects;

public class IdToken extends Token {
    private final String _id;

    public IdToken(String id) {
        this._id = id;
    }

    public String value() {
        return _id;
    }

    @Override
    public Token deepcopy() {
        return new IdToken(value());
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
