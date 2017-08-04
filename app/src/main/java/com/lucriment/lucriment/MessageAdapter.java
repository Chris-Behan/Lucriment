package com.lucriment.lucriment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ChrisBehan on 8/3/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<Chat> chatList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, message;
        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.fetch_chat_sender);
            message = (TextView) view.findViewById(R.id.fetch_chat_messgae);

        }
    }
    public MessageAdapter(List<Chat> chats){
        this.chatList = chats;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Chat chat = chatList.get(position);
        holder.name.setText(chat.senderName);
        holder.message.setText(chat.text);


    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
