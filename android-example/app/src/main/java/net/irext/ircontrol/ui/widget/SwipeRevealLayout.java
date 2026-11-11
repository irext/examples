package net.irext.ircontrol.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Filename:       SwipeRevealLayout.java
 * Revised:        Date: 2026-11-11
 * Revision:       Revision: 1.0
 * <p>
 * Description:    A two-child container that reveals a hidden action panel (e.g. delete)
 *                 by dragging the foreground content panel horizontally.
 *                 The first child is the revealed (behind) panel, the second is the content.
 *                 Touch model:
 *                   - when closed: the container only claims horizontal drag gestures, so a
 *                     normal tap falls through to the parent ListView (item click);
 *                   - when revealed: the container claims every gesture, so a tap on the
 *                     content closes the row, a tap on the action area triggers its click,
 *                     and a drag settles open/closed.
 * <p>
 * Revision log:
 * 2026-11-11: created by strawmanbobi
 */
public class SwipeRevealLayout extends FrameLayout {

    public interface OnRevealListener {
        void onRevealed(SwipeRevealLayout layout);
    }

    private View mRevealView;
    private View mContentView;

    private int mRevealWidth;
    private int mTouchSlop;

    private float mDownX;
    private float mDownY;
    private boolean mDragging;
    private boolean mRevealed;

    private ValueAnimator mAnimator;
    private OnRevealListener mOnRevealListener;

    public SwipeRevealLayout(Context context) {
        super(context);
        init(context);
    }

    public SwipeRevealLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeRevealLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalStateException("SwipeRevealLayout requires exactly two children");
        }
        mRevealView = getChildAt(0);
        mContentView = getChildAt(1);
        // the reveal view is triggered by performClick from this container, so it must not
        // consume raw touches itself (otherwise a closed row would swallow right-edge taps)
        mRevealView.setClickable(false);
        // keep the reveal panel fully hidden while closed, so it never peeks out from behind
        // the content panel in a freshly bound / recycled row
        mRevealView.setVisibility(INVISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRevealWidth = mRevealView.getMeasuredWidth();
    }

    public void setOnRevealListener(OnRevealListener listener) {
        this.mOnRevealListener = listener;
    }

    public boolean isRevealed() {
        return mRevealed;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                mDragging = false;
                cancelAnimator();
                // a revealed row is fully owned by the container for this gesture
                return mRevealed;

            case MotionEvent.ACTION_MOVE:
                // only reached while closed; start claiming once a clear horizontal drag shows
                float dx = ev.getX() - mDownX;
                float dy = ev.getY() - mDownY;
                if (!mDragging && Math.abs(dx) > mTouchSlop && Math.abs(dx) > Math.abs(dy)) {
                    mDragging = true;
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                return mDragging;

            default:
                return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // claimed on DOWN only when revealed; keep the gesture alive
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - mDownX;
                if (!mDragging && Math.abs(dx) < mTouchSlop) {
                    return true;
                }
                mDragging = true;
                // show the reveal panel as soon as a drag begins so it can follow the finger
                if (dx < 0) {
                    mRevealView.setVisibility(VISIBLE);
                }
                float start = mRevealed ? -mRevealWidth : 0f;
                mContentView.setTranslationX(clamp(start + dx));
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                if (!mDragging) {
                    handleTap(event);
                } else {
                    float tx = mContentView.getTranslationX();
                    animateTo(tx <= -mRevealWidth / 2f ? -mRevealWidth : 0f,
                            tx <= -mRevealWidth / 2f);
                }
                mDragging = false;
                return true;

            default:
                return super.onTouchEvent(event);
        }
    }

    // a tap that reached the container only happens while the row is revealed
    private void handleTap(MotionEvent event) {
        if (!mRevealed) {
            return;
        }
        if (event.getX() >= getWidth() - mRevealWidth) {
            // tapped the revealed action area -> forward as a click
            mRevealView.performClick();
        } else {
            // tapped the content area -> collapse the row back
            animateTo(0f, false);
        }
    }

    private float clamp(float value) {
        if (value > 0f) {
            return 0f;
        }
        if (value < -mRevealWidth) {
            return -mRevealWidth;
        }
        return value;
    }

    private void animateTo(float target, boolean revealed) {
        cancelAnimator();
        float from = mContentView.getTranslationX();
        if (revealed) {
            mRevealView.setVisibility(VISIBLE);
        }
        mAnimator = ValueAnimator.ofFloat(from, target);
        mAnimator.setDuration(180);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mContentView.setTranslationX((float) animation.getAnimatedValue());
            }
        });
        if (!revealed) {
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRevealView.setVisibility(INVISIBLE);
                }
            });
        }
        mAnimator.start();
        mRevealed = revealed;
        if (revealed && null != mOnRevealListener) {
            mOnRevealListener.onRevealed(this);
        }
    }

    private void cancelAnimator() {
        if (null != mAnimator) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    public void revealClose() {
        if (mRevealed) {
            animateTo(0f, false);
        }
    }

    // reset instantly without animation, used when a row view is recycled
    public void resetImmediately() {
        cancelAnimator();
        mDragging = false;
        mRevealed = false;
        mContentView.setTranslationX(0f);
        mRevealView.setVisibility(INVISIBLE);
    }
}
