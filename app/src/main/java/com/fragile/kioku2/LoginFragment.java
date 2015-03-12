package com.fragile.kioku2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.fragile.kioku2.LoginFragment.LoginFragmentListener} interface
 * to handle interaction fragment_events_layout.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private EditText username = null;
    private EditText password = null;
    private TextView attempts;
    private Button login;
    private CheckBox rememberMe;

    public static final int LOGIN_ACTIVITY = 1;

    private LoginFragmentListener mListener;

    public interface LoginFragmentListener {
        void onLoggingResult(boolean isLogged);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_layout, container, false);
        username = (EditText) view.findViewById(R.id.username_edittext);
        password = (EditText) view.findViewById(R.id.password_edittext);
        username.setText("GenjitsuGame");
        password.setText("androidkioku");
        login = (Button) view.findViewById(R.id.login_button);
        rememberMe = (CheckBox) view.findViewById(R.id.rememberme_checkBox);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context context = getActivity();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameString = username.getText().toString();
                String passwordString = password.getText().toString();
                try {
                    if (new LastFmOperations.Connection().execute(usernameString, passwordString).get()) {

                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("LOGIN", KiokuApplication.getSession().getName());
                        intent.putExtra("SESSION_TOKEN", KiokuApplication.getSession().getKey());
                        intent.putExtra("SUBSCRIBER", KiokuApplication.getSession().getSubscriber());

                        startActivityForResult(intent, LOGIN_ACTIVITY);

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }


        });
    }

    public void onLoggingResult(Boolean isLogged) {
        if (mListener != null) {
            mListener.onLoggingResult(isLogged);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                onLoggingResult(true);
            } else {
                onLoggingResult(false);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LoginFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
