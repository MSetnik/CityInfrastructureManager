package com.example.cityinfrastrukturemanager.Adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.cityinfrastrukturemanager.Fragment.RijeseniIspadiFragment;
import com.example.cityinfrastrukturemanager.Fragment.TrenutniIspadiFragment;
import com.example.cityinfrastrukturemanager.Model.IspadPrikaz;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragmentList;
    private ArrayList<String> fragmentTitle;
    public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment>list, ArrayList<String> title) {
        super(fm);
        fragmentList = list;
        fragmentTitle = title;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
