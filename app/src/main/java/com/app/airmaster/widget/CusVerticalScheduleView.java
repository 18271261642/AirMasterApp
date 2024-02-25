package com.app.airmaster.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.app.airmaster.R;
import com.app.airmaster.utils.DisplayUtils;
import com.bonlala.widget.utils.MiscUtil;

import timber.log.Timber;

/**
 * 绘制进度条
 *
 * @author Admin
 */
public class CusVerticalScheduleView extends View {

    private static final String TAG = "CusScheduleView";

    //总的进度画笔
    private Paint allSchedulePaint;
    private Path bgPath;
    //当前进度画笔
    private Paint currSchedulePaint;

    private Path currPath;
    private Path path;

    //总的进度颜色
    private int allShceduleColor;
    //当前进度颜色
    private int currShceduleColor;

    //宽度
    private float mWidth,mHeight;

    //所有进度值
    private float allScheduleValue = 200f;
    //当前进度值
    private float currScheduleValue = 0;

    private float animatCurrScheduleValue= 0f;

    private ValueAnimator objectAnimator;


    /**设置显示的文字**/
    private Paint txtPaint;
    private String showTxt;
    /**文字颜色**/
    private int txtColor;
    /**是否显示文字，默认不显示**/
    private boolean isShowTxt =true;



    //分组计时


    public CusVerticalScheduleView(Context context) {
        super(context);
    }

    public CusVerticalScheduleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttar(context,attrs);
    }

    public CusVerticalScheduleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttar(context,attrs);
    }

    //初始化默认配置
    private void initAttar(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CusScheduleView);
        allShceduleColor = typedArray.getColor(R.styleable.CusScheduleView_cus_all_schedule_color,Color.BLUE);
        currShceduleColor = typedArray.getColor(R.styleable.CusScheduleView_cus_curr_schedule_color,Color.BLUE);
        txtColor = typedArray.getColor(R.styleable.CusScheduleView_cus_txt_color,Color.BLACK);
        isShowTxt = typedArray.getBoolean(R.styleable.CusScheduleView_cus_is_show_txt,false);
        typedArray.recycle();

        initPaint();
    }


    //初始化画笔
    private void initPaint() {
        allSchedulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        allSchedulePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        allSchedulePaint.setAntiAlias(true);
        allSchedulePaint.setColor(allShceduleColor);
        allSchedulePaint.setStrokeCap(Paint.Cap.ROUND);
        allSchedulePaint.setTextSize(1f);



        currSchedulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currSchedulePaint.setStyle(Paint.Style.FILL);
        currSchedulePaint.setColor(currShceduleColor);
        currSchedulePaint.setTextSize(1f);
        currSchedulePaint.setStrokeCap(Paint.Cap.SQUARE);
        currSchedulePaint.setAntiAlias(true);

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        txtPaint.setColor(txtColor);
        txtPaint.setTextSize(DisplayUtils.dip2px(getContext(),18f));
        txtPaint.setTextAlign(Paint.Align.CENTER);
        txtPaint.setAntiAlias(true);

        currPath = new Path();
        bgPath = new Path();
        path = new Path();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.translate(0,mHeight);
//        canvas.save();
        drawSchedule(canvas);
    }

    //开始绘制
    RectF currRectf;
    private void drawSchedule(Canvas canvas) {
        float currV = (currScheduleValue * mHeight /allScheduleValue);
        //Timber.e("-----currV="+currV+" "+mHeight);
        if(currV>mHeight){
            currV = mHeight;
        }

        Shader shader = new LinearGradient(0,0,mWidth,mHeight,new int[]{Color.parseColor("#2C2B29"),Color.parseColor("#4A4C4D")},null,Shader.TileMode.CLAMP);
        allSchedulePaint.setShader(shader);
        RectF rectF = new RectF(0,0,mWidth,mHeight);
//        canvas.drawRoundRect(rectF,mHeight/2,mHeight/2,allSchedulePaint);
        allSchedulePaint.setColor(allShceduleColor);
        bgPath.addRoundRect(rectF,0,0, Path.Direction.CCW);
        canvas.drawPath(bgPath,allSchedulePaint);

        if(currRectf == null){
            currRectf = new RectF();
        }


        currRectf.left = 0;
        currRectf.top = mHeight-currV ;
        currRectf.right = mWidth;
        currRectf.bottom = mHeight;


//        RectF currRectf = new RectF(5f,y,currV,0);
        currPath.addRoundRect(currRectf,0,0,Path.Direction.CW);
        path.op(bgPath,currPath,Path.Op.INTERSECT);
        currSchedulePaint.setColor(currShceduleColor);
        canvas.drawPath(path,currSchedulePaint);
        if(isShowTxt){
            if(showTxt != null){
                float txtHeight = MiscUtil.measureTextHeight(txtPaint);
                canvas.drawText(showTxt,mWidth/2,mHeight/2+txtHeight/2,txtPaint);
            }
        }
        currPath.reset();
        bgPath.reset();
        path.reset();
        currSchedulePaint.reset();
    }


    public float getAllScheduleValue() {
        return allScheduleValue;
    }

    public void setAllScheduleValue(float allScheduleValue) {
        this.allScheduleValue = allScheduleValue;
        invalidate();
    }

    public float getCurrScheduleValue() {
        return currScheduleValue;
    }

    public void setCurrScheduleValue(float currScheduleValue) {
        this.currScheduleValue = Math.min(currScheduleValue, allScheduleValue);

        invalidate();
    }



    //属性动画效果
    public void setCurrScheduleValue(float currScheduleValues, final long time){
        float currV = currScheduleValue >= allScheduleValue ? getMeasuredWidth() : currScheduleValue / allScheduleValue * getMeasuredWidth();
        objectAnimator = ObjectAnimator.ofFloat(0,currV);//new TranslateAnimation(0,currV, Animation.ABSOLUTE,Animation.ABSOLUTE);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float tmpV = (float) animation.getAnimatedValue();

               // currScheduleValue = (float) animation.getAnimatedValue();
               // postInvalidate();
            }
        });
        objectAnimator.setStartDelay(500);
        objectAnimator.setDuration(time);
        objectAnimator.setRepeatCount(1);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();

    }

    public String getShowTxt() {
        return showTxt;
    }

    public void setShowTxt(String showTxt) {
        this.showTxt = showTxt;
        invalidate();
    }
}
