package com.daowang.app.dwbroker.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.daowang.app.dwbroker.R;
import com.daowang.app.dwbroker.adapter.DropDownAdapter;
import com.daowang.app.dwbroker.utils.CheckUtils;
import com.daowang.app.dwbroker.utils.DpUtils;
import com.daowang.app.dwbroker.view.holder.DropDownMenuHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hugo on 2016/5/11.
 */
public class DropDownMenu extends LinearLayout {
    private static final int RBT_KEY_STATE = R.id.mine_radio_bt;
    private static final String TAG = "DropDownMenu";

    //菜单字体属性
    private float mMenuTextSize = 12;
    private int mMenuTextColorState = R.color.select_condit_text;

    //菜单栏高度
    private float mMenuHeight = 38;

    //箭头的icon
    private int mMenuArrowIcon = R.drawable.select_arrow;
    //分隔线的颜色
    private int mMenuDividerColor = 0xffe5e5e5;
    //菜单栏背景色
    private int mMenuBgColor = 0x00000000;

    //下拉表内容的高度
    private float mPopupHeight = 300;

    //弹出菜单的父布局
    private LinearLayout mContainerView;

    //弹出菜单父布局
    private FrameLayout popupMenuViews;
    //菜单栏
    private RadioGroup mRadioGroup;
    //菜单的item
    private List<RadioButton> mMenuBts = new ArrayList<RadioButton>();
    private View maskView;
    private DropDownAdapter mContentAdapter;
    private DropDownMenuHolder mHolder;
    private List<String> mMenuTitles;

    //当前点击的菜单栏位置
    private int mCurrentMenuPos;

