package co.usam.donateblood.chatApp.adapter;

/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import co.usam.donateblood.chatApp.fragment.ChatsFragment;
import co.usam.donateblood.chatApp.fragment.ContactsFragment;

public class TabAdapter extends FragmentStatePagerAdapter {
    private String[] tabTitles = {"CHATS", "CONTACTS"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new ChatsFragment();
                break;
            case 1:
                fragment = new ContactsFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
