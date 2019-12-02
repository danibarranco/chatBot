package com.example.chatbot.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.chatbot.Messages;
import com.example.chatbot.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> userMessagesList;
    private LayoutInflater inflater;


    public MessageAdapter(Context context) {
        inflater= LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= inflater.inflate(R.layout.item_messages,parent,false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        if(userMessagesList!=null){
            final Messages messages= userMessagesList.get(position);
            String message=messages.getMessage();
            String type=messages.getType();

            if(type.equalsIgnoreCase("bot")){
                holder.receiver.setVisibility(View.VISIBLE);
                holder.sender.setVisibility(View.INVISIBLE);
                holder.receiver.setText(message);
            }else {
                holder.sender.setVisibility(View.VISIBLE);
                holder.receiver.setVisibility(View.INVISIBLE);
                holder.sender.setText(message);
            }
        }
    }

    public void setUserMessagesList(List<Messages> userMessagesList){
        this.userMessagesList=userMessagesList;
        notifyDataSetChanged();
    }
    
    @Override
    public int getItemCount() {
        int elements=0;
        if(userMessagesList !=null){
            elements= userMessagesList.size();
        }
        return elements;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView sender, receiver;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sender=itemView.findViewById(R.id.tvMe);
            receiver=itemView.findViewById(R.id.tvReceiver);
        }
    }
}
