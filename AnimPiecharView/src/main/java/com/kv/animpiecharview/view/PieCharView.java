package com.kv.animpiecharview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.kv.animpiecharview.R;

import java.util.List;

public class PieCharView extends View {

    /*
     * Paint
     */
    private Paint mPaint; //UI

    /*
     * UI
     */
    private int mMaxWidth = 0;

    private int mMaxHeight = 0;

    /*
     * area position
     */
    private int mRoundTop = 0;

    private int mRoundLeft = 0;

    /*
     * center position
     */
    private float mCenterX = 0;

    private float mCenterY = 0;

    /*
     * angle
     */
    private float mStartAngle = -90;

    private float mMoveTempAngleBefore = 0;

    private float mMoveTempAngleAfter = 0;

    private float mJudgeAngle = 0;

    /*
     * color
     */
    private int[] mColorArray;

    private RectF mOval = null;

    private Rect mBmpArrowDesRect = null;

    /*
     * adjust event members
     */
    private float mTempStartAngle = 0;

    private double mSrcAngle = 0;

    private double mChangedAngle = 0;

    private float mTargetAngle = 0;

    private float mAdjustDeltaAngle = 0;

    private float mAdjectTempAngle = 0; //adjust temp

    private float mAdjectTimePiece = 0;

    private float mAdjectUsingTime = 0;

    private static final int ADJUST_NORMAL_SPEED = 5;

    private float mSelectPan = 90; //select area pointer

    /*
     * Event member
     */
    private static final int ANIM_INIT = 0;

    private static final int ANGLE_PLUS = 1;

    private static final int ANGLE_MINUS = 2;

    private static final int ANGLE_FLING_PLUS = 3;

    private static final int ANGLE_FLING_MINUS = 4;

    /*
     *  biz
     */
    private boolean mIsInitMeasure = false;

    private boolean mIsInitAnimData = false;

    private boolean mIsInitSetData = false;

    private List<PieCharBean> mPieCharBeanList = null;

    /*
     * Temp members
     */
    private PieCharBean mTempPieCharBean;

    private float mTempTotalAngle = 0;

    private double mDeltaAngle = 0;

    /*
     * Fling
     */
    private double mSrcVelocity = 0; //the velocity when action up

    private double mFlingDeltaAngle = 0; //the total of fling angle when action up

    private double mFlingTempAngle = 0;

    private int mFlingCountTime = 0;

    private double mFlingTime = 0;

    private static final float A = 2f;

    //velocity
    private VelocityTracker mVelocityTracker;

    private int mPointerId;

    private float mScaleMaxVelocity;
    
    private float mFlingVelocityX;
    
    private float mFlingVelocityY;

    /*
     * bmp
     */
    private Bitmap mArrowBmp = null;

    /*
     * listener
     */
    private OnPieCharListener mOnPieCharListener = null;

    /*
     * interceptor
     */
    private DecelerateInterpolator mDInterpolator;

    /*
     * Handler
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ANIM_INIT:
                animInit();
                break;
            case ANGLE_PLUS:
                anglePlus();
                break;
            case ANGLE_MINUS:
                angleMinus();
                break;
            case ANGLE_FLING_PLUS:
                angleFlingPlus();
                break;
            case ANGLE_FLING_MINUS:
                angleFlingMinus();
                break;
            default:
                break;
            }
        }

    };



    /**
     * anim init.
     */
    private void animInit() {
        boolean isFinished = true;
        for (PieCharBean pieCharBean : mPieCharBeanList) {
            if (pieCharBean.getAnimAngleSize() < pieCharBean.getAngleSize()) {
                pieCharBean.setAnimAngleSize(pieCharBean.getAnimAngleSize() + 1.5f);
                isFinished = false;
            }
        }
        if (isFinished) {
            mIsInitAnimData = true;
            judgeArea();
        } else {
            invalidate();
            mHandler.sendEmptyMessageDelayed(ANIM_INIT, 10);
        }
    }

