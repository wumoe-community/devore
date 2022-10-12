package cn.devore.lang;

import cn.devore.lang.token.KeywordToken;

import java.util.ArrayList;
import java.util.List;

public class Ast {
    private Token _op;
    private AstType _type;
    private List<Ast> _children;

    public Ast() {
        this(KeywordToken.KEYWORD_EMPTY);
    }

    public Ast(Token op) {
        this(op, new ArrayList<>());
    }

    public Ast(Token op, List<Ast> children) {
        this._op = op;
        this._children = children;
        this._type = AstType.BASIC;
    }

    public void setType(AstType type) {
        this._type = type;
    }

    public void setOp(Token op) {
        this._op = op;
    }

    public void setChildren(List<Ast> children) {
        this._children = children;
    }

    public Token op() {
        return _op;
    }

    public AstType type() {
        return _type;
    }

    public List<Ast> children() {
        return _children;
    }

    public void add(Ast child) {
        _children.add(child);
    }

    public Ast copy() {
        Ast ast = new Ast(_op);
        for (Ast child : _children)
            ast.add(child.copy());
        return ast;
    }

    public boolean isEmpty() {
        return _children.isEmpty();
    }

    public int size() {
        return _children.size();
    }

    public void clear() {
        _children.clear();
    }

    public Ast get(int index) {
        return _children.get(index);
    }

    public enum AstType {
        FUNCTION, BASIC
    }
}
