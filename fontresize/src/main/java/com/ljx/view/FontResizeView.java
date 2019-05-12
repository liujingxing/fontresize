package com.ljx.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ljx.view.fontresize.R;

/**
 * 字体大小调整控件
 * User: ljx
 * Date: 2018/05/11
 * Time: 09:53
 */
public class FontResizeView extends View {

    //默认线条颜色
    private static final int DEFAULT_LINE_COLOR = Color.parseColor("#222222");
    boolean isCoincide;//是否重合

    private int width, height;//FontAdjustView的宽高
    private float minSize;//最小字体大小
    private float maxSize;//最大字体大小
    private float standardSize;//标准字体大小

    private String leftText;   //左边文本
    private String middleText; //中间文本
    private String rightText;  //右边文本

    private int leftTextColor;     //左边文本颜色
    private int middleTextColor;   //中间文本颜色
    private int rightTextColor;    //右边文本颜色

    private int totalGrade;//总的等级
    private int standardGrade;//标准等级
    private int lineColor; //线条颜色
    private int horizontalLineLength;   //横向线段长度
    private int verticalLineLength;     //纵向线段长度
    private int lineStrokeWidth;        //线条宽度
    private int lineAverageWidth;       //每段水平线条的长度

    private int   sliderGrade;       //滑块等级
    private int   sliderColor;       //滑块颜色
    private int   sliderShadowColor; //滑块阴影颜色
    private Point sliderPoint;       //滑块位置

