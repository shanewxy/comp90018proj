package com.unimelb.checkmein.ui.gallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.unimelb.checkmein.R;

import java.util.ArrayList;
import java.util.List;

public class CollectRecycleAdapter extends RecyclerView.Adapter<CollectRecycleAdapter.myViewHodler> {
    private Context context;
    private List<Subject> goodsEntityList;

    //创建构造函数
    public CollectRecycleAdapter(Context context,List<Subject> goodsEntityList) {
        //将传递过来的数据，赋值给本地变量
        this.context = context;//上下文
        this.goodsEntityList = goodsEntityList;//实体类数据ArrayList
    }

    /**
     * 创建viewhodler，相当于listview中getview中的创建view和viewhodler
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public myViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建自定义布局
        View itemView = View.inflate(context, R.layout.recycler_example, null);
        return new myViewHodler(itemView);
    }

    /**
     * 绑定数据，数据与view绑定
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(myViewHodler holder, int position) {
        //根据点击位置绑定数据
        Subject data = goodsEntityList.get(position);
//        holder.mItemGoodsImg;
        holder.mItemGoodsName.setText(data.name);//获取实体类中的name字段并设置
        holder.mItemGoodsPrice.setText(data.code);//获取实体类中的price字段并设置

    }

    /**
     * 得到总条数
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return goodsEntityList.size();
    }

    //自定义viewhodler
    class myViewHodler extends RecyclerView.ViewHolder {
        private ImageView mItemGoodsImg;
        private TextView mItemGoodsName;
        private TextView mItemGoodsPrice;

        public myViewHodler(View itemView) {
            super(itemView);
//            mItemGoodsImg = (ImageView) itemView.findViewById(R.id.item_goods_img);
            mItemGoodsName = (TextView) itemView.findViewById(R.id.list_example_code);
            mItemGoodsPrice = (TextView) itemView.findViewById(R.id.list_example_text);
            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //可以选择直接在本位置直接写业务处理
                    Toast.makeText(context,"点击了xxx", Toast.LENGTH_SHORT).show();
                    //此处回传点击监听事件
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, goodsEntityList.get(getLayoutPosition()));
                    }
                }
            });

        }
    }

    /**
     * 设置item的监听事件的接口
     */
    public interface OnItemClickListener {
        /**
         * 接口中的点击每一项的实现方法，参数自己定义
         *
         * @param view 点击的item的视图
         * @param data 点击的item的数据
         */
        public void OnItemClick(View view, Subject data);
    }

    //需要外部访问，所以需要设置set方法，方便调用
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
