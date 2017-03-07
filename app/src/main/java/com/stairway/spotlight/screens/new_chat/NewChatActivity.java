package com.stairway.spotlight.screens.new_chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stairway.spotlight.AccessTokenManager;
import com.stairway.spotlight.api.ApiManager;
import com.stairway.spotlight.core.Logger;
import com.stairway.spotlight.core.lib.ImageUtils;
import com.stairway.spotlight.db.BotDetailsStore;
import com.stairway.spotlight.db.ContactStore;
import com.stairway.spotlight.models.AccessToken;
import com.stairway.spotlight.R;
import com.stairway.spotlight.core.BaseActivity;
import com.stairway.spotlight.core.lib.AndroidUtils;
import com.stairway.spotlight.models.ContactResult;
import com.stairway.spotlight.screens.message.MessageActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by vidhun on 08/01/17.
 */
public class NewChatActivity extends BaseActivity implements NewChatContract.View, NewChatAdapter.ContactClickListener{
    @Bind(R.id.rv_contact_list)
    RecyclerView contactList;

    @Bind(R.id.tb_new_chat)
    Toolbar toolbar;

    @Bind(R.id.et_new_chat_search1)
    EditText toolbarSearch;

    @Bind(R.id.tv_new_chat_title)
    TextView toolbarTitle;

    NewChatAdapter newChatAdapter;

    @Bind(R.id.ll_new_chat)
    LinearLayout newChatLayout;

    final ProgressDialog[] progressDialog = new ProgressDialog[1];

    private PopupWindow addContactPopupWindow;
    private View addContactPopupView;
    private AccessToken userSession;
    private boolean showSoftInput;

    NewChatPresenter newChatPresenter;

    private static final String KEY_SHOW_SOFT_INPUT = "KEY_SHOW_SOFT_INPUT";

