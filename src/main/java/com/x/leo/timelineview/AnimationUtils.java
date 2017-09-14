package com.x.leo.timelineview;

import android.animation.Animator;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @作者:My
 * @创建日期: 2017/5/25 10:01
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class AnimationUtils {
   //private static final String TAG = "AnimationUtils";
    private static List<Animation> sAnimationList;
    private static Handler sHandler;
    private static boolean isAnimationRunning = false;
    private static List<ViewAnimatorBean> sAnimatorList;

    //animation 会同时启动
    public static void addAnimation(Animation animation) {
        if (sAnimationList == null) {
            synchronized (AnimationUtils.class) {
                if (sAnimationList == null) {
                    sAnimationList = new ArrayList<>();
                }
                if (sHandler == null) {
                    sHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 0) {
                                if (sAnimationList != null && sAnimationList.size() > 0) {
                                    Animation remove = null;
                                    synchronized (AnimationUtils.class) {
                                        remove = sAnimationList.remove(0);
                                    }
                                    if (remove != null) {
                                        startAnimation(remove);
                                    }
                                } else {
                                    isAnimationRunning = false;
                                }
                            }
                        }
                    };
                }
            }
        }
        synchronized (AnimationUtils.class) {
            if (isAnimationRunning) {
                sAnimationList.add(animation);
            } else {
                isAnimationRunning = true;
                startAnimation(animation);
            }
        }
    }

    public synchronized static void addAnimators(View view, Animator animation) {

        if (sAnimatorList == null) {
            sAnimatorList = new LinkedList<>();
        }
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 0) {
                        if (sAnimatorList != null && sAnimatorList.size() > 0) {
                            ViewAnimatorBean remove = null;
                            synchronized (AnimationUtils.class) {
                                remove = sAnimatorList.remove(0);
                            }
                            if (remove != null) {
                                startAnimator(remove.mView, remove.mAnimator);
                            }
                            //Log.w(TAG, "handleMessage: " + remove.mAnimator + "view:" + remove.mView + "==time:" + System.currentTimeMillis());
                        } else {
                            isAnimationRunning = false;
                        }
                    }
                }
            };
        }


        if (isAnimationRunning) {
           // Log.w(TAG, "addAnimators: add:" + view.toString() + "==time:" + System.currentTimeMillis());
            deleteDuplicate(view);
            sAnimatorList.add(new ViewAnimatorBean(view, animation));
        } else {
           // Log.w(TAG, "addAnimators: run:" + view.toString() + "==time:" + System.currentTimeMillis());
            isAnimationRunning = true;
            startAnimator(view, animation);
        }

    }

    private static void deleteDuplicate(View view) {
        ListIterator<ViewAnimatorBean> viewAnimatorBeanListIterator = sAnimatorList.listIterator();
        while (viewAnimatorBeanListIterator.hasNext()) {
            if (viewAnimatorBeanListIterator.next().mView == view) {
                viewAnimatorBeanListIterator.remove();
            }
        }
    }

    private static void startAnimator(final View view, final Animator remove) {


        remove.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //Log.w(TAG, "onAnimationStart: " + view.toString() + System.currentTimeMillis());
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (sHandler != null) {
                    sHandler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        remove.start();
    }

    private static boolean isViewDisplayed(View view) {
        int width = view.getResources().getDisplayMetrics().widthPixels;
        int height = view.getResources().getDisplayMetrics().heightPixels;
        if (view.getLeft() >= width
                || view.getRight() <= 0
                || view.getTop() >= height
                || view.getBottom() <= 0) {
            return false;
        }
        return true;
    }

    private static void startAnimation(Animation animation) {
        if (animation.hasStarted() && !animation.hasEnded()) {
            animation.cancel();
        }
        final Animation.AnimationListener listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (sHandler != null) {
                    sHandler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        animation.setAnimationListener(listener);
        animation.start();
    }
}
