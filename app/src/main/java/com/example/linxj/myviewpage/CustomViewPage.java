package com.example.linxj.myviewpage;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * Created by linxj on 2015/11/12.
 */
public class CustomViewPage extends ViewGroup {
    private float mStart;
    private float mEnd;
    private int mLastX;
    private int screen;
    private Context mContext;
    private Scroller mScroller ;
    private WindowManager wm ;
    private int mScreenWidth = 0;
    private int length;
    private final Matrix mMatrix = new Matrix();
    public CustomViewPage(Context context) {
        super(context,null);

    }

    public CustomViewPage(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        mScroller = new Scroller(context);
        wm = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
    }

    public CustomViewPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
       mScroller = new Scroller(mContext);
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int count = getChildCount();
        MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
        lp.width = mScreenWidth*count;
        setLayoutParams(lp);
        int width = 0;
        int height = 0;
        for(int i = 0;i < count;i++){
            View child = getChildAt(i);

            //height = child.getMeasuredHeight();
            if(child.getVisibility()!=View.GONE){
                child.layout(i*mScreenWidth,t,(i+1)*mScreenWidth,b);
               // width += child.getMeasuredWidth();
            }
        }

    }

    public RectF getMatrixRectF(){
        Matrix m = mMatrix;
        RectF r = new RectF();
        CustomViewPage c = this;
        if(c != null){
            r.set(0,0,c.getWidth(),c.getHeight());

            m.mapRect(r);
        }
        return  r;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        //float sx = (float )getScrollX();
        switch(event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mStart = getScrollX();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                int dx = x - mLastX;
                float s = getMatrixRectF().left;
                float s1 = getMatrixRectF().right;
               if(getScrollX() < 0){
                    dx  = 0;
                }
                if(getScrollX() > 2*mScreenWidth){
                    dx = 0;
                }
                scrollBy(-dx,0);
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
                mEnd = getScrollX();
                int dScrollx=(int)(mEnd - mStart);
                if(dScrollx > 0){
                    if(dScrollx < mScreenWidth/3&& length<3*mScreenWidth){
                        mScroller.startScroll(getScrollX(),0,-dScrollx,0);
                       // mScroller.startScroll(0,getScrollX(),0,-dScrollx);
                    }else if(dScrollx >= mScreenWidth/3){
                        mScroller.startScroll(getScrollX(),0,mScreenWidth-dScrollx,0);
                        //mScroller.startScroll(0,getScrollX(),0,mScreenWidth-dScrollx);
                        length+=mScreenWidth;
                    }
                }else{
                    if(-dScrollx < mScreenWidth/3){
                        mScroller.startScroll(getScrollX(),0,-dScrollx,0);
                       // mScroller.startScroll(0,getScrollX(),0,-dScrollx);
                    }else if(-dScrollx >= mScreenWidth/3 ){
                        mScroller.startScroll(getScrollX(),0,-mScreenWidth-dScrollx,0);
                       // mScroller.startScroll(0,getScrollX(),0,-mScreenWidth-dScrollx);
                        length-=mScreenWidth;
                    }
                }
                break;
        }
        postInvalidate();
        return  true;
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
    }


    @Override
    protected  void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth =MeasureSpec.getMode(widthMeasureSpec);
        int modeHehght = MeasureSpec.getMode(heightMeasureSpec);
        screen = sizeWidth;
       // int width
        int count = getChildCount();
        for(int i = 0;i<count;i++){
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }
}
