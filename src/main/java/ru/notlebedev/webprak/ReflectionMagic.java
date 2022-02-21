package ru.notlebedev.webprak;

import java.lang.reflect.ParameterizedType;

public class ReflectionMagic {
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getGeneric(Class<?> clazz, int paramNum) {
        return (Class<T>) ((ParameterizedType) clazz.getGenericSuperclass())
                .getActualTypeArguments()[paramNum];
    }

}
