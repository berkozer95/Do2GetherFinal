package com.berkozer.do2getherfinal.Chat.UI.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.berkozer.do2getherfinal.Chat.UI.Adapters.UserListingPagerAdapter;
import com.berkozer.do2getherfinal.PlaceActivity;
import com.berkozer.do2getherfinal.R;
import com.berkozer.do2getherfinal.UsersActivity;
import com.google.android.gms.location.places.Place;

/**
 * Created by berkozer on 04/05/2017.
 */

public class UserListingActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TabLayout mTabLayoutUserListing;
    private ViewPager mViewPagerUserListing;
    private  Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        bindViews();

        mToolbar.setTitle("Users Who Are Here");
        setSupportActionBar(mToolbar);

        // set the view pager adapter
        UserListingPagerAdapter userListingPagerAdapter = new UserListingPagerAdapter(getSupportFragmentManager());
        mViewPagerUserListing.setAdapter(userListingPagerAdapter);

        // attach tab layout with view pager
        mTabLayoutUserListing.setupWithViewPager(mViewPagerUserListing);
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTabLayoutUserListing = (TabLayout) findViewById(R.id.tab_layout_user_listing);
        mViewPagerUserListing = (ViewPager) findViewById(R.id.view_pager_user_listing);
    }
}
