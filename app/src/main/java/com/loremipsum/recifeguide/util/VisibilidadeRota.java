package com.loremipsum.recifeguide.util;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by BREVE DEUS VEM on 24/11/2016.
 */

public enum VisibilidadeRota implements Serializable{

    @SerializedName("0")
    PUBLICA(0),

    @SerializedName("1")
    PRIVADA(1);

    public final int valor;

    VisibilidadeRota(int valor) {
        this.valor = valor;
    }
}
