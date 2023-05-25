/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 *
 */

package com.example.rtmquickstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmCallEventListener;
import io.agora.rtm.RtmCallManager;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;

public class UsersActivity extends AppCompatActivity {
    // <Vg k="MESS" /> client instance
    private RtmClient mRtmClient;
    // <Vg k="MESS" /> channel instance
    private RtmChannel mRtmChannel;
    private EditText userInput;
    private Button callButton;
    private Button setButton;

    // Agora engine instance
    private RtcEngine agoraEngine;

    private String uid;
    // <Vg k="MESS" /> channel name
    private String channel_name = "1001_1002";
    private String token;
    // Agora App ID
    private String appId = "9b82a4a719e24fe280fe652fe06f2f6b";

    private String rtmTokenString;
    private RtmCallManager rtmCallManager;
    private RemoteInvitation currentRemoteInvitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        userInput  = findViewById(R.id.editTextTextPersonName);
        setButton  = findViewById(R.id.set);
        callButton  = findViewById(R.id.button);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uid = userInput.getText().toString();
                if(uid.equals("1001")){
                    token = "007eJxTYMhk2NdyRH/tLjUTh/PyE/9OmBt2rPEM/+6sMvk+B6MLvx0VGCyTLIwSTRLNDS1TjUzSUo0sDNJSzUyN0lINzNKM0syS/uvmpzQEMjK8E7VkZWRgZGABYhCfCUwyg0kWMMnJYGhgYBgPJIwYGAAu0CIZ";
                    rtmTokenString = "0069b82a4a719e24fe280fe652fe06f2f6bIADoGUgqGKvCsZa1tSi8r2C/axLiqIJhrb8QEGqq+Fz96oGTIMMAAAAAEACixWso7X9wZAEA6APtf3Bk";
                }else if(uid.equals("1002")){
                    token = "007eJxTYLhg2ZLArbtBrfSK1YM0nynP85093MTPRP28FL8ua7GBdKkCg2WShVGiSaK5oWWqkUlaqpGFQVqqmalRWqqBWZpRmlmSmV5+SkMgI0PMYm1GRgZGBhYgBvGZwCQzmGQBk5wMhgYGhvFAwoiBAQCnOyBi";
                    rtmTokenString = "0069b82a4a719e24fe280fe652fe06f2f6bIABGPPUBSp6BDTS00CMx/kvmdaCT8ZdnA++TIdVWNmWHxTvCKVoAAAAAEACixWsoAYBwZAEA6AMBgHBk";
                }
                doLogin();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinLeaveChannel(view);
            }
        });

        setupVoiceSDKEngine();

        // Initialize the <Vg k="MESS" /> client
        try {
            mRtmClient = RtmClient.createInstance(getBaseContext(), appId,
                    new RtmClientListener() {
                        @Override
                        public void onConnectionStateChanged(int state, int reason) {
                            String text = "Connection state changed to " + state + "Reason: " + reason + "\n";
                        }

                        @Override
                        public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {
                        }

                        @Override
                        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {
                        }

                        @Override
                        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
                        }

                        @Override
                        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
                        }

                        @Override
                        public void onTokenExpired() {
                        }

                        @Override
                        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {
                        }

                        @Override
                        public void onMessageReceived(RtmMessage rtmMessage, String peerId) {
                            String text = "Message received from " + peerId + " Message: " + rtmMessage.getText() + "\n";
                        }
                    });

            rtmCallManager = mRtmClient.getRtmCallManager();

            rtmCallManager.setEventListener(new RtmCallEventListener() {
                @Override
                public void onLocalInvitationReceivedByPeer(LocalInvitation localInvitation) {
                    Log.d(TAG, "onLocalInvitationReceivedByPeer() called with: localInvitation = [" + localInvitation + "]");
                    showMessage("onLocalInvitationReceivedByPeer");
                }

                @Override
                public void onLocalInvitationAccepted(LocalInvitation localInvitation, String s) {
                    Log.d(TAG, "onLocalInvitationAccepted() called with: localInvitation = [" + localInvitation + "], s = [" + s + "]");
                    showMessage("onLocalInvitationAccepted");
                }

                @Override
                public void onLocalInvitationRefused(LocalInvitation localInvitation, String s) {
                    Log.d(TAG, "onLocalInvitationRefused() called with: localInvitation = [" + localInvitation + "], s = [" + s + "]");
                    showMessage("onLocalInvitationRefused");
                    // user refused the call
                    // leave the channel to end the call
                    leaveChannel();
                }

                @Override
                public void onLocalInvitationCanceled(LocalInvitation localInvitation) {
                    Log.d(TAG, "onLocalInvitationCanceled() called with: localInvitation = [" + localInvitation + "]");
                    showMessage("onLocalInvitationCanceled");
                }

                @Override
                public void onLocalInvitationFailure(LocalInvitation localInvitation, int i) {
                    Log.d(TAG, "onLocalInvitationFailure() called with: localInvitation = [" + localInvitation + "], i = [" + i + "]");
                    showMessage("onLocalInvitationFailure");
                }

                @Override
                public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {
                    Log.d(TAG, "onRemoteInvitationReceived() called with: remoteInvitation = [" + remoteInvitation + "]");

                    showMessage("onRemoteInvitationReceived");

                    String callerId = remoteInvitation.getCallerId();
                    String channelId = remoteInvitation.getContent();

                    showMessage("call from " + callerId + " for channel: " + channelId);

                    currentRemoteInvitation = remoteInvitation;
                    showCallNotificaiton(callerId, channelId);
                }

                @Override
                public void onRemoteInvitationAccepted(RemoteInvitation remoteInvitation) {
                    Log.d(TAG, "onRemoteInvitationAccepted() called with: remoteInvitation = [" + remoteInvitation + "]");
                    showMessage("onRemoteInvitationAccepted");

                    joinLeaveChannel(null);
                }

                @Override
                public void onRemoteInvitationRefused(RemoteInvitation remoteInvitation) {
                    Log.d(TAG, "onRemoteInvitationRefused() called with: remoteInvitation = [" + remoteInvitation + "]");
                    showMessage("onRemoteInvitationRefused");
                }

                @Override
                public void onRemoteInvitationCanceled(RemoteInvitation remoteInvitation) {
                    Log.d(TAG, "onRemoteInvitationCanceled() called with: remoteInvitation = [" + remoteInvitation + "]");
                    showMessage("onRemoteInvitationCanceled");
                }

                @Override
                public void onRemoteInvitationFailure(RemoteInvitation remoteInvitation, int i) {
                    Log.d(TAG, "onRemoteInvitationFailure() called with: remoteInvitation = [" + remoteInvitation + "], i = [" + i + "]");
                    showMessage("onRemoteInvitationFailure");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        createNotificationChannel();
    }

    private void leaveChannel() {
        if(isJoined){
            agoraEngine.leaveChannel();
            isJoined = false;
            callButton.setText("Join");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if(action.equals("accept_call")){
            showMessage("call being acceptd");
            answerCall(currentRemoteInvitation);
        }else if(action.equals("reject_call")){
            showMessage("call being rejected");
            refuseRemoteInvitation(currentRemoteInvitation);
        }
    }

    private void showCallNotificaiton(String callerId, String channelId) {
        Intent acceptIntent = new Intent(this, UsersActivity.class);
        acceptIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        acceptIntent.setAction("accept_call");
        Intent rejectIntent = new Intent(this, UsersActivity.class);
        rejectIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        rejectIntent.setAction("reject_call");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1234")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Incoming call from " + callerId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(0, "Accept", PendingIntent.getActivity(this, 0, acceptIntent, PendingIntent.FLAG_IMMUTABLE))
                .addAction(1, "Reject", PendingIntent.getActivity(this, 0, rejectIntent, PendingIntent.FLAG_IMMUTABLE));
        builder.setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(0,builder.build());
    }

    private void setupVoiceSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error."  + e.getMessage());
        }
    }


    private void showMessage(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UsersActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinChannel() {
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.autoSubscribeAudio = true;
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine.joinChannel(token, channel_name, Integer.parseInt(uid), options);
    }

    public void joinLeaveChannel(View view) {
        if (isJoined) {

            if(localInvitation != null){
                cancelLocalInvitation();
            }

            agoraEngine.leaveChannel();
            callButton.setText("Join");
        } else {
            joinChannel();
            callButton.setText("Leave");
        }
    }

    private boolean isJoined = false;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote user joining the channel.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(()->showMessage("Remote user joined: " + uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            // Successfully joined a channel
            isJoined = true;
            showMessage("Joined Channel " + channel);
            runOnUiThread(()->showMessage("Waiting for a remote user to join"));

            // notify
            inviteCall("1002", "1001_1002");
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            // Listen for remote users leaving the channel
            showMessage("Remote user offline " + uid + " " + reason);
            if (isJoined) runOnUiThread(()->showMessage("Waiting for a remote user to join"));
        }

        @Override
        public void onLeaveChannel(RtcStats 	stats) {
            // Listen for the local user leaving the channel
            runOnUiThread(()->showMessage("Press the button to join a channel"));
            isJoined = false;
        }
    };

    private static final String TAG = "UsersActivity";

    private LocalInvitation localInvitation;
    void inviteCall(final String peerUid, final String channel) {
        // Creates LocalInvitation
        localInvitation = rtmCallManager.createLocalInvitation(peerUid);
        localInvitation.setContent(channel);
        // Sends call invitation
        rtmCallManager.sendLocalInvitation(localInvitation, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    void cancelLocalInvitation() {
        if (rtmCallManager != null && localInvitation != null) {
            rtmCallManager.cancelLocalInvitation(localInvitation, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {

                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
        }
    }

    void answerCall(final RemoteInvitation invitation) {
        if (rtmCallManager != null && invitation != null) {
            rtmCallManager.acceptRemoteInvitation(invitation, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    showMessage("remote call accepted");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
        }
    }

    void refuseRemoteInvitation(@NonNull RemoteInvitation invitation) {
        if (rtmCallManager != null) {
            rtmCallManager.refuseRemoteInvitation(invitation, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    showMessage("remote call rejected");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
        }
    }

    private void doLogin() {
        mRtmClient.login(rtmTokenString, uid, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showMessage("RTM login success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    private void logout(){
        mRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mRtmClient.release();
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        agoraEngine.leaveChannel();
        logout();

        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;

        }).start();

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = "test_channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1234", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}