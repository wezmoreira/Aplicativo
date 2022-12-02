package com.tcccesumar.savepet.Main.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.tcccesumar.savepet.R;
import com.tcccesumar.savepet.Utils.Heart;
import com.tcccesumar.savepet.Utils.SquareImageView;
import com.tcccesumar.savepet.Utils.UniversalImageLoader;
import com.tcccesumar.savepet.models.Likes;
import com.tcccesumar.savepet.models.Photo;
import com.tcccesumar.savepet.models.Users;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";

    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }



    private SquareImageView mPostImage;
    private TextView mCaption, mUsername, mTimestamp,mTags,mLikes,mtotalComments;
    private ImageView mBackArrow, mComments, mHeartRed, mHeart, mProfileImage,moption,msend,mRemove;
    String lcaption,ltags,lusername;
    private ProgressBar mProgressBar;

    Photo mPhoto;
    private Heart mheart;
    Boolean mLikedByCurrentUser;
    StringBuilder mUsers;
    Users user;
    String mLikesString = "";

    private GestureDetector mGestureDetector;

    DatabaseReference databaseReference,ref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_posts_viewer, container, false);
        mPostImage = (SquareImageView) view.findViewById(R.id.post_imagee);
        mBackArrow = (ImageView) view.findViewById(R.id.back_from_post_viewer);
        mCaption = (TextView) view.findViewById(R.id.txt_caption);
        mTags = (TextView) view.findViewById(R.id.txt_tags);
        mUsername = (TextView) view.findViewById(R.id.username);
        mTimestamp = (TextView) view.findViewById(R.id.txt_timePosted);
        mtotalComments = (TextView) view.findViewById(R.id.txt_commments);
        mLikes = (TextView) view.findViewById(R.id.txt_likes);
        mComments = (ImageView) view.findViewById(R.id.img_comments);
        mHeartRed = (ImageView) view.findViewById(R.id.img_heart_red);
        mHeart = (ImageView) view.findViewById(R.id.img_heart);
        mProfileImage = (ImageView) view.findViewById(R.id.user_img);
        //moption = (ImageView) view.findViewById(R.id.option);
        //msend = (ImageView) view.findViewById(R.id.img_send);
        mProgressBar = (ProgressBar) view.findViewById(R.id.viewpostProgressBar);


        mheart = new Heart(mHeart, mHeartRed);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());



        try{
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_Path(), mPostImage, null, "");
            retrivingData();
            getLikesString();

        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }

        return view;
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

                            if(mUsers.toString().contains(user.getUsername()+ ",")){
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
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
    private void addNewLike(){

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
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mPhoto.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);

        mheart.toggleLike();
        getLikesString();
        addLikeNotification(mPhoto.getUser_id(),mPhoto.getPhoto_id());
    }

    /**
     * recupera a foto do pacote recebido do profilefragment
     * @return
     */
    private Photo getPhotoFromBundle(){

        Bundle bundle = this.getArguments();
        Log.d(TAG, "getPhotoFromBundle: arguments: " + bundle.getParcelable("PHOTO"));

        if(bundle != null) {
            return bundle.getParcelable("PHOTO");
        }else{
            return null;
        }
    }

    /**
     * Retorna uma string representando o número de dias atrás em que a postagem foi feita
     * @return
     */
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
            Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
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

        if(mPhoto.getComments().size() > 0){
            mtotalComments.setText("Ver todos " + mPhoto.getComments().size() + " comentários");
        }else{
            mtotalComments.setText("");
        }

        mtotalComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: navigating to comments through text");

                Intent b = new Intent(getActivity(), ViewComments.class);
                //Criar o pacote
                Bundle bundle = new Bundle();
                //Adiciona seus dados do método getFactualResults ao pacote
                bundle.putParcelable("Photo", mPhoto);
                b.putExtra("commentcount",mPhoto.getComments().size());
                //Adiciona o pacote a intent
                b.putExtras(bundle);
                startActivity(b);

            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ViewPostFragment", "onClick: navigating to profile");

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ViewPostFragment", "onClick: navigating to comments through icon");

                Intent b = new Intent(getActivity(), ViewComments.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Photo", mPhoto);

                b.putExtras(bundle);
                b.putExtra("commentcount",mPhoto.getComments().size());
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

        // Recuperando dados
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        ref = FirebaseDatabase.getInstance().getReference("User_Photo").child(userid).child(mPhoto.getPhoto_id());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                Glide.with(ViewPostFragment.this)
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
    public void onResume() {

        super.onResume();
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    getActivity().getSupportFragmentManager().popBackStack();
                }
                return true;
            }
        });
    }

    private void addLikeNotification(String userid,String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications");

        HashMap<String, Object> hashMappp = new HashMap<>();
        hashMappp.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMappp.put("postid", postid);
        hashMappp.put("text", "liked your post");
        hashMappp.put("ispost", true);
        reference.child(userid).push().setValue(hashMappp);

    }
}