    public static Intent callingIntent(Context context, boolean showSoftInput) {
        Intent intent = new Intent(context, NewChatActivity.class);
        intent.putExtra(KEY_SHOW_SOFT_INPUT, showSoftInput);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        contactList.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemAnimator animator = contactList.getItemAnimator();
        if (animator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        newChatAdapter = new NewChatAdapter(this, this);
        contactList.setAdapter(newChatAdapter);

        userSession = AccessTokenManager.getInstance().load();
        newChatPresenter = new NewChatPresenter(ContactStore.getInstance(), ApiManager.getUserApi(), BotDetailsStore.getInstance(), ApiManager.getBotApi());

        Intent receivedIntent = getIntent();
        if(!receivedIntent.hasExtra(KEY_SHOW_SOFT_INPUT))
            return;

        showSoftInput = receivedIntent.getBooleanExtra(KEY_SHOW_SOFT_INPUT, false);

        if(showSoftInput) {
            toolbarSearch.setVisibility(View.VISIBLE);
            toolbarTitle.setVisibility(View.GONE);
            AndroidUtils.showSoftInput(this, toolbarSearch);
        } else {
            AndroidUtils.hideSoftInput(this);
            toolbarSearch.setVisibility(View.GONE);
            toolbarTitle.setVisibility(View.VISIBLE);
        }

        newChatPresenter.attachView(this);
        newChatPresenter.initContactList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if((id == android.R.id.home)) {
            onBackPressed();
            return true;
        } else if(id == R.id.action_add_contact) {
            final Handler handler = new Handler();
            AndroidUtils.hideSoftInput(this);
            handler.postDelayed(() -> showAddContactPopup(), 250);
        } else if(id == R.id.action_search) {
            toolbarSearch.setVisibility(View.VISIBLE);
            AndroidUtils.showSoftInput(this,toolbarSearch);
            toolbarTitle.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(showSoftInput)
            getMenuInflater().inflate(R.menu.new_chat_toolbar, menu);
        else
            getMenuInflater().inflate(R.menu.contacts_toolbar, menu);
        return true;
    }

    public void showContactAddedSuccess(String name, String username, boolean isExistingContact) {
        toolbarSearch.clearFocus();
        AndroidUtils.hideSoftInput(this);

        if(progressDialog[0].isShowing()) {
            progressDialog[0].dismiss();
        }

        String message;
        if(isExistingContact) {
            message = "<b>" + name + "</b> is already in your contacts on iChat.";
        } else {
            message = "<b>" + name + "</b> is added to your contacts on iChat.";
        }

        AlertDialog alertDialog = new AlertDialog.Builder(NewChatActivity.this).create();
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
        alertDialog.show();

//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
//        View addedContactView = inflater.inflate(R.layout.popup_contact_added, null);
//        PopupWindow addedPopupWindow = new PopupWindow(
//                addedContactView,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                true
//        );
//        if(Build.VERSION.SDK_INT>=21)
//            addedPopupWindow.setElevation(5.0f);
//
//        addedPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
//        addedPopupWindow.showAtLocation(newChatLayout, Gravity.CENTER,0,0);
//
//        RelativeLayout out = (RelativeLayout) addedContactView.findViewById(R.id.fl_added_contact);
//        out.setOnClickListener(view -> addedPopupWindow.dismiss());
//
//        Button sendMessage = (Button) addedContactView.findViewById(R.id.btn_send_message);
//        sendMessage.setOnClickListener(v1 -> {
//            addedPopupWindow.dismiss();
//            this.navigateToMessageActivity(username);
//        });
//
//        TextView resultMessage = (TextView) addedContactView.findViewById(R.id.tv_add_result_message);
//        resultMessage.setText(message);
//        ImageView profileImage = (ImageView) addedContactView.findViewById(R.id.iv_profileImage);
//        profileImage.setImageDrawable(ImageUtils.getDefaultProfileImage(name, username, 18));
        newChatPresenter.initContactList();
    }

    @Override
    public void showInvalidIDError() {
        toolbarSearch.clearFocus();
        AndroidUtils.hideSoftInput(this);

        if(progressDialog[0].isShowing()) {
            progressDialog[0].dismiss();
        }
//        showMessageAlertDialog("Please enter a valid iChat ID.");

        AlertDialog alertDialog = new AlertDialog.Builder(NewChatActivity.this).create();
        alertDialog.setMessage("Please enter a valid iChat ID.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    private void showAddContactPopup() {
        LinearLayout parent = new LinearLayout(this);

        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding((int)AndroidUtils.px(24),(int)AndroidUtils.px(8), (int)AndroidUtils.px(24), 0);

        EditText editText = new EditText(this);
        ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(lparams);
        editText.setHint(getResources().getString(R.string.add_contact_hint));
        editText.setHintTextColor(Color.parseColor("#9E9E9E"));
        parent.addView(editText);

        TextView tv = new TextView(this);
        tv.setText(getResources().getString(R.string.add_contact_subtitle));
        tv.setTextColor(ContextCompat.getColor(this, R.color.textColor));
        tv.setTextSize(12);
        parent.addView(tv);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a contact");

        builder.setPositiveButton("ADD", ((dialog, which) -> {
//            dialog.dismiss();
            if(editText.getText().length()>=1) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(toolbarSearch.getWindowToken(), 0);

                progressDialog[0] = ProgressDialog.show(NewChatActivity.this, "", "Loading. Please wait...", true);
                newChatPresenter.addContact(editText.getText().toString(), userSession.getAccessToken());
            }
        }));
        builder.setView(parent);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.setOnDismissListener(dialog -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(toolbarSearch.getWindowToken(), 0);
        });

        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

//    public void showMessageAlertDialog(String message) {
//        if(progressDialog[0].isShowing()) {
//            progressDialog[0].dismiss();
//        }
//        //TODO: Something wrong. 16?
//        final int WIDTH = 294, HEIGHT = 98;
//        int layout = R.layout.alert;
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(layout, null);
//
//        dialogBuilder.setView(dialogView);
//        AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//
//        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH+16, getResources().getDisplayMetrics());
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEIGHT+16, getResources().getDisplayMetrics());
//        alertDialog.getWindow().setLayout(width, height);
//
//        TextView messageText = (TextView) dialogView.findViewById(R.id.tv_alert_message);
//        messageText.setText(message);
//        Button ok = (Button) dialogView.findViewById(R.id.btn_alert_ok);
//        ok.setOnClickListener(v -> alertDialog.dismiss());
//    }

    @Override
    public void onContactItemClicked(String userId) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(toolbarSearch.getWindowToken(), 0);

        this.navigateToMessageActivity(userId);
    }

    @Override
    public void displayContacts(List<NewChatItemModel> newChatItemModel) {
        newChatAdapter.setContactList(newChatItemModel);
    }

    @OnTextChanged(R.id.et_new_chat_search1)
    public void onSearchChanged() {
        newChatAdapter.filterList(toolbarSearch.getText().toString());
    }

    private void navigateToMessageActivity(String username) {
        Activity activity = this;
        ContactStore.getInstance().getContactByUserName(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ContactResult>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(ContactResult contactResult) {
                        Logger.d(this, contactResult.toString());
                        String name = "";
                        if(!contactResult.getContactName().isEmpty())
                            name = contactResult.getContactName();
                        if(!contactResult.getDisplayName().isEmpty())
                            name = contactResult.getDisplayName();
                        startActivity(MessageActivity.callingIntent(activity, username, name));
                    }
                });
    }
}