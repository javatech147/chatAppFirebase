package com.waytojava.ichatapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.waytojava.ichatapp.R;
import com.waytojava.ichatapp.firebase.MyFirebaseUser;
import com.waytojava.ichatapp.utils.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private static final String TAG = ChatListAdapter.class.getSimpleName();
    private ArrayList<MyFirebaseUser> firebaseUsers;
    private Context context;

    public ChatListAdapter(ArrayList<MyFirebaseUser> firebaseUsers, Context context) {
        this.firebaseUsers = firebaseUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.single_row_user_list, viewGroup, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder chatListViewHolder, int i) {
        MyFirebaseUser firebaseUser = firebaseUsers.get(i);

        Utils.log(TAG, "Name : --- " + firebaseUser.name);
        Utils.log(TAG, "Profile Image  : --- " + firebaseUser.profileimage);

        chatListViewHolder.tvUserName.setText(firebaseUser.name);


        Picasso.get().load(firebaseUser.profileimage).into(chatListViewHolder.profileImage);

    }

    @Override
    public int getItemCount() {
        return firebaseUsers.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profileImage;
        public TextView tvUserName;
        public TextView tvLastMessage;
        public TextView tvLastMessageTime;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.civ_single_row);
            tvUserName = itemView.findViewById(R.id.tv_single_row_user_name);
            tvLastMessage = itemView.findViewById(R.id.tv_single_row_last_message);
            tvLastMessageTime = itemView.findViewById(R.id.tv_single_row_last_message_time);
        }
    }
}
