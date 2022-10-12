package cn.devore.lang.token.list;

import cn.devore.lang.Token;
import cn.devore.lang.token.BoolToken;
import cn.devore.lang.token.ComparableToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImmutableListToken extends ListToken {
    private final List<Token> _list;

    public ImmutableListToken() {
        this._list = new ArrayList<>();
    }

    public ImmutableListToken(List<Token> list) {
        this._list = new ArrayList<>();
        _list.addAll(list);
    }

    public ImmutableListToken copy() {
        ImmutableListToken list = new ImmutableListToken();
        for (int i = 0; i < size(); ++i)
            list = (ImmutableListToken) list.add(get(i));
        return list;
    }

    @Override
    public Token deepcopy() {
        ImmutableListToken list = new ImmutableListToken();
        for (int i = 0; i < size(); ++i)
            list = (ImmutableListToken) list.add(get(i).deepcopy());
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
        ImmutableListToken list = copy();
        list._list.add(other);
        return list;
    }

    @Override
    public ListToken add(ListToken list) {
        ImmutableListToken newList = copy();
        for (int i = 0; i < newList.size(); ++i)
            newList._list.add(list.get(i));
        return newList;
    }

    @Override
    public ListToken add(int index, Token other) {
        ImmutableListToken newList = copy();
        newList._list.add(index, other);
        return newList;
    }

    @Override
    public ListToken add(int index, ListToken list) {
        ListToken newList = copy();
        for (int i = index; i < list.size() + index; ++i)
            newList = newList.add(i, list.get(i - index));
        return newList;
    }

    @Override
    public ListToken set(int index, Token other) {
        ImmutableListToken newList = copy();
        newList._list.set(index, other);
        return newList;
    }

    @Override
    public ListToken set(int index, ListToken list) {
        ListToken newList = copy();
        for (int i = index; i < list.size() + index; ++i)
            newList = newList.set(i, list.get(i - index));
        return newList;
    }

    @Override
    public ListToken remove(Token other) {
        ImmutableListToken newList = copy();
        newList._list.remove(other);
        return newList;
    }

    @Override
    public ListToken remove(int index) {
        ImmutableListToken newList = copy();
        newList._list.remove(index);
        return newList;
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
        ImmutableListToken newList = new ImmutableListToken();
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
        ImmutableListToken newList = new ImmutableListToken();
        for (int i = 1; i < size(); ++i)
            newList.add(get(i));
        return newList;
    }

    @Override
    public ListToken reverse() {
        ImmutableListToken newList = copy();
        Collections.reverse(newList._list);
        return newList;
    }

    @Override
    public ListToken sort() {
        List<ComparableToken> comparableList = new ArrayList<>();
        for (int i = 0; i < size(); ++i) {
            Token item = get(i);
            comparableList.add((ComparableToken) item);
        }
        Collections.sort(comparableList);
        ImmutableListToken newList = new ImmutableListToken();
        comparableList.forEach(newList::add);
        return newList;
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
