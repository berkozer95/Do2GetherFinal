package com.berkozer.do2getherfinal.Chat.Core.Chat;

import android.content.Context;

import com.berkozer.do2getherfinal.Chat.Model.ChatModel;

/**
 * Created by berkozer on 04/05/2017.
 */

public class ChatPresenter implements ChatInterface.Presenter, ChatInterface.OnSendMessageListener, ChatInterface.OnGetMessagesListener {

    private ChatInterface.View mView;
    private ChatInteractor mChatInteractor;

    public ChatPresenter(ChatInterface.View view) {
        this.mView = view;
        mChatInteractor = new ChatInteractor(this, this);
    }
    @Override
    public void sendMessage(Context context, ChatModel chat, String receiverFirebaseToken) {
        mChatInteractor.sendMessageToFirebaseUser(context, chat, receiverFirebaseToken);
    }

    @Override
    public void getMessage(String senderUid, String receiverUid) {
        mChatInteractor.getMessageFromFirebaseUser(senderUid, receiverUid);
    }

    @Override
    public void onSendMessageSuccess() {
        mView.onSendMessageSuccess();
    }

    @Override
    public void onGetMessagesSuccess(ChatModel chat) {
        mView.onGetMessagesSuccess(chat);
    }

}
