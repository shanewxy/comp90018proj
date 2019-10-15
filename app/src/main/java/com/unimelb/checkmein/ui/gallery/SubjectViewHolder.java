package com.unimelb.checkmein.ui.gallery;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.unimelb.checkmein.R;
import com.unimelb.checkmein.bean.Subject;


public class SubjectViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public Switch aSwitch;
    public TextView numStarsView;
    public TextView bodyView;

    public SubjectViewHolder(View itemView) {
        super(itemView);

//        titleView = itemView.findViewById(R.id.postTitle);
//        authorView = itemView.findViewById(R.id.postAuthor);
        aSwitch = itemView.findViewById(R.id.switch1);
//        numStarsView = itemView.findViewById(R.id.postNumStars);
//        bodyView = itemView.findViewById(R.id.postBody);
        titleView = itemView.findViewById(R.id.list_example_code);
        bodyView = itemView.findViewById(R.id.list_example_text);
    }

    public void bindToPost(Subject subject, Switch.OnCheckedChangeListener switchListener) {
        titleView.setText(subject.getCode());
//        authorView.setText(subject.author);
//        numStarsView.setText(String.valueOf(subject.starCount));
        bodyView.setText(subject.getName());

        aSwitch.setOnCheckedChangeListener(switchListener);
    }
}
