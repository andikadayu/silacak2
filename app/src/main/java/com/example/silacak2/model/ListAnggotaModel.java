package com.example.silacak2.model;

import java.io.Serializable;

public class ListAnggotaModel implements Serializable {
    private String name;
    private String id;
    private String foto;

    public ListAnggotaModel(String name, String id,String foto) {
        this.name = name;
        this.id = id;
        this.foto = foto;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
