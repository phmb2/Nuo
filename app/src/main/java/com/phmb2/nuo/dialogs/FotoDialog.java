package com.phmb2.nuo.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.phmb2.nuo.R;
import com.phmb2.nuo.basicas.Foto;
import com.phmb2.nuo.extras.AdapterRecyclerViewGeneric;
import com.phmb2.nuo.extras.ElementRecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by phmb2 on 21/07/17.
 */

public class FotoDialog extends DialogFragment implements AdapterRecyclerViewGeneric.OnClickItemListiner
{
    public static final int CAMERA = 1;
    public static final int GALERIA = 2;

    public static final int IMG_PHOTO_ID = 1001;

    public static final int REQUEST_PHOTO_GALERY = 1009;
    public static final int REQUEST_PHOTO_CAMERA = 1010;

    public Typeface fontText;

    public interface ImagemDialogListener
    {
        void onDialogImageClick(DialogFragment dialog, int ID_imagem ,int imagem, int nome_imagem);

        void onDialogPhotoClick(DialogFragment dialog, String IMAGE_PATH, File directory, Bitmap imageBitmap);
    }

    private static File directory;
    protected Dialog dialogPhoto;
    protected int ID_image;

    protected static ImagemDialogListener mImagemListener;
    protected AdapterRecyclerViewGeneric adapterRecyclerViewGeneric;

    public FotoDialog(){}

    public static final String AGS_IMAGEM_ID_RESOURCE = "imagemId_resource";
    public static final String AGS_IMAGE = "image";

    public static FotoDialog newInstance(int ID_image , Bitmap bitmap)
    {
        FotoDialog im = new FotoDialog();

        Bundle args = new Bundle();
        args.putInt(AGS_IMAGEM_ID_RESOURCE, ID_image);

        if(bitmap != null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            args.putByteArray(AGS_IMAGE, byteArray);
        }

        im.setArguments(args);

        return im;
    }

    Bitmap bmp;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fontText = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fira-sans.regular.ttf");

        bmp = null;
        Bundle b = getArguments();

