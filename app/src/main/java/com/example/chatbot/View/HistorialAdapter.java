package com.example.chatbot.View;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatbot.ChatSentence;
import com.example.chatbot.HistoryChatActivity;
import com.example.chatbot.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.MessageViewHolder>{
    private List<String> userDayList;
    private LayoutInflater inflater;
    private Context context;

    public HistorialAdapter(Context context) {
        inflater= LayoutInflater.from(context);
        this.context=context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= inflater.inflate(R.layout.item_historial,parent,false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, final int position) {
        if(userDayList !=null){
            final String dateBD=userDayList.get(position);
            String date= dateBD.substring(0,4)+"-"+dateBD.substring(4,6)+"-"+dateBD.substring(6);
            holder.tvHistorial.setText(holder.tvHistorial.getText()+" "+date);
            holder.tvHistorial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, HistoryChatActivity.class).putExtra("day",dateBD));
                }
            });
        }
    }

    public void setUserDayList(List<String> userDayList){
        this.userDayList = userDayList;
        notifyDataSetChanged();
    }
    
    @Override
    public int getItemCount() {
        int elements=0;
        if(userDayList !=null){
            elements= userDayList.size();
        }
        return elements;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView tvHistorial;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistorial=itemView.findViewById(R.id.tvHistorial);
        }
    }
}
