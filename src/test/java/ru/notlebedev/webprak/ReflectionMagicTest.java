package ru.notlebedev.webprak;

import org.junit.jupiter.api.Test;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReflectionMagicTest {

    @Test
    void getGeneric() {
        GenericTestClass<?, ?, ?> impl0 = new GenericTestClassImpl0();
        assertEquals(Number.class, impl0.typeT);
        assertEquals(Integer.class, impl0.typeV);
        assertEquals(Double.class, impl0.typeK);

        GenericTestClass<?, ?, ?> impl1 = new GenericTestClassImpl1();
        assertEquals(List.class, impl1.typeT);
        assertEquals(Integer.class, impl1.typeV);
        assertEquals(ArrayList.class, impl1.typeK);

        assertThrows(IllegalArgumentException.class, GenericTestClassImpl2::new);
    }

    @Test
    void getLazyFields() throws NoSuchFieldException {
        List<Field> fields = ReflectionMagic.getLazyFields(GetLazyFieldsTestClass.class);

        assertThat(fields)
                .containsExactlyInAnyOrder(
                        GetLazyFieldsTestClass.class.getDeclaredField("manyToOneLazy"),
                        GetLazyFieldsTestClass.class.getDeclaredField("oneToManyLazy"));
    }

    @Test
    void applyToField() {
    }

    private static abstract class GenericTestClass<T, V extends Number, K extends T> {
        public Class<?> typeT = ReflectionMagic.getGeneric(getClass(), 0);
        public Class<?> typeV = ReflectionMagic.getGeneric(getClass(), 1);
        public Class<?> typeK = ReflectionMagic.getGeneric(getClass(), 2);
    }

    private static class GenericTestClassImpl0 extends GenericTestClass<Number, Integer, Double> {}
    private static class GenericTestClassImpl1 extends GenericTestClass<List<Integer>, Integer, ArrayList<Integer>> {}
    private static class GenericTestClassImpl2<T, V extends Number, K extends T> extends GenericTestClass<T, V, K> {}

    private static class GetLazyFieldsTestClass {
        private String notAnnotated;

        @ManyToOne(fetch = FetchType.EAGER)
        private String notLazy0;
        @OneToMany(fetch = FetchType.EAGER)
        private String notLazy1;

        @ManyToOne(fetch = FetchType.LAZY)
        private String manyToOneLazy;

        @OneToMany(fetch = FetchType.LAZY)
        private String oneToManyLazy;
    }
}