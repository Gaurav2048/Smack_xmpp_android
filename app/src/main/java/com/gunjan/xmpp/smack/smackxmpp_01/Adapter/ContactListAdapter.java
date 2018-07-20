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
import com.gunjan.xmpp.smack.smackxmpp_01.model.Contact;
import com.gunjan.xmpp.smack.smackxmpp_01.model.ContactModel;

import java.util.List;

/**
 * Created by saurav on 7/16/2018.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.viewHolder> {
    public interface OnItemClickListener {
        public void onItemClick(String contactJid);
    }
    private List<Contact> mContacts;
    private Context mContext;
    private static final String LOGTAG = "ContactListAdapter";
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    public OnItemLongClickListener getmOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    public void setmOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
    public OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public interface OnItemLongClickListener{
        public void onItemLongClick(int uniqueId, String contactJid ,View anchor);
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    public ContactListAdapter(Context context){
        mContacts = ContactModel.get(context).getContacts();
        mContext = context;
        Log.d(LOGTAG,"Constructor of ChatListAdapter , the size of the backing list is :" +mContacts.size());
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater
                .inflate(R.layout.contact_list_item, parent,
                        false);
        return new viewHolder(view,this);    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        holder.bindContact(contact);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{


        private TextView jidTexView;
        private TextView subscriptionTypeTextView;
        private Contact mContact;
        private ImageView profile_image;
        private ContactListAdapter mAdapter;
        private static final String LOGTAG = "ContactHolder";


        public viewHolder(final View itemView, ContactListAdapter adapter) {
            super(itemView);
            mAdapter = adapter;
            jidTexView = (TextView) itemView.findViewById(R.id.contact_jid_string);
            subscriptionTypeTextView = (TextView) itemView.findViewById(R.id.suscription_type);
            profile_image = (ImageView) itemView.findViewById(R.id.profile_contact);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOGTAG,"User clicked on Contact Item");
                    ContactListAdapter.OnItemClickListener listener = mAdapter.getmOnItemClickListener();
                    if ( listener != null)
                    {
                        Log.d(LOGTAG,"Calling the listener method");

                        listener.onItemClick(jidTexView.getText().toString());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ContactListAdapter.OnItemLongClickListener listener = mAdapter.getmOnItemLongClickListener();
                    if ( listener != null)
                    {
                        mAdapter.getmOnItemLongClickListener().onItemLongClick(mContact.getPersistID(),mContact.getJid(),itemView);
                        return true;
                    }
                    return false;
                }
            });

        }

        void bindContact(Contact c)
        {
            mContact = c;
            if ( mContact == null)
            {
                return;
            }
            jidTexView.setText(mContact.getJid());
            subscriptionTypeTextView.setText("NONE_NONE");
            profile_image.setImageResource(R.drawable.ic_user);

        }
    }
    public void onContactCountChange() {
        mContacts = ContactModel.get(mContext).getContacts();
        notifyDataSetChanged();
        Log.d(LOGTAG, "ContactListAdapter knows of the change in messages");
    }
}
