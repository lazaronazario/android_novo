package com.loremipsum.recifeguide.util;

import android.content.Context;

import com.loremipsum.recifeguide.R;

/**
 * Created by BREVE DEUS VEM on 02/10/2016.
 */

public class EnumHelper{

    public static String ObterDescricao(CategoriaLocal cat, Context context) {


        String desc = "";
        switch (cat) {
            case COMPRAS:
                desc = context.getResources().getString(R.string.compras);
                //desc = context.getResources().getString(R.string.compras);
                break;
            case SEMCATEGORIA:
                desc = context.getResources().getString(R.string.sem_categoria);
                break;
            case FEIRALIVRE:
                desc = context.getResources().getString(R.string.feira_livre);
                break;
            case MERCADOPUBLICO:
                desc = context.getResources().getString(R.string.mercado_publico);
                break;
            case MUSEU:
                desc = context.getResources().getString(R.string.museu);
                break;
            case PONTES:
                desc = context.getResources().getString(R.string.pontes);
                break;
            case TEATRO:
                desc = context.getResources().getString(R.string.teatro);
                break;
            default:
                break;

        }

        return desc;
    }

    public static String ObterDescricaoIdioma(TipoIdioma idioma){
        String desc = "";
        switch (idioma) {
            case PT:
                desc = "Português";
                break;
            case EN:
                desc = "Inglês";
                break;
        }
        return desc;
    }

}
