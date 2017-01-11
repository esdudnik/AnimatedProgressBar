package com.android.esdudnik.animatedprogressbar.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.esdudnik.animatedprogressbar.R;

import java.text.DecimalFormat;

/**
 * author   : Eugene Dudnik
 * date     : 1/11/17
 * e-mail   : esdudnik@gmail.com
 */
public class CashbackBar extends RelativeLayout {

    private static final int INTEGER_PART_STEP_ANIMATION_DURATION = 500;
    private static final int DECIMAL_PART_STEP_ANIMATION_DURATION = 100;

    private static final String FINAL_VALUE_PARAMETER_NAME = "FINAL_VALUE_PARAMETER_NAME";
    private static final String CURRENT_VALUE_PARAMETER_NAME = "CURRENT_VALUE_PARAMETER_NAME";

    TextSwitcher tsCashbackTotal;
    AnimatingProgressBar animatingProgressBar;

    private float mCurrentProgress = 0;
    private int mIntegerPartDuration = INTEGER_PART_STEP_ANIMATION_DURATION;
    private int mDecimalPartDuration = DECIMAL_PART_STEP_ANIMATION_DURATION;

    private ViewRefreshHandler viewRefreshHandler;
    private Animation mFadeInAnimation;
    private Animation mFadeOutAnimation;

    public CashbackBar(Context context) {
        this(context, null);
    }

    public CashbackBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CashbackBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CashbackBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cashback_bar, this, true);

        if (isInEditMode()) return;

        tsCashbackTotal = (TextSwitcher) findViewById(R.id.tsCashbackTotal);
        tsCashbackTotal.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView myText = (TextView) inflater.inflate(R.layout.textview, null);
                return myText;
            }
        });
        animatingProgressBar = (AnimatingProgressBar) findViewById(R.id.progChallenge);
        animatingProgressBar.setProgress((int) mCurrentProgress);
        tsCashbackTotal.setCurrentText(String.valueOf(mCurrentProgress));
        viewRefreshHandler = new ViewRefreshHandler();
        mFadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_anim);
        mFadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_anim);
    }

    public void setIntegerPartStepAnimationDuration(int integerPartStepAnimationDuration) {
        this.mIntegerPartDuration = integerPartStepAnimationDuration;
    }

    public void setDecimalPartStepAnimationDuration(int decimalPartStepAnimationDuration) {
        this.mDecimalPartDuration = decimalPartStepAnimationDuration;
    }

    public void addValue(float newValue) {
        animateProgress(newValue);
        animateValue(newValue);
    }

    private void animateProgress(float newValue) {
        int animationTime = (int) (((int) (newValue - mCurrentProgress)) * mIntegerPartDuration +
                (Math.floor(mCurrentProgress) * mDecimalPartDuration));
        animatingProgressBar.setAnimationTime(animationTime);
        animatingProgressBar.setAnimate(true);
        animatingProgressBar.setProgress((int) newValue);
    }

    private void animateValue(float newValue) {
        if (newValue - mCurrentProgress > 1)
            startIntegerPartAnimation(newValue);
        else
            startDecimalPartAnimation(newValue, mCurrentProgress);
    }

    private void startIntegerPartAnimation(final float finalValue) {
        Bundle args = new Bundle();
        args.putFloat(FINAL_VALUE_PARAMETER_NAME, finalValue);
        args.putFloat(CURRENT_VALUE_PARAMETER_NAME, mCurrentProgress);
        mFadeOutAnimation.setDuration(mIntegerPartDuration);
        mFadeInAnimation.setDuration(mIntegerPartDuration);
        tsCashbackTotal.setInAnimation(mFadeInAnimation);
        tsCashbackTotal.setOutAnimation(mFadeOutAnimation);
        viewRefreshHandler.executePerInterval(new IntegerPartRunnable(tsCashbackTotal, args, new IntegerPartRunnable.RunnableStopListener() {
            @Override
            public void stopped(float value) {
                if (finalValue - (int) finalValue > 0) {
                    startDecimalPartAnimation(finalValue, value);
                } else{
                    mCurrentProgress = value;
                }
            }
        }), mIntegerPartDuration);
    }

    private void startDecimalPartAnimation(float finalValue, float mCurrentValue) {
        Bundle args = new Bundle();
        args.putFloat(FINAL_VALUE_PARAMETER_NAME, finalValue);
        args.putFloat(CURRENT_VALUE_PARAMETER_NAME, mCurrentValue);
        mFadeOutAnimation.setDuration(mDecimalPartDuration);
        mFadeInAnimation.setDuration(mDecimalPartDuration);
        tsCashbackTotal.setInAnimation(mFadeInAnimation);
        tsCashbackTotal.setOutAnimation(mFadeOutAnimation);
        viewRefreshHandler.executePerInterval(new DecimalPartRunnable(tsCashbackTotal, args, new DecimalPartRunnable.RunnableStopListener() {
            @Override
            public void stopped(float value) {
                mCurrentProgress = value;
            }
        }), mDecimalPartDuration);
    }

    /**
     * uses a static inner-class to avoid the runnable object holding a reference to the activity.
     */
    private static class IntegerPartRunnable extends ViewRefreshHandler.ViewRunnable<TextSwitcher> {

        private float mCount;
        private float mFinalValue;
        private DecimalFormat mDecimalFormat;
        private RunnableStopListener mRunnableStopListener;

        IntegerPartRunnable(TextSwitcher textSwitcher, Bundle args, RunnableStopListener runnableStopListener) {
            super(textSwitcher, args);
            mCount = args.getFloat(CURRENT_VALUE_PARAMETER_NAME);
            mCount = (float) Math.floor(mCount);
            mFinalValue = args.getFloat(FINAL_VALUE_PARAMETER_NAME);
            mDecimalFormat = new DecimalFormat("#.0");
            mRunnableStopListener = runnableStopListener;
        }

        @Override
        protected void run(TextSwitcher textSwitcher, Bundle args) {
            mCount++;
            textSwitcher.setText(mDecimalFormat.format(mCount));
            if (mFinalValue - mCount < 1) {
                terminate();
                if (mRunnableStopListener != null) {
                    mRunnableStopListener.stopped(Float.parseFloat(mDecimalFormat.format(mCount)));
                }
            }
        }

        interface RunnableStopListener {
            void stopped(float finalValue);
        }
    }

    private static class DecimalPartRunnable extends ViewRefreshHandler.ViewRunnable<TextSwitcher> {

        private float mCount;
        private float mFinalValue;
        private DecimalFormat mDecimalFormat;
        private RunnableStopListener mRunnableStopListener;

        DecimalPartRunnable(TextSwitcher textSwitcher, Bundle args, RunnableStopListener runnableStopListener) {
            super(textSwitcher, args);
            mCount = args.getFloat(CURRENT_VALUE_PARAMETER_NAME);
            mFinalValue = args.getFloat(FINAL_VALUE_PARAMETER_NAME);
            mDecimalFormat = new DecimalFormat("#.0");
            mRunnableStopListener = runnableStopListener;
        }

        @Override
        protected void run(TextSwitcher textSwitcher, Bundle args) {
            mCount = (float) (mCount + 0.1);
            textSwitcher.setText(mDecimalFormat.format(mCount));
            if (mCount >= mFinalValue) {
                terminate();
                if (mRunnableStopListener != null) {
                    mRunnableStopListener.stopped(Float.parseFloat(mDecimalFormat.format(mCount)));
                }
            }
        }

        interface RunnableStopListener {
            void stopped(float finalValue);
        }
    }
}
