package com.berkozer.do2getherfinal.Chat.Model;

/**
 * Created by berkozer on 04/05/2017.
 */

public class ChatModel {
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public long timestamp;

    public ChatModel() {

    }

    public ChatModel(String sender, String receiver, String senderUid, String receiverUid, String message, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;

    }
}
