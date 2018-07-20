package com.gunjan.xmpp.smack.smackxmpp_01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gunjan.xmpp.smack.smackxmpp_01.Adapter.ChatListAdapter;
import com.gunjan.xmpp.smack.smackxmpp_01.Adapter.ChatmessageAdapter;
import com.gunjan.xmpp.smack.smackxmpp_01.model.CgatMessage;
import com.gunjan.xmpp.smack.smackxmpp_01.model.CgatMessageModel;
import com.gunjan.xmpp.smack.smackxmpp_01.model.Contact;
import com.gunjan.xmpp.smack.smackxmpp_01.model.ContactModel;
import com.gunjan.xmpp.smack.smackxmpp_01.model.chat;
import com.gunjan.xmpp.smack.smackxmpp_01.xmpp.RoosterConnectionService;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

public class ChatViewACtivity extends AppCompatActivity implements
ChatmessageAdapter.OnInformRecyclerViewToScrollDownListner,
ChatmessageAdapter.OnItemLongClickListener
{
RecyclerView recyclerView_chat_message;
EditText message_editText;
BroadcastReceiver mReceiveMessageBroadcastReceiver;
private static final String LOGTAG = "ChatViewActivity" ;
ImageView send_button;
ChatmessageAdapter adapter;
String counterPartJid;
    private View snackbar;
    private View snackbarStranger;

    private TextView snackBarActionAccept;
    private TextView snackBarActionDeny;

    private TextView snackBarStrangerAddContact;
    private TextView snackBarStrangerBlock;
    private chat.ContactType chatType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view_activity);

        snackbar = findViewById(R.id.snackbar);
        snackbarStranger = findViewById(R.id.snackbar_stranger);


        Intent intent = getIntent();
        counterPartJid = intent.getStringExtra("contactjid");
        chatType = (chat.ContactType)intent.getSerializableExtra("chat_type");
        setTitle(counterPartJid);
        recyclerView_chat_message = (RecyclerView) findViewById(R.id.recyclerview_chat);

        recyclerView_chat_message.setLayoutManager(new LinearLayoutManager(this));
          adapter = new ChatmessageAdapter(getApplicationContext(), counterPartJid);
        adapter.setOnItemLongClickListener(this);
        adapter.setOnInformRecyclerViewToScrollDownListner(this);
        recyclerView_chat_message.setAdapter(adapter);

        message_editText= (EditText) findViewById(R.id.message_editText);
        send_button = (ImageView) findViewById(R.id.send_button);

send_button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        RoosterConnectionService.getConnection().sendMessage(
                message_editText.getText().toString(), counterPartJid);
                message_editText.getText().clear();;
