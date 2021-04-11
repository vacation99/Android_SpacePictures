package com.example.spacepictures.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.spacepictures.object.Picture;
import com.example.spacepictures.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SimpleViewHolder> {

    private ArrayList<Picture> arrayList = new ArrayList<>();
    private OnImageSelectedListener monImageSelectedListener;

    public RecyclerViewAdapter(OnImageSelectedListener onImageSelectedListener) {
        this.monImageSelectedListener = onImageSelectedListener;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new SimpleViewHolder(view, monImageSelectedListener);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.bind(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setItems(ArrayList<Picture> pictures) {
        arrayList.clear();
        arrayList.addAll(pictures);
        notifyDataSetChanged();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        OnImageSelectedListener onImageSelectedListener;

        public SimpleViewHolder(View itemView, OnImageSelectedListener onImageSelectedListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewMain);
            this.onImageSelectedListener = onImageSelectedListener;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                onImageSelectedListener.onImageClick(position);
            });
        }

        public void bind(Picture picture) {
            Picasso.get()
                    .load(picture.getUrl())
                    .error(R.drawable.gif)
                    .into(imageView);
        }
    }

    public interface OnImageSelectedListener {
        void onImageClick(int position);
    }
}