package ru.notlebedev.webprak.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FinalNonNullPair<F, S> {
    private final F first;
    private final S second;
}
