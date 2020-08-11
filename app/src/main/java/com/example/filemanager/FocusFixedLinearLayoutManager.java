package com.example.filemanager;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

public class FocusFixedLinearLayoutManager extends LinearLayoutManager {
        public FocusFixedLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public View onInterceptFocusSearch(View focused, int direction) {

            int count = getItemCount();//获取item的总数
            int fromPos = getPosition(getFocusedChild());//当前焦点的位置
            int lastVisibleItemPos = findLastVisibleItemPosition();//最新的已显示的Item的位置

            switch (direction) {//根据按键逻辑控制position
                case View.FOCUS_RIGHT:
                    fromPos++;
                    break;
                case View.FOCUS_LEFT:
                    fromPos--;
                    break;
            }

            if (fromPos < 0 || fromPos >= count) {
                //如果下一个位置<0,或者超出item的总数，则返回当前的View，即焦点不动
                return focused;
            } else {
                //如果下一个位置大于最新的已显示的item，即下一个位置的View没有显示，则滑动到那个位置，让他显示，就可以获取焦点了
                if (fromPos > lastVisibleItemPos) {
                    scrollToPosition(fromPos);
                }
            }
            return super.onInterceptFocusSearch(focused, direction);
        }

    }
