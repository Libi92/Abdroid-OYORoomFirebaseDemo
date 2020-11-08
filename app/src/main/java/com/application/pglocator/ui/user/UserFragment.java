package com.application.pglocator.ui.user;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pglocator.R;
import com.application.pglocator.adapter.UserAdapter;
import com.application.pglocator.db.DatabaseManager;
import com.application.pglocator.db.UserListener;
import com.application.pglocator.model.User;

import java.util.List;

import io.sulek.ssml.SSMLLinearLayoutManager;

public class UserFragment extends Fragment implements UserListener, UserAdapter.OnUserItemListener {

    private RecyclerView recyclerViewUsers;
    private DatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        init();
        initView(view);
        return view;
    }

    private void init() {
        databaseManager = new DatabaseManager.Builder()
                .userListener(this)
                .build();
        databaseManager.getUsers();
    }

    private void initView(View view) {
        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new SSMLLinearLayoutManager(requireContext()));
    }

    @Override
    public void onGetUser(User user) {

    }

    @Override
    public void onListUser(List<User> users) {
        UserAdapter userAdapter = new UserAdapter(users);
        userAdapter.setOnUserItemListener(this);
        recyclerViewUsers.setAdapter(userAdapter);
    }

    @Override
    public void onDelete(User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Action")
                .setMessage("Delete user - " + user.getDisplayName())
                .setPositiveButton("Confirm", (dialog, which) -> {
                    databaseManager.deleteUser(user);
                    Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
