package com.unimelb.checkmein.bean;

import java.util.HashMap;
import java.util.Map;

public class Subject {
    private String code;
    private Map<String, User> students = new HashMap<>();
    private String name;
    public Building building;

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


    public Map<String, User> getStudents() {
        return students;
    }

    public void setStudents(Map<String, User> students) {
        this.students = students;
    }



    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("name", name);
        result.put("students", students);
        result.put("building", building);
        return result;
    }

}
