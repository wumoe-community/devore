package cn.devore.lang.token.list;

import cn.devore.lang.Token;
import cn.devore.lang.token.BoolToken;

public abstract class ListToken extends Token {
    public abstract Token get(int index);

    public abstract ListToken add(Token other);

    public abstract ListToken add(ListToken list);

    public abstract ListToken add(int index, Token other);

    public abstract ListToken add(int index, ListToken list);

    public abstract ListToken set(int index, Token other);

    public abstract ListToken set(int index, ListToken list);

    public abstract ListToken remove(Token other);

    public abstract ListToken remove(int index);

    public abstract int size();

    public abstract Token head();

    public abstract ListToken init();

    public abstract Token last();

    public abstract ListToken tail();

    public abstract ListToken reverse();

    public abstract ListToken sort();

    public abstract BoolToken contains(Token other);
}
