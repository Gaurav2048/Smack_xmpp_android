package com.gunjan.xmpp.smack.smackxmpp_01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button =   findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        XMPPTCPConnectionConfiguration connectionConfiguration = null;

                        try {
                            connectionConfiguration = XMPPTCPConnectionConfiguration.builder()
                                                             .setUsernameAndPassword("overlord1","overlordactual")
                                                             .setXmppDomain("chinwag.im")
                                                             .setKeystoreType(null)
                                                             .build();

                        } catch (XmppStringprepException e) {
                            e.printStackTrace();
                        }


                        AbstractXMPPConnection connection = null;
                        connection = new XMPPTCPConnection(connectionConfiguration);
                        connection.addConnectionListener(new ConnectionListener() {
                            @Override
                            public void connected(XMPPConnection connection) {
                                Log.e( "connected: ","connected" );
                            }

                            @Override
                            public void authenticated(XMPPConnection connection, boolean resumed) {
                                Log.e( "connected: ","authenticated" );

                            }

                            @Override
                            public void connectionClosed() {
                                Log.e( "connected: ","connectionClosed" );

                            }

                            @Override
                            public void connectionClosedOnError(Exception e) {
                                Log.e( "connected: ","connectionClosedOnError"+e.getCause()+" "+e.getMessage() );

                            }

                            @Override
                            public void reconnectionSuccessful() {
                                Log.e( "connected: ","authenticated" );

                            }

                            @Override
                            public void reconnectingIn(int seconds) {
                                Log.e( "connected: ",seconds+"reconnectingIn" );

                            }

                            @Override
                            public void reconnectionFailed(Exception e) {
                                Log.e( "connected: ","reconnectionFailed" );

                            }
                        });
                        ChatManager chatManager = ChatManager.getInstanceFor(connection);
                        chatManager.addIncomingListener(new IncomingChatMessageListener() {
                            @Override
                            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

                                Log.e( "newIncomingMessage: ",message.getBody()+" " );

                            }
                        });
                        try {
                            connection.connect().login();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        } catch (SmackException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                };
                Thread connecttionThread = new Thread(runnable);
                connecttionThread.start();

            }
        });



    }
}
