package com.phmb2.nuo.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phmb2.nuo.R;
import com.phmb2.nuo.bd.Database;
import com.phmb2.nuo.extras.FotosFragmentPageAdapter;
import com.phmb2.nuo.extras.NuoApplication;
import com.phmb2.nuo.fragments.FotoFragment;

public class MainActivity extends AppCompatActivity
{
    private ImageView fab;
    private ViewPager viewPager;
    private FotosFragmentPageAdapter fotosFragmentPageAdapter;

    public TextView titulo_tela;

    public static int KEY_FOTOS = 1501;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_layout_principal);

        Typeface fontText = Typeface.createFromAsset(getAssets(), "fonts/fira-sans.regular.ttf");

        titulo_tela = (TextView) findViewById(R.id.titulo);
        titulo_tela.setTypeface(fontText);
        titulo_tela.setText(getResources().getString(R.string.titulo_principal_foto));

        this.fab = (ImageView) super.findViewById(R.id.fab_inserirFotos);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fotosFragmentPageAdapter = new FotosFragmentPageAdapter (getSupportFragmentManager(), this);
        viewPager.setAdapter(fotosFragmentPageAdapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        NuoApplication.quantidadeFotos = Database.getInstance(this).temFotoInserida();

        if(NuoApplication.quantidadeFotos <= 15) {

            fab.setVisibility(View.VISIBLE);

            this.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MainActivity.this, AdicionarFotoActivity.class);
                    MainActivity.this.startActivityForResult(intent, KEY_FOTOS);
                }
            });
        }
        else
        {
            fab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == KEY_FOTOS)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                FotoFragment photo = (FotoFragment) fotosFragmentPageAdapter.getItem(0);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
