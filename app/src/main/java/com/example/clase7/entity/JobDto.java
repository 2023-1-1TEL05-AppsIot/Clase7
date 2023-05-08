package com.example.clase7.entity;

import java.io.Serializable;

public class JobDto implements Serializable {

    private ListaJobs _embedded;

    public ListaJobs get_embedded() {
        return _embedded;
    }

    public void set_embedded(ListaJobs _embedded) {
        this._embedded = _embedded;
    }
}
