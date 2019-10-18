package com.unimelb.checkmein.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.unimelb.checkmein.R;
import com.unimelb.checkmein.bean.Subject;
import com.unimelb.checkmein.bean.User;

public abstract class SubjectFragment extends Fragment {

    private static final String TAG = "SubjectFragment";
    private User user;
    // [START define_database_reference]
    protected DatabaseReference mDatabase;
    // [END define_database_reference]

    protected FirebaseRecyclerAdapter<Subject, SubjectViewHolder> mAdapter;
    protected RecyclerView mRecycler;
    protected LinearLayoutManager mManager;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public SubjectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_mysubject, container, false);
        user = getUser();
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = rootView.findViewById(R.id.recyclerView);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
//        Query postsQuery = getQuery(mDatabase);
//
//        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Subject>()
//                .setQuery(postsQuery, Subject.class)
//                .build();
//
//        mAdapter = new FirebaseRecyclerAdapter<Subject, SubjectViewHolder>(options) {
//
//            @Override
//            public SubjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//                return new SubjectViewHolder(inflater.inflate(R.layout.recycler_item, viewGroup, false));
//            }
//
//            @Override
//            protected void onBindViewHolder(SubjectViewHolder viewHolder, int position, final Subject model) {
//
//                final DatabaseReference postRef = getRef(position);
//
//                // Set click listener for the whole post view
//                final String postKey = postRef.getKey();
//                if (model.getStudents().containsKey(getUid())) {
//                    viewHolder.count.setChecked(true);
//                } else viewHolder.count.setChecked(false);
////              Bind Post to ViewHolder, setting OnClickListener for the star button
//                viewHolder.bindToPost(model, new Switch.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                        DatabaseReference globalPostRef = mDatabase.child("subject").child(postRef.getKey());
////                        DatabaseReference userPostRef = mDatabase.child("user-subjects").child(model.getCode()).child(postRef.getKey());
//                        onChecked(globalPostRef);
////                        onChecked(userPostRef);
//                        Map<String, Object> updates = new HashMap<>();
//                        if (b) {
//                            Log.d(TAG, "onCheckedChanged: " + model);
//                            updates.put("/user-subjects/" + getUid() + "/" + postKey, model.toMap());
//                        } else {
////                            model.setValid(false);
//                            updates.put("/user-subjects/" + getUid() + "/" + postKey, null);
//
//                        }
//                            mDatabase.updateChildren(updates);
//                    }
//                });
//            }
//        };
//        mRecycler.setAdapter(mAdapter);
    }

    // [START post_stars_transaction]
    protected void onChecked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Subject p = mutableData.getValue(Subject.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.getStudents().containsValue(user)) {
                    // Unstar the post and remove self from stars
//                    p.setTimes(p.getTimes() - 1);
                    p.getStudents().remove(getUid());
                } else {
                    // Star the post and add self to stars
//                    p.starCount = p.starCount + 1;
                    p.getStudents().put(getUid(), user);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]


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

    public String getUid() {

        return currentUser.getUid();
    }

    public User getUser() {

        return new User(getUid(), currentUser.getEmail());
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
