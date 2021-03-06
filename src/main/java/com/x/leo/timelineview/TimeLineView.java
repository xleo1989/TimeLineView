package com.x.leo.timelineview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @作者:My
 * @创建日期: 2017/5/24 14:52
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class TimeLineView extends android.support.v7.widget.AppCompatTextView {

    private static final String TAG = "TimeLineView";
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private int mRadius;
    private Point mCenterPointer = new Point();
    private int mStrokeWidth;
    private Paint mPaint;
    private int mDirectionStyle;
    private int mOffset;

    private boolean drawText;

    private int mMarkStyle;
    private int mCircleBigActColor;
    private int mCircleSmallActColor;
    private int mCurrentStatus;
    private int mRadiusInner;
    private int mCompleteRes;
    private boolean notShowPointLine;
    private int mMarkPosition;
    private int lastState;
    private boolean doAnimation;
    private int mInactiveRes;
    private int mActiveRes;
    private ValueAnimator animator;
    private float markerAnimatorValue = 1;
    private float stroke2AnimatorValue = 1;
    private float stroke1AnimatorValue = 1;
    private int mCircleInactColor;
    private int nextState;
    private ColorStateList mStrokeColor;
    private boolean isInactiveSmall = false;

    public void setLastState(@CurrentState int lastState) {
        if (this.lastState != lastState) {
            this.lastState = lastState;
            invalidate();
        }
    }

    public void setNextState(int nextState) {
        if (this.nextState != nextState) {
            this.nextState = nextState;
            invalidate();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true,
            value = {
                    TEXTSTYLE, CIRCLESTYLE
            })
    public @interface MarkStyle {
    }

    public static final int TEXTSTYLE = 0;
    public static final int CIRCLESTYLE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({COMPLETE, ACTIVE, INACTIVE})
    public @interface CurrentState {
    }

    public static final int COMPLETE = 2;
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true,
            value = {
                    BEGIN,
                    CENTER,
                    END
            })
    public @interface RelativeEnum {
    }

    public static final int BEGIN = 0;
    public static final int CENTER = 1;
    public static final int END = 1 << 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true,
            value = {
                    HORIZONTAL,
                    VERTICAL
            })
    public @interface Direction_Style {
    }

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POINT_START, POINT_MIDDLE, POINT_END})
    public @interface MarkPosition {
    }

    public static final int POINT_START = 1;
    public static final int POINT_MIDDLE = 0;
    public static final int POINT_END = 2;

    private int mStrokeStyle = 0;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FORWARD, BACKWARD, NONE})
    public @interface StrokeStyle {
    }

    public static final int FORWARD = 1 << 3;
    public static final int BACKWARD = 2 << 3;
    public static final int NONE = 0;
    public static final int CURRENT = 3 << 3;

    private int mRelativeGravity;

    public TimeLineView(Context context) {
        this(context, null);
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TimeLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeLineView);
        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.TimeLineView_strokeWidth, 10);
        mRelativeGravity = typedArray.getInt(R.styleable.TimeLineView_relativeGravity, TimeLineView.BEGIN);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.TimeLineView_radius, 0);
        mDirectionStyle = typedArray.getInt(R.styleable.TimeLineView_directionStyle, -1);
        mOffset = typedArray.getDimensionPixelSize(R.styleable.TimeLineView_offset, 0);
        if (typedArray.hasValue(R.styleable.TimeLineView_strokeColor)) {
            try {
                mStrokeColor = typedArray.getColorStateList(R.styleable.TimeLineView_strokeColor);
            } catch (Throwable e) {
                mStrokeColor = new ColorStateList(new int[][]{}, new int[]{typedArray.getColor(R.styleable.TimeLineView_strokeColor, Color.GRAY)});
            }
        }
        if (typedArray.hasValue(R.styleable.TimeLineView_strokeStyle)) {
            mStrokeStyle = typedArray.getInt(R.styleable.TimeLineView_strokeStyle, 0) << 3;
        }
        isInactiveSmall = typedArray.getBoolean(R.styleable.TimeLineView_smallInactive, false);
        mMarkStyle = typedArray.getInt(R.styleable.TimeLineView_markerStyle, 0);
        notShowPointLine = typedArray.getBoolean(R.styleable.TimeLineView_notShowPointLine, false);
        lastState = typedArray.getInt(R.styleable.TimeLineView_lastStatus, mCurrentStatus);
        doAnimation = typedArray.getBoolean(R.styleable.TimeLineView_doAnimation, false);
        if (doAnimation) {
            markerAnimatorValue = 0;
            stroke2AnimatorValue = 0;
            stroke1AnimatorValue = 0;
        }
        if (notShowPointLine) {
            mMarkPosition = typedArray.getInt(R.styleable.TimeLineView_markPosition, 0);
        }
        switch (mMarkStyle) {
            case TEXTSTYLE:
                break;
            case CIRCLESTYLE:
                mCircleBigActColor = typedArray.getColor(R.styleable.TimeLineView_circleActiveBigger, Color.GRAY);
                mCircleSmallActColor = typedArray.getColor(R.styleable.TimeLineView_circleActive, mCircleBigActColor);
                mCircleInactColor = typedArray.getColor(R.styleable.TimeLineView_circleInactive, Color.GRAY);
                mRadiusInner = typedArray.getDimensionPixelSize(R.styleable.TimeLineView_radiusInner, mRadius - 10);
                mCurrentStatus = typedArray.getInt(R.styleable.TimeLineView_CurrentStatus, 0);
                drawText = typedArray.getBoolean(R.styleable.TimeLineView_drawText, false);
                mCompleteRes = typedArray.getResourceId(R.styleable.TimeLineView_completeRes, 0);
                mInactiveRes = typedArray.getResourceId(R.styleable.TimeLineView_inactiveRes, 0);
                mActiveRes = typedArray.getResourceId(R.styleable.TimeLineView_activeRes, 0);
                break;
        }
        typedArray.recycle();
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void setIsDrawText(boolean isDrawText) {
        drawText = isDrawText;
    }

    public boolean isDrawText() {
        return drawText;
    }


