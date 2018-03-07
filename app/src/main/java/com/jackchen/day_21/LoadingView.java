package com.jackchen.day_21;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.renderscript.Sampler;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

/**
 * Created by JackChen on 2018/3/7.
 * Decription : 仿58同城数据加载动画
 */

public class LoadingView extends LinearLayout {

    // 上边的形状
    private ShapeView mShapeView;
    // 中间的阴影
    private View mShadowView;
    // 下落的高度
    private int mTranslationDistance ;
    // 动画执行的时间
    private final long ANIMATOR_DURATION = 350;
    // 是否停止动画
    private boolean mIsStopAnimator = false;


    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTranslationDistance = dp2px(80) ;
        // 初始化加载布局
        initLayout() ;
    }


    /**
     * dp - px
     * @param dip
     * @return
     */
    private int dp2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP , dip , getResources().getDisplayMetrics());
    }


    /**
     * 初始化加载布局
     */
    private void initLayout() {
        // 1. 加载写好的 ui_loading_view
        // 1.1 实例化view
//        View loadView = inflate(getContext() , R.layout.ui_loading_view , null) ;
//        // 1.2 添加到该View中
//        addView(loadView);


        // 找一下 插件式换肤资源加载那一节内容
        // this就代表把 ui_loading_view 加载到 LoadingView中
        inflate(getContext() , R.layout.ui_loading_view , this) ;

        mShapeView = (ShapeView) findViewById(R.id.shape_view);
        mShadowView = (View) findViewById(R.id.shadow_view);

        post(new Runnable() {
            @Override
            public void run() {
                // onResume()之后 view绘制流程执行完毕之后  开始动画
                startFallAnimation() ;
            }
        }) ;
    }


    /**
     * 开始下落动画
     */
    private void startFallAnimation() {
        // 如果动画正在进行，就让其停止
        if (mIsStopAnimator){
            return;
        }
        // 下落的位移动画  translationY表示位移动画 Y方向
        // 参数一：动画作用在谁身上 参数二：动画名称 参数三：起点位置 参数四：高度
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mShapeView , "translationY", 0 , mTranslationDistance) ;
        // 配合中间阴影缩小 scaleX表示缩放动画 X方向
        ObjectAnimator scaleAnimatator = ObjectAnimator.ofFloat(mShadowView , "scaleX" , 1f , 0.3f) ;


        AnimatorSet animatorSet = new AnimatorSet();
        // 时长
        animatorSet.setDuration(ANIMATOR_DURATION);
        // 下落的速度应该是越来越快
        animatorSet.setInterpolator(new AccelerateInterpolator());
        // 多个动画一起执行
        animatorSet.playTogether(translationAnimator , scaleAnimatator);
        // 按顺序执行 先执行translationAnimator 后执行scaleAnimatator
//        animatorSet.playSequentially(translationAnimator , scaleAnimatator);

        animatorSet.start();

        // 下落完之后就开始上抛，监听动画执行完毕
        animatorSet.addListener(new AnimatorListenerAdapter() {

            // 动画执行完之后
            @Override
            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
                // 改变形状
                mShapeView.exchange();
                // 下落完之后就开始上抛了
                startUpAnimator() ;
            }
        });
    }


    /**
     *  开始执行上抛动画
     */
    private void startUpAnimator() {
        // 如果动画正在进行，就让其停止
        if(mIsStopAnimator){
            return;
        }

        // 下落的位移动画 translationY 位移动画 Y方向
        //上抛动画
        ObjectAnimator transtationAnimator = ObjectAnimator.ofFloat(mShapeView , "translationY" , mTranslationDistance , 0) ;
        // 配合中间阴影放大
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(mShadowView , "scaleX" , 0.3f , 1f) ;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(ANIMATOR_DURATION) ;
        // 上抛的时候越来越慢
        animatorSet.setInterpolator(new DecelerateInterpolator()); //差值器
        animatorSet.playTogether(transtationAnimator , scaleAnimator);

        // 上抛完之后就开始下落，监听动画执行完毕
        animatorSet.addListener(new AnimatorListenerAdapter() {

            // 动画执行完毕
            @Override
            public void onAnimationEnd(Animator animation) {
                // 上抛完之后就开始下落了
                startFallAnimation();
            }

            // 动画开始执行
            @Override
            public void onAnimationStart(Animator animation) {
                // 开始旋转
                startRotationAnimator() ;

            }
        });

        // 开始动画
        animatorSet.start();
    }

    private void startRotationAnimator() {
        ObjectAnimator rotationAnimator = null ;
        switch (mShapeView.getCurrentShape()){
            case Circle:
            case Square:
                // 180
                rotationAnimator = ObjectAnimator.ofFloat(mShapeView,"rotation",0,180);
                break;
            case Triangle:
                // 120
                rotationAnimator = ObjectAnimator.ofFloat(mShapeView,"rotation",0,-120);
                break;
        }

        rotationAnimator.setDuration(ANIMATOR_DURATION) ;
        rotationAnimator.setInterpolator(new DecelerateInterpolator());
        rotationAnimator.start();

    }



    // 设置控件的隐藏
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(View.INVISIBLE);    //不要再去摆放和计算，少走一些系统的源码， 参考View的绘制流程
        // 清理动画
        mShapeView.clearAnimation();
        mShadowView.clearAnimation();
        // 把LoadingView从父布局中移除
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null){
            parent.removeView(this);
            removeAllViews();
        }
        mIsStopAnimator=true;

    }
}
