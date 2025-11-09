package com.mojiscan.ocr.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.mojiscan.ocr.R;
import com.mojiscan.ocr.data.entity.TranscriptionEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TranscriptionAdapter extends ListAdapter<TranscriptionEntity, TranscriptionAdapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(TranscriptionEntity transcription);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(TranscriptionEntity transcription);
    }

    public TranscriptionAdapter() {
        super(new DiffUtil.ItemCallback<TranscriptionEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull TranscriptionEntity oldItem, @NonNull TranscriptionEntity newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull TranscriptionEntity oldItem, @NonNull TranscriptionEntity newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transcription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TranscriptionEntity transcription = getItem(position);
        holder.bind(transcription);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView textPreviewTextView;
        private TextView dateTextView;
        private View deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            textPreviewTextView = itemView.findViewById(R.id.textPreviewTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getItem(getAdapterPosition()));
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(getItem(getAdapterPosition()));
                }
            });
        }

        void bind(TranscriptionEntity transcription) {
            titleTextView.setText(transcription.getTitle());
            
            String text = transcription.getText();
            if (text != null && text.length() > 100) {
                textPreviewTextView.setText(text.substring(0, 100) + "...");
            } else {
                textPreviewTextView.setText(text);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            dateTextView.setText(sdf.format(new Date(transcription.getTimestamp())));
        }
    }
}

