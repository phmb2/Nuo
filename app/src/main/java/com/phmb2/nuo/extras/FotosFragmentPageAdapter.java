package com.phmb2.nuo.extras;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.phmb2.nuo.fragments.FotoFragment;

/**
 * Created by phmb2 on 24/07/17.
 */

public class FotosFragmentPageAdapter extends FragmentPagerAdapter
{
    private static int NUM_FRAGMENTS = 1;

    private Fragment[] fragments = new Fragment[NUM_FRAGMENTS];

    private String frags[] = new String[] {""};

    private Context context;

    public FotosFragmentPageAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;

        switch (position)
        {
            case 0:

                if(fragments[position] == null)
                {
                    fragments[position] = FotoFragment.newInstance(position);
                }

                fragment = fragments[position];
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return frags.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return frags[position];
    }

}


