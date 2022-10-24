package cn.devore.lang.token;

import cn.devore.lang.Token;

import java.util.ArrayList;
import java.util.List;

public class StructureToken extends Token {
    private final String _type;
    private final List<Token> _values;

    public StructureToken(String type, List<Token> values) {
        this._type = type;
        this._values = values;
    }

    @Override
    public String type() {
        return _type;
    }

    public Token get(int index) {
        return _values.get(index);
    }

    public void set(int index, Token value) {
        _values.set(index, value);
    }

    public int size() {
        return _values.size();
    }

    @Override
    public Token deepcopy() {
        List<Token> tokens = new ArrayList<>();
        for (Token token : _values)
            tokens.add(token.deepcopy());
        return new StructureToken(_type, tokens);
    }

    @Override
    public boolean bool() {
        return _values.size() > 0;
    }

    @Override
    public boolean equiv(Token o) {
        if (!(o instanceof StructureToken structure))
            return false;
        if (!(structure._type.equals(_type)))
            return false;
        if (structure.size() != size())
            return false;
        for (int i = 0; i < size(); ++i)
            if (!structure.get(i).equiv(get(i)))
                return false;
        return true;
    }
}
