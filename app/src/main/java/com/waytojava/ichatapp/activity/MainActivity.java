package com.waytojava.ichatapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.waytojava.ichatapp.R;
import com.waytojava.ichatapp.adapter.TabLayoutAdapter;
import com.waytojava.ichatapp.fragment.CallFragment;
import com.waytojava.ichatapp.fragment.ChatFragment;
import com.waytojava.ichatapp.fragment.ContactFragment;
import com.waytojava.ichatapp.sharedpreferences.MyPreferences;
import com.waytojava.ichatapp.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabLayoutAdapter adapter;
    private Context context;
    private MyPreferences myPreferences;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getId();
        context = this;
        myPreferences = new MyPreferences(this);
        myPreferences.saveBoolean(MyPreferences.IS_USER_ACCESS_HOME_PAGE, true);

        mAuth = FirebaseAuth.getInstance();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth != null) {
                    if (firebaseAuth.getCurrentUser() != null) {
                        String email = firebaseAuth.getCurrentUser().getEmail();
                        Utils.log(TAG, "User is logged In with email : " + email);
                    } else {
                        Utils.log(TAG, "User is not logged In");
                    }
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth != null && authStateListener != null) {
            mAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAuth != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void getId() {
        toolbar = findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        TextView tvToolbarText = toolbar.findViewById(R.id.tool_bar_text);
        tvToolbarText.setText(getString(R.string.app_name));

        ImageView ivBackImage = toolbar.findViewById(R.id.toolbar_back_image);
        //ivBackImage.setVisibility(View.VISIBLE);
        ivBackImage.setOnClickListener(this);

        tabLayout = findViewById(R.id.tab_layout_activity_main);
        viewPager = findViewById(R.id.view_pager_activity_main);
        adapter = new TabLayoutAdapter(getSupportFragmentManager());

        adapter.addTab(new CallFragment(), getString(R.string.txt_calls));
        adapter.addTab(new ChatFragment(), getString(R.string.txt_chats));
        adapter.addTab(new ContactFragment(), getString(R.string.txt_contacts));

        viewPager.setAdapter(adapter);

        //Open CHATS tab by default.
        viewPager.setCurrentItem(1);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back_image:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.super.onBackPressed();
                    }
                }).setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Logout");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                logOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        myPreferences.clearPreferences();

        startActivity(new Intent(context, LoginActivity.class));
        finishAffinity();
    }
}
