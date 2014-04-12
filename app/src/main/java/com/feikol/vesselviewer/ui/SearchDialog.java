package com.feikol.vesselviewer.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feikol.vesselviewer.app.R;
import com.feikol.vesselviewer.data.DataSource;
import com.feikol.vesselviewer.util.DebugUtil;

import java.net.URLEncoder;

/**
 * Created by FeikoLai on 12/4/14.
 */
public class SearchDialog extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText editText;
    private LinearLayout linearLayout;
    private DataSource dataSource;

    public SearchDialog(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_dialog, container);
        this.editText = (EditText) view.findViewById(R.id.search_box);
        this.linearLayout = (LinearLayout) view.findViewById(R.id.result_list);
        getDialog().setTitle("Search vessel/port");

        editText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        editText.setOnEditorActionListener(this);
        return view;

    }
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            try
            {
                String searchText = editText.getText().toString();
                String encodedText = URLEncoder.encode(searchText, "UTF-8");
                dataSource.searchVesselsAndPorts(encodedText);
                getActivity().setProgressBarIndeterminateVisibility(true);
                dismiss();
            }catch (Exception e)
            {
                DebugUtil.Log(e);
            }
            return true;
        }
        return false;
    }
}
