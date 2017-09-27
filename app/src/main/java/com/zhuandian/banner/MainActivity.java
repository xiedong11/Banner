package com.zhuandian.banner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.zhuandian.banner.view.BannerWithPoint;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BannerWithPoint.BannerWithPointClickListener {

    private BannerWithPoint mBanner;
    private int [] imgs = new int[]{
            R.drawable.a,R.drawable.b,R.drawable.c
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        GlobalVar.WIDTH = dm.widthPixels;  //得到当前手机屏幕的尺寸，

        mBanner = (BannerWithPoint) findViewById(R.id.banner);


        List<Bitmap> list = new ArrayList<>();
        for (int i = 0; i < imgs.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),imgs[i]);
            list.add(bitmap);
        }
        mBanner.addBitmapsToBanner(list);
        mBanner.setBannerWithPointClickListener(this);
        //mBanner.setBannerClickListener(this);
//        for (int i = 0; i < imgs.length; i++) {
//            ImageView imageView = new ImageView(this);
//            //给ImageView设置LayoutPaiams，解决屏幕大小自适应问题
//            imageView.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setImageResource(imgs[i]);
//            mBanner.addView(imageView);
//        }
    }

    @Override
    public void clickIndex(int pos) {
        Toast.makeText(this, "点击"+pos, Toast.LENGTH_SHORT).show();
        System.out.println("----"+pos);
    }
}
