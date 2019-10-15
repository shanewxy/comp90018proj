package com.unimelb.checkmein.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unimelb.checkmein.R;

import java.util.ArrayList;
import java.util.List;

import static com.unimelb.checkmein.R.id.recyclerView;

public class GalleryFragment extends Fragment {
    public RecyclerView mCollectRecyclerView;//定义RecyclerView
    private View root;
    private GalleryViewModel galleryViewModel;
    private List<Subject> subjects;
    private CollectRecycleAdapter mCollectRecyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        subjects = new ArrayList<>();
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        root = inflater.inflate(R.layout.fragment_gallery, container, false);
        initRecyclerView();
        //模拟数据
        initData(galleryViewModel);
//        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
//
//        // 2. set layoutManger
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        // this is data fro recycler view
//        galleryViewModel.getText().observe(this, new Observer<List>() {
//            @Override
//            public void onChanged(List list) {
//                subjects.addAll(list);
//            }
//        });
//
//        // 3. create an adapter
//        CollectRecycleAdapter mAdapter = new CollectRecycleAdapter(subjects);
//        // 4. set adapter
//        recyclerView.setAdapter(mAdapter);
//        // 5. set item animator to DefaultAnimator
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        RecyclerView mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
//        Log.d("debugMode", "The application stopped after this");
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        RecyclerAdapter mAdapter = new RecyclerAdapter(getNames());
//        mRecyclerView.setAdapter(mAdapter);
        return root;
    }

    private void initRecyclerView() {
        //获取RecyclerView
        mCollectRecyclerView = (RecyclerView) root.findViewById(recyclerView);
        //创建adapter
        mCollectRecyclerAdapter = new CollectRecycleAdapter(this.getContext(),subjects);
        //给RecyclerView设置adapter
        mCollectRecyclerView.setAdapter(mCollectRecyclerAdapter);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        mCollectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //设置item的分割线
        mCollectRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        mCollectRecyclerAdapter.setOnItemClickListener(new CollectRecycleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Subject data) {
                //此处进行监听事件的业务处理
                Toast.makeText(getActivity(), "我是item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData(GalleryViewModel galleryViewModel) {
        galleryViewModel.getText().observe(this, new Observer<List>() {
            @Override
            public void onChanged(List list) {
                if(subjects==null)subjects=new ArrayList<>();
                subjects.addAll(list);
            }
        });
    }

}