package com.example.myapplication.data;

public class OtherDocModel extends BaseModel {
    private String name;

    public OtherDocModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
