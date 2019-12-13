package com.phmb2.nuo.basicas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by phmb2 on 20/07/17.
 */

public class Foto
{
    private int id;
    private String data;
    private String descricao;
    private int imagem;
    private String img_path;

    public Foto(int id, String data, String descricao, int imagem, String img_path)
    {
        this.id = id;
        this.data = data;
        this.descricao = descricao;
        this.imagem = imagem;
        this.img_path = img_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public Bitmap getImagemCaminho(Context context)
    {
        String[] temp = separePathAndFilename(getImg_path());
        Bitmap bitmap = null;

        if(temp != null){
            if (temp.length > 1) {
                System.out.println(temp[1]);
                try {
                    File f = new File(temp[0], temp[1]);
                    System.out.println("Diretório Completo: " + f.getAbsolutePath());
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {

                }
            } else {
                if(temp.length > 0 && temp[0] != null)
                    bitmap = getBitmapFromUri(context, Uri.parse(getImg_path()));
            }
        }

        if(bitmap != null){

            bitmap = resize(bitmap, context);
        }

        return bitmap;
    }

    //Identifica se tirou a foto pelo app ou pegou uma imagem da galeria, e separa o path do filename (caso tenha, tira foto pelo app)
    private String[] separePathAndFilename(String IMAGE_PATH)
    {
        String[] retorno;

        try {

            if(IMAGE_PATH.contains("|")){
                retorno = new String[2];

                String[] temp = IMAGE_PATH.split("\\|");
                if(temp.length >= 2) {
                    retorno[0] = temp[0];
                    retorno[1] = temp[temp.length - 1];
                }else{
                    retorno = new String[1];
                    retorno[0] = IMAGE_PATH;
                }

            }else{

                retorno = new String[1];
                retorno[0] = IMAGE_PATH;
            }
        }catch (Exception e) {

            retorno = new String[1];
            retorno[0] = IMAGE_PATH;

        }
        return retorno;
    }

    private Bitmap getBitmapFromUri(Context context, Uri uri)
    {
        ParcelFileDescriptor parcelFileDescriptor;
        Bitmap image = null;

        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return image;
    }


    public static float getResize(Context context){

        if(context == null || context.getResources() == null || context.getResources().getDisplayMetrics() == null) return -1;
        float scale = context.getResources().getDisplayMetrics().density;
        float px = (192 * scale);

        return px;

    }

    public static Bitmap resize(Bitmap bitmap, Context context)
    {
        if(context == null || context.getResources() == null || context.getResources().getDisplayMetrics() == null) return bitmap;
        float scale = context.getResources().getDisplayMetrics().density;
        float px = (192 * scale);

        scale = px/((float)bitmap.getHeight());
        Log.v("photo", "scale = " + scale);

        if(scale < 1 && scale > 0){
            Bitmap ret = Bitmap.createScaledBitmap(bitmap,(int) (bitmap.getWidth()*scale + 0.5),
                    (int) (bitmap.getHeight()*scale + 0.5), false);
            bitmap.recycle();
            bitmap = ret;
        }

        return bitmap;
    }
}
