package com.cmpundhir.cm.cmsattendenceapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cmpundhir.cm.cmsattendenceapp.R;
import com.cmpundhir.cm.cmsattendenceapp.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    TextView t1,t2;
    Button b1;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Constants.ATTENDENCE);
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        t1 = view.findViewById(R.id.t1);
        t2 = view.findViewById(R.id.t2);
        b1 = view.findViewById(R.id.b1);
        t1.setText("Welcome "+pref.getString(Constants.NAME,""));
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markAttendence();
                b1.setEnabled(false);
                b1.setText("You are marked for the day");
            }
        });
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());
        t2.setText(currentDateandTime);
        String[] arr = currentDateandTime.split("/");
        String year,mon,day;
        year = arr[0];
        mon = arr[1];
        day = arr[2];
        int today = Integer.parseInt(year+mon+day);
        int prev = pref.getInt(Constants.TODAY_ATTEND,0);
        if(today>prev){
            b1.setEnabled(true);
        }else{
            b1.setEnabled(false);
            b1.setText("You are marked for the day");
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            pref = context.getSharedPreferences("prefs",MODE_PRIVATE);
            editor = pref.edit();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void markAttendence(){
        String year,mon,day,course,uid,time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        String[] arr = currentDateandTime.split("/");
        year = arr[0];
        mon = arr[1];
        day = arr[2];
        time = arr[3];
        course = pref.getString(Constants.COURSE,"Not found");
        uid = FirebaseAuth.getInstance().getUid();
        myRef.child(year+"/"+mon+"/"+day+"/"+course+"/"+uid).setValue(time);
        editor.putInt(Constants.TODAY_ATTEND,Integer.parseInt(year+mon+day));
        editor.commit();

    }
}
