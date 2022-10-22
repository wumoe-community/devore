package cn.devore.exception;

public class DevoreAssert {
    public static void paperAssert(boolean condition, String message) {
        if (!condition)
            throw new DevorePaperException(message);
    }

    public static void runtimeAssert(boolean condition, String message) {
        if (!condition)
            throw new DevoreRuntimeException(message);
    }

    public static void typeAssert(boolean condition, String message) {
        if (!condition)
            throw new DevoreTypeException(message);
    }
}
