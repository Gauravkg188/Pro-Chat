package com.kg.prochat.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.kg.prochat.Model.User;
import com.kg.prochat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList=new ArrayList<>();
    private ChatUserAdapter.OnItemClickListener listener;


    public ChatUserAdapter(Context context)
    {
        this.context=context;

    }

    @NonNull
    @Override
    public ChatUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false);
        return new ChatUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserAdapter.ViewHolder holder, int position) {

        final User user=userList.get(position);
        Picasso.with(context).load(user.getImageUri()).into(holder.userImage);
        holder.userName.setText(user.getName());
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new
                        AlertDialog.Builder(context);
                View mView = LayoutInflater.from(context).inflate(R.layout.zoom_image, null);
                PhotoView photoView = mView.findViewById(R.id.imageView);
                Picasso.with(context).load(user.getImageUri()).into(photoView);

                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userName;
        public ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.profile_image);
            userName=itemView.findViewById(R.id.profile_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (listener != null && pos != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(userList.get(pos));
                    }
                }
            });
        }
    }

    public void setUserList(List<User> userList)
    {
        this.userList=userList;
        notifyDataSetChanged();

    }

    public interface OnItemClickListener {

        void OnItemClick(User user);
    }

    public void setOnItemClickListener(ChatUserAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


}