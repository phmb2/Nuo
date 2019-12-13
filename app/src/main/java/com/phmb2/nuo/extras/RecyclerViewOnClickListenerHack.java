package com.phmb2.nuo.extras;

import android.view.View;

/**
 * Created by phmb2 on 23/07/17.
 */

public interface RecyclerViewOnClickListenerHack
{
    void onClickListener(View view, int position1, int position2);
    boolean onLongClickListener(View view, int position);
}
