package com.example.filemanager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class mTextView extends View {
    private String text;
    private Paint paint;
    private Paint translationPaint;
    public mTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.mTextView);
        int textColor=typedArray.getColor(R.styleable.mTextView_mTextColor,Color.RED);
        float textSize=typedArray.getDimensionPixelOffset(R.styleable.mTextView_mTextSize,30);
        typedArray.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(textColor);
        paint.setTextSize(textSize);

        translationPaint=new Paint();
        translationPaint.setColor(Color.TRANSPARENT);

    }
    public void setText(String str){
        text=str;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width= MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics metrics=paint.getFontMetrics();
        float f=(metrics.bottom-metrics.top)/2-metrics.bottom;


        Bitmap bitmap= Bitmap.createBitmap(getHeight(),getWidth(), Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas=new Canvas(bitmap);

        bitmapCanvas.drawText(text,20,bitmap.getHeight()/2+f,paint);

        Bitmap bitmap2=Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2=new Canvas(bitmap2);
        Matrix matrix=new Matrix();
        matrix.setRotate(90,getWidth()/2,getWidth()/2);

        canvas2.drawBitmap(bitmap,matrix,new Paint());

        if(!bitmap.isRecycled()){
            bitmap.recycle();
        }

        Matrix matrix1=new Matrix();
        matrix1.setRotate(180,getWidth()/2,getHeight()/2);
        canvas.drawBitmap(bitmap2,matrix1,new Paint());

        if(!bitmap2.isRecycled()){
            bitmap2.recycle();
        }
    }
}
