package com.appdev.schoudhary.wittylife.ui.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appdev.schoudhary.wittylife.R;

public class SearchBottomSheetDialogFragment extends android.support.design.widget.BottomSheetDialogFragment {

    public static SearchBottomSheetDialogFragment newInstance() {
        return new SearchBottomSheetDialogFragment();
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.bottonsheet_layout, container, false);

            return  view;
    }
}
