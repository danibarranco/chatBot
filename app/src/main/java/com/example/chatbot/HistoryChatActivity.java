package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.chatbot.View.HistorialAdapter;
import com.example.chatbot.View.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class HistoryChatActivity extends AppCompatActivity {

    private ArrayList<ChatSentence>messages= new ArrayList<>();
    private MessageAdapter adapter;
    private RecyclerView rvList;
    private String refDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_chat);
        refDay=getIntent().getStringExtra("day");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getChatByDay();
    }

    private void getChatByDay() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference refItem= database.getReference("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+refDay);

        refItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    ChatSentence chatSentence = new ChatSentence();
                    Map<String,String> property=(Map<String,String>)iterator.next().getValue();
                    chatSentence.setTalker(property.get("talker"));
                    chatSentence.setSetenceEs(property.get("sentenceEs"));
                    chatSentence.setSentenceEn(property.get("sentenceEn"));
                    chatSentence.setTime(property.get("time"));
                    messages.add(chatSentence);
                    chatSentence = new ChatSentence();
                }
                init();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("si", "Failed to read value.", error.toException());
            }
        });
    }

    private void init() {
        adapter= new MessageAdapter(this);
        rvList = findViewById(R.id.rvList);
        final LinearLayoutManager mManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(mManager);
        rvList.setAdapter(adapter);
        adapter.setUserMessagesList(messages);
    }
}
