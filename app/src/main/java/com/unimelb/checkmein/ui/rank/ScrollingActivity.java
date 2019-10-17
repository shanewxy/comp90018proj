package com.unimelb.checkmein.ui.rank;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.unimelb.checkmein.R;
import com.unimelb.checkmein.bean.User;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Map;

public class ScrollingActivity extends AppCompatActivity {

    protected DatabaseReference mDatabase;
    // [END define_database_reference]
    private final String TAG = ScrollingActivity.class.toString();

    protected FirebaseRecyclerAdapter<User, RankViewHolder> mAdapter;
    protected RecyclerView mRecycler;
    protected LinearLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = findViewById(R.id.scroll_recycler);
        mRecycler.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        Query postsQuery = getQuery(mDatabase);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(postsQuery, User.class)
                .build();
        Log.d(TAG, "onCreate: ");
        mAdapter = new FirebaseRecyclerAdapter<User, RankViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RankViewHolder holder, int position, @NonNull User model) {
                Log.d(TAG, "onBindViewHolder: " + model);
                holder.bindToPost(position, model);

            }

            @NonNull
            @Override
            public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: ");
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new RankViewHolder(inflater.inflate(R.layout.rank_item, parent, false));
            }
        };
        mRecycler.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_posts_query]
        // My top posts by number of stars
        String myUserId = getIntent().getStringExtra("subject");
        Log.d(TAG, "getQuery: "+myUserId);
        Query myTopPostsQuery = databaseReference.child("subject").child(myUserId).child("students").orderByChild("count");
        // [END my_top_posts_query]

        return myTopPostsQuery;
    }
}
