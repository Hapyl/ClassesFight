package ru.hapyl.classesfight.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Reward {
    /**
     * Annotates that field content is only obtainable by reward
     */
    String name() default "Levelling";
}