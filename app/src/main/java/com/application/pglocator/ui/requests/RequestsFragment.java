package com.application.pglocator.ui.requests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pglocator.R;
import com.application.pglocator.adapter.PGRequestAdapter;
import com.application.pglocator.constants.UserType;
import com.application.pglocator.db.DatabaseManager;
import com.application.pglocator.db.PGRequestListener;
import com.application.pglocator.model.PGRequest;
import com.application.pglocator.util.Globals;

import java.util.List;

public class RequestsFragment extends Fragment implements PGRequestListener, PGRequestAdapter.RequestClickListener {

    private RecyclerView recyclerViewRequests;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_requests, container, false);

        init();
        initLayout(root);

        return root;
    }

    private void init() {
        DatabaseManager databaseManager = new DatabaseManager.Builder()
                .requestListener(this)
                .build();

        if (Globals.user.getUserType().equals(UserType.PG.getValue())) {
            databaseManager.getPGRequests(null, Globals.user.getUId());
        } else if (Globals.user.getUserType().equals(UserType.USER.getValue())) {
            databaseManager.getPGRequests(Globals.user.getUId(), null);
        }
    }

    private void initLayout(View view) {
        recyclerViewRequests = view.findViewById(R.id.recyclerViewRequests);
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onGetPGRequest(List<PGRequest> requests) {
        PGRequestAdapter adapter = new PGRequestAdapter(requests);
        adapter.setRequestClickListener(this);
        recyclerViewRequests.setAdapter(adapter);
    }

    @Override
    public void onClick(PGRequest request) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle bundle = new Bundle();
        bundle.putSerializable(RequestDetailsFragment.ARG_REQUEST, request);
        navController.navigate(R.id.action_nav_request_to_details, bundle);
    }
}