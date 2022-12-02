package com.tcccesumar.savepet.Services.Messages.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tcccesumar.savepet.Services.Messages.Fragments.ChatsFragment;
import com.tcccesumar.savepet.Services.Messages.Fragments.FriendsFragment;

public class PageAdapter extends FragmentPagerAdapter {


    public PageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "CHAT";
            case 1:
                return "AMIGOS";
            default:
                return null;
        }
    }
}
