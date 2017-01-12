package com.android.esdudnik.animatedprogressbar.ui.view;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

/**
 * author   : Eugene Dudnik
 * date     : 1/11/17
 * e-mail   : esdudnik@gmail.com
 */
public class AnimatingProgressBar extends ProgressBar {

    private static final Interpolator DEFAULT_INTERPOLATOR = new LinearInterpolator();
    private static final int DEFAULT_ANIMATION_TIME = 2000;

    private ValueAnimator mProgressAnimator;
    private ValueAnimator mSecondaryProgressAnimator;
    private boolean mAnimate = true;
    private int mAnimationTime = DEFAULT_ANIMATION_TIME;

    public AnimatingProgressBar(Context context) {
        this(context, null);
    }

    public AnimatingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatingProgressBar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public AnimatingProgressBar(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle, defStyleRes);
        initValueAnimators();
    }

    public boolean isAnimate() {
        return mAnimate;
    }

    public void setAnimate(boolean animate) {
        this.mAnimate = animate;
    }

    public int getAnimationTime() {
        return mAnimationTime;
    }

    public void setAnimationTime(int mAnimationTime) {
        this.mAnimationTime = mAnimationTime;
        updateAnimationTime();
    }

    public void setProgressAnimator(ValueAnimator mProgressAnimator) {
        this.mProgressAnimator = mProgressAnimator;
    }

    public void setSecondaryProgressAnimator(ValueAnimator mSecondaryProgressAnimator) {
        this.mSecondaryProgressAnimator = mSecondaryProgressAnimator;
    }

    @Override
    public synchronized void setProgress(int progress) {
        if (!mAnimate) {
            super.setProgress(progress);
            return;
        }
        mProgressAnimator.cancel();
        mProgressAnimator.setIntValues(getProgress(), progress);
        mProgressAnimator.start();
        Log.v("Footer bar", "Footer bar progress onAnimationStart = " + String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public synchronized void setSecondaryProgress(int secondaryProgress) {
        if (!mAnimate) {
            super.setSecondaryProgress(secondaryProgress);
            return;
        }
        mSecondaryProgressAnimator.cancel();
        mSecondaryProgressAnimator.setIntValues(getProgress(), secondaryProgress);
        mSecondaryProgressAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mProgressAnimator != null)
            mProgressAnimator.cancel();
        if (mSecondaryProgressAnimator != null)
            mSecondaryProgressAnimator.cancel();
    }

    private void initValueAnimators() {
        mProgressAnimator = ValueAnimator.ofInt(getProgress(), getProgress());
        mProgressAnimator.setInterpolator(DEFAULT_INTERPOLATOR);
        mProgressAnimator.setDuration(mAnimationTime);
        mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                AnimatingProgressBar.super.setProgress((Integer) animation.getAnimatedValue());
            }

        });
        mSecondaryProgressAnimator = ValueAnimator.ofInt(getProgress(), getProgress());
        mSecondaryProgressAnimator.setDuration(mAnimationTime);
        mSecondaryProgressAnimator.setInterpolator(DEFAULT_INTERPOLATOR);
        mSecondaryProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                AnimatingProgressBar.super.setSecondaryProgress((Integer) animation
                        .getAnimatedValue());
            }
        });
    }

    private void updateAnimationTime() {
        mProgressAnimator.setDuration(mAnimationTime);
        mSecondaryProgressAnimator.setDuration(mAnimationTime);
    }

    public void addProgressListener(AnimatorListenerAdapter animatorListenerAdapter) {
        if (mProgressAnimator != null) {
            mProgressAnimator.addListener(animatorListenerAdapter);
        }
    }

    public void addSecondaryProgressListener(AnimatorListenerAdapter animatorListenerAdapter) {
        if (mSecondaryProgressAnimator != null) {
            mSecondaryProgressAnimator.addListener(animatorListenerAdapter);
        }
    }

}
