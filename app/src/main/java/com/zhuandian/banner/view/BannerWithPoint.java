package com.zhuandian.banner.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhuandian.banner.GlobalVar;
import com.zhuandian.banner.R;

import java.util.List;

/**
 * Created by 谢栋 on 2017/6/18.
 */

public class BannerWithPoint extends FrameLayout implements ImagerBannerViewGroup.BannerSelectListener,ImagerBannerViewGroup.BannerClickListener{
    private ImagerBannerViewGroup bannerGroup;
    private LinearLayout linearLayout;
    private BannerWithPointClickListener bannerWithPointClickListener;

    public BannerWithPoint(@NonNull Context context) {
        super(context);
        initBannerViewGroup();
        initPointLayout();
    }

    public BannerWithPoint(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBannerViewGroup();
        initPointLayout();
    }

    public BannerWithPoint(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBannerViewGroup();
        initPointLayout();
    }

    /**
     * 初始化底部圆点布局
     */
    private void initPointLayout() {
        linearLayout = new LinearLayout(getContext());
        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,40);
        linearLayout.setLayoutParams(lp);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setBackgroundColor(Color.RED);

        addView(linearLayout);
        FrameLayout.LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM;
        linearLayout.setLayoutParams(layoutParams);

        //3.0前后设置背景透明度的方法
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
            linearLayout.setAlpha(0.5f);
        }else {
            linearLayout.getBackground().setAlpha(100);
        }

    }

    /**
     * 初始化BannerViewGroup
     */
    private void initBannerViewGroup() {
        bannerGroup = new ImagerBannerViewGroup(getContext());
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bannerGroup.setLayoutParams(params);
        bannerGroup.setSelectListener(this);
        bannerGroup.setBannerClickListener(this);
        addView(bannerGroup);

    }

    public void addBitmapsToBanner(List<Bitmap> list) {
        for (int i = 0; i < list.size(); i++) {
            Bitmap bitmap = list.get(i);
            addBitmapToImageBannerViewGroup(bitmap);
            addPointToLinearLayout();
        }
    }

    /**
     * 添加底部导航圆点
     */
    private void addPointToLinearLayout() {
        ImageView image = new ImageView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,40);
        lp.setMargins(5,5,5,5);
        image.setLayoutParams(lp);
        image.setImageResource(R.drawable.ponit_unselect);
        linearLayout.addView(image);
    }

    /**
     * 添加ImageVIew到Banner
     */
    private void addBitmapToImageBannerViewGroup(Bitmap bitmap) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(GlobalVar.WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setImageBitmap(bitmap);

        bannerGroup.addView(imageView);
    }

    @Override
    public void select(int index) {
        int count = linearLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView iv = (ImageView) linearLayout.getChildAt(i);
            if (i==index){
                iv.setImageResource(R.drawable.ponit_select);
            }else {
                iv.setImageResource(R.drawable.ponit_unselect);
            }
        }
    }

    @Override
    public void clickIndex(int pos) {
        bannerWithPointClickListener.clickIndex(pos);
    }

    public void setBannerWithPointClickListener(BannerWithPointClickListener bannerWithPointClickListener) {
        this.bannerWithPointClickListener = bannerWithPointClickListener;
    }

    public interface BannerWithPointClickListener{
        void clickIndex(int pos);
    }
}
