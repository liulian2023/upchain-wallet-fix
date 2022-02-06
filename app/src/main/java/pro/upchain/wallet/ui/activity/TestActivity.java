package pro.upchain.wallet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;

public class TestActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_test2;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
    }

    @OnClick({R.id.button})
    public void  onClick(View v){
        switch (v.getId()){
            case R.id.button:
//                expandCollapseView(next_iv,next_iv.getVisibility() == View.VISIBLE?false:true);
                break;
        }
    }
    private synchronized void expandCollapseView(@NotNull View view, boolean expandView)
    {
        //detect if view is expanded or collapsed
        boolean isViewExpanded = view.getVisibility() == View.VISIBLE;

        //Collapse view
        if (isViewExpanded && !expandView)
        {
            int finalWidth = view.getWidth();
            ValueAnimator valueAnimator = slideAnimator(finalWidth, 0, view);
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            valueAnimator.start();
        }
        //Expand view
        else if (!isViewExpanded && expandView)
        {
            view.setVisibility(View.VISIBLE);

            int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            view.measure(widthSpec, heightSpec);
            int width = view.getMeasuredWidth();
            ValueAnimator valueAnimator = slideAnimator(0, width, view);
            valueAnimator.start();
        }
    }

    @NotNull
    private ValueAnimator slideAnimator(int start, int end, final View view) {

        final ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(valueAnimator -> {
            // Update Height
            int value = (Integer) valueAnimator.getAnimatedValue();

            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = value;
            view.setLayoutParams(layoutParams);
        });
        animator.setDuration(100);
        return animator;
    }
}