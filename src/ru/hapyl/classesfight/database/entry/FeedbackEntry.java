package ru.hapyl.classesfight.database.entry;

import ru.hapyl.classesfight.database.Database;

import javax.annotation.Nullable;

public class FeedbackEntry extends DatabaseEntry {
    public FeedbackEntry(Database database) {
        super(database);
    }

    @Nullable
    public String getFeedback() {
        return this.database.getYaml().getString("feedback", null);
    }

    public boolean hasFeedback() {
        return getFeedback() != null;
    }

    public void setFeedback(String args) {
        this.database.getYaml().set("feedback", args);
    }

}
