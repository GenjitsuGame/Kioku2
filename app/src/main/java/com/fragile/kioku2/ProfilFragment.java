package com.fragile.kioku2;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import fm.last.api.User;

public class ProfilFragment extends Fragment {

    private ImageView image;

    private TextView username;

    private TextView playcount;

    private TextView joinDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile, container, false);

        username = (TextView) view.findViewById(R.id.txtName);
        playcount = (TextView) view.findViewById(R.id.txtNumber);
        joinDate = (TextView) view.findViewById(R.id.txtDate);
        User user = null;
        try {
            user = new LastFmOperations.UserInfo().execute(KiokuApplication.getSession().getName()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (user != null) {
            username.setText(user.getName());
            playcount.setText(user.getPlaycount() + " scrobbles");
            Calendar date = Calendar.getInstance();
            date.setTime(user.getJoinDate());
            joinDate.setText("à rejoint le : " + date.get(Calendar.DAY_OF_MONTH) + "/" + date.get(Calendar.MONTH) + 1 + "/" + date.get(Calendar.YEAR));
        }
        return view;

    }
}