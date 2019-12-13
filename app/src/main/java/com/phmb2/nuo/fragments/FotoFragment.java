package com.phmb2.nuo.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.phmb2.nuo.R;
import com.phmb2.nuo.activities.AdicionarFotoActivity;
import com.phmb2.nuo.activities.DetalhesFotoActivity;
import com.phmb2.nuo.adapters.FotoAdapter;
import com.phmb2.nuo.bd.Database;
import com.phmb2.nuo.extras.RecyclerViewOnClickListenerHack;

/**
 * Created by phmb2 on 23/07/17.
 */

public class FotoFragment extends android.support.v4.app.Fragment implements RecyclerViewOnClickListenerHack
{
    public static String POSICAO = "posicao";

    private RecyclerView mRecyclerView;
    private GridLayoutManager glm;

    private FotoAdapter fotoAdapter;

    private Cursor cursor;

    public FotoFragment()
    {
    }

    public static FotoFragment newInstance(int posicao)
    {
        Bundle param = new Bundle();
        param.putInt(POSICAO, posicao);

        FotoFragment frag = new FotoFragment();
        frag.setArguments(param);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View fragView_planejamento = inflater.inflate(R.layout.fragment_foto, container, false);

        mRecyclerView = (RecyclerView) fragView_planejamento.findViewById(R.id.recycler_view_foto);
        mRecyclerView.setHasFixedSize(true); //O tamanho da view não vai mudar (otimização da view)

        glm = new GridLayoutManager(getActivity(), 4);
        glm.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(glm);

        fotoAdapter = new FotoAdapter(getActivity(), cursor, this);
        mRecyclerView.setAdapter(fotoAdapter);

        return fragView_planejamento;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        cursor = Database.getInstance(getActivity()).listarFotos();
        fotoAdapter.swapCursor(cursor);
        fotoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        fotoAdapter.swapCursor(null);
    }

    @Override
    public void onClickListener(View view, int id_foto, int header)
    {
        Context context = view.getContext();
        Intent intent = null;

        if(header == -1)
        {
            intent = new Intent(context, DetalhesFotoActivity.class);
            intent.putExtra(DetalhesFotoActivity.EXTRA_ID_FOTO, id_foto);
        }
        else //header == 1
        {
            intent = new Intent(context, AdicionarFotoActivity.class);
        }

        context.startActivity(intent);
    }

    @Override
    public boolean onLongClickListener(View view, final int id_foto)
    {
        AlertDialog.Builder builder_deletaCartaoView = new AlertDialog.Builder(view.getContext(), R.style.NuoDialog);
        builder_deletaCartaoView.setMessage(R.string.mensagem_deletar_foto);
        builder_deletaCartaoView.setCancelable(false);
        builder_deletaCartaoView.setPositiveButton(R.string.botao_Sim, new Dialog.OnClickListener() {

            public void onClick(DialogInterface dialog, int id)
            {
                Database.getInstance(getActivity()).deletarFoto(id_foto);
                cursor = Database.getInstance(getActivity()).listarFotos();
                fotoAdapter.notifyItemRemoved(id_foto);
                fotoAdapter.swapCursor(cursor);
                Toast.makeText(getActivity(), R.string.mensagem_foto_deletada, Toast.LENGTH_SHORT).show();
            }
        });
        builder_deletaCartaoView.setNegativeButton(R.string.botao_Nao, new Dialog.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });

        AlertDialog alerta = builder_deletaCartaoView.create();
        alerta.show();
        return true;
    }
}
