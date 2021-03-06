package com.chat.ichat.screens.home;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
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
import com.chat.ichat.core.lib.CircleTransformation;
import com.google.gson.JsonSyntaxException;
import com.chat.ichat.R;
import com.chat.ichat.core.GsonProvider;
import com.chat.ichat.core.Logger;
import com.chat.ichat.core.lib.AndroidUtils;
import com.chat.ichat.core.lib.ImageUtils;
import com.chat.ichat.models.Message;
import com.chat.ichat.models.MessageResult;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<ChatItem> chatList;
    private List<ChatItem> temp;
    private ChatClickListener chatClickListener;
    private final int VIEW_CHAT = 0;
    private final int VIEW_EMPTY = 1;
    private final int VIEW_NO_CHAT = 2;

    public ChatListAdapter(Context context, ChatClickListener chatClickListener) {
        this.chatClickListener = chatClickListener;
        this.chatList = new ArrayList<>();
        temp = new ArrayList<>();
        this.context = context;
    }

    public void setChatList(List<ChatItem> chatItems) {
        this.chatList = chatItems;
        this.notifyItemRangeChanged(0, chatItems.size());
    }

    List<ChatItem> newChatMessage(ChatItem chatItem){
        int i;
        for (i = 0; i < chatList.size(); i++) {
            if(chatItem.getChatId().equals(chatList.get(i).getChatId())){
                chatItem.setNotificationCount(chatItem.getNotificationCount() + chatList.get(i).getNotificationCount());
                chatItem.setChatName(chatList.get(i).getChatName());

                if(i==0) {
                    chatList.set(0, chatItem);
                    notifyItemChanged(0);
                } else {
                    chatList.remove(i);
                    notifyItemRemoved(i);
                    chatList.add(0, chatItem);
                    notifyItemInserted(0);
                }
                break;
            }
        }
        if(i==chatList.size()) {
            chatList.add(0, chatItem);
            notifyItemInserted(0);
        }

        return chatList;
    }

    List<ChatItem> updateDeliveryStatus(String messageId, String deliveryReceiptId, MessageResult.MessageStatus deliveryStatus) {
        Logger.d(this, "updateDeliveryStatus "+messageId+", "+deliveryReceiptId+", "+deliveryStatus.name());
        for (int i = 0; i < chatList.size(); i++) {
            Logger.d(this, ""+chatList.get(i).toString());
            ChatItem item = chatList.get(i);
            if(messageId!=null && item.getMessageId()!=null && item.getMessageId().equals(messageId)) {
                if(!messageId.isEmpty())
                    item.setMessageId(messageId);
            }
            if(deliveryReceiptId!=null && item.getReceiptId()!=null && item.getReceiptId().equals(deliveryReceiptId)) {
                if(!deliveryReceiptId.isEmpty())
                    item.setReceiptId(deliveryReceiptId);
            }
            chatList.set(i, item);
            if(item.isMe() && ((item.getReceiptId()!=null && deliveryReceiptId!=null && item.getReceiptId().equals(deliveryReceiptId))
                    || (messageId!=null && item.getMessageId()!=null && item.getMessageId().equals(messageId)))) {
                item.setMessageStatus(deliveryStatus);
                chatList.set(i, item);
                notifyItemChanged(i);
                break;
            }
        }
        return chatList;
    }

    void setChatState(String fromId, String chatState){
        for (int i = 0; i < chatList.size(); i++) {
            if(fromId.equals(chatList.get(i).getChatId())){
                if(chatState.equals(chatList.get(i).getLastMessage()))
                    return;
                ChatItem item = chatList.get(i);
                temp.add(new ChatItem(
                        item.getChatId(),
                        item.getChatName(),
                        item.getLastMessage(),
                        item.getTime(),
                        item.getMessageStatus(),
                        item.getReceiptId(),
                        item.getMessageId(),
                        item.getNotificationCount()
                ));
                String highlightColor = "#"+Integer.toHexString(ContextCompat.getColor( context, R.color.activeIndicator) & 0x00ffffff );
                item.setLastMessage("<font color=\""+highlightColor+"\">"+ chatState +"</font>");
                chatList.set(i, item);
                notifyItemChanged(i);
                return;
            }
        }
    }

    void resetChatState(String fromId){
        ChatItem tempItem, item;
        for (int i = 0; i < chatList.size() ; i++) {
            item = chatList.get(i);
            if (fromId.equals(item.getChatId())) {
                for (int j = 0; j < temp.size(); j++) {
                    tempItem = temp.get(j);
                    if(fromId.equals(tempItem.getChatId())) {
                        chatList.set(i, tempItem);
                        notifyItemChanged(i);
                        temp.remove(j);
                        return;
                    }
                }
            }
        }
    }

    void removeChat(String chatId) {
        for (int i = 0; i < chatList.size() ; i++) {
            if(chatList.get(i).getChatId().equals(chatId)) {
                chatList.remove(i);
                this.notifyItemRemoved(i);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(chatList.size()==0)
            return VIEW_NO_CHAT;
        if(position<chatList.size())
            return VIEW_CHAT;
        else
            return VIEW_EMPTY;
    }

    @Override
    public int getItemCount() {
        return chatList.size()+1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_CHAT:
                View withoutNotificationView = inflater.inflate(R.layout.item_chat, parent, false);
                viewHolder = new ChatItemViewHolder(withoutNotificationView);
                break;
            case VIEW_EMPTY:
                View emptyView = inflater.inflate(R.layout.item_chat, parent, false);
                viewHolder = new EmptyViewHolder(emptyView);
                break;
            case VIEW_NO_CHAT:
                View noChatViewHolder = inflater.inflate(R.layout.item_no_chats, parent, false);
                viewHolder = new NoChatViewHolder(noChatViewHolder);
                break;
            default:
                return null;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_CHAT:
                ChatItemViewHolder chatItemViewHolder = (ChatItemViewHolder) holder;
                chatItemViewHolder.renderItem(chatList.get(position));
                break;
            default:
                break;
        }
    }

    private String getFormattedTime(DateTime time) {
        DateTime timeNow = DateTime.now();
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern("h:mm a");
        if(timeNow.getDayOfMonth() == time.getDayOfMonth()) {
            return time.toString(timeFormat).toUpperCase().replace(".", "");
        } else if(time.getDayOfMonth() > (timeNow.getDayOfMonth()-7)) {
            return time.dayOfWeek().getAsShortText();
        } else if(timeNow.getYear() == time.getYear()) {
            return time.monthOfYear().getAsShortText()+" "+time.getDayOfMonth();
        } else {
            return time.monthOfYear().getAsShortText()+" "+time.getDayOfMonth()+" AT "+time.toString(timeFormat)+" "+time.getYear();
        }
    }

    class ChatItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_item_chat)
        LinearLayout chatListContent;

        @Bind(R.id.iv_chatItem_profileImage)
        ImageView profileImage;

        @Bind(R.id.tv_chatItem_contactName)
        TextView contactName;

        @Bind(R.id.tv_chatItem_message)
        TextView lastMessage;

        @Bind(R.id.tv_chatItem_time)
        TextView time;

        ChatItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            chatListContent.setOnClickListener(view -> {
                if(chatClickListener != null)
                    chatClickListener.onChatItemClicked(contactName.getTag().toString());
            });

            chatListContent.setOnLongClickListener(v -> {
                if(chatClickListener != null)
                    chatClickListener.onChatItemLongClicked(contactName.getTag().toString());
                return true;
            });
        }

        void renderItem(ChatItem chatListItem) {
            if(chatListItem.getNotificationCount()==0) {
                lastMessage.setTypeface(null, Typeface.NORMAL);
                contactName.setTypeface(null, Typeface.NORMAL);
                time.setTextColor(0xff9e9e9e);
                lastMessage.setTextColor(0xff757575);
            } else {
                lastMessage.setTypeface(null, Typeface.BOLD);
                contactName.setTypeface(null, Typeface.BOLD);
                lastMessage.setTextColor(ContextCompat.getColor(context, R.color.textColor));
                time.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }

            contactName.setText(AndroidUtils.displayNameStyle(chatListItem.getChatName()));

            try {
                Message message = GsonProvider.getGson().fromJson(chatListItem.getLastMessage(), Message.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                    lastMessage.setText(Html.fromHtml(message.getDisplayText(), Html.FROM_HTML_MODE_LEGACY));
                else
                    lastMessage.setText(Html.fromHtml(message.getDisplayText()));
            } catch(JsonSyntaxException e) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                    lastMessage.setText(Html.fromHtml(chatListItem.getLastMessage(), Html.FROM_HTML_MODE_LEGACY));
                else
                    lastMessage.setText(Html.fromHtml(chatListItem.getLastMessage()));
            }

            if(chatListItem.isMe()) {
                lastMessage.setText("You: "+ lastMessage.getText());
            }

            time.setText(getFormattedTime(chatListItem.getTime()));

            if(chatListItem.getProfileDP()!=null && !chatListItem.getProfileDP().isEmpty()) {
                Logger.d(this, "Setting profile dp: "+chatListItem.getProfileDP());
                Glide.with(context)
                        .load(chatListItem.getProfileDP().replace("https://", "http://"))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .crossFade()
                        .placeholder(ImageUtils.getDefaultProfileImage(chatListItem.getChatName(), chatListItem.getChatId(), 18))
                        .bitmapTransform(new CenterCrop(context), new CircleTransformation(context))
                        .into(profileImage);
            } else {
                profileImage.setImageDrawable(ImageUtils.getDefaultProfileImage(chatListItem.getChatName(), chatListItem.getChatId(), 18));
            }

            contactName.setTag(chatListItem.getChatId());
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_item_chat)
        LinearLayout chatListContent;


        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            chatListContent.setVisibility(View.INVISIBLE);
        }
    }

    class NoChatViewHolder extends RecyclerView.ViewHolder {
        NoChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ChatClickListener {
        void onChatItemClicked(String username);
        void onChatItemLongClicked(String username);
    }
}