package com.application.pglocator.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.application.pglocator.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.rizlee.rangeseekbar.utils.PixelUtil;

import java.util.List;

public class SearchView extends LinearLayout {
    private ChipGroup chipGroup;
    private ImageView imageView;
    private HorizontalScrollView horizontalScrollView;

    private OnClearListener onClearListener;

    public SearchView(Context context) {
        super(context);
        init();
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnClearListener(OnClearListener onClearListener) {
        this.onClearListener = onClearListener;
    }

    private void init() {
        setOrientation(HORIZONTAL);

        Context context = getContext();

        int chipGroupHeight = PixelUtil.INSTANCE.dpToPx(getContext(), 36);
        chipGroup = new ChipGroup(context);
        chipGroup.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, chipGroupHeight));

        horizontalScrollView = new HorizontalScrollView(getContext());
        horizontalScrollView.setLayoutParams(new LinearLayout.LayoutParams(0, chipGroupHeight, 1));
        horizontalScrollView.addView(chipGroup);

        imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setImageResource(R.drawable.search);
        imageView.setColorFilter(R.color.grey);
        setGravity(Gravity.CENTER_VERTICAL);

        addView(horizontalScrollView);
        addView(imageView);

        imageView.setOnClickListener(v -> {
            chipGroup.removeAllViews();
            if (onClearListener != null) {
                onClearListener.onClear();
                imageView.setImageResource(R.drawable.search);
            }
        });
    }

    public void addItems(List<String> items) {
        for (String text : items) {
            Chip chip = new Chip(getContext());
            chip.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT));
            chip.setText(text);

            chipGroup.addView(chip);
        }
        imageView.setImageResource(R.drawable.close_circle);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        horizontalScrollView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                horizontalScrollView.performClick();
            }
            return false;
        });
        horizontalScrollView.setOnClickListener(l);
    }

    public interface OnClearListener {
        void onClear();
    }
}
