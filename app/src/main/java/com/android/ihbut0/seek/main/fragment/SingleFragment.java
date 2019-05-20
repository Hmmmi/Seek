package com.android.ihbut0.seek.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SingleFragment extends Fragment {

    public static final String BUNDLE_TITLE = "title";
    private String mTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = getArguments();
        if(bundle != null)
            mTitle = bundle.getString(BUNDLE_TITLE);

        TextView textView = new TextView(getActivity());
        textView.setText(mTitle);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }


    /**
     * 获取实例
     * @param title
     * @return
     */
    public static Fragment newInstance(String title)
    {
        Fragment fragment = new SingleFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE,title);
        fragment.setArguments(bundle);

        switch (title){
            case "Chat":
                fragment = new ChatListFragment();break;

//            case "Near":
//                fragment = new NearbyFragment();break;

            case "Friend":
                fragment = new FriendFragment();break;

            case "Me":
                fragment = new MineFragment();break;

            default:
                break;
        }
        return fragment;
    }


}
