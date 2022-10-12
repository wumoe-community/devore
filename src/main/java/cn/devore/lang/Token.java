package cn.devore.lang;

public abstract class Token {
    public abstract Token deepcopy();

    public abstract boolean bool();

    public abstract boolean equiv(Token o);

    @Override
    public boolean equals(Object o) {
        return o instanceof Token && this.equiv((Token) o);
    }
}