adapter.onMEssageAdded();

    }
});

        Contact contactCheck = ContactModel.get(getApplicationContext()).getContactByJidString(counterPartJid);

        if(!ContactModel.get(getApplicationContext()).isContactStranger(counterPartJid))
        {
            if(contactCheck.isOnlineStatus())
            {
                Log.d(LOGTAG,counterPartJid + "is ONLINE");
                send_button.setImageDrawable(ContextCompat.getDrawable(ChatViewACtivity.this,R.drawable.ic_send_text_online));
            }else
            {
                send_button.setImageDrawable(ContextCompat.getDrawable(ChatViewACtivity.this,R.drawable.ic_send_text_offline));

                Log.d(LOGTAG,counterPartJid + "is OFFLINE");
            }

        }



        if( !ContactModel.get(getApplicationContext()).isContactStranger(counterPartJid))
        {
            snackbarStranger.setVisibility(View.GONE);
            Log.d(LOGTAG,counterPartJid + " is not a stranger");
            Contact contact = ContactModel.get(this).getContactByJidString(counterPartJid);
            Log.d(LOGTAG,"We got a contact with JID :" + contact.getJid());

            if( contact.isPendingFrom())
            {
                Log.d(LOGTAG," Your subscription to "+ contact.getJid() + " is in the FROM direction is in pending state. Should show the snackbar");
                int paddingBottom = getResources().getDimensionPixelOffset(R.dimen.chatview_recycler_view_padding_huge);
                recyclerView_chat_message.setPadding(0,0,0,paddingBottom);
                snackbar.setVisibility(View.VISIBLE);
            }else
            {
                int paddingBottom = getResources().getDimensionPixelOffset(R.dimen.chatview_recycler_view_padding_normal);
                recyclerView_chat_message.setPadding(0,0,0,paddingBottom);
                snackbar.setVisibility(View.GONE);
            }

        }else
        {
            if(chatType == chat.ContactType.STRANGER)
            {
                Log.d(LOGTAG,"Chat type is STRANGER");
                //We fall here if this was a subscription request from a stranger
                int paddingBottom = getResources().getDimensionPixelOffset(R.dimen.chatview_recycler_view_padding_huge);
                recyclerView_chat_message.setPadding(0,0,0,paddingBottom);
                snackbar.setVisibility(View.VISIBLE);
                snackbarStranger.setVisibility(View.GONE);

            }else
            {
                Log.d(LOGTAG,counterPartJid + " is a stranger. Hiding snackbar");
                int paddingBottom = getResources().getDimensionPixelOffset(R.dimen.chatview_recycler_view_padding_huge);
                recyclerView_chat_message.setPadding(0,0,0,paddingBottom);
                snackbarStranger.setVisibility(View.VISIBLE);
                snackbar.setVisibility(View.GONE);

            }


        }
        snackBarActionAccept = (TextView) findViewById(R.id.snackbar_action_accept);
        snackBarActionAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //User accepts presence subscription

                //Add Them to your roster if they are strangers
                if (ContactModel.get(getApplicationContext()).isContactStranger(counterPartJid))
                {
                    if(ContactModel.get(getApplicationContext()).addContact(new Contact(counterPartJid, Contact.SubscriptionType.NONE)))
                    {
                        Log.d(LOGTAG,"Previously stranger contact "+counterPartJid + "now successfully added to local Roster");
                    }
                }
                Log.d(LOGTAG," Accept presence subscription from :" + counterPartJid);
                if(RoosterConnectionService.getConnection().subscribed(counterPartJid))
                {
                    ContactModel.get(getApplicationContext()).updateContactSubscriptionOnSendSubscribed(counterPartJid);
                    Toast.makeText(ChatViewACtivity.this,"Subscription from "+counterPartJid + "accepted",
                            Toast.LENGTH_LONG).show();
                }
                snackbar.setVisibility(View.GONE);

            }
        });


        snackBarActionDeny = (TextView) findViewById(R.id.snackbar_action_deny);
        snackBarActionDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //User denies presence subscription
                Log.d(LOGTAG," Deny presence subscription from :" + counterPartJid);
                if(RoosterConnectionService.getConnection().unsubscribed(counterPartJid))
                {
                    ContactModel.get(getApplicationContext()).updateContactSubscriptionOnSendSubscribed(counterPartJid);

                    //No action required in the Contact Model regarding subscriptions
                    Toast.makeText(getApplicationContext(),"Subscription Rejected",Toast.LENGTH_LONG).show();
                }
                snackbar.setVisibility(View.GONE);

            }
        });

        snackBarStrangerAddContact= findViewById(R.id.snackbar_action_accept_stranger);
        snackBarStrangerAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContactModel.get(getApplicationContext()).addContact(new Contact(counterPartJid, Contact.SubscriptionType.NONE)))
                {
                    if(RoosterConnectionService.getConnection().addContactToRoster(counterPartJid))
                    {
                        Log.d(LOGTAG,counterPartJid + " successfully added to remote roster");
                        snackbarStranger.setVisibility(View.GONE);
                    }
                }

            }
        });
        snackBarStrangerBlock = findViewById(R.id.snackbar_action_deny_stranger);
        snackBarStrangerBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatViewACtivity.this,"Feature not implemented yet",Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    protected void onPause() {
unregisterReceiver(mReceiveMessageBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        adapter.informRecyclerViewToScrollDown();
        mReceiveMessageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action)
                {
                    case Constants.BroadCastMessages.UI_NEW_MESSAGE_FLAG:
                        adapter.onMEssageAdded();
                        return;
                    case Constants.BroadCastMessages.UI_ONLINE_STATUS_CHANGE:
                        String contactJid = intent.getStringExtra(Constants.ONLINE_STATUS_CHANGE_CONTACT);
                        Log.d(LOGTAG," Online status change for "+contactJid + " received in ChatViewActivity");

                        if(!ContactModel.get(getApplicationContext()).isContactStranger(counterPartJid))
                        {
                            if(counterPartJid.equals(contactJid))
                            {
                                Contact mContact = ContactModel.get(getApplicationContext()).getContactByJidString(contactJid);
                                if(mContact.isOnlineStatus())
                                {
                                    Log.d(LOGTAG,"From Chat View, user " +contactJid + " has come ONLINE");
                                    send_button.setImageDrawable(ContextCompat.getDrawable(ChatViewACtivity.this,R.drawable.ic_send_text_online));

                                }else
                                {
                                    Log.d(LOGTAG,"From Chat View, user " +contactJid + " has gone OFFLINE");
                                    send_button.setImageDrawable(ContextCompat.getDrawable(ChatViewACtivity.this,R.drawable.ic_send_text_offline));

                                }
                            }

                        }
                        return;
                }

            }
        };

        IntentFilter filter = new IntentFilter(Constants.BroadCastMessages.UI_NEW_MESSAGE_FLAG);
        registerReceiver(mReceiveMessageBroadcastReceiver,filter);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat_menu, menu);
return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.contact_detail_chat_view){
            Intent intent = new Intent(getApplicationContext(), ContactDetailActivity.class);
            intent.putExtra("contactjid", counterPartJid);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInformRecyclerViewToScrollDown(int size) {
        recyclerView_chat_message.scrollToPosition(size-1);
    }

    @Override
    public void onItemLongClick(final int uniqueId,final  View anchor) {

        PopupMenu popup = new PopupMenu(ChatViewACtivity.this,anchor, Gravity.CENTER);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.chat_view_popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch( item.getItemId())
                {
                    case R.id.delete_message :
                        if(CgatMessageModel.get(getApplicationContext()).deleteMessage(uniqueId) )
                        {
                            adapter.onMEssageAdded();
                            Toast.makeText(
                                    ChatViewACtivity.this,
                                    "Message deleted successfully ",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();


    }
}
