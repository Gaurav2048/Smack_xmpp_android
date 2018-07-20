package com.gunjan.xmpp.smack.smackxmpp_01;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gunjan.xmpp.smack.smackxmpp_01.Adapter.ContactListAdapter;
import com.gunjan.xmpp.smack.smackxmpp_01.model.Contact;
import com.gunjan.xmpp.smack.smackxmpp_01.model.ContactModel;
import com.gunjan.xmpp.smack.smackxmpp_01.model.chat;
import com.gunjan.xmpp.smack.smackxmpp_01.model.chatModel;
import com.gunjan.xmpp.smack.smackxmpp_01.xmpp.RoosterConnectionService;

import java.util.List;

public class FloatingListActivity extends AppCompatActivity implements ContactListAdapter.OnItemClickListener, ContactListAdapter.OnItemLongClickListener{
    private RecyclerView contactListRecyclerView;
    private static final String LOGTAG = "ContactListActivity";
    ContactListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton newContactButton = (FloatingActionButton) findViewById(R.id.fab);
        newContactButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        newContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();

            }
        });
        contactListRecyclerView = (RecyclerView) findViewById(R.id.contact_list_recycler_view);
        contactListRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

          mAdapter = new ContactListAdapter(getApplicationContext());
          mAdapter.setmOnItemLongClickListener(this);
        mAdapter.setmOnItemClickListener(this);
        contactListRecyclerView.setAdapter(mAdapter);
    }

    private void addContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_contact_label_text);
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.add_contact_confirm_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOGTAG,"User clicked on OK");

                if(ContactModel.get(getApplicationContext()).addContact(new Contact(input.getText().toString(), Contact.SubscriptionType.NONE)))
                {

                     Log.d(LOGTAG,"Contact added successfully");
                     mAdapter.onContactCountChange();
                }
                else
                {
                    Log.d(LOGTAG,"Could  not add contact");
                }

            }
        });
        builder.setNegativeButton(R.string.add_contact_cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOGTAG,"User clicked on Cancel");
                dialog.cancel();
            }
        });
        builder.show();


    }

    @Override
    public void onItemClick(String contactJid) {
        Log.d(LOGTAG,"Inside contactListActivity The clicked contact is :"+contactJid);

        //Create a Chat in the Chat List table associated with this contact
        List<chat> chats = chatModel.get(getApplicationContext()).getChatsByJid(contactJid);
        if( chats.size() == 0)
        {
            Log.d(LOGTAG, contactJid + " is a new chat, adding them. With timestamp :"+ Utility.getFormattedTime(System.currentTimeMillis()));

            chat chat = new chat(contactJid,"", com.gunjan.xmpp.smack.smackxmpp_01.model.chat.ContactType.ONE_ON_ONE,System.currentTimeMillis(),0);
            chatModel.get(getApplicationContext()).addChat(chat);

            //Inside here we start the chat activity
            Intent intent = new Intent(FloatingListActivity.this
                    ,ChatViewACtivity.class);
            intent.putExtra("contactjid",contactJid);
            startActivity(intent);

            finish();

        }else
        {
            Log.d(LOGTAG, contactJid + " is ALREADY in chat db.Just opening conversation");
            //Inside here we start the chat activity
            Intent intent = new Intent(FloatingListActivity.this
                    ,ChatViewACtivity.class);
            intent.putExtra("contact_jid",contactJid);
            startActivity(intent);

            finish();
        }

    }

    @Override
    public void onItemLongClick(final int uniqueId,final String contactJid, View anchor) {
        PopupMenu popup = new PopupMenu(FloatingListActivity.this,anchor, Gravity.CENTER);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.contact_list_popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch( item.getItemId())
                {
                    case R.id.delete_contact :
                        if(ContactModel.get(getApplicationContext()).deleteContact(uniqueId) )
                        {
                            mAdapter.onContactCountChange();
                            if(RoosterConnectionService.getConnection().removeRosterEntry(contactJid))
                            {
                                Log.d(LOGTAG,contactJid + "Successfully deleted from Roster");
                                Toast.makeText(
                                        FloatingListActivity.this,
                                        "Contact deleted successfully ",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                        break;

                    case R.id.contact_details:

                        Intent intent = new Intent(getApplicationContext(), ChatViewACtivity.class);
                        intent.putExtra("contactjid", contactJid);
                        startActivity(intent);

                        return true;
                }
                return true;
            }
        });
        popup.show();
    }
}
