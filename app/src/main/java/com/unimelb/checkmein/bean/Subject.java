package com.unimelb.checkmein.bean;

import java.util.HashMap;
import java.util.Map;

public class Subject {
    private String code;
    private int times = 0;
    private Map<String, User> students = new HashMap<>();
    private String name;
    private boolean isValid = true;

    public Subject() {
    }

    public Subject(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public Map<String, User> getStudents() {
        return students;
    }

    public void setStudents(Map<String, User> students) {
        this.students = students;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("name", name);
        result.put("times", times);
        result.put("students", students);
        result.put("valid", isValid);
        return result;
    }
}