//    @Override
//    protected void onVisibilityChanged(View changedView, int visibility) {
//        if (changedView == this && visibility == View.VISIBLE && doAnimation) {
//            // Log.w(TAG, changedView.toString() + ":enterAnimation" + "==time:" + System.currentTimeMillis());
//            enterAnimation();
//        } else {
//            //  Log.w(TAG, "onVisibilityChanged: ");
//        }
//    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (doAnimation) {
            enterAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (doAnimation) {
            stopAnimation();
            markerAnimatorValue = 0;
            stroke2AnimatorValue = 0;
            stroke1AnimatorValue = 0;
        }
    }

    private void stopAnimation() {
        if (animator != null && (animator.isRunning() || animator.isStarted())) {
            animator.cancel();
        }
    }

    private int durationFaction = 180;

    private void enterAnimation() {
        if (notShowPointLine) {
            switch (mMarkPosition) {
                case POINT_END:
                    animator = ValueAnimator.ofFloat(-1f, 0.0f, 1f);
                    animator.setDuration(2 * durationFaction);
                    break;
                case POINT_MIDDLE:
                    animator = ValueAnimator.ofFloat(-1f, 0.0f, 1f, 2);
                    animator.setDuration(3 * durationFaction);
                    break;
                case POINT_START:
                    animator = ValueAnimator.ofFloat(0.0f, 1f, 2);
                    animator.setDuration(2 * durationFaction);
                    break;
                default:
                    throw new IllegalArgumentException("error mark positiion");
            }
        } else {
            animator = ValueAnimator.ofFloat(-1f, 0.0f, 1f, 2);
            animator.setDuration(3 * durationFaction);
        }
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                if (animatedValue < 0) {
                    stroke1AnimatorValue = 1 + animatedValue;
                } else if (animatedValue <= 1) {
                    if (stroke1AnimatorValue != 1) {
                        stroke1AnimatorValue = 1;
                    }
                    markerAnimatorValue = animatedValue;
                } else if (animatedValue <= 2) {
                    if (markerAnimatorValue != 1) {
                        markerAnimatorValue = 1;
                    }
                    stroke2AnimatorValue = animatedValue - 1;
                }
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (((isStroke1Show() && stroke1AnimatorValue != 1) || markerAnimatorValue != 1 || (isStroke2Show() && stroke2AnimatorValue != 1)) && getWindowVisibility() != View.GONE) {
                    enterAnimation();
                }
            }
        });
        AnimationUtils.addAnimators(this, animator);
    }

    private boolean isStroke2Show() {
        return !(notShowPointLine && mMarkPosition == POINT_END);
    }

    private boolean isStroke1Show() {
        return !(notShowPointLine && mMarkPosition == POINT_START);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
        if (mRadius == 0) {
            mRadius = Math.min(mMeasuredHeight - getPaddingTop() - getPaddingBottom(), mMeasuredWidth - getPaddingLeft() - getPaddingRight()) / 2;
        }
        if (mDirectionStyle == -1) {
            if (mMeasuredHeight - getPaddingTop() - getPaddingBottom() > mMeasuredWidth - getPaddingLeft() - getPaddingRight()) {
                mDirectionStyle = TimeLineView.VERTICAL;
            } else {
                mDirectionStyle = TimeLineView.HORIZONTAL;
            }
        }
        if (mDirectionStyle == TimeLineView.VERTICAL) {
            mCenterPointer.x = mMeasuredWidth / 2 + getPaddingLeft();
            switch (mRelativeGravity) {
                case TimeLineView.BEGIN:
                    mOffset = mOffset < 0 ? 0 : mOffset;
                    mCenterPointer.y = mRadius + getPaddingTop() + mOffset;
                    break;
                case TimeLineView.CENTER:
                    mCenterPointer.y = mMeasuredHeight / 2 + mOffset;
                    break;
                case TimeLineView.END:
                    mOffset = mOffset > 0 ? 0 : mOffset;
                    mCenterPointer.y = mMeasuredHeight - getPaddingBottom() - mRadius + mOffset;
                    break;
            }
        } else {
            mCenterPointer.y = mMeasuredHeight / 2 + getPaddingTop();
            switch (mRelativeGravity) {
                case TimeLineView.BEGIN:
                    mOffset = mOffset < 0 ? 0 : mOffset;
                    mCenterPointer.x = getPaddingLeft() + mRadius + mOffset;
                    break;
                case TimeLineView.CENTER:
                    mCenterPointer.x = mMeasuredWidth / 2 + mOffset;
                    break;
                case TimeLineView.END:
                    mOffset = mOffset > 0 ? 0 : mOffset;
                    mCenterPointer.x = mMeasuredWidth - getPaddingRight() - mRadius + mOffset;
                    break;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //lines no animator
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        if (notShowPointLine) {
            switch (mMarkPosition) {
                case POINT_START:
                    drawSecondLine(canvas);
                    break;
                case POINT_END:
                    drawFirstLine(canvas);

                    break;
                case POINT_MIDDLE:
                    drawFirstLine(canvas);
                    drawSecondLine(canvas);
                    break;
            }
        } else {
            drawFirstLine(canvas);
            drawSecondLine(canvas);
        }
        canvas.save();
        //text and circles do animator
        //canvas.rotate(markerAnimatorValue);
        switch (mDirectionStyle) {
            case HORIZONTAL:
                canvas.scale(markerAnimatorValue, markerAnimatorValue, mCenterPointer.x, mCenterPointer.y);
                break;
            case VERTICAL:
                canvas.scale(markerAnimatorValue, markerAnimatorValue, mCenterPointer.x, mCenterPointer.y);
                break;
            default:
                throw new IllegalArgumentException("wrong direction");
        }
        CharSequence text = getText();
        mPaint.setStrokeWidth(0f);
        switch (mMarkStyle) {
            case TEXTSTYLE:
                switch (mCurrentStatus) {
                    case INACTIVE:
                        mPaint.setColor(mCircleInactColor);
                        break;
                    case ACTIVE:
                        mPaint.setColor(mCircleSmallActColor);
                        break;
                    case COMPLETE:
                        mPaint.setColor(mCircleInactColor);
                        break;
                }

                canvas.drawCircle(mCenterPointer.x, mCenterPointer.y, mRadius, mPaint);
                drawIndexText(canvas, text);
                break;
            case CIRCLESTYLE:
                switch (mCurrentStatus) {
                    case COMPLETE:
                        mPaint.setColor(mCircleBigActColor);
                        if (mCompleteRes == 0) {
                            canvas.drawCircle(mCenterPointer.x, mCenterPointer.y, mRadius, mPaint);
                        }
                        if (mCompleteRes != 0) {
                            drawDrawable(canvas, mCompleteRes);
                        }
                        break;
                    case INACTIVE:
                        mPaint.setColor(mCircleInactColor);
                        if (mInactiveRes == 0) {
                            int localRadius = isInactiveSmall ? mRadiusInner : mRadius;
                            canvas.drawCircle(mCenterPointer.x, mCenterPointer.y, localRadius, mPaint);
                        } else {
                            drawDrawable(canvas, mInactiveRes);
                        }
                        if (drawText) {
                            drawIndexText(canvas, text);
                        }
                        break;
                    case ACTIVE:
                        mPaint.setColor(mCircleBigActColor);
                        if (mActiveRes == 0) {
                            canvas.drawCircle(mCenterPointer.x, mCenterPointer.y, mRadius, mPaint);
                            mPaint.setColor(mCircleSmallActColor);
                            canvas.drawCircle(mCenterPointer.x, mCenterPointer.y, mRadiusInner, mPaint);
                        } else {
                            drawDrawable(canvas, mActiveRes);
                        }
                        if (drawText) {
                            drawIndexText(canvas, text);
                        }
                        break;
                }

                break;
        }
        canvas.restore();
    }

    private void drawDrawable(Canvas canvas, int res) {
        int localRadius = isInactiveSmall ? mRadiusInner : mRadius;
        Drawable drawable = getContext().getResources().getDrawable(res);
        Rect desRect = new Rect(mCenterPointer.x - localRadius, mCenterPointer.y - localRadius, mCenterPointer.x + localRadius, mCenterPointer.y + localRadius);
        drawable.setBounds(desRect);
        drawable.draw(canvas);
    }


    private void drawFirstLine(Canvas canvas) {
        obtainStrokeColorWithStatusAndStyle(true);
        switch (mDirectionStyle) {
            case TimeLineView.HORIZONTAL:
                float localEndX = getPaddingLeft() + stroke1AnimatorValue * (mCenterPointer.x - getPaddingLeft()) - markerAnimatorValue * (mRadiusInner - mStrokeWidth);
                canvas.drawLine(getPaddingLeft(), mCenterPointer.y, localEndX, mCenterPointer.y, mPaint);
                break;
            case TimeLineView.VERTICAL:
                float localEndY = getPaddingTop() + stroke1AnimatorValue * (mCenterPointer.y - getPaddingLeft()) - markerAnimatorValue * (mRadiusInner - mStrokeWidth);
                canvas.drawLine(mCenterPointer.x, getPaddingTop(), mCenterPointer.x, localEndY, mPaint);
                break;
        }
    }

    private void obtainStrokeColorWithStatusAndStyle(boolean isFirst) {
        switch (mStrokeStyle) {
            case NONE:
                mPaint.setColor(mStrokeColor.getDefaultColor());
                break;
            case FORWARD:
                if (isFirst) {
                    obtainByState(mCurrentStatus);
                } else {
                    obtainByState(nextState);
                }
                break;
            case BACKWARD:
                if (isFirst) {
                    obtainByState(lastState);
                } else {
                    obtainByState(mCurrentStatus);
                }
                break;
            case CURRENT:
                obtainByState(mCurrentStatus);
                break;
            default:
                throw new IllegalArgumentException("error stroke style");
        }
    }

    private void obtainByState(int status) {
        switch (status) {
            case COMPLETE:
                mPaint.setColor(mStrokeColor.getColorForState(new int[]{android.R.attr.state_selected}, mStrokeColor.getDefaultColor()));
                break;
            case INACTIVE:
                mPaint.setColor(mStrokeColor.getDefaultColor());
                break;
            case ACTIVE:
                mPaint.setColor(mStrokeColor.getColorForState(new int[]{android.R.attr.state_activated}, mStrokeColor.getDefaultColor()));
                break;
        }
    }


    private void drawSecondLine(Canvas canvas) {
        obtainStrokeColorWithStatusAndStyle(false);
        switch (mDirectionStyle) {
            case TimeLineView.HORIZONTAL:
                float localEndX = mCenterPointer.x + mRadiusInner - mStrokeWidth + stroke2AnimatorValue * (mMeasuredWidth - getPaddingRight() - (mCenterPointer.x + mRadiusInner - mStrokeWidth));
                canvas.drawLine(mCenterPointer.x + mRadiusInner - mStrokeWidth, mCenterPointer.y, localEndX, mCenterPointer.y, mPaint);
                break;
            case TimeLineView.VERTICAL:
                float localEndY = stroke2AnimatorValue * (mMeasuredHeight - getPaddingBottom() - (mCenterPointer.y + mRadiusInner - mStrokeWidth)) + mCenterPointer.y + mRadiusInner - mStrokeWidth;
                canvas.drawLine(mCenterPointer.x, mCenterPointer.y + mRadiusInner - mStrokeWidth, mCenterPointer.x, localEndY, mPaint);
                break;
        }
    }

    public void setMarkPosition(@MarkPosition int position) {
        if (mMarkPosition != position) {
            mMarkPosition = position;
            invalidate();
        }
    }

    private void drawIndexText(Canvas canvas, CharSequence text) {
        if (text != null && text.length() != 0) {
            int length = text.length();
            TextPaint paint = getPaint();
            paint.setColor(getTextColors().getColorForState(getDrawableState(), Color.BLACK));
            Rect bounds = new Rect();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.getTextBounds(text.toString(), 0, length, bounds);
            canvas.drawText(text.toString(), mCenterPointer.x, mCenterPointer.y + bounds.height() / 2, paint);
//            canvas.drawLine(mCenterPointer.x - bounds.width() / 2,0f,mCenterPointer.x - bounds.width() / 2,mMeasuredHeight,paint);
//            canvas.drawLine(0,mCenterPointer.y + bounds.height() / 2,mMeasuredWidth,mCenterPointer.y + bounds.height() / 2,paint);
//            canvas.drawLine(mCenterPointer.x + bounds.width() / 2,0,mCenterPointer.x + bounds.width() / 2,mMeasuredHeight,paint);
//            canvas.drawLine(0,mCenterPointer.y - bounds.height() / 2,mMeasuredWidth,mCenterPointer.y - bounds.height() / 2,paint);
        }
    }

    public void setCurrentStatus(@CurrentState int status) {
        if (mMarkStyle == CIRCLESTYLE && mCurrentStatus != status) {
            mCurrentStatus = status;
            invalidate();
        }
    }

    public
    @CurrentState
    int getCurrentStatus() {
        return mCurrentStatus;
    }

    public void toggleStatus() {
        if (mMarkStyle == CIRCLESTYLE) {
            switch (mCurrentStatus) {
                case INACTIVE:
                    mCurrentStatus = ACTIVE;
                    break;
                case ACTIVE:
                    mCurrentStatus = COMPLETE;
                    break;
                case COMPLETE:
                    break;
            }
            invalidate();
        }
    }

    public void setRadius(int radius) {
        mRadius = Utils.dp2Px(getContext(), radius);
    }

    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = Utils.dp2Px(getContext(), strokeWidth);
    }

    public void setDirectionStyle(@Direction_Style int directionStyle) {
        mDirectionStyle = directionStyle;
    }

    public void setRelativeGravity(@RelativeEnum int relativeGravity) {
        mRelativeGravity = relativeGravity;
    }

    public int getRadius() {
        return mRadius;
    }

    public int getStrokeWidth() {
        return mStrokeWidth;
    }

    public int getDirectionStyle() {
        return mDirectionStyle;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setOffset(int offset) {
        mOffset = Utils.dp2Px(getContext(), offset);
    }
}

