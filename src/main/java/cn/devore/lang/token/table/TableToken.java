package cn.devore.lang.token.table;

import cn.devore.lang.Token;
import cn.devore.lang.token.BoolToken;
import cn.devore.lang.token.list.ListToken;

public abstract class TableToken extends Token {
    public abstract TableToken put(Token key, Token value);

    public abstract Token get(Token key);

    public abstract TableToken remove(Token key);

    public abstract int size();

    public abstract ListToken keys();

    public abstract ListToken values();

    public abstract BoolToken containsKey(Token key);

    public abstract BoolToken containsValue(Token value);
}
