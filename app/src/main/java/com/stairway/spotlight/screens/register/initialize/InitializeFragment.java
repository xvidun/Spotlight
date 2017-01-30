package com.stairway.spotlight.screens.register.initialize;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stairway.data.config.Logger;
import com.stairway.data.source.contacts.ContactApi;
import com.stairway.data.source.contacts.ContactContent;
import com.stairway.data.source.contacts.ContactStore;
import com.stairway.spotlight.AccessTokenManager;
import com.stairway.spotlight.R;
import com.stairway.spotlight.core.BaseFragment;
import com.stairway.spotlight.core.di.component.ComponentContainer;
import com.stairway.spotlight.screens.home.HomeActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by vidhun on 09/12/16.
 */
public class InitializeFragment extends BaseFragment implements InitializeContract.View{

    InitializePresenter initializePresenter;

    @Bind(R.id.tv_fragment_initialize)
    TextView initializeText;

    private ContactApi contactApi;
    private ContactContent contactContent;
    private ContactStore contactStore;
    private AccessTokenManager accessTokenManager;


    public static InitializeFragment getInstance() {
        InitializeFragment initializeFragment = new InitializeFragment();
        return initializeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.contactApi = new ContactApi();
        this.contactStore = new ContactStore();
        this.contactContent = new ContactContent(this.getContext());
        this.accessTokenManager = AccessTokenManager.getInstance();
        initializePresenter = new InitializePresenter(contactApi, contactContent, contactStore, accessTokenManager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_initialize, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializePresenter.attachView(this);
        initializeText.setText("Initializing...");
        initializePresenter.syncContacts();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void navigateToHome() {
        Logger.d(this, "Navigate to home");
        startActivity(HomeActivity.callingIntent(getActivity()));
        getActivity().finish();
    }

    @Override
    protected void injectComponent(ComponentContainer componentContainer) {
    }
}