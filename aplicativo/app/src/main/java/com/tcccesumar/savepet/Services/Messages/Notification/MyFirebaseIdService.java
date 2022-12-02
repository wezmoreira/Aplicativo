package com.tcccesumar.savepet.Services.Messages.Notification;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.e("TOKEN",mToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    private void updateToken(String refreshToken)
    {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        Token token=new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

}