        if(b != null)
        {
            ID_image = b.getInt(AGS_IMAGEM_ID_RESOURCE, 1);

            if(b.containsKey(AGS_IMAGE))
            {
                byte[] byteArray = b.getByteArray(AGS_IMAGE);
                if(byteArray != null)
                {
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }
            }
        }else
        {
            ID_image = 1;
        }

    }

    @Override
    public void onDestroy()
    {
        if(bmp != null)
        {
            bmp.recycle();
        }
        super.onDestroy();
    }

    protected class Imagens implements ElementRecyclerView
    {
        private int id_imagem;
        private int imagem;
        private int nome_imagem;

        public int getId_imagem() {
            return id_imagem;
        }

        public int getImagem() {
            return imagem;
        }

        public int getNome_imagem() {
            return nome_imagem;
        }

        public Imagens (int id_imagem, int imagem, int nome_imagem)
        {
            this.id_imagem = id_imagem;
            this.imagem = imagem;
            this.nome_imagem = nome_imagem;
        }

        @Override
        public int getXMLid() {
            return  R.layout.dialog_foto_item;
        }

        @Override
        public void bindView(View view, final AdapterRecyclerViewGeneric adapterRecyclerViewGeneric, final int i)
        {
            final ImageView image = (ImageView) view.findViewById(R.id.imagem_foto);
            final TextView nome = (TextView) view.findViewById(R.id.origem_foto);
            nome.setTypeface(fontText);

            if(image != null)
            {
                image.setImageResource(imagem);
                nome.setText(nome_imagem);
                view.setBackgroundResource(R.color.white);

                switch (getId_imagem())
                {
                    case CAMERA:
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tirarFoto(getId_imagem());
                            }
                        });

                        break;

                    case GALERIA:
                        view.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                imagem_galeria();
                            }
                        });
                        break;

                    default:

                        if(ID_image == imagem)
                        {
                            nome.setTextColor(Color.parseColor("#FF5722"));
                        }
                        else
                        {
                            nome.setTextColor(Color.BLACK);
                        }

                        break;
                }

            }

        }
    }

    protected class PhotoValues implements ElementRecyclerView
    {
        protected Bitmap bitmap;

        public PhotoValues(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public int getID() {
            return IMG_PHOTO_ID;
        }

        @Override
        public int getXMLid() {
            return R.layout.dialog_foto_item;
        }

        @Override
        public void bindView(View view, AdapterRecyclerViewGeneric adapterReciclerViewGeneric, int i)
        {

        }
    }

    public void tirarFoto(int codigo)
    {
        if(codigo == 1)
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_PHOTO_CAMERA);
        }
    }

    public void imagem_galeria()
    {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
        {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PHOTO_GALERY);
        }
        else
            {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PHOTO_GALERY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        activity_result(this, getActivity(), requestCode, resultCode, data);
    }

    public static void activity_result(DialogFragment dialog, Context context, int requestCode, int resultCode, Intent data)
    {
        Log.v("photo", requestCode + " " + resultCode + " " + data);
        if(data == null)
            return;

        requestCode = 0x0000FFFF & requestCode;
        if(requestCode == REQUEST_PHOTO_CAMERA)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Bundle extras = data.getExtras();
                Bitmap image = (Bitmap) extras.get("data");

                final String imageFilePath = createImagesDirectory(context);
                System.out.println(imageFilePath);
                image = Foto.resize(image, context);

                mImagemListener.onDialogPhotoClick(dialog, imageFilePath, directory, image);
            }
        }
        else if(requestCode == REQUEST_PHOTO_GALERY)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Uri uri = data.getData();

                System.out.println(uri);

                ParcelFileDescriptor parcelFileDescriptor;
                Bitmap image = null;
                try {
                    parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds=true;
                    float px = Foto.getResize(context);

                    BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);
                    px = px/options.outHeight;
                    options.inJustDecodeBounds = false;

                    if(px < 1 && px > 0)
                    {
                        int scall = (int) ((1/px)+0.5);
                        int i = 1;
                        for(i = 1; 2*i <= scall; i*=2);
                        options.inSampleSize = i;
                        image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

                    }else {
                        image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

                    }
                    parcelFileDescriptor.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

                final String imageFilePath = createImagesDirectory(context);
                image = Foto.resize(image,context);

                mImagemListener.onDialogPhotoClick(dialog, imageFilePath, directory, image);
            }
        }
    }

    @Override
    public void OnClickItemListiner(View v, int i, ElementRecyclerView element)
    {
        if(element instanceof Imagens)
        {
            Imagens img = (Imagens) element;

            mImagemListener.onDialogImageClick(this, img.getId_imagem(), img.getImagem(), img.getNome_imagem());
            adapterRecyclerViewGeneric.setOnClickItemListiner(null);

            dialogPhoto.dismiss();
        }

        if(element instanceof PhotoValues)
        {
            final String imageFilePath = createImagesDirectory(getActivity().getApplicationContext());

            bmp = Foto.resize(bmp, getContext());
            mImagemListener.onDialogPhotoClick(FotoDialog.this, imageFilePath, directory, bmp);

            ID_image = IMG_PHOTO_ID;
        }

        adapterRecyclerViewGeneric.notifyDataSetChanged();
    }

    Imagens[] imagens = {
            new Imagens(CAMERA, R.mipmap.ic_camera, R.string.tirarFoto),
            new Imagens(GALERIA, R.mipmap.ic_gallery, R.string.galeriaFoto)
    };

    protected View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final List<ElementRecyclerView> list = new ArrayList<>(imagens.length);
        Collections.addAll(list, imagens);

        if(bmp != null)
        {
            list.add(new PhotoValues(bmp));
        }

        view = inflater.inflate(R.layout.dialog_foto_recyclerview, (ViewGroup) getView(), false);
        final RecyclerView v = (RecyclerView) view.findViewById(R.id.recycler_view);

        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                calculateSwipeRefreshFullHeight();
            }

            protected void calculateSwipeRefreshFullHeight() {

                if (list.size() > v.getChildCount()) return;
                int height = 0;
                RecyclerView recycler = v;
                for (int idx = 0; idx < v.getChildCount(); idx++) {
                    View v = recycler.getChildAt(idx);
                    height += v.getHeight();
                }
                try {
                    View parent = (View) recycler.getParent();
                    SwipeRefreshLayout.LayoutParams params = parent.getLayoutParams();
                    params.height = height;
                    parent.setLayoutParams(params);
                } catch (Exception e) {

                }
            }
        });

        v.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterRecyclerViewGeneric = new AdapterRecyclerViewGeneric(getActivity(), list, this, null);
        v.setAdapter(adapterRecyclerViewGeneric);

        View title = inflater.inflate(R.layout.title_dialog, v, false);
        TextView titleText= (TextView) title.findViewById(R.id.texto);
        titleText.setTypeface(fontText);
        titleText.setText(R.string.escolha_foto);

        builder.setCustomTitle(title);
        builder.setView(view);

        dialogPhoto = builder.create();
        return dialogPhoto;
    }

    private static String createImagesDirectory(Context context)
    {
        ContextWrapper cw = new ContextWrapper(context);
        directory = cw.getDir("Images_app", Context.MODE_PRIVATE);
        return directory.getAbsolutePath();
    }

    @Override
    public void onPause()
    {
        if(dialogPhoto != null)
        {
            dialogPhoto.dismiss();
        }
        dismiss();
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mImagemListener = (ImagemDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CoresDialogListener");
        }
    }

}

