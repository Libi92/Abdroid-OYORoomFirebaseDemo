package com.application.pglocator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pglocator.R;
import com.application.pglocator.constants.RequestAction;
import com.application.pglocator.constants.UserType;
import com.application.pglocator.model.PGRequest;
import com.application.pglocator.model.PGRoom;
import com.application.pglocator.model.User;
import com.application.pglocator.util.DateUtil;
import com.application.pglocator.util.Globals;
import com.bumptech.glide.Glide;

import java.util.List;

public class PGRequestAdapter extends RecyclerView.Adapter<PGRequestAdapter.PGRequestViewHolder> {
    private final List<PGRequest> pgRequests;
    private RequestClickListener requestClickListener;

    public PGRequestAdapter(List<PGRequest> pgRequests) {
        this.pgRequests = pgRequests;
    }

    public void setRequestClickListener(RequestClickListener requestClickListener) {
        this.requestClickListener = requestClickListener;
    }

    @NonNull
    @Override
    public PGRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_pgrequest, parent, false);
        return new PGRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PGRequestViewHolder holder, int position) {
        PGRequest request = pgRequests.get(position);

        PGRoom pgRoom = request.getPgRoom();
        User targetUser = request.getTargetUser();
        User requestedUser = request.getRequestedUser();
        if (pgRoom != null && targetUser != null && requestedUser != null) {
            holder.textViewTitle.setText(pgRoom.getTitle());
            if (Globals.user.getUserType().equals(UserType.PG.getValue())) {
                holder.textViewUser.setText(String.format("Request from: %s", requestedUser.getDisplayName()));
            } else {
                holder.textViewUser.setText(String.format("Owner: %s", targetUser.getDisplayName()));
            }
            holder.textViewAddress.setText(pgRoom.getAddress());
            holder.textViewTime.setText(String.format("Requested on: %s", DateUtil.getDate(request.getRequestTime())));

            List<String> images = pgRoom.getImages();
            if (images != null && !images.isEmpty()) {
                Glide.with(holder.imageViewPG.getContext()).load(images.get(0))
                        .placeholder(R.mipmap.home).into(holder.imageViewPG);
            }

            if (!request.getStatus().equals(RequestAction.Pending.getValue())) {
                holder.textViewStatus.setText(String.format("%sED", request.getStatus()));
                holder.textViewStatus.setVisibility(View.VISIBLE);
            } else {
                holder.textViewStatus.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                if (requestClickListener != null) {
                    requestClickListener.onClick(request);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return pgRequests.size();
    }

    public interface RequestClickListener {
        void onClick(PGRequest request);
    }

    static class PGRequestViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final ImageView imageViewPG;
        private final TextView textViewTitle;
        private final TextView textViewUser;
        private final TextView textViewAddress;
        private final TextView textViewTime;
        private final TextView textViewStatus;

        public PGRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            imageViewPG = itemView.findViewById(R.id.imageViewPG);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewUser = itemView.findViewById(R.id.textViewUser);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
