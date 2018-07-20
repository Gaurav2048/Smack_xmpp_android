package com.gunjan.xmpp.smack.smackxmpp_01.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gunjan.xmpp.smack.smackxmpp_01.R;
import com.gunjan.xmpp.smack.smackxmpp_01.model.CgatMessage;
import com.gunjan.xmpp.smack.smackxmpp_01.model.CgatMessageModel;

import java.util.List;

/**
 * Created by saurav on 7/15/2018.
 */

public class ChatmessageAdapter extends RecyclerView.Adapter<ChatmessageAdapter.viewHolderSend>

{
    public interface OnItemLongClickListener{
        public void onItemLongClick(int uniqueId , View anchor);
    }
    private OnItemLongClickListener onItemLongClickListener;
    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
    public interface OnInformRecyclerViewToScrollDownListner {
        public void onInformRecyclerViewToScrollDown(int size);
    }
    public static  final int SEND =0;
    public static  final int RECEIVE =1;
    private List<CgatMessage> mChatMessage;
    private LayoutInflater mLayoutInflater;
    private Context context;

    private String string;

    public void setOnInformRecyclerViewToScrollDownListner(OnInformRecyclerViewToScrollDownListner onInformRecyclerViewToScrollDownListner) {
        this.onInformRecyclerViewToScrollDownListner = onInformRecyclerViewToScrollDownListner;
    }

    private  OnInformRecyclerViewToScrollDownListner onInformRecyclerViewToScrollDownListner;


    public ChatmessageAdapter(Context context, String contactJid) {

        this.context = context;
        this.string=contactJid;
        this.mLayoutInflater= LayoutInflater.from(context);
        mChatMessage = CgatMessageModel.get(context).getMessages(string);

    }

    @NonNull
    @Override
    public viewHolderSend onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         if(viewType==0){

             return new viewHolderSend(mLayoutInflater.inflate(R.layout.chat_message_sent, parent, false), this);
         }else if (viewType==1) {
             return new viewHolderSend(mLayoutInflater.inflate(R.layout.chat_message_received, parent, false), this);
         }
         else
         {
             return null;
         }

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolderSend holder, int position) {

        holder.bindChat(mChatMessage.get(position));

    }

    @Override
    public int getItemViewType(int position) {
        if(mChatMessage.get(position).getType()==CgatMessage.Type.SENT){
            return SEND;

        }else if (mChatMessage.get(position).getType()==CgatMessage.Type.RECEIVE){
            return RECEIVE;

        }else {
            return  33;
        }
    }

    @Override
    public int getItemCount() {
        return mChatMessage.size();
    }

     public void onMEssageAdded() {
        mChatMessage= CgatMessageModel.get(context).getMessages(string);
    notifyDataSetChanged();
        Log.e( "onMEssageAdded: ","size"+mChatMessage.size()+"here" );
        informRecyclerViewToScrollDown();
    }
public void informRecyclerViewToScrollDown(){
    onInformRecyclerViewToScrollDownListner.onInformRecyclerViewToScrollDown(mChatMessage.size());
}

    public class viewHolderSend extends RecyclerView.ViewHolder{
ImageView imageView;
TextView textView;
TextView timeStamp;
private CgatMessage mChatMessage;

        public viewHolderSend(final View itemView, final ChatmessageAdapter mAdapter) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.participant_send_image);
            textView = (TextView) itemView.findViewById(R.id.message_space);
            timeStamp = (TextView) itemView.findViewById(R.id.time_send);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

ChatmessageAdapter.OnItemLongClickListener listener = mAdapter.getOnItemLongClickListener();
if(listener!=null){
    listener.onItemLongClick(mChatMessage.getPersistID(), itemView);

}
                    return false;
                }
            });
        }

         public void bindChat(CgatMessage cgatMessage)
         {
             mChatMessage= cgatMessage;
             textView.setText(cgatMessage.getMessage());
             timeStamp.setText(String.valueOf(cgatMessage.getTimestamp()).toString().substring(4,7));

         }

    }

}
