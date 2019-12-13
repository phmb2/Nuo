package com.phmb2.nuo.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.phmb2.nuo.R;
import com.phmb2.nuo.basicas.Foto;
import com.phmb2.nuo.bd.Database;
import com.phmb2.nuo.dialogs.FotoDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;


/**
 * Created by phmb2 on 20/07/17.
 */

public class AdicionarFotoActivity extends AppCompatActivity implements FotoDialog.ImagemDialogListener, View.OnFocusChangeListener, View.OnClickListener
{
    protected ImageView imagemFoto;
    protected TextView titulo_tela;
    protected EditText descricaoFoto;
    protected Button button_inserirFoto;
    protected String dataFoto;
    protected int numero_aleatorio;

    protected TextInputLayout textInputDescricao;

    protected ImageView keyboardLeft;

    protected Dialog dialog;
    protected DialogFragment dialogFragment;

    protected Foto foto;
    protected String data;
    protected File directory;
    protected Bitmap imageBitmap;

    public static final int FOTO_NULL = -1;

    protected void closeDialog()
    {
        if(dialog != null)
        {
            dialog.dismiss();
        }
        if(dialogFragment != null)
        {
            dialogFragment.dismiss();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionarfoto);

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_left);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_layout_auxiliar);

        Typeface fontIcon = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        Typeface fontText = Typeface.createFromAsset(getAssets(), "fonts/fira-sans.regular.ttf");

        keyboardLeft = (ImageView) findViewById(R.id.keyboard_left);

        titulo_tela = (TextView) findViewById(R.id.titulo);
        titulo_tela.setTypeface(fontText);
        titulo_tela.setText(getResources().getString(R.string.titulo_adicionar_foto));

        imagemFoto = (ImageView) super.findViewById(R.id.foto_atual);
        imagemFoto.setBackgroundColor(getCustomColor());

        textInputDescricao = (TextInputLayout) super.findViewById(R.id.descricaoFotoTextInputLayout);
        descricaoFoto = (EditText) super.findViewById(R.id.descricaoFotoInserir);
        descricaoFoto.setTypeface(fontText);

        button_inserirFoto = (Button) super.findViewById(R.id.btn_inserirFoto);
        button_inserirFoto.setTypeface(fontIcon);
        button_inserirFoto.setText("\uf0c7" + " " + getResources().getString(R.string.inserir_foto));

        dataFoto = getString(R.string.data_foto_criada);

        Random gerador = new Random();
        numero_aleatorio = gerador.nextInt(1000);

        //Inicializando os dados na tela Adicionar Foto

        //Descrição da foto
        final String descricao = descricaoFoto.getText().toString();

        if (descricao.trim().length() > 0) {
            descricaoFoto.setText(descricao.trim());
        }

        descricaoFoto.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                foto.setDescricao(descricaoFoto.getText().toString().trim());
                boolean cond = descricaoFoto.getText().toString().trim().equalsIgnoreCase("");
                textInputDescricao.setError(cond ? getString(R.string.please_erro_descricao) : null);
            }
        });

        keyboardLeft.setOnClickListener(this);
        imagemFoto.setOnClickListener(this);
        button_inserirFoto.setOnClickListener(this);

        foto = new Foto(FOTO_NULL, "", "", -1, "");
    }

    private int getCustomColor()
    {
        return Color.argb(100, 216, 191, 216);
    }

    public void semTeclado()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(descricaoFoto.getWindowToken(), 0);

        if(descricaoFoto.isFocused()) {

            descricaoFoto.setFocusable(false);
            descricaoFoto.setFocusableInTouchMode(false);
            descricaoFoto.setFocusable(true);
            descricaoFoto.setFocusableInTouchMode(true);
        }
    }

    //Escolha da foto
    public void escolherFoto()
    {
        closeDialog();

        prepareDialog("Imagem");
        DialogFragment dialog_imagem = FotoDialog.newInstance(foto.getImagem(), imageBitmap);
        dialogFragment = dialog_imagem;
        dialog_imagem.show(getSupportFragmentManager(), "Imagem");

    }

    public void data_foto(String dataFoto)
    {
        //Data de criação da foto
        long date = System.currentTimeMillis();
        final Locale myLocale = new Locale("pt", "BR");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", myLocale);
        data = dataFoto + " " + sdf.format(date);
    }

    //Cadastro do dados da foto
    public void inserirDados()
    {
        final String descricao_foto = descricaoFoto.getText().toString();

        semTeclado();
        closeDialog();
        int message = 0;

        foto.setImagem(foto.getImagem());

        if(descricao_foto.equals(""))
        {
            message = R.string.please_erro_descricao;
            textInputDescricao.setError(getString(message));
        }
        else
        {
            foto.setDescricao(descricao_foto);
        }

        if(message == 0)
        {
            data_foto(dataFoto);
            foto.setData(data);

            if(imageBitmap != null)
            {
                saveToInternalStorage();
            }
            else
            {
                foto.setImg_path("ImagemNaoConcluida");
            }

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);

            finish();

            boolean bool = Database.getInstance(this).salvarFoto(foto);
        }
        else
        {
            AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AdicionarFotoActivity.this, R.style.NuoDialog);
            builder.setMessage(message)
                    .setPositiveButton(R.string.botao_OK, new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.setCancelable(false);

            dialog = builder.show();

        }
    }

    public void prepareDialog(String TAG)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(TAG);

        if (prev != null)
        {
            ft.remove(prev);
        }

        ft.addToBackStack(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_adicionarfoto, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onDialogImageClick(DialogFragment dialog, int ID_imagem, int imagem, int nome_imagem)
    {
        foto.setImagem(imagem);
    }

    public void onDialogPhotoClick(DialogFragment dialog, String IMAGE_PATH, File directory, Bitmap imageBitmap)
    {
        this.directory = directory;

        imagemFoto.setImageBitmap(BitmapFactory.decodeFile(this.directory.getAbsolutePath()));

        if(imageBitmap != null)
        {
            Log.v("Foto ou Galeria", foto.getImg_path());
            imagemFoto.setImageBitmap(imageBitmap);
        }
        else
        {
            Log.v("Sem foto", "Testando");
            imagemFoto.setImageResource(R.drawable.add_photo);
        }

        if(this.imageBitmap != null)
        {
            this.imageBitmap.recycle();
        }
        this.imageBitmap = imageBitmap;
    }

    //Armazenamento da imagem na memória interna do celular
    public void saveToInternalStorage()
    {
        foto.setImg_path(directory.getAbsolutePath() + "|" + "Foto" + foto.getId() + "-" + numero_aleatorio + ".jpg");
        File mypath = new File(directory, "Foto" + foto.getId() + "-" + numero_aleatorio + ".jpg");

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(mypath);
            //Use the compress method on the Bitmap object to write image to the OutputStream
            boolean result = this.imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            System.out.println(result);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus)
    {
        if(hasFocus)
        {
            onClick(view);
        }
    }

    @Override
    public void onDestroy()
    {
        if(imageBitmap != null){

            imageBitmap.recycle();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        FotoDialog.activity_result(dialogFragment, this, requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.foto_atual:
                escolherFoto();
                break;
            case R.id.botao_inserir_foto:
            case R.id.btn_inserirFoto:
                inserirDados();
                break;
            case R.id.keyboard_left:
                finish();
                break;
            default:
                break;
        }
    }

}
