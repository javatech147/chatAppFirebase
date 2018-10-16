package com.waytojava.ichatapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.waytojava.ichatapp.R;
import com.waytojava.ichatapp.activity.MainActivity;
import com.waytojava.ichatapp.adapter.ChatListAdapter;
import com.waytojava.ichatapp.firebase.FirebaseUtils;
import com.waytojava.ichatapp.firebase.MyFirebaseUser;
import com.waytojava.ichatapp.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private static final String TAG = ChatFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private MainActivity mainActivity;
    private ArrayList<MyFirebaseUser> userArrayList;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        getUserListFromFirebaseDatabase();
        recyclerView = view.findViewById(R.id.recycler_view_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(userArrayList, mainActivity);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void getUserListFromFirebaseDatabase() {

        Utils.showProgressDialog(mainActivity);
        userArrayList = new ArrayList<>();
        final DatabaseReference databaseRootReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersReference = databaseRootReference.child(FirebaseUtils.USERS);
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Utils.log(TAG, "Data SnapShot : " + dataSnapshot);
                Utils.dismissProgressDialog();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String value = dataSnapshot1.child(FirebaseUtils.CREDENTIALS).getValue().toString();
                    MyFirebaseUser myFirebaseUser = dataSnapshot1.child(FirebaseUtils.CREDENTIALS).getValue(MyFirebaseUser.class);
                    userArrayList.add(myFirebaseUser);
                    Utils.log(TAG, "Value -- : " + value);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