    private Paint                mPaint;//画笔
    private Line                 mHorizontalLine;   //一条横线
    private Line[]               mVerticalLines;    //n条竖线
    private GestureDetector      mGestureDetector;//手势检测
    private OnFontChangeListener onFontChangeListener;
    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            isCoincide = sliderPoint.coincide(e.getX(), e.getY());
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.onDown(e);
        }

        /**
         * 单击事件
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            final Line horizontalLine = mHorizontalLine;
            float x = e.getX() > horizontalLine.stopX ? horizontalLine.stopX : e.getX();
            moveSlider(x - horizontalLine.startX, true);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (isCoincide) {
                float x = sliderPoint.getX();
                setSliderPointX(x - distanceX, false);
                postInvalidate();
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    public FontResizeView(Context context) {
        this(context, null);
    }

    public FontResizeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontResizeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int padding = dp2px(35);
        setPadding(padding, padding, padding, padding);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FontResizeView);
        minSize = ta.getDimension(R.styleable.FontResizeView_minSize, dp2px(15));
        maxSize = ta.getDimension(R.styleable.FontResizeView_maxSize, dp2px(25));
        totalGrade = ta.getInt(R.styleable.FontResizeView_totalGrade, 6);
        standardGrade = ta.getInt(R.styleable.FontResizeView_standardGrade, 2);
        if (standardGrade < 1 || standardGrade > 6) {
            standardGrade = 1;
        }
        sliderGrade = standardGrade;

        leftText = ta.getString(R.styleable.FontResizeView_leftText);
        if (TextUtils.isEmpty(leftText)) leftText = "A";
        middleText = ta.getString(R.styleable.FontResizeView_middleText);
        if (TextUtils.isEmpty(middleText))
            middleText = context.getString(R.string.font_resize_standard);
        rightText = ta.getString(R.styleable.FontResizeView_rightText);
        if (TextUtils.isEmpty(rightText)) rightText = "A";

        leftTextColor = ta.getColor(R.styleable.FontResizeView_leftTextColor, Color.BLACK);
        middleTextColor = ta.getColor(R.styleable.FontResizeView_middleTextColor, Color.BLACK);
        rightTextColor = ta.getColor(R.styleable.FontResizeView_rightTextColor, Color.BLACK);

        lineColor = ta.getColor(R.styleable.FontResizeView_lineColor, DEFAULT_LINE_COLOR);
        lineStrokeWidth = ta.getDimensionPixelOffset(R.styleable.FontResizeView_lineStrokeWidth, dp2px(0.5f));
        horizontalLineLength = ta.getDimensionPixelOffset(R.styleable.FontResizeView_horizontalLineLength, -1);
        verticalLineLength = ta.getDimensionPixelOffset(R.styleable.FontResizeView_verticalLineLength, -1);

        sliderColor = ta.getColor(R.styleable.FontResizeView_sliderColor, Color.WHITE);
        sliderShadowColor = ta.getColor(R.styleable.FontResizeView_sliderShadowColor, Color.GRAY);
        float sliderRadius = ta.getDimension(R.styleable.FontResizeView_sliderRadius, dp2px(25));
        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        width = getResources().getDisplayMetrics().widthPixels;
        height = dp2px(140);

        standardSize = (maxSize - minSize) / (totalGrade - 1) * (standardGrade - 1) + minSize;

        mHorizontalLine = new Line();
        mVerticalLines = new Line[totalGrade];
        for (int i = 0; i < mVerticalLines.length; i++) {
            mVerticalLines[i] = new Line();
        }

        sliderPoint = new Point(sliderRadius);
        mGestureDetector = new GestureDetector(context, gestureListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);

        int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (specWidthMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                width = Math.min(width, specWidthSize);
                break;
            case MeasureSpec.EXACTLY:
                width = specWidthSize;
                break;
        }
        switch (specHeightMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                height = Math.min(height, specHeightSize);
                break;
            case MeasureSpec.EXACTLY:
                height = specHeightSize;
                break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (horizontalLineLength == -1)
            horizontalLineLength = w - getPaddingLeft() - getPaddingRight();
        if (verticalLineLength == -1) verticalLineLength = dp2px(10);

        lineAverageWidth = horizontalLineLength / (totalGrade - 1);
        //初始化横线起点位置
        int horizontalLineStartX = (width - horizontalLineLength) / 2;
        int horizontalLineStartY = (int) (height * 0.6);
        //初始化横线起点、终点位置
        mHorizontalLine.set(horizontalLineStartX, horizontalLineStartY, horizontalLineStartX + horizontalLineLength, horizontalLineStartY);
        float lineAverageWidth = horizontalLineLength * 1.0f / (totalGrade - 1);
        final Line[] verticalLines = mVerticalLines;
        for (int i = 0; i < verticalLines.length; i++) {
            float startX = horizontalLineStartX + lineAverageWidth * i;
            verticalLines[i].set(startX, horizontalLineStartY - verticalLineLength / 2f, startX, horizontalLineStartY + verticalLineLength / 2f);
        }
        //初始化滑块的等级及位置
        sliderPoint.setGrade(sliderGrade - 1);
        setSliderPointX(verticalLines[sliderGrade - 1].startX, true);
        sliderPoint.setY(verticalLines[sliderGrade - 1].startY + verticalLines[sliderGrade - 1].getHeight() / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final Line horizontalLine = mHorizontalLine;
        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(lineStrokeWidth);

        //绘制横线
        canvas.drawLine(horizontalLine.startX, horizontalLine.startY, horizontalLine.stopX, horizontalLine.stopY, mPaint);

        //绘制线段
        for (Line line : mVerticalLines) {
            canvas.drawLine(line.startX, line.startY, line.stopX, line.stopY, mPaint);
        }

        //绘制左边文本
        mPaint.setColor(leftTextColor);
        mPaint.setTextSize(minSize);
        float width = mPaint.measureText(leftText);
        float startY = horizontalLine.startY - dp2px(20);
        canvas.drawText(leftText, horizontalLine.startX - width / 2, startY, mPaint);

        //绘制中间文本
        mPaint.setColor(middleTextColor);
        mPaint.setTextSize(standardSize);
        width = mPaint.measureText(middleText);
        float startX = mVerticalLines[standardGrade - 1].startX - width / 2;
        if (standardGrade == 1 || standardGrade == totalGrade) {
            startY -= dp2px(7) + standardSize;
        }
        canvas.drawText(middleText, startX, startY, mPaint);

        //绘制右边文本
        mPaint.setColor(rightTextColor);
        mPaint.setTextSize(maxSize);
        width = mPaint.measureText(rightText);
        canvas.drawText(rightText, horizontalLine.stopX - width / 2, startY, mPaint);

        //绘制 滑块
        mPaint.setColor(sliderColor);
        float radius = sliderPoint.getRadius();
        mPaint.setShadowLayer(10f, 2, 2, sliderShadowColor);
        canvas.drawCircle(sliderPoint.getX(), sliderPoint.getY(), radius, mPaint);
        mPaint.setShadowLayer(0, 0, 0, sliderShadowColor);//关闭阴影
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP && isCoincide) {
            //手指抬起时，滑块不在整点，则移动到整点
            float x = sliderPoint.getX() - mHorizontalLine.startX;
            moveSlider(x, false);
        }
        return true;
    }

    /**
     * 移动滑块
     */
    public void moveSlider(float destX, final boolean isClick) {
        int grade = (int) destX / lineAverageWidth;//目标等级
        float remainder = destX % lineAverageWidth;
        if (remainder > lineAverageWidth / 2) grade++;

        final int tempGrade = grade;
        int gradeDiffer = Math.abs(sliderPoint.getGrade() - tempGrade);
        if (gradeDiffer == 0) {
            if (isClick) return;
            gradeDiffer = 1;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(sliderPoint.getX(), mVerticalLines[tempGrade].startX);
        animator.setDuration(100 + gradeDiffer * 30);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                //如果是单击触发的移动事件，动画未结束前仅更新滑块的位置，动画结束后再更新滑块的等级
                setSliderPointX(value, isClick);
                postInvalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setSliderPointX(mVerticalLines[tempGrade].startX, false);
            }
        });
        animator.start();
    }

    /**
     * 设置滑块X坐标的统一入口
     *
     * @param onlySetX 是否仅仅更新滑块的位置
     */
    public void setSliderPointX(float x, boolean onlySetX) {
        float horizontalLineStartX = mHorizontalLine.startX;
        float horizontalLineStopX = mHorizontalLine.stopX;
        if (x < horizontalLineStartX) {
            x = horizontalLineStartX;
        } else if (x > horizontalLineStopX) {
            x = horizontalLineStopX;
        }
        sliderPoint.setX(x);
        if (onlySetX) return;

        int oldGrade = sliderPoint.getGrade();
        int newGrade = (int) (x - horizontalLineStartX) / lineAverageWidth;
        if (oldGrade == newGrade) return;
        sliderPoint.setGrade(newGrade);

        if (onFontChangeListener != null) {
            float size = (maxSize - minSize) / (totalGrade - 1);
            float sp = (minSize + size * newGrade) / getResources().getDisplayMetrics().scaledDensity;
            onFontChangeListener.onFontChange(sp);
        }
    }

    public void setFontSize(float fontSize) {
        fontSize *= getResources().getDisplayMetrics().scaledDensity;
        sliderGrade = (int) ((fontSize - minSize) / ((maxSize - minSize) / (totalGrade - 1))) + 1;
        if (sliderGrade < 0) sliderGrade = 1;
        if (sliderGrade > totalGrade) sliderGrade = totalGrade;
    }

    public int dp2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setOnFontChangeListener(OnFontChangeListener onFontChangeListener) {
        this.onFontChangeListener = onFontChangeListener;
    }

    public interface OnFontChangeListener {
        /**
         * @param fontSize 字体大小、单位sp
         */
        void onFontChange(float fontSize);
    }

    class Point {
        float x;
        float y;
        float radius;

        int grade;

        Point(float radius) {
            this.radius = radius;
        }

        float getX() {
            return x;
        }

        void setX(float x) {
            this.x = x;
        }

        float getY() {
            return y;
        }

        void setY(float y) {
            this.y = y;
        }

        float getRadius() {
            return radius;
        }

        int getGrade() {
            return grade;
        }

        void setGrade(int grade) {
            this.grade = grade;
        }

        /**
         * 是否重合
         */
        boolean coincide(float movingX, float movingY) {
            //开方，如果两点之间的距离小于规定的半径r则定义为重合
            return Math.sqrt((x - movingX) * (x - movingX)
                    + (y - movingY) * (y - movingY)) < radius + dp2px(20);
        }
    }

    class Line {

        float startX;
        float startY;
        float stopX;
        float stopY;

        void set(float startX, float startY, float stopX, float stopY) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }

        float getHeight() {
            return Math.abs(stopY - startY);
        }
    }
}
