package cn.devore.parse;

import cn.devore.Devore;
import cn.devore.error.DevoreAssert;
import cn.devore.lang.Ast;
import cn.devore.lang.Token;
import cn.devore.lang.token.IdToken;
import cn.devore.lang.token.KeywordToken;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Parser {
    public static Ast parser(List<Token> tokens) {
        Ast ast = null;
        Deque<Ast> stack = new ArrayDeque<>();
        int state = -1;
        Ast temp;
        int index = 0;
        while (index < tokens.size()) {
            if (state == 1) {
                if (tokens.get(index) == KeywordToken.KEYWORD_RB) {
                    temp = Devore.AST_EMPTY;
                    stack.push(temp);
                    state = -1;
                    ++index;
                    continue;
                }
                if (tokens.get(index) == KeywordToken.KEYWORD_LB)
                    tokens.add(index, new IdToken("apply"));
                temp = new Ast(tokens.get(index));
                stack.push(temp);
                ast = temp;
                state = -1;
            } else if (state == 2) {
                if (tokens.get(index) == KeywordToken.KEYWORD_RB) {
                    temp = Devore.AST_EMPTY;
                    DevoreAssert.paperAssert(stack.peek() != null, "Stack is empty.");
                    assert stack.peek() != null;
                    stack.peek().add(temp);
                    state = -1;
                    ++index;
                    continue;
                }
                if (tokens.get(index) == KeywordToken.KEYWORD_LB)
                    tokens.add(index, new IdToken("apply"));
                temp = new Ast(tokens.get(index));
                DevoreAssert.paperAssert(stack.peek() != null, "Stack is empty.");
                assert stack.peek() != null;
                stack.peek().add(temp);
                stack.push(temp);
                state = -1;
            } else if (tokens.get(index) == KeywordToken.KEYWORD_LB)
                state = stack.isEmpty() ? 1 : 2;
            else if (tokens.get(index) == KeywordToken.KEYWORD_RB) {
                if (index >= 2 && tokens.get(index - 2) == KeywordToken.KEYWORD_LB) {
                    DevoreAssert.paperAssert(stack.peek() != null, "Stack is empty.");
                    assert stack.peek() != null;
                    stack.peek().setType(Ast.AstType.FUNCTION);
                }
                stack.pop();
            } else {
                DevoreAssert.paperAssert(stack.peek() != null, "Stack is empty.");
                assert stack.peek() != null;
                stack.peek().add(new Ast(tokens.get(index)));
            }
            ++index;
        }
        return ast;
    }
}
