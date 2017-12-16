package com.assistance.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Attendance extends RealmObject {
    @PrimaryKey
    private String id;
    private String personName;
    private long timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
