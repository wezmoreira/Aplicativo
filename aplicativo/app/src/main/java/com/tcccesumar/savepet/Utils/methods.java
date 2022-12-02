package com.tcccesumar.savepet.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

public class methods {

    //******************************MÉTODO PARA OBTER CONTAGEM DE POSTS***********************
    public int getImagecount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds : dataSnapshot.child("User_Photo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
    }
}