    public DropDownMenu(Context context) {
        this(context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        initView();
        initParams(context, attrs);
        initEvent();
    }

    private void initEvent() {
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDropDownMenu();
                clearAllState();
            }
        });
    }

    private void initView() {

        //下拉表的内容部分
        mContainerView = new LinearLayout(this.getContext());
        mContainerView.setOrientation(VERTICAL);
        mContainerView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mContainerView.setBackgroundColor(Color.TRANSPARENT);


        //弹出菜单的部分
        popupMenuViews = new FrameLayout(this.getContext());
        popupMenuViews.setLayoutParams(new ViewGroup.LayoutParams(-1, DpUtils.viewDp2Px(mPopupHeight)));
        popupMenuViews.setBackgroundColor(Color.WHITE);

        //空白view
        maskView = new View(this.getContext());
        maskView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        maskView.setBackgroundColor(Color.TRANSPARENT);


    }

    /**
     * 添加标题和下划线
     *
     * @param menuTitles
     */
    public void setMenuView(List<String> menuTitles) {

        if (menuTitles == null || menuTitles.size() == 0) {
            return;
        }
        //保存一份菜单栏的数据
        mMenuTitles = menuTitles;

        mRadioGroup = new RadioGroup(this.getContext());
        //设置属性
        mRadioGroup.setOrientation(HORIZONTAL);
        mRadioGroup.setLayoutParams(new ViewGroup.LayoutParams(-1, DpUtils.viewDp2Px(mMenuHeight)));
        //mRadioGroup.setBackgroundColor(getResources().getColor(R.color.blue_theme));
        mRadioGroup.setBackgroundColor(mMenuBgColor);

        //数据清空
        mMenuBts.clear();

        for (int i = 0; i < menuTitles.size(); i++) {

            RadioButton rbt = new RadioButton(this.getContext());

            rbt.setTextSize(mMenuTextSize);

            ColorStateList colors = getResources().getColorStateList(mMenuTextColorState);
            rbt.setTextColor(colors);
            rbt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(mMenuArrowIcon), null);
            rbt.setCompoundDrawablePadding(DpUtils.viewDp2Px(5));
            rbt.setPadding(DpUtils.viewDp2Px(5), 0, DpUtils.viewDp2Px(5), 0);

            rbt.setButtonDrawable(android.R.color.transparent);

            rbt.setText(menuTitles.get(i));
            rbt.setSingleLine();
            rbt.setEllipsize(TextUtils.TruncateAt.END);
            rbt.setGravity(Gravity.CENTER);


            FrameLayout.LayoutParams rbtParams = new FrameLayout.LayoutParams(-2, -1);
            rbtParams.gravity = Gravity.CENTER;

            //test
            // rbt.setBackgroundColor(Color.BLACK);

            //容器
            RadioGroup.LayoutParams containerParams = new RadioGroup.LayoutParams(0, -1, 1.0f);
            FrameLayout container = new FrameLayout(this.getContext());

            container.addView(rbt, rbtParams);
            mRadioGroup.addView(container, containerParams);
            mMenuBts.add(rbt);

            //初始化点击事件
            rbt.setTag(i);
            rbt.setOnClickListener(new OnClickListener() {
                //private boolean toggle = false;

                @Override
                public void onClick(View v) {
                    //ToastUtils.show(v.getContext(),"click:"+v);
                    int i = (int) v.getTag();
                    RadioButton bt = (RadioButton) v;

                    //isChecked是RadioButton 内部的一个状态值，
                    if (bt.getTag(RBT_KEY_STATE) == null) {
                        bt.setTag(RBT_KEY_STATE, true);
                    } else {
                        bt.setTag(RBT_KEY_STATE, !(Boolean) bt.getTag(RBT_KEY_STATE));
                    }

                    bt.setChecked((Boolean) bt.getTag(RBT_KEY_STATE));

                    //更新RadioGroup 的显示
                    refreshRadioGroup(i);

                    //更新下拉表的内容
                    showDropMenu(i, (Boolean) bt.getTag(RBT_KEY_STATE));

                    //当前被点击的菜单栏位置
                    mCurrentMenuPos = i;

                }


            });

            //添加竖线
            if (i == (menuTitles.size() - 1)) {
                continue;
            }
            View grayView = new View(this.getContext());
            grayView.setBackgroundColor(mMenuDividerColor);

            RadioGroup.LayoutParams grayParams = new RadioGroup.LayoutParams(DpUtils.viewDp2Px(0.5f), -1);
            grayParams.topMargin = DpUtils.viewDp2Px(2);
            grayParams.bottomMargin = DpUtils.viewDp2Px(2);
            mRadioGroup.addView(grayView, grayParams);


        }

        //添加到容器
        this.addView(mRadioGroup);

        //添加横线
        View grayLine = new View(this.getContext());
        grayLine.setLayoutParams(new ViewGroup.LayoutParams(-1, DpUtils.viewDp2Px(0.5f)));
        grayLine.setBackgroundColor(mMenuDividerColor);

        this.addView(grayLine);

        //添加弹出菜单父布局，先隐藏
        mContainerView.setVisibility(GONE);
        //添加到容器
        mContainerView.addView(popupMenuViews);
        mContainerView.addView(maskView);

        this.addView(mContainerView);

    }

    /**
     * 更新菜单栏的显示
     *
     * @param position
     */
    private void refreshRadioGroup(int position) {
        if (mRadioGroup == null) {
            return;
        }

        for (int i = 0; i < mMenuBts.size(); i++) {

            if (i == position) {
                continue;
            }

            mMenuBts.get(i).setTag(RBT_KEY_STATE, false);
            mMenuBts.get(i).setChecked((boolean) mMenuBts.get(i).getTag(RBT_KEY_STATE));


        }
    }

    /**
     * 下拉的列表内容
     *
     * @param position
     */
    private void showDropMenu(int position, boolean isChecked) {

        //ToastUtils.show(this.getContext(),"pos:"+position+",checked = "+isChecked);


        if (isChecked) {
            //第一次打开
            Object obj = popupMenuViews.getTag();
            if (obj == null) {
                //弹出菜单
                openDropDownMenu();
                // 显示当前position 的内容
                //ToastUtils.show(getContext(),"current pos: "+position);


            } else {
                Boolean popupState = (Boolean) obj;
                if (popupState) {

                    //  进行popupview的内容切换
                    //ToastUtils.show(getContext(),"switch  pos: "+position);
                } else {
                    //打开菜单并显示当前position 的内容
                    openDropDownMenu();
                    //ToastUtils.show(getContext(),"current pos: "+position);
                    // 显示当前position 的内容
                }
            }


            //更新当前下拉菜单内容
/*            if (mContentAdapter != null) {
                mContentAdapter.setCurrentPos(position);
            }*/

            //不同内容的显示
            if (mHolder != null) {
                mHolder.showCurrentView(position, popupMenuViews);
            }

        } else {
            //隐藏菜单
            closeDropDownMenu();

        }

        //设置popupMenuViews 的是否打开状态值
        popupMenuViews.setTag(isChecked);


    }

    private void openDropDownMenu() {

        mContainerView.setVisibility(VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.menu_in);
        maskView.setVisibility(VISIBLE);
        popupMenuViews.startAnimation(animation);
    }

    public void closeDropDownMenu() {

        Animation animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.menu_out);
        maskView.setVisibility(GONE);

        popupMenuViews.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mContainerView.setVisibility(GONE);

            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //TODO 发起搜索请求
        Map<String, String> currentSearchParams = getCurrentSearchParams();
        Log.v(TAG, currentSearchParams.toString());
    }

    public void clearAllState() {
        for (int i = 0; i < mMenuBts.size(); i++) {
            mMenuBts.get(i).setTag(RBT_KEY_STATE, false);
            mMenuBts.get(i).setChecked(false);
        }
        if (popupMenuViews != null) {
            popupMenuViews.setTag(false);
        }
    }

    private void initParams(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        //字体属性
        mMenuTextSize = typedArray.getDimension(R.styleable.DropDownMenu_menuTextSize, mMenuTextSize);
        mMenuTextColorState = typedArray.getResourceId(R.styleable.DropDownMenu_menuTextColorState, mMenuTextColorState);

        //箭头icon
        mMenuArrowIcon = typedArray.getResourceId(R.styleable.DropDownMenu_menuArrowIcon, mMenuArrowIcon);

        //分割线颜色
        mMenuDividerColor = typedArray.getColor(R.styleable.DropDownMenu_menuDividerColor, mMenuDividerColor);

        //背景色
        mMenuBgColor = typedArray.getColor(R.styleable.DropDownMenu_menuBgColor, mMenuBgColor);

        //菜单栏的高度
        mMenuHeight = typedArray.getDimension(R.styleable.DropDownMenu_menuHeight, mMenuHeight);

        //弹出菜单内容的高度
        mPopupHeight = typedArray.getDimension(R.styleable.DropDownMenu_popupHeight, mPopupHeight);
        typedArray.recycle();


    }


    /**
     * test 加入一个recycleview
     *
     * @param contentView
     */
    @Deprecated
    public void setContentViewWithAdapter(RecyclerView contentView) {
        if (popupMenuViews != null) {
            popupMenuViews.addView(contentView);
        }
        if (contentView.getAdapter() != null && contentView.getAdapter() instanceof DropDownAdapter) {
            mContentAdapter = (DropDownAdapter) contentView.getAdapter();
        }
    }


    public void setHolder(DropDownMenuHolder holder) {
        mHolder = holder;
    }

    /**
     * 更新选择中的条件
     * getCheckButton 内部代码有误
     *
     * @param text
     */
    @Deprecated
    public void setSelectMenuTitle(String text) {
        RadioButton rbt = getCheckButton();
        if (rbt != null) {
            if (text.contains("不限")) {
                //刷新为原标题
                rbt.setText(mMenuTitles.get(mCurrentMenuPos));
            } else {
                rbt.setText(text);
            }
        }
    }


    public void setSelectMenuText(String text) {

        if (CheckUtils.isNull(mMenuBts,mMenuBts.get(mCurrentMenuPos))) {
            return;
        }

        RadioButton rbt = mMenuBts.get(mCurrentMenuPos);

        if (text.contains("不限")) {
            //刷新为原标题
            rbt.setText(mMenuTitles.get(mCurrentMenuPos));
        } else {
            rbt.setText(text);
        }
    }

    /**
     * 获取当前选中的RadioButton
     * 当用户快速点击时，该代码回响应上一个点击的button
     *
     * @return
     */
    @Deprecated
    public RadioButton getCheckButton() {

        if (mMenuBts == null) {
            return null;
        }

        RadioButton radioButton = null;
        for (int i = 0; i < mMenuBts.size(); i++) {
            radioButton = mMenuBts.get(i);
            if (radioButton.isChecked()) {
                return radioButton;
            }

        }

        return radioButton;
    }


    public Map<String, String> getCurrentSearchParams() {
        Map<String, String> map = new HashMap<String, String>();

        for (int i = 0; i < mMenuBts.size(); i++) {
            String code = mMenuBts.get(i).getText().toString().trim();
            String checkStr = mMenuTitles.get(i);
            switch (i) {
                case 0:

                    if (!code.contains(checkStr) && !code.contains("不限")) {
                        map.put("l5AreaCode", code);
                    }
                    break;
                case 1:

                    if (!code.contains(checkStr) && !code.contains("不限")) {
                        map.put("htypeCode", code);
                    }
                    break;
                case 2:

                    if (!code.contains(checkStr) && !code.contains("不限")) {
                        map.put("priceCode", code);
                    }
                    break;
                case 3:

                    if (!code.contains(checkStr) && !code.contains("不限")) {
                        map.put("sizeCode", code);
                    }
                    break;
                case 4:

                    if (!code.contains(checkStr) && !code.contains("不限")) {
                        map.put("decorationCode", code);
                    }
                    break;
                default:
                    break;
            }
        }

        return map;
    }
}
