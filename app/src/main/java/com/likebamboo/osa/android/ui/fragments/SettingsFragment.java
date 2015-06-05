package com.likebamboo.osa.android.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.AboutActivity;
import com.likebamboo.osa.android.ui.BaseActivity;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;
import com.likebamboo.osa.android.ui.view.blur.BlurDialogFragmentHelper;

/**
 * 关于Fragment
 *
 * @author likebamboo
 */
public class SettingsFragment extends DialogFragment {

    private BlurDialogFragmentHelper mHelper;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new BlurDialogFragmentHelper(this);
        mHelper.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        ListView listView = (ListView) v.findViewById(R.id.dialog_content);
        listView.setAdapter(new ArrayAdapter<>(
                getActivity(), R.layout.simple_text,
                android.R.id.text1,
                getResources().getStringArray(R.array.about_list)
        ));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (getActivity() == null) {
                    return;
                }
                switch (i) {
                    case 0:// 意见反馈
                        ActivityNavigator.openWebView(getActivity(), null, RequestUrl.ISSUES_URL);
                        break;
                    case 1:// 关于作者
                        ActivityNavigator.openWebView(getActivity(), null, RequestUrl.ABOUT_ME_URL);
                        break;
                    case 2:// 关于app
                        Intent intent = new Intent(getActivity(), AboutActivity.class);
                        intent.putExtra(BaseActivity.EXTRA_TITLE, getString(R.string.about));
                        ActivityNavigator.startActivity(getActivity(), intent);
                        break;
                }
            }
        });
        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHelper.onActivityCreated();
    }

    @Override
    public void onStart() {
        super.onStart();
        mHelper.onStart();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mHelper.onDismiss();
        super.onDismiss(dialog);
    }
}