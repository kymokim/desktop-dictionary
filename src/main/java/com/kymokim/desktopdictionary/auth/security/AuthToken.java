package com.kymokim.desktopdictionary.auth.security;

public interface AuthToken<T> {
    boolean validate();
    T getClaims();
}
