package com.chat.ichat.screens.new_chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.chat.ichat.R;
import com.chat.ichat.api.ApiManager;
import com.chat.ichat.api.contact.ContactResponse;
import com.chat.ichat.api.user._User;
import com.chat.ichat.core.Logger;
import com.chat.ichat.core.lib.AndroidUtils;
import com.chat.ichat.core.lib.CircleTransformation;
import com.chat.ichat.core.lib.ImageUtils;
import com.chat.ichat.models.ContactResult;

import org.jivesoftware.smack.packet.Presence;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.chat.ichat.MessageController.LAST_SEEN_PREFS_FILE;
/**
 * Created by vidhun on 01/09/16.
 */
public class NewChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ContactClickListener contactClickListener;
    private List<NewChatItemModel> itemList;
    private final int CONTACT  = 1;
    private final int NO_RESULT = 3;
    private final int HEADER = 4;
    private final int DISCOVER_BOTS = 8;
    private final int NO_CONTACTS = 5;
    private final int SEARCH = 6;
    private final int INVITE_CONTACT = 7;
    private final int HEADER_DISCOVER_BOTS = 9;

    private int invitePosition = -1;

    private Context context;

    private List<Integer> filteredList;
    private String filterQuery;
    private SharedPreferences sharedPreferences;
    private String highlightColor = "#26BEFF";

    public NewChatAdapter(Context context, ContactClickListener contactClickListener) {
        this.sharedPreferences = context.getSharedPreferences(LAST_SEEN_PREFS_FILE, Context.MODE_PRIVATE);
        this.context = context;
        this.contactClickListener = contactClickListener;
        this.itemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        filterQuery = "";
    }

    public void setContactList(List<NewChatItemModel> contactItems) {
        Logger.d(this, "Contacts size: "+contactItems.size());
        for (int i = 0; i < contactItems.size(); i++) {
            NewChatItemModel contactItem = contactItems.get(i);
            if(contactItem.isRegistered()) {
                Logger.d(this, "ContactItem: "+contactItem.toString());
                long millis = sharedPreferences.getLong(contactItem.getUserName(), 0);
                String onlineHtml = "<font color=\"" + highlightColor + "\">Online</font>";
                if (millis == 0) {
                    if (contactItem.getUserName().startsWith("o_")) {
                        contactItem.setPresence(onlineHtml);
                    } else {
                        contactItem.setPresence("Last seen recently");
                    }
                } else if ((new DateTime(millis).plusSeconds(5).getMillis() >= DateTime.now().getMillis())) {
                    contactItem.setPresence(onlineHtml);
                } else {
                    String lastSeen = AndroidUtils.lastActivityAt(new DateTime(millis));
                    contactItem.setPresence(context.getResources().getString(R.string.chat_presence_away, lastSeen));
                }
            } else if(invitePosition==-1) {
                invitePosition = i;
            }
        }
        Logger.d(this, "invite position: "+invitePosition);
        this.itemList = contactItems;
        this.notifyItemRangeChanged(0, contactItems.size());
    }

    public void onPresenceChanged(String userId, Presence.Type type) {
        if(filterQuery.isEmpty() && itemList.size()>0) {
            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).getUserName().equals(userId)) {
                    if (type == Presence.Type.available) {
                        Logger.d(this, "Online: " + userId);
                        NewChatItemModel m = itemList.get(i);
                        m.setPresence("<font color=\"" + highlightColor + "\">Online</font>");
                        itemList.set(i, m);
                        this.notifyItemChanged(i + 1);
                    } else if (type == Presence.Type.unavailable) {
                        NewChatItemModel m = itemList.get(i);
                        m.setPresence(context.getResources().getString(R.string.chat_presence_away, AndroidUtils.lastActivityAt(DateTime.now())));
                        itemList.set(i, m);
                        this.notifyItemChanged(i + 1);
                    }
                }
            }
        }
    }

    public void filterList(String query) {
        int modPos = 0, temp, item;
        filterQuery = query;
        String queryLower = filterQuery.toLowerCase();
        String contactNameLower;
        filteredList.clear();
        if(query.isEmpty()) {
            notifyDataSetChanged();
            return;
        }
        for (NewChatItemModel newChatItemModel : itemList) {
            contactNameLower = newChatItemModel.getContactName().toLowerCase();
            if (contactNameLower.contains(queryLower)) {
                item = itemList.indexOf(newChatItemModel);
                filteredList.add(item);

                if (contactNameLower.startsWith(queryLower)) {
                    temp = filteredList.get(modPos);
                    filteredList.set(modPos, item);
                    filteredList.set(filteredList.size() - 1, temp);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(filteredList.size()==0 && !filterQuery.isEmpty()) {
            return NO_RESULT;
        }
//        else if(position == 2 && filterQuery.isEmpty()) {
//            return HEADER;
//        } else if(position == 1 && filterQuery.isEmpty()) {
//            return DISCOVER_BOTS;
//        }  else if(position == 0 && filterQuery.isEmpty()) {
//            return HEADER_DISCOVER_BOTS;
//        }
        else {
            if(filterQuery.length() == 0) {
                if(itemList.size() == 0 && position == 0) {
                    return NO_CONTACTS;
                } else if(position < invitePosition || invitePosition == -1) {
                    return CONTACT;
                } else {
                    return INVITE_CONTACT;
                }
            }
            else
                return SEARCH;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case CONTACT:
                View contactView = inflater.inflate(R.layout.item_contact, parent, false);
                viewHolder = new ContactsViewHolder(contactView);
                break;
            case SEARCH:
                View searchView = inflater.inflate(R.layout.item_chat, parent, false);
                viewHolder = new SearchViewHolder(searchView);
                break;
            case NO_RESULT:
                View noResultView = inflater.inflate(R.layout.item_no_result, parent, false);
                viewHolder = new NoResultViewHolder(noResultView);
                break;
            case HEADER:
                View newGroup = inflater.inflate(R.layout.item_search_category, parent, false);
                viewHolder = new CategoryViewHolder(newGroup);
                break;
            case HEADER_DISCOVER_BOTS:
                View newGroup1 = inflater.inflate(R.layout.item_search_category, parent, false);
                viewHolder = new CategoryViewHolder(newGroup1);
                break;
            case NO_CONTACTS:
                View noContacts = inflater.inflate(R.layout.item_no_contacts, parent, false);
                viewHolder = new NoContactsViewHolder(noContacts);
                break;
            case INVITE_CONTACT:
                View inviteContacts = inflater.inflate(R.layout.item_invite_contact, parent, false);
                viewHolder = new InviteContactsViewHolder(inviteContacts);
                break;
            case DISCOVER_BOTS:
                View discoverBots = inflater.inflate(R.layout.recycler_view, parent, false);
                viewHolder = new SuggestionsViewHolder(discoverBots);
                break;
            default:
                return null;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int vPos) {
        int position;
        if(!filterQuery.isEmpty() && filteredList.size() > 0 && vPos < filteredList.size())
            position = filteredList.get(vPos);
        else
            position = vPos;

        switch (holder.getItemViewType()) {
            case CONTACT:
                ContactsViewHolder cVH = (ContactsViewHolder) holder;
//                boolean isStart;
//                boolean isEnd;
//                isStart = position == 0 || Character.toUpperCase(itemList.get(position).getContactName().charAt(0)) != Character.toUpperCase(itemList.get(position - 1).getContactName().charAt(0));
//                isEnd = position != (itemList.size() - 1) && Character.toUpperCase(itemList.get(position).getContactName().charAt(0)) != Character.toUpperCase(itemList.get(position + 1).getContactName().charAt(0));
                cVH.renderItem(itemList.get(position), false, false);
                break;
            case INVITE_CONTACT:
                InviteContactsViewHolder ivh = (InviteContactsViewHolder) holder;
                ivh.renderItem(itemList.get(position).getContactName());
                break;
            case SEARCH:
                SearchViewHolder sVH = (SearchViewHolder) holder;
                sVH.renderItem(itemList.get(position), filterQuery);
                break;
            case NO_RESULT:
                NoResultViewHolder noResultViewHolder = (NoResultViewHolder) holder;
                noResultViewHolder.renderItem(filterQuery);
                break;
            case HEADER:
                CategoryViewHolder categoryNameViewHolder3 = (CategoryViewHolder) holder;
                categoryNameViewHolder3.renderItem("Favorites");
                break;
            case HEADER_DISCOVER_BOTS:
                CategoryViewHolder categoryNameViewHolder6 = (CategoryViewHolder) holder;
                categoryNameViewHolder6.renderItem("Suggested");
                break;
            case DISCOVER_BOTS:
                SuggestionsViewHolder suggestionsViewHolder = (SuggestionsViewHolder) holder;
                suggestionsViewHolder.renderItem();
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(!filterQuery.isEmpty()) {
            if(filteredList.size()==0)
                return 1;
            else
                return filteredList.size() + 1;
        }
        if(itemList.size() == 0)
            return 1;
        return itemList.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_item_chat)
        LinearLayout contactListContent;

        @Bind(R.id.iv_chatItem_profileImage)
        ImageView profileImage;

        @Bind(R.id.tv_chatItem_contactName)
        TextView contactName;

        ContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            contactListContent.setOnClickListener(view -> {
                if(contactClickListener != null)
                    contactClickListener.onContactItemClicked(contactName.getTag().toString(), 0);
            });
        }

        @SuppressWarnings("deprecation")
        void renderItem(NewChatItemModel contactItem, boolean isStart, boolean isEnd) {
            Logger.d(this, "render: "+contactItem.getContactName());
            contactName.setText(contactItem.getContactName());

            if(contactItem.getProfileDP()!=null && !contactItem.getProfileDP().isEmpty()) {
                Glide.with(context)
                        .load(contactItem.getProfileDP().replace("https://", "http://"))
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(ImageUtils.getDefaultProfileImage(contactItem.getContactName(), contactItem.getUserName(), 18))
                        .bitmapTransform(new CenterCrop(context), new CircleTransformation(context))
                        .into(profileImage);
            } else {
                profileImage.setImageDrawable(ImageUtils.getDefaultProfileImage(contactItem.getContactName(), contactItem.getUserName(), 18));
            }
            contactName.setTag(contactItem.getUserName());
        }
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_item_chat)
        LinearLayout contactListContent;

        @Bind(R.id.iv_chatItem_profileImage)
        ImageView profileImage;

        @Bind(R.id.tv_chatItem_contactName)
        TextView contactName;

        @Bind(R.id.tv_chatItem_message)
        TextView statusView;

        @Bind(R.id.iv_delivery_status)
        ImageView deliveryStatus;

        SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            contactListContent.setOnClickListener(view -> {
                if(contactClickListener != null)
                    contactClickListener.onContactItemClicked(contactName.getTag().toString(), 1);
            });
        }

        @SuppressWarnings("deprecation")
        void renderItem(NewChatItemModel contactItem, String query) {
            Logger.d(this, "renderSearch: ");
            deliveryStatus.setVisibility(View.GONE);

            String highlightColor = "#"+Integer.toHexString(ContextCompat.getColor( context, R.color.searchHighlight) & 0x00ffffff );
            String contactNameLower = contactItem.getContactName().toLowerCase();
            int startPos = contactNameLower.indexOf(query.toLowerCase());
            if(!query.isEmpty() && startPos>=0) {
                String textHTML = contactItem.getContactName().substring(0,startPos)
                        +"<font color=\""+highlightColor+"\">"+contactItem.getContactName().substring(startPos, startPos+query.length()) +"</font>"
                        +contactItem.getContactName().substring(startPos+query.length());

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    contactName.setText(Html.fromHtml(textHTML, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    contactName.setText(Html.fromHtml(textHTML));
                }
            } else {
                contactName.setText(contactItem.getContactName());
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                statusView.setText(Html.fromHtml(contactItem.getPresence(), Html.FROM_HTML_MODE_LEGACY));
            else
                statusView.setText(Html.fromHtml(contactItem.getPresence()));

            if(contactItem.getProfileDP()!=null && !contactItem.getProfileDP().isEmpty()) {
                Glide.with(context)
                        .load(contactItem.getProfileDP().replace("https://", "http://"))
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(ImageUtils.getDefaultProfileImage(contactItem.getContactName(), contactItem.getUserName(), 18))
                        .bitmapTransform(new CenterCrop(context), new CircleTransformation(context))
                        .into(profileImage);
            } else {
                profileImage.setImageDrawable(ImageUtils.getDefaultProfileImage(contactItem.getContactName(), contactItem.getUserName(), 18));
            }
            contactName.setTag(contactItem.getUserName());
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_search_category)
        TextView categoryTextView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void renderItem(String categoryName) {
            Logger.d(this, "renderCategory: ");
            categoryTextView.setText(categoryName);
        }
    }

    class InviteContactsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_chatItem_contactName)
        TextView name;

        private String contactName;

        InviteContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void renderItem(String contactName) {
            Logger.d(this, "renderInviteContacts: ");
            this.contactName = contactName;
            name.setText(contactName);
        }

        @OnClick(R.id.layout)
        public void onClick() {
            contactClickListener.onInviteContact(contactName, "");
        }
    }

    class InviteFriendsViewHolder extends RecyclerView.ViewHolder {
        InviteFriendsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.layout)
        void onClick() {
            Logger.d(this, "renderFriends:");
            contactClickListener.onInviteFriendsClicked();
        }
    }

    class DiscoverBotsViewHolder extends RecyclerView.ViewHolder {
        DiscoverBotsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.layout)
        void onClick() {
            Logger.d(this, "renderFriends:");
            contactClickListener.onDiscoverBotsClicked();
        }
    }

    class ContactsNumber extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_chatItem_contact)
        TextView contactNumber;

        public ContactsNumber(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void renderItem(int number) {
            Logger.d(this, "renderContactsNumber: ");
            contactNumber.setVisibility(View.GONE);
        }
    }

    class NoResultViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.no_result)
        TextView noResult;
        public NoResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void renderItem(String filterQuery) {
            Logger.d(this, "renderNoResult: ");
            noResult.setText("No results found for '"+filterQuery+"'");
        }
    }

    class NoContactsViewHolder extends RecyclerView.ViewHolder {
        public NoContactsViewHolder(View itemView) {
            super(itemView);
            Logger.d(this, "renderNoContacts: ");
        }
    }

    class SuggestionsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rv_search_suggestions)
        RecyclerView recyclerView;
        @Bind(R.id.view_contactItem_divider)
        View divider;

        SuggestionsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void renderItem() {
            Logger.d(this, "SuggestionsViewHolder RenderItem");
            divider.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            List<ContactResult> contactResults = new ArrayList<>();
            contactResults.add(new ContactResult("91", "9999999999", "  "));
            recyclerView.setAdapter(new SuggestionsAdapter(context, contactResults , contactClickListener));

            ApiManager.getContactApi().getUserSuggestions()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ContactResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.d(this, "Error fetch: ");
                        }

                        @Override
                        public void onNext(ContactResponse contactResponse) {
                            List<ContactResult> contactsResultsm = new ArrayList<>(contactResponse.getContacts().size());
                            for (_User user : contactResponse.getContacts()) {
                                ContactResult contactResult = new ContactResult(user.getCountryCode(), user.getPhone(), user.getName());
                                contactResult.setProfileDP(user.getProfileDP());
                                contactResult.setAdded(false);
                                contactResult.setUsername(user.getUsername());
                                contactResult.setUserId(user.getUserId());
                                contactResult.setUserType(user.getUserType());
                                contactsResultsm.add(contactResult);
                            }
                            recyclerView.setAdapter(new SuggestionsAdapter(context, contactsResultsm , contactClickListener));
                        }
                    });
        }
    }

    interface ContactClickListener {
        void onContactItemClicked(String userId, int from);
        void onInviteFriendsClicked();
        void onDiscoverBotsClicked();
        void onInviteContact(String contactName, String number);
    }
}