package com.benjamin.mylib;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by benja on 2018/9/19.
 */

public class MyBanner extends ViewGroup {

    private Scroller mScroller;
    private int mLeftBorder,mRightBorder;
    private int mTouchSlop;
    private float mXDown,mXLastMove,mXMove,mXUp;
    private VelocityTracker mVelocityTracker;
    private int mWidth,mHeight;
    private int mMinimumVelocity,mMaximumVelocity;

    private Adapter mAdapter;
    private Context mContext;

    public abstract static class Adapter<VH extends ViewHolder>{
        public abstract VH  onCreateViewHolder(ViewGroup parent);
        public abstract void onBindViewHolder(VH holder,int position);
        public abstract int getItemCount();

        public final VH createViewHolder(ViewGroup parent){
            final VH holder=onCreateViewHolder(parent);
            return holder;
        }
    }

    public abstract static class ViewHolder{
        public View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }

    public MyBanner(Context context, Scroller mScroller) {
        super(context);
    }

    //通过findViewById引用必须写该Constructor
    public MyBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        mScroller=new Scroller(context,new DecelerateInterpolator());

        ViewConfiguration configuration = ViewConfiguration.get(context);
        // 获取TouchSlop值
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mMinimumVelocity=configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity=configuration.getScaledMaximumFlingVelocity();

    }

    public void setAdapter(Adapter adapter){
        removeViewsFromBanner();

        mAdapter=adapter;

        addViewsFromAdapter();

        requestLayout();
    }

    private void removeViewsFromBanner(){
        int childCount=getChildCount();
        if(childCount!=0){
            removeViews(0,childCount);
        }
    }

    private void addViewsFromAdapter(){
        int itemCount=mAdapter.getItemCount();
        for(int i=0;i<itemCount;i++){
            ViewHolder viewHolder=mAdapter.createViewHolder(this);
            viewHolder.itemView.setClickable(true);
            mAdapter.onBindViewHolder(viewHolder,i);
            addView(viewHolder.itemView);
        }
        Log.i("addViewsFromAdapter",getChildCount()+"");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth=MeasureSpec.getSize(widthMeasureSpec);
        mHeight=MeasureSpec.getSize(heightMeasureSpec);

        int childCount=getChildCount();
        for(int i=0;i<childCount;i++){
            View childView=getChildAt(i);
            measureChildWithMargins(childView,widthMeasureSpec,0,heightMeasureSpec,0);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount=getChildCount();

        int baseLeft=0;
        int fLeftMargin=0;       //第一个child的leftMargin
        int lRightMargin=0;      //最后一个child的rightMargin
        mLeftBorder=0;
        mRightBorder=0;
        for(int i=0;i<childCount;i++){
            View childView=getChildAt(i);
            ViewGroup.MarginLayoutParams params=(MarginLayoutParams) childView.getLayoutParams();
            int left=baseLeft+params.leftMargin;
            childView.layout(left,params.topMargin,left+childView.getMeasuredWidth(),params.topMargin+childView.getMeasuredHeight());
            baseLeft+=params.leftMargin+params.rightMargin+childView.getMeasuredWidth();
            if(i==0){
                fLeftMargin=params.leftMargin;
            }
            mRightBorder+=params.leftMargin+params.rightMargin+childView.getMeasuredWidth();
        }


        mLeftBorder=getChildAt(0).getLeft()-fLeftMargin;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getRawX();
                mXLastMove = mXDown;
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getRawX();
                float diff = Math.abs(mXMove - mXDown);
                mXLastMove = mXMove;
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mTouchSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000,mMaximumVelocity);
                mXMove = event.getRawX();
                int scrolledX = (int) (mXLastMove - mXMove);
                if (getScrollX() + scrolledX <= mLeftBorder) {
                    scrollTo(mLeftBorder, 0);
                    Log.i("sc","不能右滑");
                    return true;
                } else if (getScrollX() + mWidth + scrolledX >= mRightBorder) {
                    scrollTo(mRightBorder - mWidth, 0);

                    Log.i("sc","不能左滑");
                    return true;
                }
                scrollBy(scrolledX, 0);
                mXLastMove = mXMove;
                break;
            case MotionEvent.ACTION_UP:

                mXUp=event.getRawX();
                scrolledX=(int)(mXLastMove-mXUp);

                if (getScrollX() + scrolledX <= mLeftBorder) {
                    scrollTo(mLeftBorder, 0);
                    Log.i("sc","不能右滑");
                    return true;
                } else if (getScrollX() + mWidth + scrolledX >= mRightBorder) {
                    scrollTo(mRightBorder - mWidth, 0);

                    Log.i("sc","不能左滑");
                    return true;
                }

                if(Math.abs(mVelocityTracker.getXVelocity())>mMinimumVelocity){
                    if(mVelocityTracker.getXVelocity()<0f){   //向左快速滑动
                        int dx=mWidth-getScrollX()%mWidth;
                        mScroller.startScroll(getScrollX(),0,dx,0,300);
                        Log.i("sc","左滑");
                    }else {    //向右快速滑动
                        Log.i("sc","右滑");
                        int dx=-getScrollX()%mWidth;
                        mScroller.startScroll(getScrollX(),0,dx,0,300);
                    }
                    invalidate();
                }else {
                    // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
                    int targetIndex = (getScrollX() + mWidth / 2) / mWidth;
                    int dx = targetIndex * mWidth - getScrollX();
                    // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
                    mScroller.startScroll(getScrollX(), 0, dx, 0,300);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }

        super.computeScroll();
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
