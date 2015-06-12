package com.likebamboo.osa.android.ui.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;

import java.util.ArrayList;

/**
 * 带选择的adapter
 * Created by wentaoli on 2015/6/12.
 */
public abstract class ChoiceAdapter<T> extends BaseAdapter<T> {

    /**
     * 选中项
     */
    protected SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

    public ChoiceAdapter(Context ctx) {
        super(ctx);
    }

    public ChoiceAdapter(Context ctx, ArrayList<T> datas) {
        super(ctx, datas);
    }

    /**
     * 判断某一项是否选中
     *
     * @param position 位置
     * @return
     */
    public boolean isItemSelected(int position) {
        return mSelectedPositions.get(position, false);
    }

    /**
     * 设置选中项
     *
     * @param position 位置
     * @param selected 选中or未选中
     */
    public void setItemSelected(int position, boolean selected) {
        boolean sel = mSelectedPositions.get(position, false);
        if (sel == selected) {
            return;
        }

        // 选中
        if (selected) {
            mSelectedPositions.put(position, true);
        } else {
            // 取消选中
            mSelectedPositions.delete(position);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置全选or全部取消
     *
     * @param selectedAll
     */
    public void setSelectedAll(boolean selectedAll) {
        // 全选
        if (selectedAll) {
            for (int i = 0; i < getCount(); i++) {
                mSelectedPositions.put(i, true);
            }
        } else {
            // 全不选
            mSelectedPositions.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 获取选中的项数。
     *
     * @return
     */
    public int getSelectedCount() {
        int result = 0;
        for (int i = 0; i < getCount(); i++) {
            if (mSelectedPositions.get(i, false)) {
                ++result;
            }
        }
        return result;
    }

    /**
     * 获取选中的项的id
     *
     * @return
     */
    public ArrayList<Integer> getSelectedPositions() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            if (mSelectedPositions.get(i, false)) {
                result.add(i);
            }
        }
        return result;
    }


}
