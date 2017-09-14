package com.x.leo.timelineview;

import android.animation.Animator;
import android.view.View;

/**
 * @作者:My
 * @创建日期: 2017/5/25 15:26
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class ViewAnimatorBean {
    public View mView;
    public Animator mAnimator;

    public ViewAnimatorBean(View view, Animator animation) {
        mView = view;
        mAnimator = animation;
    }
}
