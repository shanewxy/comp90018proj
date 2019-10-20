package com.unimelb.checkmein.ui.mySubjects;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unimelb.checkmein.MainActivity;
import com.unimelb.checkmein.MapsActivity;
import com.unimelb.checkmein.R;
import com.unimelb.checkmein.bean.Subject;
import com.unimelb.checkmein.ui.SubjectFragment;
import com.unimelb.checkmein.ui.SubjectViewHolder;

public class MySubjectFragment extends SubjectFragment {
    private String TAG = "MySubjectFragment";

    public MySubjectFragment() {

    }


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-subjects")
                .child(getUid());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Query postsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Subject>()
                .setQuery(postsQuery, Subject.class)
                .build();
        mProgressDialog.dismiss();

        mAdapter = new FirebaseRecyclerAdapter<Subject, SubjectViewHolder>(options) {

            @Override
            public SubjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

                return new SubjectViewHolder(inflater.inflate(R.layout.recycler_item, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(SubjectViewHolder viewHolder, int position, final Subject model) {

                final DatabaseReference postRef = getRef(position);
                viewHolder.aSwitch.setVisibility(View.GONE);
                viewHolder.bindToPost(model, new TextView.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), MapsActivity.class);
                        intent.putExtra("dbkey", postRef.getKey());
                        startActivity(intent);
                    }
                });

            }
        };
        mRecycler.setAdapter(mAdapter);

    }

}
