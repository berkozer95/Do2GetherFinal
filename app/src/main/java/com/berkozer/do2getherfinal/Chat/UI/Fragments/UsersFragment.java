package com.berkozer.do2getherfinal.Chat.UI.Fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.berkozer.do2getherfinal.Chat.Core.Users.Get.GetUserInterface;
import com.berkozer.do2getherfinal.Chat.Core.Users.Get.GetUserPresenter;
import com.berkozer.do2getherfinal.Chat.Model.User;
import com.berkozer.do2getherfinal.Chat.UI.Adapters.UserListingRecyclerAdapter;
import com.berkozer.do2getherfinal.Chat.Utils.ItemClickUtils;
import com.berkozer.do2getherfinal.R;
import com.berkozer.do2getherfinal.SelectedUserActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by berkozer on 04/05/2017.
 */


public class UsersFragment extends Fragment implements GetUserInterface.View, ItemClickUtils.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_TYPE = "type";
    public static final String TYPE_CHATS = "type_chats";
    public static final String TYPE_ALL = "type_all";
    public static ArrayList<String> scores = new ArrayList<>();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewAllUserListing;

    private UserListingRecyclerAdapter mUserListingRecyclerAdapter;

    private GetUserPresenter mGetUserPresenter;

    public static UsersFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_users, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerViewAllUserListing = (RecyclerView) view.findViewById(R.id.recycler_view_all_user_listing);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mGetUserPresenter = new GetUserPresenter(this);
        getUsers();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        ItemClickUtils.addTo(mRecyclerViewAllUserListing)
                .setOnItemClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);    }

    //Check if new user did check in
    @Override
    public void onRefresh() {
        getUsers();
    }


    //Get Users in the place
    private void getUsers() {
        if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_CHATS)) {
            try {
                readFile();
            }catch (IOException e){
                e.printStackTrace();
            }


        } else if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_ALL)) {
            mGetUserPresenter.getAllUsers();
            try {
                readFile();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        SelectedUserActivity.startActivity(getActivity(),
                mUserListingRecyclerAdapter.getUser(position).email,
                mUserListingRecyclerAdapter.getUser(position).uid,
                mUserListingRecyclerAdapter.getUser(position).firebaseToken);
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mUserListingRecyclerAdapter = new UserListingRecyclerAdapter(users);
        mRecyclerViewAllUserListing.setAdapter(mUserListingRecyclerAdapter);
        mUserListingRecyclerAdapter.notifyDataSetChanged();
    }

    public void readFile() throws IOException {
        File root = new File(Environment.getExternalStorageDirectory(), "Hobbies");
        File[] files = root.listFiles();

        for (File f : files) {
            if(f.isFile()) {
                BufferedReader inputStream = null;

                try {
                    inputStream = new BufferedReader(
                            new FileReader(f));
                    String line;

                    while ((line = inputStream.readLine()) != null) {
                        System.out.println(line);
                        scores.add(line);
                    }
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        }
    }
}