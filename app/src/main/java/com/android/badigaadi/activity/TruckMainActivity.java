package com.android.badigaadi.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.badigaadi.R;
import com.android.badigaadi.helper.NonSwipeableViewPager;
import com.android.badigaadi.helper.PagerAdapter;

import me.relex.circleindicator.CircleIndicator;

public class TruckMainActivity extends AppCompatActivity {


    ViewPager viewpager;
    int position;
    Button next1, next2;
    LinearLayout confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.truck_activity_main);
        viewpager = (NonSwipeableViewPager) findViewById(R.id.pager);
        next1 = (Button) findViewById(R.id.next1);
        next2 = (Button) findViewById(R.id.next2);
        confirm = (LinearLayout) findViewById(R.id.confirm);


        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = viewpager.getCurrentItem();
                viewpager.setCurrentItem(position + 1, true);
                next1.setVisibility(View.GONE);
                next2.setVisibility(View.VISIBLE);
            }
        });

        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = viewpager.getCurrentItem();
                viewpager.setCurrentItem(position + 1, true);
                next2.setVisibility(View.GONE);
                confirm.setVisibility(View.VISIBLE);
            }
        });

        PagerAdapter padapter = new PagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(padapter);
        viewpager.setClipToPadding(false);
        viewpager.setPadding(60, 0, 60, 0);
        CircleIndicator titleIndicator = (CircleIndicator) findViewById(R.id.indicator);
        titleIndicator.setViewPager(viewpager);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //for crate home button

        toolbar.setTitleTextColor(0x000000);
        toolbar.setTitle("Add A Truck");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}
