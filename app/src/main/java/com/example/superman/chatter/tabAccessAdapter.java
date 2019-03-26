package com.example.superman.chatter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class tabAccessAdapter extends FragmentPagerAdapter
{
    public tabAccessAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;
            case 1:
                GroupsFragment groupsFragment=new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactsFragment contactsFragment=new ContactsFragment();
                return contactsFragment;
            case 3:
                Requestragment requestragment=new Requestragment();
                return requestragment;

                default:
                    return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
               return "Chat";
            case 1:
               return "Group";
            case 2:
                return "Contact";
            case 3:
                return "Request";

            default:
                return null;
        }

    }
}