    /**
     * angle plus.
     */
    private void anglePlus() {
        if (mAdjectTimePiece > 0) {
            mAdjectTempAngle = ADJUST_NORMAL_SPEED
                    * mDInterpolator.getInterpolation(mAdjectTimePiece / mAdjectUsingTime);
            mStartAngle += mAdjectTempAngle;

            mAdjectTimePiece -= 1;
            //Log.e("xx", "add start=" + mStartAngle + " target=" + mTargetAngle + " adtemp=" + mAdjectTempAngle + " rest=" + mAdjectTimePiece/mAdjectUsingTime);
            invalidate();
            mHandler.sendEmptyMessageDelayed(ANGLE_PLUS, 10);
        } else {
            mStartAngle = mTargetAngle;
            invalidate();
        }
    }

    /**
     * angle minus.
     */
    private void angleMinus() {
        if (mAdjectTimePiece > 0) {
            mAdjectTempAngle = ADJUST_NORMAL_SPEED
                    * mDInterpolator.getInterpolation(mAdjectTimePiece / mAdjectUsingTime);
            mStartAngle -= mAdjectTempAngle;

            mAdjectTimePiece -= 1;
            //Log.e("xx", "minus start=" + mStartAngle + " target=" + mTargetAngle + " adtemp=" + mAdjectTempAngle + " rest=" + mAdjectTimePiece/mAdjectUsingTime);
            invalidate();
            mHandler.sendEmptyMessageDelayed(ANGLE_MINUS, 10);
        } else {
            mStartAngle = mTargetAngle;
            invalidate();
        }
    }

    /**
     * Angle fling plus.
     */
    private void angleFlingPlus() {
        // S = V0 * t - 1/2 * a t * t
        // V0 - 1/2 * a * t
        mFlingTempAngle = mSrcVelocity - 0.5 * A * mFlingCountTime;

        if (mFlingTempAngle > 0) {
            mStartAngle += mFlingTempAngle;
            mFlingCountTime += 1;
            invalidate();
            mHandler.sendEmptyMessageDelayed(ANGLE_FLING_PLUS, 10);
        } else {
            judgeArea();
        }
    }

    /**
     * Angle fling minus.
     */
    private void angleFlingMinus() {
        // S = V0 * t - 1/2 * a t * t
        // V0 - 1/2 * a * t
        mFlingTempAngle = mSrcVelocity - 0.5 * A * mFlingCountTime;

        if (mFlingTempAngle > 0) {
            mStartAngle -= mFlingTempAngle;
            mFlingCountTime += 1;
            invalidate();
            mHandler.sendEmptyMessageDelayed(ANGLE_FLING_MINUS, 10);
        } else {
            judgeArea();
        }
    }

