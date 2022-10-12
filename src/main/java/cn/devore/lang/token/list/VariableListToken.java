package cn.devore.lang.token.list;

import cn.devore.lang.Token;
import cn.devore.lang.token.BoolToken;
import cn.devore.lang.token.ComparableToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariableListToken extends ListToken {
    private final List<Token> _list;

    public VariableListToken() {
        this._list = new ArrayList<>();
    }

    public VariableListToken(List<Token> list) {
        this._list = new ArrayList<>();
        _list.addAll(list);
    }

    @Override
    public Token deepcopy() {
        VariableListToken list = new VariableListToken();
        for (int i = 0; i < size(); ++i)
            list = (VariableListToken) list.add(get(i).deepcopy());
        return list;
    }

    @Override
    public boolean bool() {
        return size() > 0;
    }

    @Override
    public boolean equiv(Token o) {
        if (!(o instanceof ListToken list))
            return false;
        if (list.size() != size())
            return false;
        boolean eq = true;
        for (int i = 0; i < size(); ++i)
            if (!list.get(i).equals(get(i)))
                eq = false;
        return eq;
    }

    @Override
    public Token get(int index) {
        return _list.get(index);
    }

    @Override
    public ListToken add(Token other) {
        _list.add(other);
        return this;
    }

    @Override
    public ListToken add(ListToken list) {
        for (int i = 0; i < size(); ++i)
            _list.add(list.get(i));
        return this;
    }

    @Override
    public ListToken add(int index, Token other) {
        _list.add(index, other);
        return this;
    }

    @Override
    public ListToken add(int index, ListToken list) {
        for (int i = index; i < list.size() + index; ++i)
            add(i, list.get(i - index));
        return this;
    }

    @Override
    public ListToken set(int index, Token other) {
        _list.set(index, other);
        return this;
    }

    @Override
    public ListToken set(int index, ListToken list) {
        for (int i = index; i < list.size() + index; ++i)
            set(i, list.get(i - index));
        return this;
    }

    @Override
    public ListToken remove(Token other) {
        _list.remove(other);
        return this;
    }

    @Override
    public ListToken remove(int index) {
        _list.remove(index);
        return this;
    }

    @Override
    public int size() {
        return _list.size();
    }

    @Override
    public Token head() {
        return get(0);
    }

    @Override
    public ListToken init() {
        VariableListToken newList = new VariableListToken();
        for (int i = 0; i < size() - 1; ++i)
            newList.add(get(i));
        return newList;
    }

    @Override
    public Token last() {
        return get(size() - 1);
    }

    @Override
    public ListToken tail() {
        VariableListToken newList = new VariableListToken();
        for (int i = 1; i < size(); ++i)
            newList.add(get(i));
        return newList;
    }

    @Override
    public ListToken reverse() {
        Collections.reverse(_list);
        return this;
    }

    @Override
    public ListToken sort() {
        List<ComparableToken> comparableList = new ArrayList<>();
        for (int i = 0; i < size(); ++i) {
            Token item = get(i);
            comparableList.add((ComparableToken) item);
        }
        Collections.sort(comparableList);
        _list.clear();
        comparableList.forEach(this::add);
        return this;
    }

    @Override
    public BoolToken contains(Token other) {
        return BoolToken.valueOf(_list.contains(other));
    }

    @Override
    public String toString() {
        return _list.toString();
    }
}
