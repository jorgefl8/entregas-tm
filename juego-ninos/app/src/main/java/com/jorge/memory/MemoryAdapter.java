package com.jorge.memory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.CardViewHolder> {

    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    private final List<CardItem> cards;
    private final OnCardClickListener listener;

    public MemoryAdapter(List<CardItem> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem card = cards.get(position);
        boolean showImage = card.isRevealed() || card.isMatched();

        if (showImage) {
            holder.cardImage.setImageResource(card.getImageResId());
            holder.cardImage.setVisibility(View.VISIBLE);
            holder.cardBack.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_option_correct));
            holder.itemView.setAlpha(card.isMatched() ? 0.75f : 1f);
        } else {
            holder.cardImage.setImageDrawable(null);
            holder.cardImage.setVisibility(View.INVISIBLE);
            holder.cardBack.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_option_default));
            holder.itemView.setAlpha(1f);
        }

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onCardClick(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void updateCard(int position) {
        if (position >= 0 && position < cards.size()) {
            notifyItemChanged(position);
        }
    }

    public void updateBoard() {
        if (!cards.isEmpty()) {
            notifyItemRangeChanged(0, cards.size());
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        final View cardBack;
        final ImageView cardImage;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBack = itemView.findViewById(R.id.cardBack);
            cardImage = itemView.findViewById(R.id.cardImage);
        }
    }
}
