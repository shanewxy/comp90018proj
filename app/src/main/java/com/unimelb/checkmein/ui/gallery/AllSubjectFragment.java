package com.unimelb.checkmein.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unimelb.checkmein.R;
import com.unimelb.checkmein.bean.Subject;
import com.unimelb.checkmein.ui.SubjectFragment;
import com.unimelb.checkmein.ui.SubjectViewHolder;

import java.util.HashMap;
import java.util.Map;

public class AllSubjectFragment extends SubjectFragment {
    private final String TAG = "AllSubjectFragment";

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query recentPostsQuery = databaseReference.child("subject")
                .limitToFirst(100);
        return recentPostsQuery;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
                Query postsQuery = getQuery(mDatabase);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Subject>()
                .setQuery(postsQuery, Subject.class)
                .build();
        mAdapter = new FirebaseRecyclerAdapter<Subject, SubjectViewHolder>(options) {

            @Override
            public SubjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new SubjectViewHolder(inflater.inflate(R.layout.recycler_item, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(SubjectViewHolder viewHolder, int position, final Subject model) {

                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                if (model.getStudents().containsKey(getUid())) {
                    viewHolder.aSwitch.setChecked(true);
                } else viewHolder.aSwitch.setChecked(false);
//              Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new Switch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        DatabaseReference globalPostRef = mDatabase.child("subject").child(postRef.getKey());
//                        DatabaseReference userPostRef = mDatabase.child("user-subjects").child(model.getCode()).child(postRef.getKey());
                        onChecked(globalPostRef);
//                        onChecked(userPostRef);
                        Map<String, Object> updates = new HashMap<>();
                        if (b) {
                            Log.d(TAG, "onCheckedChanged: " + model);
                            updates.put("/user-subjects/" + getUid() + "/" + postKey, model.toMap());
                        } else {
//                            model.setValid(false);
                            updates.put("/user-subjects/" + getUid() + "/" + postKey, null);

                        }
                            mDatabase.updateChildren(updates);
                    }
                });
//
            }
        };
        mRecycler.setAdapter(mAdapter);
    }
}
