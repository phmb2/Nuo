package com.phmb2.nuo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phmb2.nuo.R;
import com.phmb2.nuo.basicas.Foto;
import com.phmb2.nuo.bd.Database;
import com.phmb2.nuo.bd.tabelas.FotoTable;
import com.phmb2.nuo.extras.NuoApplication;
import com.phmb2.nuo.extras.RecyclerViewOnClickListenerHack;

import java.util.HashMap;

import static com.phmb2.nuo.R.color.transparent;

/**
 * Created by phmb2 on 22/07/17.
 */

public class FotoAdapter extends HeaderCursorRecyclerViewAdapter<RecyclerView.ViewHolder>
{
    private Context context;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

    private HashMap<String, Integer> mapPlus = new HashMap<>();

    public FotoAdapter(Context context, Cursor cursor, RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack)
    {
        super(context, cursor, null);
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mRecyclerViewOnClickListenerHack = mRecyclerViewOnClickListenerHack;
    }

    @Override
    public void onBindSectionHeaderViewHolder(RecyclerView.ViewHolder viewHolder, String header, final int position)
    {
        HeaderHolder headerHolder = (HeaderHolder) viewHolder;

        headerHolder.setHeader(header);

        headerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecyclerViewOnClickListenerHack != null) {
                    mRecyclerViewOnClickListenerHack.onClickListener(v, position, 1);
                }
            }
        });

    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor, final int position)
    {
        Foto mFoto;
        Bitmap bitmap;

        ItemHolder itemHolder = (ItemHolder) viewHolder;

        mFoto = Database.getInstance(context).fotofromCursor(cursor);
        final int ID_foto = cursor.getInt(FotoTable.ID_COL_INDEX);

        bitmap = mFoto.getImagemCaminho(context);

        if(bitmap != null)
        {
            Log.v("Foto ou Galeria", mFoto.getImg_path());
            itemHolder.viewImagemFoto.setImageBitmap(bitmap);
        }
        else if(mFoto.getImg_path().equals("ImagemNaoConcluida"))
        {
            Log.v("Sem foto", "Testando");
            itemHolder.viewImagemFoto.setImageResource(R.drawable.add_photo);
        }

        String btn_plus = String.valueOf(ID_foto);

        if(!mapPlus.containsKey(btn_plus) && ID_foto == 1)
        {
            mSectionsIndexer.put(position, btn_plus);
            mapPlus.put(btn_plus, position);

            Handler handler = new Handler(context.getMainLooper());

            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(position);
                    notifyDataSetChanged();
                }
            });
        }

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecyclerViewOnClickListenerHack != null) {
                    mRecyclerViewOnClickListenerHack.onClickListener(v, ID_foto, -1);
                }
            }
        });

        itemHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mRecyclerViewOnClickListenerHack != null) {
                    return mRecyclerViewOnClickListenerHack.onLongClickListener(view, ID_foto);

                }
                return true;
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent)
    {
        View itemView = mLayoutInflater.inflate(R.layout.header_botao_foto, parent, false);
        return new HeaderHolder(itemView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_fotos, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor)
    {
        mapPlus.clear();
        mSectionsIndexer.clear();
        return super.swapCursor(newCursor);
    }

    private class HeaderHolder extends RecyclerView.ViewHolder
    {
        protected TextView viewBotao;

        public HeaderHolder(View item)
        {
            super(item);
            viewBotao = (TextView) item.findViewById(R.id.header_botao);
        }

        public void setHeader(String header)
        {
            viewBotao.setText("+");
            if (NuoApplication.quantidadeFotos <= 15)
            {
                viewBotao.setTextColor(Color.parseColor("#00000000"));
            }
            else
            {
                viewBotao.setTextColor(Color.parseColor("#3A002E"));
            }
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        protected ImageView viewImagemFoto;

        public ItemHolder(View item)
        {
            super(item);
            viewImagemFoto = (ImageView) item.findViewById(R.id.item_imagemFoto);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (mRecyclerViewOnClickListenerHack != null) {
                mRecyclerViewOnClickListenerHack.onClickListener(view, getOldPosition(), getOldPosition());
            }
        }
    }
}
