package com.berkozer.do2getherfinal.Chat.Core.Chat;

import android.content.Context;
import android.util.Log;

import com.berkozer.do2getherfinal.Chat.Model.ChatModel;
import com.berkozer.do2getherfinal.Chat.Utils.Constants;
import com.berkozer.do2getherfinal.SelectedUserActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by berkozer on 04/05/2017.
 */

public class ChatInteractor implements ChatInterface.Interactor {

    private static final String TAG = "ChatInteractor";

    private ChatInterface.OnSendMessageListener mOnSendMessageListener;
    private ChatInterface.OnGetMessagesListener mOnGetMessagesListener;

    public ChatInteractor(ChatInterface.OnSendMessageListener onSendMessageListener,
                          ChatInterface.OnGetMessagesListener onGetMessagesListener) {
        this.mOnSendMessageListener = onSendMessageListener;
        this.mOnGetMessagesListener = onGetMessagesListener;
    }


    @Override
    public void sendMessageToFirebaseUser(final Context context, final ChatModel chat, final String receiverFirebaseToken) {
        final String room_type_1 = chat.senderUid + "_" + SelectedUserActivity.chatUserUid;
        final String room_type_2 = SelectedUserActivity.chatUserUid + "_" + chat.senderUid;

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(Constants.ARG_CHATS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                    mDatabase.child(Constants.ARG_CHATS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                    mDatabase.child(Constants.ARG_CHATS).child(room_type_2).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
                    mDatabase.child(Constants.ARG_CHATS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                    getMessageFromFirebaseUser(chat.senderUid, SelectedUserActivity.mUserId);
                }

                mOnSendMessageListener.onSendMessageSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + SelectedUserActivity.chatUserUid;
        final String room_type_2 = SelectedUserActivity.chatUserUid + "_" + senderUid;

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(Constants.ARG_CHATS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHATS)
                            .child(room_type_1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHATS)
                            .child(room_type_2).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    Log.e(TAG, "Room does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
