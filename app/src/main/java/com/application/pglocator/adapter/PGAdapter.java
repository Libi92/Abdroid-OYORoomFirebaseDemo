package com.application.pglocator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pglocator.R;
import com.application.pglocator.model.PGRoom;
import com.bumptech.glide.Glide;

import java.util.List;

public class PGAdapter extends RecyclerView.Adapter<PGAdapter.PGViewHolder> {
    private final List<PGRoom> rooms;
    private PGClickListener pgClickListener;

    public PGAdapter(List<PGRoom> rooms) {
        this.rooms = rooms;
    }

    public PGClickListener getPgClickListener() {
        return pgClickListener;
    }

    public void setPgClickListener(PGClickListener pgClickListener) {
        this.pgClickListener = pgClickListener;
    }

    @NonNull
    @Override
    public PGViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_pg, parent, false);
        return new PGViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PGViewHolder holder, int position) {
        PGRoom pgRoom = rooms.get(position);
        holder.textViewTitle.setText(pgRoom.getTitle());
        holder.textViewDescription.setText(pgRoom.getDescription());
        holder.textViewAddress.setText(pgRoom.getAddress());
        holder.textViewRent.setText(String.format("Rs. %s (per month)", pgRoom.getRent()));
        List<String> images = pgRoom.getImages();
        if (images != null && !images.isEmpty()) {
            Glide.with(holder.imageViewPG.getContext()).load(images.get(0))
                    .placeholder(R.mipmap.home).into(holder.imageViewPG);
        }

        holder.itemView.setOnClickListener(v -> {
            if (pgClickListener != null) {
                pgClickListener.onPGItemClick(pgRoom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public interface PGClickListener {
        void onPGItemClick(PGRoom pgRoom);
    }

    static class PGViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final ImageView imageViewPG;
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final TextView textViewAddress;
        private final TextView textViewRent;

        public PGViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            imageViewPG = itemView.findViewById(R.id.imageViewPG);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewRent = itemView.findViewById(R.id.textViewRent);
        }
    }
}
