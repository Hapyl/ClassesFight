package ru.hapyl.classesfight.exceptions;

import ru.hapyl.classesfight.Singleton;

public class SingletonInstantiationException extends RuntimeException {

    public SingletonInstantiationException(Singleton c) {
        super(String.format("Singleton class %s already has been initiated!", c));
    }

}
