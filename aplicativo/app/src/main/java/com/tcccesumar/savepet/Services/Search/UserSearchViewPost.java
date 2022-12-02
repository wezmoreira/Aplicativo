package com.tcccesumar.savepet.Services.Search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.tcccesumar.savepet.Main.Profile.ViewComments;
import com.tcccesumar.savepet.R;
import com.tcccesumar.savepet.Utils.Heart;
import com.tcccesumar.savepet.Utils.SquareImageView;
import com.tcccesumar.savepet.Utils.UniversalImageLoader;
import com.tcccesumar.savepet.models.Likes;
import com.tcccesumar.savepet.models.Photo;
import com.tcccesumar.savepet.models.Users;

public class UserSearchViewPost extends AppCompatActivity {

    private static final String TAG = "UserSearchViewPost";
    private SquareImageView mPostImage;
    private TextView mCaption, mUsername, mTimestamp,mTags,mLikes,mtotalComments;
    private ImageView mBackArrow, mComments, mHeartRed, mHeart, mProfileImage;
    String lcaption,ltags,lusername;
    private ProgressBar mProgressBar;
    Photo mPhoto;
    private Heart mheart;
    Boolean mLikedByCurrentUser;
    StringBuilder mUsers;
    Users user;
    String mLikesString = "";
    Integer Commentcount;
    private Users mCurrentUser;
    private GestureDetector mGestureDetector;
    DatabaseReference databaseReference,ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search_view_post);
        mPostImage = (SquareImageView)findViewById(R.id.UserSearchViewPost_postImage);
        mBackArrow = (ImageView)findViewById(R.id.UserSearchViewPost_back);
        mCaption = (TextView)findViewById(R.id.UserSearchViewPost_txt_caption);
        mTags = (TextView)findViewById(R.id.UserSearchViewPost_txt_tags);
        mUsername = (TextView)findViewById(R.id.UserSearchViewPost_username);
        mTimestamp = (TextView)findViewById(R.id.UserSearchViewPost_txt_timePosted);
        mtotalComments = (TextView)findViewById(R.id.UserSearchViewPost_txt_commments);
        mLikes = (TextView)findViewById(R.id.UserSearchViewPost_txt_likes);
        mComments = (ImageView)findViewById(R.id.UserSearchViewPost_img_comments);
        mHeartRed = (ImageView)findViewById(R.id.UserSearchViewPost_img_heart_red);
        mHeart = (ImageView)findViewById(R.id.UserSearchViewPost_img_heart);
        mProfileImage = (ImageView)findViewById(R.id.UserSearchViewPost_user_img);
        mProgressBar = (ProgressBar)findViewById(R.id.UserSearchViewPost_ProgressBar);
        mheart = new Heart(mHeart, mHeartRed);
        mGestureDetector = new GestureDetector(UserSearchViewPost.this, new GestureListener());

        try{
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_Path(), mPostImage, null, "");
            Commentcount = getIntent().getIntExtra("Commentcount",0);
            retrivingData();
            getCurrentUser();
        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }
    }

    private Photo getPhotoFromBundle() {
        Bundle bundle = getIntent().getExtras();
        Log.d(TAG, "getPhotoFromBundle: arguments: " + bundle.getParcelable("Photo"));
        if(bundle != null) {
            return bundle.getParcelable("SearchedUserPhoto");
        }else{
            return null;
        }
    }

    private void getCurrentUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("Users")
                .orderByChild("user_id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    mCurrentUser = singleSnapshot.getValue(Users.class);
                }
                getLikesString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child("Photo")
                    .child(mPhoto.getPhoto_id())
                    .child("likes");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();

                        if(mLikedByCurrentUser &&
                                singleSnapshot.getValue(Likes.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            reference.child("Photo")
                                    .child(mPhoto.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();
                            reference.child("User_Photo")
                                    .child(mPhoto.getUser_id())
                                    .child(mPhoto.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();
                            mheart.toggleLike();
                            getLikesString();
                        }
                        else if(!mLikedByCurrentUser){
                            addNewLike();
                            break;
                        }
                    }
                    if(!dataSnapshot.exists()){
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return true;
        }
    }

    private void getLikesString(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("Photo")
                .child(mPhoto.getPhoto_id())
                .child("likes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child("Users")
                            .orderByChild("user_id")
                            .equalTo(singleSnapshot.getValue(Likes.class).getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                mUsers.append(singleSnapshot.getValue(Users.class).getUsername());
                                mUsers.append(",");
                            }

                            String[] splitUsers = mUsers.toString().split(",");

                            if(mUsers.toString().contains(mCurrentUser.getUsername()+ ",")){
                                mLikedByCurrentUser = true;
                            }else{
                                mLikedByCurrentUser = false;
                            }
                            int length = splitUsers.length;
                            if(length == 1){
                                mLikesString = "Liked by " + splitUsers[0];
                            }
                            else if(length == 2){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + " and " + splitUsers[1];
                            }
                            else if(length == 3){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + " and " + splitUsers[2];
                            }
                            else if(length == 4){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + splitUsers[3];
                            }
                            else if(length > 4){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + (splitUsers.length - 3) + " others";
                            }
                            setupWidgets();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                if(!dataSnapshot.exists()){
                    mLikesString = "";
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addNewLike(){
        Log.d(TAG, "addNewLike: adding new like");

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        String newLikeID = myRef.push().getKey();
        Likes like = new Likes();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.child("Photo")
                .child(mPhoto.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);
        myRef.child("User_Photo")
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);
        mheart.toggleLike();
        getLikesString();
        addLikeNotification(mPhoto.getUser_id(),mPhoto.getPhoto_id());
    }

    private String getTimestampDifference(){
        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = mPhoto.getDate_Created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Toast.makeText(UserSearchViewPost.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            difference = "0";
        }
        return difference;
    }

    private void setupWidgets(){
        String timestampDiff = getTimestampDifference();
        if(!timestampDiff.equals("0")){
            mTimestamp.setText(timestampDiff + " dias atrás");
        }else{
            mTimestamp.setText("Hoje");
        }
        mLikes.setText(mLikesString);
        mCaption.setText(mPhoto.getCaption());

        try {
            if(Commentcount > 0){
                mtotalComments.setText("Visualizar todos " + Commentcount.toString() + " comentários");
            }else{
                mtotalComments.setText("");
            }
        }catch (NullPointerException e){
            Log.e(TAG, "SetupWidget: NullPointerException: " + e.getMessage() );
        }

        mtotalComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to comments through text");

                Intent b = new Intent(UserSearchViewPost.this, ViewComments.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Photo", mPhoto);
                b.putExtra("commentcount",Commentcount);
                b.putExtras(bundle);
                startActivity(b);
            }
        });

        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ViewPostFragment", "onClick: navigating to comments through icon");

                Intent b = new Intent(UserSearchViewPost.this, ViewComments.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Photo", mPhoto);
                b.putExtras(bundle);
                b.putExtra("commentcount",Commentcount);
                startActivity(b);
            }
        });

        if(mLikedByCurrentUser){
            mHeart.setVisibility(View.GONE);
            mHeartRed.setVisibility(View.VISIBLE);
            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }
        else{
            mHeart.setVisibility(View.VISIBLE);
            mHeartRed.setVisibility(View.GONE);
            mHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }
    }

    private void retrivingData(){
        String userid = mPhoto.getUser_id();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        ref = FirebaseDatabase.getInstance().getReference("User_Photo").child(userid).child(mPhoto.getPhoto_id());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                Glide.with(UserSearchViewPost.this)
                        .load(user.getProfilePhoto())
                        .into(mProfileImage);
                lcaption = mPhoto.getCaption();
                ltags = mPhoto.getTags();
                lusername = user.getUsername();
                mTags.setText(ltags);
                mUsername.setText(lusername);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void addLikeNotification(String userid,String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications");
        HashMap<String, Object> hashMappp = new HashMap<>();
        hashMappp.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMappp.put("postid", postid);
        hashMappp.put("text", "curtiu seu post");
        hashMappp.put("ispost", true);
        reference.child(userid).push().setValue(hashMappp);
    }
}