package com.unimelb.checkmein.ui.rank;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.unimelb.checkmein.R;
import com.unimelb.checkmein.bean.Subject;
import com.unimelb.checkmein.bean.User;
import com.unimelb.checkmein.ui.SubjectViewHolder;

import java.util.Map;

public class RankFragment extends Fragment {

    private String subjectKey;
    protected DatabaseReference mDatabase;
    // [END define_database_reference]
    private String TAG = RankFragment.class.toString();
    protected FirebaseRecyclerAdapter<User, RankViewHolder> mAdapter;
    protected RecyclerView mRecycler;
    protected LinearLayoutManager mManager;


    public static Fragment newInstance(String subject) {
        RankFragment fragmentOne = new RankFragment(subject);
        Bundle bundle = new Bundle();

        bundle.putString("name", subject);
        //fragment保存参数，传入一个Bundle对象
        fragmentOne.setArguments(bundle);
        return fragmentOne;
    }

    public RankFragment() {
    }

    public RankFragment(String subject) {
        subjectKey = subject;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        subjectKey = getArguments().getString("name");
        View rootView = inflater.inflate(R.layout.rank_fragment, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        Log.d(TAG, "onCreateView: ");
        mRecycler = rootView.findViewById(R.id.recyclerView_rank);
        mRecycler.setHasFixedSize(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // TODO: Use the ViewModel
        Query postsQuery = getQuery(mDatabase);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(postsQuery, User.class)
                .build();
        Log.d(TAG, "onActivityCreated: "+options.getSnapshots());
        mAdapter = new FirebaseRecyclerAdapter<User, RankViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RankViewHolder holder, int position, @NonNull User model) {
                holder.bindToPost(position, model);
                Log.d(TAG, "onBindViewHolder: " + model);
            }

            @NonNull
            @Override
            public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new RankViewHolder(inflater.inflate(R.layout.rank_item, parent, false));
            }

        };
        mRecycler.setAdapter(mAdapter);
        Log.d(TAG, "onActivityCreated: "+mAdapter.getItemCount());
    }

    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_posts_query]
        // My top posts by number of stars
        Log.d(TAG, "getQuery: "+subjectKey);
        Query myTopPostsQuery = databaseReference.child("subject").child(subjectKey).child("students");
        // [END my_top_posts_query]
        return myTopPostsQuery;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}
