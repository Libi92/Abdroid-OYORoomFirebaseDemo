package com.application.pglocator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pglocator.R;
import com.application.pglocator.model.Feedback;
import com.application.pglocator.model.User;
import com.application.pglocator.util.DateUtil;
import com.bumptech.glide.Glide;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private final List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);

        User user = feedback.getUser();
        String displayName = user.getDisplayName();
        holder.textViewName.setText(displayName);
        String title = feedback.getTitle();
        holder.textViewTitle.setText(title);
        holder.textViewDescription.setText(feedback.getDescription());
        holder.textViewTime.setText(DateUtil.getDate(feedback.getFeedbackTime()));

        if (title == null || title.isEmpty()) {
            holder.textViewTitle.setVisibility(View.GONE);
        }

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
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewName;
        private final MaterialLetterIcon imageViewUser;
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final TextView textViewTime;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imageViewUser = itemView.findViewById(R.id.imageViewUser);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }
    }
}
