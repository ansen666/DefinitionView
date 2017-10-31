package com.ansen.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ansen.ansenview.R;

import java.util.Random;

/**
 * 自定义View
 * Created by  ansen
 * Create Time 2016-12-10
 */
public class MyView extends View{
    private MyThread myThread;

    private Paint paint;//画笔

    private RectF rectF=new RectF(190,190,380,380);
    private int sweepAngle=0;//弧的当前度数
    private int sweepAngleAdd=20;//弧每次增加度数
    private Random random=new Random();
    private boolean running=true;//控制循环

    public MyView(Context context) {
        this(context,null);
    }

    public MyView(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    //初始化
    private void  init(Context context,AttributeSet attrs){
        //获取自定义属性的值
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.customStyleView);
        sweepAngleAdd=typedArray.getInt(R.styleable.customStyleView_sweepAngleAdd,0);
        typedArray.recycle();

        paint=new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.i("MyView","onDraw");
        if(null==myThread){
            myThread=new MyThread();
            myThread.start();
        }else{
            //第一个参数是RectF   左上的x y坐标   右下的x y坐标
            //第二个参数是 弧形的开始角度
            //第三个参数是 弧形的结束角度
            //第四个参数是 true:画扇形   false:画弧线
            //第五个参数是 画笔
            canvas.drawArc(rectF, 0, sweepAngle, true, paint);
        }
    }

    //开启一个子线程绘制ui
    private class MyThread extends Thread{
        @Override
        public void run() {
            while(running){
                logic();
                postInvalidate();//重新绘制,会调用onDraw
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void logic() {
        sweepAngle+=sweepAngleAdd;//每次增加弧度

        //随机设置画笔的颜色
        int r=random.nextInt(255);
        int g=random.nextInt(255);
        int b=random.nextInt(255);
        paint.setARGB(255, r, g, b);

        if(sweepAngle>=360){//如果弧度大于360°  从头开始
            sweepAngle=0;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        running=false;//销毁View的时候设置成false,退出无限循环
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        //获得它的父容器为它设置的测量模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY){//指定宽度或者match_parent
            width = widthSize;
        } else{
            width = (int) (getPaddingLeft() + getPaddingRight() + rectF.width()*2);
        }

        if (heightMode == MeasureSpec.EXACTLY){//指定高度或者match_parent
            height = heightSize;
        } else{
            height = (int) (getPaddingTop()+getPaddingBottom()+rectF.height()*2);
        }
        setMeasuredDimension(width, height);
    }
}
