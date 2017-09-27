package com.zhuandian.banner.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 测量->布局->绘制
 * Created by 谢栋 on 2017/6/18.
 */

public class ImagerBannerViewGroup extends ViewGroup {
    private static final int START_BANNER = 0;  //handler消息码
    private int childCount;
    private int childwidth;
    private int childheight;
    private int currentX;  //当前移动点的位置
    private int currentY;

    private int index = 0; //当前图片的索引

    private BannerSelectListener selectListener;
    public void setSelectListener(BannerSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    private Scroller mScroller;

    private boolean isAuto = true;  //是否开启自动轮播功能
    private Timer mTimer = new Timer();
    private TimerTask mTask;

    private boolean isClick; //判断是否为点击事件
    //点击回调接口
    private BannerClickListener mListener;

    public void setBannerClickListener(BannerClickListener mListener) {
        this.mListener = mListener;
    }

    public interface BannerClickListener {
        void clickIndex(int pos);
    }

    private Handler autoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_BANNER:
                    if (++index >= childCount) { //如果轮播到最后一张，重新开始
                        index = 0;
                    }
                    scrollTo(childwidth * index, 0);
                    selectListener.select(index); //监听当前页
                    break;
            }
        }
    };

    private void startAutoBanner() {
        isAuto = true;
    }

    private void stopAutoBanner() {
        isAuto = false;
    }

    public ImagerBannerViewGroup(Context context) {
        super(context);
        initScroller();
    }

    public ImagerBannerViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScroller();
    }

    public ImagerBannerViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initScroller();
    }

    private void initScroller() {
        mScroller = new Scroller(getContext());
        mTask = new TimerTask() {
            @Override
            public void run() {
                if (isAuto) {
                    autoHandler.sendEmptyMessage(START_BANNER);
                }
            }
        };

        mTimer.schedule(mTask, 100, 3000);
    }

    /**
     * 使用Scroller对象实现滑动，需要完成computeScroll方法
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {//判断scroll是否已经滑动完毕
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();//重绘
        }
    }

    /**
     * @param changed 位置发生改变的时候为true，没改变为false
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (changed) {

            int leftMargin = 0;

            for (int i = 0; i < childCount; i++) {
                View view = getChildAt(i);
                view.layout(leftMargin, 0, leftMargin + childwidth, childheight);
                leftMargin += childwidth;
            }
        }

    }


    /**
     * 想测量ViewGroup的宽高度，必须先知道子视图的宽高度，子视图的个数
     * 1.求出子视图的个数
     * 2.测量出子视图的宽高度
     * 3.根据子视图的宽度和高度，求出当前ViewGroup的宽高度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        childCount = getChildCount(); //1.求出子视图的个数
        if (0 == childCount) {
            setMeasuredDimension(0, 0);
        } else {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            View view = getChildAt(0);   //得到第一个子视图

            //2.测量出子视图的宽高度
            childwidth = view.getMeasuredWidth();
            childheight = view.getMeasuredHeight();
            int width = view.getMeasuredWidth() * childCount;
            setMeasuredDimension(width, childheight);
        }
    }


    /**
     * 事件传递过程, 拦截(onInterceptTouchEvent)的返回值为true的时候，当前VIewGroup
     * 会处理此拦截事件，事件不再继续向下传递。返回false不会处理，该事件将继续向下传递T
     * 返回true：真正处理事件的方法为onTouchEvent
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    /**
     * 在滑动屏幕图片的过程中，其实是对ViewGroup的子视图移动的过程，
     * 1.得到滑动之前和之后的横坐标值，（求出此过程中移动的距离），调用
     * scrollBy方法实现图片的滑动
     * 2.在我们按下的一瞬间内，移动之前跟之后的恒坐标点是相等的
     * 3.在不断的移动过程中，会不断调用ACTION_MOVE方法，此时把移动之前跟之后的坐标点值保存
     * 4.在手指抬起的一瞬间，根据横坐标值，计算出要移动到那张图片上
     * <p>
     * (当前ViewGroup的滑动位置 +　第一张图片的宽度/2) / 每张图片的宽度
     * <p>
     * 调用scrollTo方法，滑动到当前图片所在的位置上
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoBanner();
                if (!mScroller.isFinished()) {//防止造成滑动混乱
                    mScroller.abortAnimation();
                }
                isClick = true;
                System.out.println("--down---");
                currentX = (int) event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int moveLength = moveX - currentX;
                scrollBy(-moveLength, 0);
                currentX = moveX;
                isClick = false; //结束掉点击事件（否则滑动有卡顿）//TODO ACTION_DOWN跟ACTION_MOVE冲突
                System.out.println("--move---");
                break;

            case MotionEvent.ACTION_UP:

                int scrollX = getScrollX();
                index = (scrollX + childwidth / 2) / childwidth;
                if (index < 0) {//已经滑动到左边第一张
                    index = 0;
                } else if (index > childCount - 1) { //已经滑动到最右边
                    index = childCount - 1;
                }

                if (isClick) {
                    mListener.clickIndex(index);
                } else {

                    //使用Scroller
                    int dx = index * childwidth - scrollX;
                    mScroller.startScroll(scrollX, 0, dx, 0);
                    postInvalidate(); //通知重绘
                    selectListener.select(index); //监听当前页

                    //使用ScrollTo、scrollBy
                    // scrollTo(index * childwidth, 0);


                }
                System.out.println("--up---");
                startAutoBanner();
                break;

            default:

                break;
        }
        return true; //返回true，截断当前事件的向下传递过程
    }

    public interface BannerSelectListener{
        void select(int index);
    }
}
