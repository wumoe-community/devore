package cn.devore.lang.token;

import cn.devore.lang.Token;

public class KeywordToken extends Token {
    public static final int LB = 101;
    public static final int RB = 102;
    public static final int EMPTY = 110;
    public static final int NIL = 111;

    public static final KeywordToken KEYWORD_LB = new KeywordToken(LB);
    public static final KeywordToken KEYWORD_RB = new KeywordToken(RB);
    public static final KeywordToken KEYWORD_EMPTY = new KeywordToken(EMPTY);
    public static final KeywordToken KEYWORD_NIL = new KeywordToken(NIL);

    public final int type;

    public KeywordToken(int type) {
        this.type = type;
    }

    @Override
    public String type() {
        return this.type == NIL ? "nil" : "undefined";
    }

    @Override
    public Token deepcopy() {
        return new KeywordToken(type);
    }

    @Override
    public boolean bool() {
        return false;
    }

    @Override
    public boolean equiv(Token o) {
        return o instanceof KeywordToken && ((KeywordToken) o).type == type;
    }

    @Override
    public String toString() {
        return switch (type) {
            case EMPTY -> "()";
            case NIL -> "nil";
            default -> "";
        };
    }
}
