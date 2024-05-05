package com.app.airmaster.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.airmaster.R;
import com.app.airmaster.bean.CheckBean;
import com.bonlala.base.BaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by Admin
 * Date 2022/11/2
 * @author Admin
 */
public class GuideAdapter extends AppAdapter<CheckBean>{


    public GuideAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseAdapter<?>.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GuideViewHolder();
    }


    private class GuideViewHolder extends BaseAdapter.ViewHolder{

        private TextView titleTv;
        private TextView contentTv;
        private TextView footTv;
        private ImageView showImg;

        private TextView itemErrorContentTv;

        public GuideViewHolder() {
            super(R.layout.item_guide_play_layout);
            titleTv = (TextView) findViewById(R.id.itemPlayTitleTv);
            contentTv = (TextView) findViewById(R.id.itemPlayContentTv);
            showImg = (ImageView) findViewById(R.id.itemGuideImgView);
            footTv = (TextView) findViewById(R.id.itemPlayContentFootTv);
            itemErrorContentTv = (TextView) findViewById(R.id.itemErrorContentTv);
        }

        @Override
        public void onBindView(int position) {
            CheckBean checkBean = getItem(position);

            titleTv.setText((position+1) +"/"+getItemCount()+"  "+checkBean.getCheckContent());


            //检测状态
            int checkState = checkBean.getCheckStatus();
            showImg.setImageResource(getCheckStateImg(checkState));

            contentTv.setText(getCheckStateDesc(checkState));
            contentTv.setTextColor(checkState == 0 ? Color.parseColor("#747474") : Color.parseColor("#FFFFFF"));
            if(checkState==0){
                titleTv.setText("");
                itemErrorContentTv.setText(checkBean.getFailDesc());
            }

            if(checkState == 1){    //检测成功
                titleTv.setText("");
            }
        }
    }


    private String getCheckStateDesc(int state){
        if(state == 2){
            return getContext().getResources().getString(R.string.string_check_ing);
        }
        if(state == 1){
            return getContext().getResources().getString(R.string.string_check_success);
        }
        return getContext().getResources().getString(R.string.string_check_fail);
    }

    private int getCheckStateImg(int state){
        if(state == 2){
            return R.mipmap.ic_check_ing;
        }
        if(state == 1){
            return R.mipmap.ic_check_success;
        }
        return R.mipmap.ic_check_failed;
    }
}
