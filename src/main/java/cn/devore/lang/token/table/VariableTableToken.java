package cn.devore.lang.token.table;

import cn.devore.lang.Token;
import cn.devore.lang.token.BoolToken;
import cn.devore.lang.token.list.ImmutableListToken;
import cn.devore.lang.token.list.ListToken;

import java.util.HashMap;
import java.util.Map;

public class VariableTableToken extends TableToken {
    private final Map<Token, Token> _table;

    public VariableTableToken() {
        this._table = new HashMap<>();
    }

    @Override
    public String type() {
        return "variable_table";
    }

    @Override
    public Token deepcopy() {
        ImmutableTableToken dict = new ImmutableTableToken();
        ListToken keys = keys();
        for (int i = 0; i < keys.size(); ++i) {
            Token key = keys.get(i);
            dict.put(key.deepcopy(), get(key).deepcopy());
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
        _table.put(key, value);
        return this;
    }

    @Override
    public Token get(Token key) {
        return _table.get(key);
    }

    @Override
    public TableToken remove(Token key) {
        _table.remove(key);
        return this;
    }

    @Override
    public int size() {
        return _table.size();
    }

    @Override
    public ListToken keys() {
        return new ImmutableListToken(_table.keySet().stream().toList());
    }

    @Override
    public ListToken values() {
        ImmutableListToken values = new ImmutableListToken();
        for (Token key : _table.values())
            values.add(key);
        return values;
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
