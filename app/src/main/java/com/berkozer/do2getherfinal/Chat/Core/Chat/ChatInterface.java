package com.berkozer.do2getherfinal.Chat.Core.Chat;

import android.content.Context;

import com.berkozer.do2getherfinal.Chat.Model.ChatModel;

/**
 * Created by berkozer on 04/05/2017.
 */

public interface ChatInterface {
    interface View {
        void onSendMessageSuccess();

        void onGetMessagesSuccess(ChatModel chat);
    }

    interface Presenter {
        void sendMessage(Context context, ChatModel chat, String receiverFirebaseToken);

        void getMessage(String senderUid, String receiverUid);
    }

    interface Interactor {
        void sendMessageToFirebaseUser(Context context, ChatModel chat, String receiverFirebaseToken);

        void getMessageFromFirebaseUser(String senderUid, String receiverUid);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(ChatModel chat);
    }
}
