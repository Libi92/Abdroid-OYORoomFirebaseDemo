package com.application.pglocator.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.application.pglocator.R;
import com.application.pglocator.model.PGRoom;
import com.rizlee.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchDialog extends DialogFragment {
    public static final String ARG_ROOMS = "arg::Rooms";
    private MultiAutoCompleteTextView multiAutoCompleteTextViewPlace;
    private RangeSeekBar rangeSeekBar;
    private OnSearchListener onSearchListener;
    private Button buttonSearch;
    private List<PGRoom> pgRooms;

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_search, container, false);
        initLayout(view);
        initFilter();
        initListeners();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void initLayout(View view) {
        multiAutoCompleteTextViewPlace = view.findViewById(R.id.multiAutoCompleteTextViewPlace);
        multiAutoCompleteTextViewPlace.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        rangeSeekBar = view.findViewById(R.id.rangeSeekBar);
        buttonSearch = view.findViewById(R.id.buttonSearch);
    }

    public void initFilter() {
        pgRooms = (List<PGRoom>) getArguments().getSerializable(ARG_ROOMS);

        if (pgRooms != null) {
            List<String> places = new ArrayList<>();

            float minRent = Float.MAX_VALUE;
            float maxRent = 0;

            for (PGRoom room : pgRooms) {
                String location = room.getLocation();
                if (!places.contains(location)) {
                    places.add(location);
                }

                float rent = room.getRent();
                if (rent < minRent) {
                    minRent = rent;
                }

                if (rent > maxRent) {
                    maxRent = rent;
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, places);
            multiAutoCompleteTextViewPlace.setAdapter(adapter);

            rangeSeekBar.setRightText(maxRent + " Rs");
            rangeSeekBar.setLeftText(minRent + " Rs");
            rangeSeekBar.setCenterText("");
            rangeSeekBar.setRange(minRent, maxRent, 100);
        }
    }

    private void initListeners() {
        buttonSearch.setOnClickListener(v -> {

            RangeSeekBar.Range currentValues = rangeSeekBar.getCurrentValues();
            float minValue = currentValues.getLeftValue();
            float maxValue = currentValues.getRightValue();

            String[] locations = multiAutoCompleteTextViewPlace.getText().toString()
                    .replace(" ", "").split(",");
            if (locations.length != 0) {
                if (locations[0].isEmpty()) {
                    locations[0] = "*";
                }
            }
            List<String> locationList = Arrays.asList(locations);
            List<String> filterText = new ArrayList<>(locationList);
            filterText.add("Min: " + minValue);
            filterText.add("Max: " + maxValue);
            filterText.remove("*");

            List<PGRoom> filterList = new ArrayList<>();

            for (PGRoom room : pgRooms) {
                String location = room.getLocation().trim();
                if (locationList.contains(location) || locationList.contains("*")) {
                    if (minValue <= room.getRent() && room.getRent() <= maxValue) {
                        filterList.add(room);
                    }
                }
            }

            if (onSearchListener != null) {
                onSearchListener.onSearch(filterList, filterText);
                dismiss();
            }
        });
    }

    public interface OnSearchListener {
        void onSearch(List<PGRoom> pgRooms, List<String> filterItems);
    }
}
