package com.berkozer.do2getherfinal.Chat.UI.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.berkozer.do2getherfinal.Chat.FirebaseChat;
import com.berkozer.do2getherfinal.Chat.UI.Fragments.ChatFragment;
import com.berkozer.do2getherfinal.Chat.Utils.Constants;
import com.berkozer.do2getherfinal.R;

/**
 * Created by berkozer on 04/05/2017.
 */

public class ChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    public static void startActivity(Context context, String receiver, String receiverUid, String firebaseToken) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bindViews();

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getIntent().getExtras().getString(Constants.ARG_RECEIVER));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_chat,
                ChatFragment.newInstance(getIntent().getExtras().getString(Constants.ARG_RECEIVER),
                        getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID),
                        getIntent().getExtras().getString(Constants.ARG_FIREBASE_TOKEN)),
                ChatFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseChat.setChatOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseChat.setChatOpen(false);
    }

}
