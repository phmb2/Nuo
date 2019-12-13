package com.phmb2.nuo.extras;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by phmb2 on 21/07/17.
 */

public class AdapterRecyclerViewGeneric extends RecyclerView.Adapter<AdapterRecyclerViewGeneric.MyHolder>
{
    private List<ElementRecyclerView> list;
    private LayoutInflater layoutInflater;
    private OnClickItemListiner listiner;
    private OnClickLongItemListiner longItemListiner;

    public AdapterRecyclerViewGeneric(Context context, List<ElementRecyclerView> list, OnClickItemListiner listiner, OnClickLongItemListiner longItemListiner){
        this.list=list;
        this.listiner=listiner;
        this.longItemListiner=longItemListiner;
        layoutInflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    public void setOnClickItemListiner(OnClickItemListiner listiner){
        this.listiner=listiner;

    }
    public void setOnClickLongItemListiner(OnClickLongItemListiner listiner){
        this.longItemListiner=listiner;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View itemView=layoutInflater.inflate(viewType,parent,false);


        return new MyHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        ElementRecyclerView element= list.get(position);
        return element.getXMLid() ;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickItemListiner{
        public void OnClickItemListiner(View v, int i, ElementRecyclerView element);
    }
    public interface OnClickLongItemListiner{
        public boolean OnClickLongItemListiner(View v, int i, ElementRecyclerView element);
    }

    public class MyHolder  extends RecyclerView.ViewHolder  implements View.OnClickListener ,View.OnLongClickListener{
        private ElementRecyclerView elementRecyclerView;
        private View view;
        public MyHolder(View itemView)
        {
            super(itemView);

            view = itemView;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);


        }
        public void bind(ElementRecyclerView elementRecyclerView){
            this.elementRecyclerView = elementRecyclerView;
            elementRecyclerView.bindView(view,AdapterRecyclerViewGeneric.this,getAdapterPosition());
        }


        @Override
        public void onClick(View v) {

            if(listiner!=null) listiner.OnClickItemListiner(v,getAdapterPosition(),elementRecyclerView);

        }


        @Override
        public boolean onLongClick(View view) {
            if(longItemListiner!=null) return longItemListiner.OnClickLongItemListiner(view,getAdapterPosition(),elementRecyclerView);
            return false;
        }
    }
}


