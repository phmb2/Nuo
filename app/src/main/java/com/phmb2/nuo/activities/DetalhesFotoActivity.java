package com.phmb2.nuo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phmb2.nuo.R;
import com.phmb2.nuo.basicas.Foto;
import com.phmb2.nuo.bd.Database;

import java.io.ByteArrayOutputStream;

/**
 * Created by phmb2 on 24/07/17.
 */

public class DetalhesFotoActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String EXTRA_ID_FOTO = "Id_detalhes_foto";

    public ImageView keyboardLeft;
    public TextView titulo_tela;
    public TextView data_foto;
    public TextView descricao_foto;

    private Foto mFoto;
    private Bitmap bmp;

    Intent intent;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhesfoto);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_layout_auxiliar);

        Typeface fontText = Typeface.createFromAsset(getAssets(), "fonts/fira-sans.regular.ttf");

        keyboardLeft = (ImageView) findViewById(R.id.keyboard_left);
        keyboardLeft.setOnClickListener(this);

        titulo_tela = (TextView) findViewById(R.id.titulo);
        titulo_tela.setTypeface(fontText);

        intent = getIntent();

        if(intent != null)
        {
            final int photo_id = intent.getIntExtra(EXTRA_ID_FOTO, 0);

            titulo_tela.setText(getResources().getString(R.string.titulo_detalhes_foto) + " " + photo_id);

            mFoto = Database.getInstance(this).findFotoByID(photo_id);

            bmp = mFoto.getImagemCaminho(this);

            final ImageView imgView = (ImageView) findViewById(R.id.detalhes_fotoAtual);

            if(bmp != null)
            {
                imgView.setImageBitmap(bmp);
            }
            else
            {
                if(mFoto.getImg_path().equals("ImagemNaoConcluida"))
                    imgView.setImageResource(R.drawable.add_photo);
                else
                    imgView.setImageBitmap(bmp);
            }

            data_foto = (TextView) findViewById(R.id.dataFoto);
            data_foto.setTypeface(fontText);
            data_foto.setText(mFoto.getData());

            descricao_foto = (TextView) findViewById(R.id.descricaoFoto);
            descricao_foto.setTypeface(fontText);
            descricao_foto.setText(mFoto.getDescricao());
        }

    }

    public void compartilharFoto()
    {

    }

    public void editarFoto()
    {
        Intent i = new Intent(DetalhesFotoActivity.this, EditarFotoActivity.class);
        i.putExtra(EditarFotoActivity.RESULT_EDITAR_FOTO_ID, mFoto.getId());
        startActivity(i);

        Intent intent = new Intent();

        setResult(RESULT_CANCELED, intent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_detalhesfoto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();

        /*if (id == R.id.action_sharePhoto)
        {
            compartilharFoto();
            return true;
        }*/

        if (id == R.id.action_editPhoto)
        {
            editarFoto();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.keyboard_left:
                finish();
                break;
        }
    }
}
