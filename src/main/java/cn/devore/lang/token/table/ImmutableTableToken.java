package cn.devore.lang.token.table;

import cn.devore.lang.Token;
import cn.devore.lang.token.BoolToken;
import cn.devore.lang.token.list.ImmutableListToken;
import cn.devore.lang.token.list.ListToken;

import java.util.HashMap;
import java.util.Map;

public class ImmutableTableToken extends TableToken {
    private final Map<Token, Token> _table;

    public ImmutableTableToken() {
        this._table = new HashMap<>();
    }

    public ImmutableTableToken copy() {
        ImmutableTableToken dict = new ImmutableTableToken();
        ListToken keys = keys();
        for (int i = 0; i < keys.size(); ++i) {
            Token key = keys.get(i);
            dict = (ImmutableTableToken) dict.put(key, get(key));
        }
        return dict;
    }

    @Override
    public String type() {
        return "immutable_table";
    }

    @Override
    public Token deepcopy() {
        ImmutableTableToken dict = new ImmutableTableToken();
        ListToken keys = keys();
        for (int i = 0; i < keys.size(); ++i) {
            Token key = keys.get(i);
            dict = (ImmutableTableToken) dict.put(key.deepcopy(), get(key).deepcopy());
        }
        return dict;
    }

    @Override
    public boolean bool() {
        return size() > 0;
    }

    @Override
    public boolean equiv(Token o) {
        if (!(o instanceof TableToken token))
            return false;
        if (token.size() != size())
            return false;
        ListToken keys = keys();
        for (int i = 0; i < keys.size(); ++i) {
            Token key = keys.get(i);
            if (!token.containsKey(key).bool())
                return false;
            if (token.get(key) != get(key))
                return false;
        }
        return true;
    }

    @Override
    public TableToken put(Token key, Token value) {
        ImmutableTableToken newDict = copy();
        newDict._table.put(key, value);
        return newDict;
    }

    @Override
    public Token get(Token key) {
        return _table.get(key);
    }

    @Override
    public TableToken remove(Token key) {
        ImmutableTableToken newDict = copy();
        newDict._table.remove(key);
        return newDict;
    }

    @Override
    public int size() {
        return _table.size();
    }

    @Override
    public ListToken keys() {
        ImmutableListToken keys = new ImmutableListToken();
        for (Token key : _table.keySet())
            keys.add(key);
        return keys;
    }

    @Override
    public ListToken values() {
        return new ImmutableListToken(_table.keySet().stream().toList());
    }

    @Override
    public BoolToken containsKey(Token key) {
        return BoolToken.valueOf(_table.containsKey(key));
    }

    @Override
    public BoolToken containsValue(Token value) {
        return BoolToken.valueOf(_table.containsValue(value));
    }
}
