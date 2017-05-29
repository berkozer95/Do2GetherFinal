package com.berkozer.do2getherfinal.Chat.Core.Users.Get;

import com.berkozer.do2getherfinal.Chat.Model.User;

import java.util.List;

/**
 * Created by berkozer on 04/05/2017.
 */

public class GetUserPresenter implements GetUserInterface.Presenter, GetUserInterface.OnGetAllUsersListener {
    private GetUserInterface.View mView;
    private GetUserInteractor mGetUserInteractor;

    public GetUserPresenter(GetUserInterface.View view) {
        this.mView = view;
        mGetUserInteractor = new GetUserInteractor(this);
    }

    @Override
    public void getAllUsers() {
        mGetUserInteractor.getAllUsersFromFirebase();
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {
        mView.onGetAllUsersSuccess(users);
    }

}
