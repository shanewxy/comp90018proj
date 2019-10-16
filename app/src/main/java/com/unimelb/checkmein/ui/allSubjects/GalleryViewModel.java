package com.unimelb.checkmein.ui.allSubjects;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unimelb.checkmein.bean.Subject;

import java.util.ArrayList;
import java.util.List;

public class GalleryViewModel extends ViewModel {
    private final String TAG = "GalleryViewModel";
    private List<Subject> subjects;
    private MutableLiveData<List> mText;

    public GalleryViewModel() {
        subjects = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("subject");
//        myRef.setValue("Hello, World!");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//                Log.d(TAG, "onChildAdded: "+dataSnapshot.getValue());
                Subject subject = dataSnapshot.getValue(Subject.class);
                Log.d(TAG, "onChildAdded: " + subject);
                subjects.add(subject);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mText = new MutableLiveData<>();
        mText.setValue(subjects);

    }

    public LiveData<List> getText() {
        return mText;
    }
}