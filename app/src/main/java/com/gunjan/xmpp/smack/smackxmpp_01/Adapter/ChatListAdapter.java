package com.gunjan.xmpp.smack.smackxmpp_01.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gunjan.xmpp.smack.smackxmpp_01.ChatViewACtivity;
import com.gunjan.xmpp.smack.smackxmpp_01.R;
import com.gunjan.xmpp.smack.smackxmpp_01.model.chat;
import com.gunjan.xmpp.smack.smackxmpp_01.model.chatModel;

import org.jivesoftware.smack.chat2.Chat;

import java.util.List;

/**
 * Created by saurav on 7/15/2018.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.viewHolder> {
    List<chat> chatList;
    Context context;
    private OnItemClickListner mOnItemClickListner;
    private OnItemLongClickListener onItemLongClickListener;

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemLongClickListener{
        public void onItemLongClick(String contactJid,int chatUniqueId ,View anchor);
    }


    public interface  OnItemClickListner{
        public void onItemClick(String contactJid, chat.ContactType contactType);
    }

    public OnItemClickListner getmOnItemClickListner() {
        return mOnItemClickListner;
    }

    public void setmOnItemClickListner(OnItemClickListner mOnItemClickListner) {
        this.mOnItemClickListner = mOnItemClickListner;
    }

    public ChatListAdapter(Context context) {
        this.context = context;
        this.chatList = chatModel.get(context).getChat();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item,parent, false), this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        chat chat = chatList.get(position);
        holder.bindChat(chat);
    }
    public void onChatCountChange() {
        chatList = chatModel.get(context).getChat();
        notifyDataSetChanged();
        Log.d("LOGTAG", "ChatListAdapter knows of the change in messages");
    }
    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder
    {

        TextView textView;
        TextView textView2;
        ImageView imageView;
        private chat mChat;
        private ChatListAdapter mChatListAdapter;

        public viewHolder(final View itemView, final ChatListAdapter chatListAdapter) {
            super(itemView);
            textView= (TextView) itemView.findViewById(R.id.textView);
            textView2=(TextView)itemView.findViewById(R.id.textView2);
            imageView=(ImageView) itemView.findViewById(R.id.imageView);
            mChatListAdapter = chatListAdapter;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

   ChatListAdapter.OnItemClickListner listner = mChatListAdapter.getmOnItemClickListner();
   if(listner!=null) {
       listner.onItemClick(textView.getText().toString(), mChat.getContactType());


   }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ChatListAdapter.OnItemLongClickListener listener = mChatListAdapter.getOnItemLongClickListener();
                    if(listener != null)
                    {
                        listener.onItemLongClick(mChat.getJid(),mChat.getPersistID(),itemView);
                        return true;
                    }
                    return false;
                }
            });

        }
        public void bindChat(chat chat){
            mChat =chat;
            textView.setText(chat.getJid());
            textView2.setText(chat.getLastmessage());

        }
    }
}
