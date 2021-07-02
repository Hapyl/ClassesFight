package ru.hapyl.classesfight.translate;

public class TranslateManager {

    public static TranslateManager current;

    public static final Locale ENGLISH = new Locale("english", "English");
    public static final Locale RUSSIAN = new Locale("russian", "Russian");

    public TranslateManager() {
        if (current != null) {
            throw new IllegalStateException("Cannot create TranslateManager since it's already created!");
        }
        current = this;
    }

    public static TranslateManager current() {
        return current;
    }

    public Locale getLocale(Locale locale) {
        return locale;
    }


}
