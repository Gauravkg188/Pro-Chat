package com.kg.prochat.Adapter;

import android.content.Context;
import android.os.TestLooperManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.kg.prochat.Model.Message;
import com.kg.prochat.Model.User;
import com.kg.prochat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    private Context context;
    private List<Message> messageList=new ArrayList<>();
    private static final int LEFT=0;
    private static final int RIGHT=1;

    String senderId;

    public ChatAdapter(Context context,String sender)
    {
        this.context=context;
        this.senderId=sender;


    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==RIGHT)
        {  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, int position) {

       final Message message=messageList.get(position);
        if(senderId.equals(message.getSender())){
            holder.text_message.setText(message.getMessage());
            if(message.getCheck())
            {
                holder.checkSeen.setImageResource(R.drawable.ic_baseline_seen);
            }
            else
            {
                holder.checkSeen.setImageResource(R.drawable.ic_baseline_delivered);
            }
        }
        else
        {
            holder.text_message.setText(message.getReceiverMessage());
            holder.checkSeen.setVisibility(View.GONE);
        }


        holder.text_date.setText(message.getDate());
        holder.text_time.setText(message.getTime());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

       TextView text_message,text_time,text_date;
       ImageView checkSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text_date=itemView.findViewById(R.id.text_date);
            text_time=itemView.findViewById(R.id.text_time);
            text_message=itemView.findViewById(R.id.text_message);
            checkSeen=itemView.findViewById(R.id.image_check);

        }
    }

    public void setMessageList(List<Message> messageList)
    {
        this.messageList=messageList;
        notifyDataSetChanged();
        Log.d("kitty", "updated");
    }

    @Override
    public int getItemViewType(int position) {
        String userId= FirebaseAuth.getInstance().getUid();
        if(messageList.get(position).getSender().equals(userId))
        {
            return LEFT;
        }
        else
        {
            return RIGHT;
        }
    }




}
