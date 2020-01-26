package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.chatbot.View.HistorialAdapter;
import com.example.chatbot.View.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HistorialActivity extends AppCompatActivity {

    private static final String TAG = "si";
    private ArrayList<String> historialDias = new ArrayList<>();
    private HistorialAdapter adapter;
    private RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getUserChatsDays();
    }

    private void getUserChatsDays() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference refItem= database.getReference("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());

        refItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Iterator<DataSnapshot>iterator=dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()){
                        String day = iterator.next().getKey();
                        historialDias.add(day);
                    }
                System.out.println(historialDias);

                    init();
                }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void init() {
        adapter= new HistorialAdapter(this);
        rvList = findViewById(R.id.rvListH);
        final LinearLayoutManager mManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(mManager);
        rvList.setAdapter(adapter);

        adapter.setUserDayList(historialDias);
    }
}
