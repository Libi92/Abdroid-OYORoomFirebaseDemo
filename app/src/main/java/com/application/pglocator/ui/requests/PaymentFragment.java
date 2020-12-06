package com.application.pglocator.ui.requests;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
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
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "Android");
        webView.loadUrl("https://firebasestorage.googleapis.com/v0/b/pg-locator-2c3d9.appspot.com/o/payment.html?alt=media&token=5774ee79-51f7-4ac5-b369-84dc210bc2c1");
    }

    @JavascriptInterface
    public void completePayment() {
        new AlertDialog.Builder(getContext())
                .setTitle("Payment Info")
                .setMessage("Payment Complete")
                .setPositiveButton("Ok", (dialog, which) -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(RequestDetailsFragment.DATA_KEY, "some data");
                    getParentFragmentManager().setFragmentResult(RequestDetailsFragment.REQ_KEY, bundle);
                    getParentFragmentManager().popBackStack();
                }).show();
    }
}
