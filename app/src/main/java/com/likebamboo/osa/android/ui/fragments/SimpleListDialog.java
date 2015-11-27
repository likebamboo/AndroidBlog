package com.likebamboo.osa.android.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.LDialogItem;
import com.likebamboo.osa.android.ui.adapter.BaseRecycleAdapter;
import com.likebamboo.osa.android.utils.DeviceUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


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
     * 标题
     */
    public static final String EXTRA_LIST_DIALOG_TITLE = "extra_list_dialog_title";
    /**
     * 标题
     */
    public static final String EXTRA_LIST_DIALOG_DATAS = "extra_list_dialog_datas";

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

    public static SimpleListDialog getInstance(String title, ArrayList<? extends LDialogItem> datas) {
        SimpleListDialog fragment = new SimpleListDialog();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_LIST_DIALOG_TITLE, title);
        bundle.putParcelableArrayList(EXTRA_LIST_DIALOG_DATAS, datas);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(EXTRA_LIST_DIALOG_TITLE);
            try {
                mDatas = getArguments().getParcelableArrayList(EXTRA_LIST_DIALOG_DATAS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            root = View.inflate(getActivity(), R.layout.simple_list_dialog, container);
        }

        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) root.getParent();
        if (parent != null) {
            parent.removeView(root);
        }

        TextView titleTv = (TextView) root.findViewById(R.id.simple_list_dialog_title);
        RecyclerView listView = (RecyclerView) root.findViewById(R.id.simple_list_view);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                DeviceUtil.dip2px(getActivity(), 50 * mDatas.size())));

        // 设置ListView数据源
        mAdapter = new ListDialogAdapter<T>(getActivity(), mDatas);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Object item = mAdapter.getItem(arg2);
                if (item != null && item instanceof LDialogItem) {
                    if (mListener != null) {
                        mListener.onItemClick((T) item);
                    }
                }
                dismiss();
            }
        });
        listView.setAdapter(mAdapter);

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
        final Dialog dialog = new Dialog(getActivity(), R.style.simple_dialog_style);
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
    private static class ListDialogAdapter<T extends LDialogItem> extends BaseRecycleAdapter<T> {

        private OnItemClickListener listener = null;

        public ListDialogAdapter(Context ctx, ArrayList<T> datas) {
            super(ctx);
            addDatas(datas);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup viewGroup, int i) {
            View root = LayoutInflater.from(mContext).inflate(R.layout.item_simple_list_dialog, null);
            ViewHolder holder = new ViewHolder(root);
            return holder;
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            LDialogItem item = getItem(position);
            if (item == null) {
                return;
            }
            ViewHolder holder = (ViewHolder) viewHolder;
            if (item.isSelected()) {
                holder.iconIv.setVisibility(View.VISIBLE);
            } else {
                holder.iconIv.setVisibility(View.INVISIBLE);
            }
            holder.titleTv.setText(item.getName());
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(null, v, position, 0);
                    }
                }
            });
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.simple_dialog_item_icon_tv)
        View iconIv;
        @InjectView(R.id.simple_dialog_item_layout)
        View root;
        @InjectView(R.id.simple_dialog_item_name_tv)
        TextView titleTv;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
