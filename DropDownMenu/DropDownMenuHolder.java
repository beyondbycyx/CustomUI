package com.daowang.app.dwbroker.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.daowang.app.dwbroker.adapter.DropDownAdapter;


/**
 * Created by hugo on 2016/5/13.
 */
public class DropDownMenuHolder {

    //二级联动的容器
    private LinearLayout mLinearContainer;

    //一级联动的容器
    private RecyclerView mRecycleContainer;

    public DropDownMenuHolder( LinearLayout mLinearContainer,RecyclerView mRecycleContainer) {
        this.mRecycleContainer = mRecycleContainer;
        this.mLinearContainer = mLinearContainer;
    }


    public void showCurrentView(int position, FrameLayout popupMenuViews) {

        switch (position) {

            //二级联动
            case 0:
                if (mLinearContainer.getParent() == popupMenuViews) {
                    //TODO

                }else{
                    popupMenuViews.addView(mLinearContainer);

                }
                //ToastUtils.show(popupMenuViews.getContext(), "pos: " + position);
                mLinearContainer.setVisibility(View.VISIBLE);
                mRecycleContainer.setVisibility(View.GONE);
                break;

            //一级联动
            default:
                if (mRecycleContainer.getParent() == popupMenuViews) {
                    if (mRecycleContainer.getAdapter() instanceof DropDownAdapter) {
                        ((DropDownAdapter) mRecycleContainer.getAdapter()).setCurrentPos(position - 1);
                    }
                } else {
                    popupMenuViews.addView(mRecycleContainer);
                }

                //ToastUtils.show(popupMenuViews.getContext(), "pos: " + position);
                mRecycleContainer.setVisibility(View.VISIBLE);
                mLinearContainer.setVisibility(View.GONE);
                break;
        }
    }
}
