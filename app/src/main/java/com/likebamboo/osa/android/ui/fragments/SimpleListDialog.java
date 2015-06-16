package com.likebamboo.osa.android.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.LDialogItem;
import com.likebamboo.osa.android.ui.adapter.BaseAdapter;

import java.util.ArrayList;


/**
 * ListView对话框
 *
 * @author likebamboo
 * @version [版本号, 2015-06-15]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class SimpleListDialog<T extends LDialogItem> extends DialogFragment {

    /**
     * 上下文对象
     */
    private Context mContext = null;

    /**
     * 根布局
     */
    private View root = null;

    /**
     * 标题，信息，
     */
    private String title = "";

    /**
     * ListView数据源
     */
    private ArrayList<T> mDatas = null;

    /**
     * 监听器
     */
    private OnDialogItemClickListener<T> mListener = null;

    /**
     * 适配器
     */
    private ListDialogAdapter<T> mAdapter = null;

    public interface OnDialogItemClickListener<T extends LDialogItem> {
        void onItemClick(T obj);
    }

    public SimpleListDialog(Context context, String title, ArrayList<T> data) {
        this.mContext = context;
        this.title = title;
        this.mDatas = data;
    }

    /**
     * @param mListener
     * @see [类、类#方法、类#成员]
     */
    public void setOnItemClickListener(OnDialogItemClickListener<T> mListener) {
        this.mListener = mListener;
    }

    /**
     * 刷新数据
     */
    public void notifyDataChanged() {
        if (mAdapter == null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = View.inflate(mContext, R.layout.simple_list_dialog, null);
        }

        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) root.getParent();
        if (parent != null) {
            parent.removeView(root);
        }

        TextView titleTv = (TextView) root.findViewById(R.id.simple_list_dialog_title);
        ListView listView = (ListView) root.findViewById(R.id.simple_list_dialog_list);

        // 设置ListView数据源
        mAdapter = new ListDialogAdapter<T>(mContext, mDatas);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Object item = arg0.getItemAtPosition(arg2);
                if (item != null && item instanceof LDialogItem) {
                    if (mListener != null) {
                        mListener.onItemClick((T) item);
                    }
                }
                SimpleListDialog.this.dismiss();
            }
        });

        // 标题
        if (TextUtils.isEmpty(title)) {
            titleTv.setVisibility(View.GONE);
        } else {
            titleTv.setText(title);
        }

        return root;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext, R.style.simple_dialog_style);
        Window window = dialog.getWindow();
        //设置显示动画
        window.setWindowAnimations(R.style.DialogSlideAnimation);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * listView dialog适配器
     *
     * @author wentaoli
     * @version [版本号, 2014年12月25日]
     * @see [相关类/方法]
     * @since [产品/模块版本]
     */
    private static class ListDialogAdapter<T extends LDialogItem> extends BaseAdapter<T> {

        public ListDialogAdapter(Context ctx, ArrayList<T> datas) {
            super(ctx, datas);
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = LayoutInflater.from(mContext).inflate(R.layout.item_simple_list_dialog, null);
                holder.iconIv = v.findViewById(R.id.simple_dialog_item_icon_tv);
                holder.titleTv = (TextView) v.findViewById(R.id.simple_dialog_item_name_tv);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            LDialogItem item = getItem(position);
            if (item == null) {
                return v;
            }
            if (item.isSelected()) {
                holder.iconIv.setVisibility(View.VISIBLE);
            } else {
                holder.iconIv.setVisibility(View.INVISIBLE);
            }
            holder.titleTv.setText(item.getName());

            return v;
        }
    }

    private static class ViewHolder {
        View iconIv;

        TextView titleTv;
    }
}
