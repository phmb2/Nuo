package com.phmb2.nuo.extras;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import com.phmb2.nuo.bd.Database;
import com.phmb2.nuo.broadcastReceivers.Boot;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by phmb2 on 20/07/17.
 */

public class NuoApplication extends Application
{
    private Database db;
    public BlockingQueue<Pair<String,Integer>> fila;

    public static int quantidadeFotos;

    private boolean fimLoadingBD = false;

    private boolean fimLoadingInApp = false;

    private synchronized void terminar(int n)
    {
        switch (n)
        {
            case 0:
                if(fimLoadingBD) return;
                fimLoadingBD = true;
                break;

            case 1:
                if(fimLoadingInApp) return;
                fimLoadingInApp = true;
                break;
        }
        if(fimLoadingInApp && fimLoadingBD)
        {
            try
            {
                if(fila != null)
                    fila.put(new Pair<String, Integer>("Iniciando app...", 100));

            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onCreate()
    {
        super.onCreate();

        Database.getDatabase(this).getReadableDatabase().close();

        Intent aIntent = new Intent(this, Boot.class);
        Log.v("scheduled", "" + aIntent);
        sendBroadcast(aIntent);

        fila = new ArrayBlockingQueue<Pair<String,Integer>>(150);
        Database.init(this, fila, new Runnable()
        {
            @Override
            public void run()
            {
                NuoApplication.this.terminar(0);
            }
        });
    }

    public Database getDatabase()
    {
        if(db == null)
            db = Database.getInstance(this);

        return db;
    }

}
