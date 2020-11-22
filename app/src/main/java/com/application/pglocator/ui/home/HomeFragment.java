package com.application.pglocator.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pglocator.R;
import com.application.pglocator.adapter.PGAdapter;
import com.application.pglocator.constants.UserType;
import com.application.pglocator.db.DatabaseManager;
import com.application.pglocator.db.PGListener;
import com.application.pglocator.dialog.SearchDialog;
import com.application.pglocator.model.PGRoom;
import com.application.pglocator.ui.PGDetailsFragment;
import com.application.pglocator.util.Globals;
import com.application.pglocator.widget.SearchView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PGListener, PGAdapter.PGClickListener,
        SearchDialog.OnSearchListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private RecyclerView recyclerViewPg;

    private SearchView searchView;
    private ProgressBar progressBar;
    private List<PGRoom> rooms;
    private List<PGRoom> adapterItems;
    private PGAdapter pgAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        init();
        initLayout(root);
        return root;
    }

    private void initLayout(View view) {
        recyclerViewPg = view.findViewById(R.id.recyclerViewPg);
        recyclerViewPg.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = view.findViewById(R.id.searchView);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        searchView.setOnClickListener(v -> {
            SearchDialog searchDialog = new SearchDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable(SearchDialog.ARG_ROOMS, (Serializable) rooms);
            searchDialog.setArguments(bundle);
            searchDialog.setOnSearchListener(this);
            searchDialog.show(getChildFragmentManager(), TAG);
        });

        searchView.setOnClearListener(() -> {
            adapterItems.clear();
            adapterItems.addAll(rooms);
            pgAdapter.notifyDataSetChanged();
        });
    }

    private void init() {
        DatabaseManager databaseManager = new DatabaseManager.Builder()
                .pgListener(this)
                .build();

        if (Globals.user.getUserType().equals(UserType.PG.getValue())) {
            databaseManager.getPG(Globals.user);
        } else {
            databaseManager.getPG(null);
        }
    }

    @Override
    public void onGetPG(List<PGRoom> rooms) {
        Log.i(TAG, "onGetPG: " + rooms);
        this.rooms = rooms;
        this.adapterItems = new ArrayList<>(rooms);
        progressBar.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        pgAdapter = new PGAdapter(adapterItems);
        pgAdapter.setPgClickListener(this);
        recyclerViewPg.setAdapter(pgAdapter);
    }

    @Override
    public void onPGItemClick(PGRoom pgRoom) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PGDetailsFragment.ARG_PGROOM, pgRoom);
        navController.navigate(R.id.action_nav_home_to_nav_pgdetails, bundle);
    }

    @Override
    public void onSearch(List<PGRoom> pgRooms, List<String> filterItems) {
        this.adapterItems.clear();
        this.adapterItems.addAll(pgRooms);
        this.pgAdapter.notifyDataSetChanged();

        searchView.addItems(filterItems);
    }
}