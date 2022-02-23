package ru.notlebedev.webprak;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReflectionMagic {
    private ReflectionMagic(){}

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getGeneric(Class<?> clazz, int paramNum) {
        Type param = ((ParameterizedType) clazz.getGenericSuperclass())
                .getActualTypeArguments()[paramNum];

        if (param instanceof ParameterizedType)
            return (Class<T>) ((ParameterizedType) param).getRawType();
        else if (param instanceof Class<?>)
            return (Class<T>) param;
        else
            throw new IllegalArgumentException("Generic superclass of clazz does not declare type arguments");
    }

    /**
     * Return all fields subject to hibernate lazy loading.
     * More specifically finds all fields annotated with
     * {@link ManyToOne} and {@link OneToMany} annotations
     * with {@link FetchType} LAZY
     * @param clazz clazz to inspect fields
     * @return lazy loading fields in same order as getDeclaredFields returns them
     */
    public static List<Field> getLazyFields(Class<?> clazz) {
        List<Field> res = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getAnnotation(ManyToOne.class) != null &&
                declaredField.getAnnotation(ManyToOne.class).fetch().equals(FetchType.LAZY)) {
                res.add(declaredField);
            } else if (declaredField.getAnnotation(OneToMany.class) != null &&
                    declaredField.getAnnotation(OneToMany.class).fetch().equals(FetchType.LAZY)) {
                res.add(declaredField);
            }
        }

        return res;
    }

    /**
     * Applies lambda to field, ensures that at time of application field is accessible
     * @param consumer Consumer that can throw IllegalAccessException (to avoid pointless checks
     *                 in lambda)
     */
    public static <T> void applyToField(T t, Field field, FieldConsumer consumer) {
        boolean isPrivate = !field.canAccess(t);
        if (isPrivate)
            field.setAccessible(true);

        try {
            consumer.accept(field);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Field expected to be accessible");
        } finally {
            if (isPrivate)
                field.setAccessible(false);
        }

    }

    public interface FieldConsumer {
        void accept(Field f) throws IllegalAccessException;
    }
}
