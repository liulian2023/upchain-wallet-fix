package pro.upchain.wallet.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.Timer;
import java.util.TimerTask;

import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.Utils;


public class BasePopupWindow extends PopupWindow implements View.OnClickListener, TextWatcher {
    public View rootView;
    public Context mContext;
   private static final int FULL_SCREEN_FLAG =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
    public BasePopupWindow(Context context) {
        super(context);
        mContext=context;
        initView();
        initPop();
    }
    /**
     *
     * @param
     */
    public void initPop() {
        this.setContentView(rootView);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
//        this.getContentView().setSystemUiVisibility(FULL_SCREEN_FLAG);
//        this.setFocusable(focusable);
        //软键盘不会挡着popupwindow
//        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.update();
        ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
        this.setBackgroundDrawable(dw);
/*        this.getContentView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                BasePopupWindow.this.getContentView().setSystemUiVisibility(FULL_SCREEN_FLAG);
            }
        });*/
        this.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Activity activity = (Activity) BasePopupWindow.this.mContext;
                Utils.darkenBackground(activity,1f);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Utils.hideSoftKeyBoard(activity);
                    }
                },200);

                if(mOnDismissListener !=null){
                    mOnDismissListener.onDissmiss();
                }


            }
        });
    }
    public void showToast(String str){
            ToastUtils.showToast(str);

    }

    public void initView() {

    }

    public void errorRefresh(){
    }

    @Override
    public void onClick(View v) {
        if(mOnPopClickListener!=null){
            mOnPopClickListener.onPopClick(v);
        }
    }
    public View getView(int viewId){
        return rootView.findViewById(viewId);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
    if(mOnTextChangedListener!=null){
        mOnTextChangedListener.onTextChange(editable);
    }
    }

    public interface  OnPopClickListener{
        void onPopClick(View view);
    }
    public   OnPopClickListener mOnPopClickListener;

    public void setOnPopClickListener(OnPopClickListener mOnPopClickListener) {
        this.mOnPopClickListener = mOnPopClickListener;
    }

    public interface  OnRecycleItemClick{
        void onPopItemClick(View view, int  position);
    }
 public   OnRecycleItemClick mOnPopItemClick;

    public void setOnPopItemClick(OnRecycleItemClick mOnPopItemClick) {
        this.mOnPopItemClick = mOnPopItemClick;
    }
/*    public void setFocusableAndupdate(){
        this.setFocusable(true);
        this.update();
    }*/
    public interface OnDismissListener {
        void onDissmiss();
    }
  public OnDismissListener mOnDismissListener;

    public void setmOnDismissListener(OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }
    public interface OnTextChangedListener {
        void onTextChange(Editable editable);
    }
    public OnTextChangedListener mOnTextChangedListener;

    public void setmOnTextChangedListener(OnTextChangedListener mOnTextChangedListener) {
        this.mOnTextChangedListener = mOnTextChangedListener;
    }

    public void showSoftKeyBoard(EditText editText){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //设置可获得焦点
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                //请求获得焦点
                editText.requestFocus();

                //调用系统输入法
                InputMethodManager inputManager = (InputMethodManager) editText
                        .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        },200);
    }
}
