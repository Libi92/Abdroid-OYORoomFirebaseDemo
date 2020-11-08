package com.application.pglocator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pglocator.R;
import com.application.pglocator.model.User;
import com.bumptech.glide.Glide;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;
    private OnUserItemListener onUserItemListener;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    public void setOnUserItemListener(OnUserItemListener onUserItemListener) {
        this.onUserItemListener = onUserItemListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        String displayName = user.getDisplayName();
        holder.textViewName.setText(displayName);
        holder.textViewUserType.setText(user.getUserType());
        holder.textViewEmail.setText(user.getEmail());
        holder.textViewPhone.setText(user.getPhone());
        String photo = user.getPhoto();
        if (photo != null) {
            Glide.with(holder.imageViewUser.getContext()).load(photo).into(holder.imageViewUser);
        } else {
            String[] nameSplit = displayName.split(" ");
            String letter;
            if (nameSplit.length > 1) {
                letter = nameSplit[0].charAt(0) + "" + nameSplit[1].charAt(0);
            } else {
                letter = nameSplit[0].charAt(0) + "";
            }
            holder.imageViewUser.setLetter(letter);
        }

        holder.buttonDelete.setOnClickListener(v -> {
            if (onUserItemListener != null) {
                onUserItemListener.onDelete(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnUserItemListener {
        void onDelete(User user);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final View buttonDelete;
        private final MaterialLetterIcon imageViewUser;
        private final TextView textViewName;
        private final TextView textViewUserType;
        private final TextView textViewEmail;
        private final TextView textViewPhone;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.buttonDelete = itemView.findViewById(R.id.buttonDelete);
            this.imageViewUser = itemView.findViewById(R.id.imageViewUser);
            this.textViewName = itemView.findViewById(R.id.textViewName);
            this.textViewUserType = itemView.findViewById(R.id.textViewUserType);
            this.textViewEmail = itemView.findViewById(R.id.textViewEmail);
            this.textViewPhone = itemView.findViewById(R.id.textViewPhone);
        }
    }
}
