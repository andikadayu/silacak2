package com.example.silacak2.model;

import java.io.Serializable;

public class ListAnggotaModel implements Serializable {
    private String name;
    private String id;

    public ListAnggotaModel(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
