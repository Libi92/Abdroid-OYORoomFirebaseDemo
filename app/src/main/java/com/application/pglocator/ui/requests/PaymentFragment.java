package com.application.pglocator.ui.requests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.pglocator.R;

public class PaymentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        initLayout(view);
        return view;
    }

    private void initLayout(View view) {
        WebView webView = view.findViewById(R.id.webView);
        webView.loadUrl("https://firebasestorage.googleapis.com/v0/b/pg-locator-2c3d9.appspot.com/o/payment.html?alt=media&token=14e14bc5-b2a6-444a-8499-23d27128b4c0");
    }
}
