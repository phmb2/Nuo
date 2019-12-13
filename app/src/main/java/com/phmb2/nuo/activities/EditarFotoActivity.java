package com.phmb2.nuo.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.phmb2.nuo.R;
import com.phmb2.nuo.bd.Database;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by phmb2 on 24/07/17.
 */

public class EditarFotoActivity extends AdicionarFotoActivity
{
    public static final String RESULT_EDITAR_FOTO_ID = "Editar_Foto_id";

    public static int KEY_DETALHESFOTO = 1500;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();

        if(i != null)
        {
            final int id_foto = i.getIntExtra(RESULT_EDITAR_FOTO_ID, 0);
            titulo_tela.setText(getResources().getString(R.string.titulo_editar_foto) + " " + id_foto);

            Log.d("id_foto:", String.valueOf(id_foto));

            foto = Database.getInstance(this).findFotoByID(id_foto);

            descricaoFoto.setText(foto.getDescricao());

            ContextWrapper cw = new ContextWrapper(EditarFotoActivity.this);
            directory = cw.getDir("Images_app", Context.MODE_PRIVATE);

            Bitmap bmp = foto.getImagemCaminho(EditarFotoActivity.this);

            if (bmp != null)
            {
                this.imageBitmap = bmp;
                imagemFoto.setImageBitmap(this.imageBitmap);
                saveToInternalStorage();
            }
            else
            {
                imagemFoto.setImageResource(R.drawable.add_photo);
            }

            dataFoto = getString(R.string.data_foto_atualizada);
            data_foto(dataFoto);

            //foto.setData(data);

            button_inserirFoto.setText("\uf0c7" + " " + getResources().getString(R.string.salvar_foto));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == KEY_DETALHESFOTO)
        {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
