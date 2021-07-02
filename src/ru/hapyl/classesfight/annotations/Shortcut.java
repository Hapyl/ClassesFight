package ru.hapyl.classesfight.annotations;

import ru.hapyl.classesfight.ClassesFight;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Shortcut {

    /**
     * Annotates that either field or method is shortcut of another.
     */

    Class<?> parent() default ClassesFight.class;

}
