package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Utils.Αdapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public int getFragmentPosition(int itemID){
        if(itemID == R.id.navigation_dashboard) return 0;
        else if(itemID == R.id.navigation_receipts){
            for (int i=0; i<mFragmentTitleList.size(); i++){
                if(mFragmentTitleList.get(i).
                        equalsIgnoreCase("All Receipts")) return i;
            }
        }
        else if(itemID == R.id.navigation_messages){
            for (int i=0; i<mFragmentTitleList.size(); i++){
                if(mFragmentTitleList.get(i).
                        equalsIgnoreCase("Messages")) return i;
            }
        }
        else if(itemID == R.id.navigation_opening_hours){
            for (int i=0; i<mFragmentTitleList.size(); i++){
                if(mFragmentTitleList.get(i).
                        equalsIgnoreCase("Opening hours")) return i;
            }
        }
        return 0;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public CharSequence getSelectedFragmentTitle(int position){
        return mFragmentTitleList.get(position);
    }

}
