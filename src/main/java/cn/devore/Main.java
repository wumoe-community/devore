package cn.devore;

import cn.devore.lang.Env;
import cn.devore.lang.Token;
import cn.devore.lang.token.KeywordToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        repl();
    }

    public static void repl() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder codeBuilder = new StringBuilder();
        Env env = Devore.newDefaultEnv();
        int size = 0;
        while (true) {
            System.out.print("[Devore] >>> ");
            if (size > 0)
                codeBuilder.append(" ");
            while (size-- > 0)
                System.out.print("    ");
            int index = 0;
            int flag = 0;
            String read = reader.readLine();
            if (read.startsWith(":exit"))
                break;
            if (read.startsWith(":clear"))
                env = Devore.newDefaultEnv();
            else if (read.startsWith(":load"))
                codeBuilder.append(Files.readString(Path.of(read.substring(6))));
            else {
                codeBuilder.append(read);
                String code = codeBuilder.toString();
                while (code.charAt(index) != '(')
                    ++index;
                do {
                    if (code.charAt(index) == '\"') {
                        boolean skip = false;
                        while (true) {
                            ++index;
                            if (index < code.length() - 1 && code.charAt(index) == '\\') {
                                skip = !skip;
                            } else if (index >= code.length() - 1 || code.charAt(index) == '\"') {
                                if (!skip)
                                    break;
                                skip = false;
                            } else if (skip)
                                skip = false;
                        }
                        ++index;
                        continue;
                    }
                    if (code.charAt(index) == '(')
                        ++flag;
                    else if (code.charAt(index) == ')')
                        --flag;
                    ++index;
                } while (index < code.length());
            }
            if (flag == 0) {
                Token result = Devore.call(codeBuilder.toString(), env);
                codeBuilder = new StringBuilder();
                if (result != KeywordToken.KEYWORD_NIL)
                    System.out.println(result.toString());
            }
            size = flag;
        }
    }
}
