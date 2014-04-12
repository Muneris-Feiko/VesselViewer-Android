package com.feikol.vesselviewer.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feikol.vesselviewer.app.R;

import java.util.List;

/**
 * Created by FeikoLai on 12/4/14.
 */
public class SearchResultDialog extends DialogFragment {


    private List<Pair<String, String>> data;

    public SearchResultDialog(List<Pair<String, String>> data) {

        this.data = data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_result, container);

        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.search_result_linear_layout);

        for (final Pair<String, String> entry : data) {
            linearLayout.addView(new TextView(getActivity()) {{
                setText(entry.first);
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), entry.second, Toast.LENGTH_LONG).show();
                    }
                });
                setPadding(10, 10, 10, 10);
                if (linearLayout.getChildCount() % 2 == 0) {
                    setBackgroundColor(Color.LTGRAY);
                }
                setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                }});
        }

        getDialog().setTitle("Search result");

        return view;
    }
}
