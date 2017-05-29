package com.berkozer.do2getherfinal.Chat.Core.Users.Get;

import com.berkozer.do2getherfinal.Chat.Model.User;

import java.util.List;

/**
 * Created by berkozer on 04/05/2017.
 */

public interface GetUserInterface {
    interface View {
        void onGetAllUsersSuccess(List<User> users);

    }

    interface Presenter {
        void getAllUsers();
    }

    interface Interactor {
        void getAllUsersFromFirebase();
    }

    interface OnGetAllUsersListener {
        void onGetAllUsersSuccess(List<User> users);

    }
}