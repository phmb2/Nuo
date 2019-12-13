package com.phmb2.nuo.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import com.phmb2.nuo.basicas.Foto;
import com.phmb2.nuo.bd.tabelas.FotoTable;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;

/**
 * Created by phmb2 on 20/07/17.
 */

public class Database extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "database.nuo"; //nome do arquivo database
    private static final int DATABASE_VERSION = 1; //número da versao do database

    private FotoTable fotoTable;

    private static Context context;
    private static Database instance;
    public static Database db;

    private static BlockingQueue<Pair<String, Integer>> fila = null;

    public static Database getDatabase(Context context)
    {
        if(context != null)
        {
            Database.context = context;
            if (instance == null) {
                instance = new Database(context);
                instance.getReadableDatabase().close();
            }
        }
        return instance;
    }

    public static Database init(Context context, BlockingQueue<Pair<String, Integer>> fi, final Runnable fim)
    {
        if (db == null) {
            db = new Database(context);
        }
        fila = fi;
        new Thread() {
            @Override
            public void run()
            {
                SQLiteDatabase dbW = db.getReadableDatabase();
                dbW.close();
                if (fim != null) fim.run();
            }
        }.start();
        return db;
    }

    public Database(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this;

        //Inicialização das tabelas do BD
        this.fotoTable = new FotoTable();

        this.context = context;
    }

    public static Database getInstance(Context context) {

        if (instance == null) {
            instance = new Database(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(fotoTable.getCreateCommandString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    private static ContentValues contentValuesFromFotoToInsert(Foto foto)
    {
        ContentValues values = new ContentValues();

        values.put(FotoTable.DATA_COL, foto.getData());
        values.put(FotoTable.DESCRICAO_COL, foto.getDescricao());
        values.put(FotoTable.IMAGEM_COL, foto.getImagem());
        values.put(FotoTable.IMG_PATH_COL, foto.getImg_path());

        return values;
    }

    private static ContentValues contentValuesFromFoto(Foto foto)
    {
        ContentValues values = new ContentValues();
        values.put(FotoTable.ID_COL, foto.getId());
        values.put(FotoTable.DATA_COL, foto.getData());
        values.put(FotoTable.DESCRICAO_COL, foto.getDescricao());
        values.put(FotoTable.IMAGEM_COL, foto.getImagem());
        values.put(FotoTable.IMG_PATH_COL, foto.getImg_path());

        return values;
    }

    public Foto fotofromCursor(Cursor cursor)
    {
        return new Foto(
                cursor.getInt(FotoTable.ID_COL_INDEX),
                cursor.getString(FotoTable.DATA_COL_INDEX),
                cursor.getString(FotoTable.DESCRICAO_COL_INDEX),
                cursor.getInt(FotoTable.IMAGEM_COL_INDEX),
                cursor.getString(FotoTable.IMG_PATH_COL_INDEX)
        );
    }

    //Insere e edita qualquer foto
    public boolean salvarFoto(Foto foto)
    {
        boolean salva_foto;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values;

        if(foto.getId() < 0) //Não tem ID
        {
            values = contentValuesFromFotoToInsert(foto);
            salva_foto = db.insert(FotoTable.TABLE_NAME, null, values) > 0;
        }
        else { //Tem ID
            values = contentValuesFromFoto(foto);
            salva_foto = db.update(FotoTable.TABLE_NAME, values, FotoTable.ID_COL + " == " + foto.getId(), null) > 0;
        }

        db.close();
        return salva_foto;
    }

    //Lista de fotos
    public Cursor listarFotos()
    {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT *" +
                " FROM " + FotoTable.TABLE_NAME + ";";

        return db.rawQuery(query, null);
    }

    //Lista de fotos não concluídas (Sem imagem)
    public List<Foto> listarFotosNaoConcluidas()
    {
        List<Foto> fotos_nao_concluidas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + FotoTable.TABLE_NAME + " WHERE "
                + FotoTable.IMG_PATH_COL + " == " + " \"ImagemNaoConcluida\"" + " ;";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                fotos_nao_concluidas.add(fotofromCursor(cursor));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return fotos_nao_concluidas;
    }

    //Consulta foto pelo seu id
    public Foto findFotoByID(int id_foto)
    {
        Foto foto = null;

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + FotoTable.TABLE_NAME + " WHERE "
                + FotoTable.ID_COL + " == " + id_foto + " ;";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst())
        {
            foto = fotofromCursor(cursor);
        }

        cursor.close();
        db.close();

        return foto;
    }

    //Deleta foto da lista de Fotos na tela principal
    public void deletarFoto(int id_foto)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FotoTable.TABLE_NAME, FotoTable.ID_COL + "==" + id_foto, null);

        db.close();
    }

    //Verifica se há foto inserida.
    public int temFotoInserida()
    {
        int photo = 0;
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*)" + " FROM " + FotoTable.TABLE_NAME + ";";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            photo = cursor.getInt(0);
        }

        cursor.close();
        return photo;
    }

    //Verifica se há foto inserida não concluída.
    public int temFotoInseridaNaoConcluida()
    {
        int fotos_nao_concluidas = 0;
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*)" + " FROM " + FotoTable.TABLE_NAME + " WHERE "
                + FotoTable.IMG_PATH_COL + " == " + " \"ImagemNaoConcluida\"" + " ;";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            fotos_nao_concluidas = cursor.getInt(0);
        }

        cursor.close();
        return fotos_nao_concluidas;
    }
}
