package com.gryszko.eventFinder.validators;

public class PasswordValidator {
    public static boolean validate(String password) {
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,30}";
        return password.matches(pattern);
    }
}
