package com.fragile.kioku2;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorFragment extends Fragment {

    private TextView error;

    public ErrorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error_layout, container, false);

        error = (TextView) view.findViewById(R.id.errorTextView);

        error.setText(getArguments().getString("MESSAGE"));

        return view;
    }


}
