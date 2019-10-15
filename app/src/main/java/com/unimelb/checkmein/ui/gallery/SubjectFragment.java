package com.unimelb.checkmein.ui.gallery;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class SubjectFragment extends PostListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query recentPostsQuery = databaseReference.child("subject")
                .limitToFirst(100);
        return recentPostsQuery;
    }
}
