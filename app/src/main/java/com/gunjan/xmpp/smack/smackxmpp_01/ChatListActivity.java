package com.gunjan.xmpp.smack.smackxmpp_01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.gunjan.xmpp.smack.smackxmpp_01.Adapter.ChatListAdapter;
import com.gunjan.xmpp.smack.smackxmpp_01.model.chat;
import com.gunjan.xmpp.smack.smackxmpp_01.model.chatModel;
import com.gunjan.xmpp.smack.smackxmpp_01.xmpp.RoosterConnectionService;

public class ChatListActivity extends AppCompatActivity implements  ChatListAdapter.OnItemClickListner, ChatListAdapter.OnItemLongClickListener{
RecyclerView recyclerView;
FloatingActionButton actionButton;
    ChatListAdapter mAdapter;
    private BroadcastReceiver mBroadcastReceiver;

    protected  static final int REQUEST_EXCEMPT_OP =188;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
    recyclerView = findViewById(R.id.recyclerview);

        boolean logged_in_state = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("xmpp_logged_in",false);
        if(!logged_in_state)
        {
            Log.d("hjbg ","Logged in state :"+ logged_in_state );
            Intent i = new Intent(ChatListActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        }else {
            if(!Utility.isServiceRunning(RoosterConnectionService.class, getApplicationContext())){
                Log.d("LOGCAT","Service not running, starting it ...");
                Intent i1 = new Intent(this,RoosterConnectionService.class);
                startService(i1);
            }else
            {
                Log.d("LOGTAG" ,"The service is already running.");
            }
        }

    actionButton = (FloatingActionButton) findViewById(R.id.new_conversation_floating_button);
recyclerView.setLayoutManager(new LinearLayoutManager(this));
  mAdapter =new ChatListAdapter(getApplicationContext());
        mAdapter.setmOnItemClickListner(this);
        mAdapter.setOnItemLongClickListener(this);
    recyclerView.setAdapter(mAdapter);

    actionButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), FloatingListActivity.class));
        }
    });
        boolean deniedBatteryOptimizationRequest = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("denied_battery_optimization_request",false);

        boolean userHasGoneThroughBatteryOptimizations = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("user_has_gone_through_battery_optimizations",false);

        if( !deniedBatteryOptimizationRequest && !userHasGoneThroughBatteryOptimizations)
        {
            requestBatteryOptimizationsFavor();
        }else
        {
            Log.d("LOGTAG","User has chosen to opt out of battery optimizations excemption. DONT'T BOTHER THEM AGAIN.");
        }
     }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_me_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void requestBatteryOptimizationsFavor()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle("Battery optimization request");
            builder.setMessage("Battery optimizations are needed to make the app work right");

            // Set up the buttons
            builder.setPositiveButton(R.string.ignore_bat_allow, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("LOGTAG","User clicked on OK");

                    Intent intent = new Intent();
                    String packageName = getPackageName();

                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivityForResult(intent,REQUEST_EXCEMPT_OP);


                }
            });
            builder.setNegativeButton(R.string.add_contact_cancel_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("LOGTAG","User clicked on Cancel");
                    //Save the user's choice and never bother them again.
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    prefs.edit().putBoolean("denied_battery_optimization_request",true).commit();
                    dialog.cancel();
                }
            });
            builder.show();
        }

    }

    @Override
    protected void onPause() {
        unregisterReceiver(mBroadcastReceiver);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action)
                {
                    case Constants.BroadCastMessages.UI_NEW_CHAT_ITEM:
                        mAdapter.onChatCountChange();
                        return;
                }

            }
        };

        IntentFilter filter = new IntentFilter(Constants.BroadCastMessages.UI_NEW_CHAT_ITEM);
        registerReceiver(mBroadcastReceiver,filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK)
        {
            if ( requestCode == REQUEST_EXCEMPT_OP)
            {
                Log.d("LOGTAG","User wants to excempt app from BATTERY OPTIMIZATIONS");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent();
                    String packageName = getPackageName();
                    PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
                    if (pm.isIgnoringBatteryOptimizations(packageName))
                    {
                        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        startActivity(intent);
                    }

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    prefs.edit().putBoolean("user_has_gone_through_battery_optimizations",true).commit();

                }
            }

        }else
        {
            if ( requestCode == REQUEST_EXCEMPT_OP)
            {
                Log.d("LOGTAG","Result code is cancel");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

if(item.getItemId()==R.id.me){
    startActivity(new Intent(getApplicationContext(), Mectivity.class));
}else if (item.getItemId()== R.id.add_chats){
   boolean bool = chatModel.get(getApplicationContext()).addChat(new chat("user@server.com", "the last message", chat.ContactType.ONE_ON_ONE, 11,11));
    if(bool){
        Log.e( "onOptionsItemSelected: ","contact added chat " );
    }
}

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onItemClick(String contactJid, chat.ContactType contactType) {
        Intent itn= new Intent(getApplicationContext(), ChatViewACtivity.class);
        itn.putExtra("contactjid", contactJid);
        itn.putExtra("chat_type", contactType);
        startActivity(itn);
    }

    @Override
    public void onItemLongClick(final String contactJid,final int chatUniqueId, View anchor) {
        PopupMenu popup = new PopupMenu(ChatListActivity.this,anchor, Gravity.CENTER);
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
                        if(chatModel.get(getApplicationContext()).deleteChat(chatUniqueId) )
                        {
                            mAdapter.onChatCountChange();
                            Toast.makeText(
                                    ChatListActivity.this,
                                    "Chat deleted successfully ",
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
