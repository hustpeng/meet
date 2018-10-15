package com.agmbat.utils;

/**
 * 程序内部断言
 */
public class Asserts {

    // Throws AssertionError if the input is false.
    public static void assertTrue(boolean cond) {
        if (!cond) {
            throw new AssertionError();
        }
    }

    // Throws AssertionError with the message. We had a method having the form
    // assertTrue(boolean cond, String message, Object ... args);
    // However a call to that method will cause memory allocation even if the
    // condition is false (due to autoboxing generated by "Object ... args"),
    // so we don't use that anymore.
    public static void fail(String message, Object... args) {
        throw new AssertionError(args.length == 0 ? message : String.format(message, args));
    }

    // Throws NullPointerException if the input is null.
    public static <T> T checkNotNull(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }

    // Returns true if two input Object are both null or equal
    // to each other.
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a == null ? false : a.equals(b));
    }

}