package com.gunjan.xmpp.smack.smackxmpp_01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.gunjan.xmpp.smack.smackxmpp_01.xmpp.RoosterConnectionService;

public class Mectivity extends AppCompatActivity {
    private TextView connectionStatusTextView;
     private BroadcastReceiver braBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mectivity);
        String status;
        connectionStatusTextView = (TextView) findViewById(R.id.connection_status);
        RoosterConnection connection = RoosterConnectionService.getConnection();
if(connection!=null){
    status = connection.getConnectionStateString();
    connectionStatusTextView.setText(status);
}

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(braBroadcastReceiver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        braBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                switch (action)
                {
                    case Constants.BroadCastMessages.UI_CONNECTION_STATUS_CHANGE_FLAG:

                        String status = intent.getStringExtra(Constants.UI_CONNECTION_STATUS_CHANGE);
                        connectionStatusTextView.setText(status);
                        break;
                }



            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BroadCastMessages.UI_CONNECTION_STATUS_CHANGE_FLAG);
        this.registerReceiver(braBroadcastReceiver, filter);
    }
    }

