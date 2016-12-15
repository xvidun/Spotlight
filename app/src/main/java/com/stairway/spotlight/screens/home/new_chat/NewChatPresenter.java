package com.stairway.spotlight.screens.home.new_chat;

import com.stairway.data.config.Logger;
import com.stairway.spotlight.core.UseCaseSubscriber;

import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by vidhun on 01/09/16.
 */
public class NewChatPresenter implements NewChatContract.Presenter {
    private NewChatContract.View contactsView;
    private GetNewChatsUseCase getNewChatsUseCase;
    private CompositeSubscription compositeSubscription;

    public NewChatPresenter(GetNewChatsUseCase getNewChatsUseCase) {
        this.getNewChatsUseCase = getNewChatsUseCase;
        this.compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(NewChatContract.View view) {
        this.contactsView = view;
    }

    @Override
    public void detachView() {
        contactsView = null;
        compositeSubscription.unsubscribe();
    }

    @Override
    public void initContactList() {
        Logger.d("ContactsPresenter");

        Subscription subscription = getNewChatsUseCase.execute()
                .observeOn(contactsView.getUiScheduler())
                .subscribe(new UseCaseSubscriber<List<NewChatItemModel>>(contactsView) {
                    @Override
                    public void onResult(List<NewChatItemModel> result) {
                        contactsView.addContacts(result);
                    }
                });

        compositeSubscription.add(subscription);
    }
}