package com.android.badigaadi.helper;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.android.badigaadi.fragment.AddTruck1;
import com.android.badigaadi.fragment.AddTruck2;
import com.android.badigaadi.fragment.AddTruck3;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        switch (arg0) {



            case 0:
                return new AddTruck1();

            case 1:
                return new AddTruck2();

            case 2:
                return new AddTruck3();

            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 3;
    }

}
