package com.kg.prochat.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kg.prochat.Model.User;
import com.kg.prochat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Context context;
    private List<User> userList=new ArrayList<>();
    private OnItemClickListener listener;


    public UsersAdapter(Context context)
    {
        this.context=context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user=userList.get(position);
        Picasso.with(context).load(user.getImageUri()).into(holder.userImage);
        holder.userName.setText(user.getName());

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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