    public PieCharView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PieCharView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieCharView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mIsInitMeasure) {
            initMeasure(widthMeasureSpec, heightMeasureSpec);
            mIsInitMeasure = true;
        }
    }

    /**
     * init measure.
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    private void initMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMaxWidth = MeasureSpec.getSize(widthMeasureSpec);
        mMaxHeight = MeasureSpec.getSize(heightMeasureSpec);

        //round position
        mRoundTop = (mMaxHeight - mMaxWidth) / 2;
        mRoundLeft = 0;

        //position
        mCenterX = mMaxWidth / 2;
        mCenterY = mRoundTop + mMaxWidth / 2;

        //color
        mPaint = new Paint();
        mColorArray = new int[15];

        mColorArray[0] = Color.rgb(221, 49, 49);
        mColorArray[1] = Color.rgb(244, 95, 29);
        mColorArray[2] = Color.rgb(245, 156, 30);
        mColorArray[3] = Color.rgb(255, 210, 18);
        mColorArray[4] = Color.rgb(157, 217, 62);
        mColorArray[5] = Color.rgb(107, 182, 38);
        mColorArray[6] = Color.rgb(5, 161, 75);
        mColorArray[7] = Color.rgb(6, 202, 204);
        mColorArray[8] = Color.rgb(19, 163, 212);
        mColorArray[9] = Color.rgb(90, 116, 237);
        mColorArray[10] = Color.rgb(56, 79, 220);
        mColorArray[11] = Color.rgb(87, 16, 114);
        mColorArray[12] = Color.rgb(120, 30, 110);
        mColorArray[13] = Color.rgb(139, 29, 163);
        mColorArray[14] = Color.rgb(228, 27, 140);

        //oval area
        mOval = new RectF(mRoundLeft, mRoundTop, mRoundLeft + mMaxWidth, mRoundTop + mMaxWidth);

        //adjust interceptor
        mDInterpolator = new DecelerateInterpolator();
        
        //fling max velocity
        mScaleMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();



        //set arrow bmp
        if (mArrowBmp == null) {
            mArrowBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.anim_pie_arrow);
//            Log.e("xx", mArrowBmp + " " + mArrowBmp.getWidth() + " " + mArrowBmp.getHeight());
            int wholePieWidth = 495;
            float scaleArrowWidth = (float) mMaxWidth/ wholePieWidth * mArrowBmp.getWidth();
            float scaleArrowHeight = (float) mMaxWidth/ wholePieWidth * mArrowBmp.getHeight();
//            Log.e("yy", "w=" + scaleArrowWidth + " h=" + scaleArrowHeight);

            //des rect
            int arrowPadding = getDip2px(1);
            mBmpArrowDesRect = new Rect((int)(mMaxWidth - scaleArrowWidth)/2, (int)(mRoundTop + mMaxWidth - scaleArrowHeight + arrowPadding), (int)(mMaxWidth + scaleArrowWidth)/2, mRoundTop + mMaxWidth + arrowPadding);
        }

    }

    /**
     * Get dip to px.
     * @param dip
     * @return
     */
    private int getDip2px(int dip) {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        return (int) (dm.density * dip + 0.5);
    }

    public void setData(List<PieCharBean> pieCharBeanList) {
        mIsInitSetData = false;
        if (pieCharBeanList != null && !pieCharBeanList.isEmpty()) {

            float percentage = 0;
            for (PieCharBean pieCharBean : pieCharBeanList) {
                //init anim angle size
                pieCharBean.setAnimAngleSize(0);
                if (pieCharBean.getPercentage() <= 0) {
                    throw new IllegalArgumentException("pie element percentage can not smaller than 0");
                }
                percentage += pieCharBean.getPercentage();
            }

            //cal pie percentage equal 100
            if (percentage != 100) {
                throw new IllegalArgumentException("pie char bean equal not 100");
            }

            if (pieCharBeanList.size() > 15) {
                throw new IllegalArgumentException("pie char can not more than 15");
            }
            mPieCharBeanList = pieCharBeanList;

            //calculate pie angle trans by 360
            calculatePieAngle();
        }
        mStartAngle = -90;
        mIsInitAnimData = false;
        mIsInitSetData = true;
        mHandler.sendEmptyMessageDelayed(ANIM_INIT, 500);
    }

    @Override
    protected void
    onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIsInitSetData && mPieCharBeanList != null && !mPieCharBeanList.isEmpty()) {
            if (mIsInitAnimData) {
                mTempTotalAngle = 0;
                //draw pie
                for (int i = 0; i < mPieCharBeanList.size(); i++) {
                    mPaint.setColor(mColorArray[i]);
                    mTempPieCharBean = mPieCharBeanList.get(i);
                    canvas.drawArc(mOval, mStartAngle + mTempTotalAngle, mTempPieCharBean.getAngleSize(), true, mPaint);
                    mTempTotalAngle += mTempPieCharBean.getAngleSize();
                }
            } else {
                //anim init data
                mTempTotalAngle = 0;
                //draw pie
                for (int i = 0; i < mPieCharBeanList.size(); i++) {
                    mPaint.setColor(mColorArray[i]);
                    mTempPieCharBean = mPieCharBeanList.get(i);
                    if (mTempPieCharBean.getAnimAngleSize() >= mTempPieCharBean.getAngleSize()) {
                        mTempPieCharBean.setAnimAngleSize(mTempPieCharBean.getAngleSize());
                    }
                    canvas.drawArc(mOval, mStartAngle + mTempTotalAngle, mTempPieCharBean.getAnimAngleSize(), true,
                            mPaint);
                    mTempTotalAngle += mTempPieCharBean.getAnimAngleSize();
                }
            }
        }

        if (mArrowBmp != null) {
            canvas.drawBitmap(mArrowBmp, null, mBmpArrowDesRect, null);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsInitAnimData) {
            return false;
        }
        //regist velocity
        acquireVelocityTracker(event);
        final VelocityTracker verTracker = mVelocityTracker;
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            //for velocity
            mPointerId = event.getPointerId(0);

            mHandler.removeMessages(ANGLE_MINUS);
            mHandler.removeMessages(ANGLE_PLUS);
            mHandler.removeMessages(ANGLE_FLING_PLUS);
            mTempStartAngle = mStartAngle;

            //src angle
            mSrcAngle = Math.atan2(mCenterY - event.getY(0), event.getX(0) - mCenterX) * 180 / Math.PI;

            break;
        case MotionEvent.ACTION_MOVE:
            //velocity
            verTracker.computeCurrentVelocity(10, mScaleMaxVelocity);
            mFlingVelocityX = verTracker.getXVelocity(mPointerId);
            mFlingVelocityY = verTracker.getYVelocity(mPointerId);

            //changed angle
            mChangedAngle = Math.atan2(mCenterY - event.getY(0), event.getX(0) - mCenterX) * 180 / Math.PI;
            countAngleChanged();
            break;
        case MotionEvent.ACTION_UP:

            //judge velocity
            judgeVelocity();

            //release velocity
            releaseVelocityTracker();

            break;
        case MotionEvent.ACTION_CANCEL:
          //release velocity
            releaseVelocityTracker();
            break;
        default:
            break;
        }
        return true;
    }

    /**
     * release velocity tracker.
     */
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * get the velocity.
     * @param event
     */
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * calculate angle changed
     */
    private void countAngleChanged() {
        mDeltaAngle = mChangedAngle - mSrcAngle;

        mMoveTempAngleBefore = mStartAngle;
        mStartAngle = (float) (mTempStartAngle - mDeltaAngle) % 360;
        mMoveTempAngleAfter = mStartAngle;
        postInvalidate();
    }

    /**
     * judge velocity
     */
    private void judgeVelocity() {
        mSrcVelocity = Math.sqrt(Math.pow(mFlingVelocityX, 2) + Math.pow(mFlingVelocityY, 2)) / Math.PI;
        
        //define the max srcVelocity
        if (mSrcVelocity > 30) {
            mSrcVelocity = 30;
        }
        
        mFlingTime = mSrcVelocity / A; //  size of  10ms

        mFlingDeltaAngle = (mSrcVelocity - 0.5 * A * mFlingTime) * mFlingTime; // distance of fling

        //        Log.e("xx", "v=" + mSrcVelocity + "t=" + mFlingTime + "dis=" + mFlingDeltaAngle);

        //        Log.e("xx", "bf=" + mMoveTempAngleBefore + " af=" + mMoveTempAngleAfter);
        mJudgeAngle = mMoveTempAngleAfter - mMoveTempAngleBefore;

        if (mJudgeAngle > 0 && Math.abs(mJudgeAngle) < 180 || mJudgeAngle < 0 && Math.abs(mJudgeAngle) > 180) {
            //            Log.e("xx", "add");
            mFlingCountTime = 1;
            mHandler.sendEmptyMessage(ANGLE_FLING_PLUS);
        } else if (mJudgeAngle < 0 && Math.abs(mJudgeAngle) < 180 || mJudgeAngle > 0 && Math.abs(mJudgeAngle) > 180) {
            //            Log.e("xx", "minus");
            mFlingCountTime = 1;
            mHandler.sendEmptyMessage(ANGLE_FLING_MINUS);
        } else {
            //            Log.e("xx", "haha");
            judgeArea();
        }

    }

    /**
     * judge area.
     */
    private void judgeArea() {
        float matchMiddleAngle = 0;
        for (PieCharBean pieCharBean : mPieCharBeanList) {
            matchMiddleAngle = pieCharBean.isContainSelect(mStartAngle, mSelectPan);
            if (matchMiddleAngle != PieCharBean.FLAG_NO_MATCH) {
                mTargetAngle = mSelectPan - matchMiddleAngle + mStartAngle; // the target according to start angle delta
                if (mOnPieCharListener != null) {
                    mOnPieCharListener.onSelect(pieCharBean);
                }
                break;
            }
        }

        // adjust plus
        if (mTargetAngle > mStartAngle) {
            mAdjustDeltaAngle = mTargetAngle - mStartAngle; //delta angle
            mAdjectUsingTime = mAdjustDeltaAngle / ADJUST_NORMAL_SPEED; // using time
            mAdjectTimePiece = mAdjectUsingTime;
            mHandler.sendEmptyMessageDelayed(ANGLE_PLUS, 10);
        } else if (mTargetAngle < mStartAngle) { //adjust minus
            mAdjustDeltaAngle = mStartAngle - mTargetAngle; //delta angle
            mAdjectUsingTime = mAdjustDeltaAngle / ADJUST_NORMAL_SPEED; // using time
            mAdjectTimePiece = mAdjectUsingTime; // time piece
            mHandler.sendEmptyMessageDelayed(ANGLE_MINUS, 10);
        }
    }

    /**
     * avoid pie angle trans by 360 missing piece
     * Calculate pie angle.
     */
    private void calculatePieAngle() {
        float totalPieAngle = 0;
        PieCharBean pieCharBean;
        if (mPieCharBeanList.size() > 1) {
            for (int i = 0; i < mPieCharBeanList.size() - 1; i++) {
                pieCharBean = mPieCharBeanList.get(i);
                pieCharBean.setAngleSize(pieCharBean.getPercentage() * 360 / 100);
                pieCharBean.setAngleStart(totalPieAngle);
                totalPieAngle += pieCharBean.getAngleSize();
                pieCharBean.setAngleEnd(totalPieAngle);
            }
        }

        //the last
        pieCharBean = mPieCharBeanList.get(mPieCharBeanList.size() - 1);
        pieCharBean.setAngleStart(totalPieAngle);
        pieCharBean.setAngleSize(360 - totalPieAngle);
        pieCharBean.setAngleEnd(360);
    }


    public void release() {
        mIsInitAnimData = false;
        mIsInitSetData = false;
        mIsInitMeasure = false;
        mHandler.removeMessages(ANGLE_PLUS);
        mHandler.removeMessages(ANGLE_MINUS);
        mHandler.removeMessages(ANGLE_FLING_PLUS);
        mHandler.removeMessages(ANGLE_FLING_MINUS);
        if (mPieCharBeanList != null) {
            mPieCharBeanList.clear();
            mPieCharBeanList = null;
        }
        if (mArrowBmp != null) {
            mArrowBmp.recycle();
            mArrowBmp = null;
        }
    }


    public void setOnPieCharListener(OnPieCharListener onPieCharListener) {
        this.mOnPieCharListener = onPieCharListener;
    }

    public static interface OnPieCharListener {
        public void onSelect(PieCharBean pieCharBean);
    }
}
