package com.unimelb.checkmein.ui.rank;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.unimelb.checkmein.R;
import com.unimelb.checkmein.bean.User;

import java.util.Map;


public class RankViewHolder extends RecyclerView.ViewHolder {

    public TextView rank;
    public TextView username;
    public TextView count;
    public static int rank_increment = 1;

    public RankViewHolder(View itemView) {
        super(itemView);
        rank = itemView.findViewById(R.id.rank);
        username = itemView.findViewById(R.id.username);
        count = itemView.findViewById(R.id.count);
    }


    public void bindToPost(int rank, User user) {
        this.rank.setText(rank_increment + "");
        rank_increment++;
        this.username.setText(user.username);
        this.count.setText(user.count + "");

    }
}

