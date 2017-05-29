package com.berkozer.do2getherfinal.Chat;

import android.app.Application;


/**
 * Created by berkozer on 04/05/2017.
 */

public class FirebaseChat extends Application {
    private static boolean isChatOpen = false;

    public static boolean chatOpen() {
        return isChatOpen;
    }

    public static void setChatOpen(boolean isChatActivityOpen) {
        FirebaseChat.isChatOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
