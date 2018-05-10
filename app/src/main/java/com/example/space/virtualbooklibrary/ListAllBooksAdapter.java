package com.example.space.virtualbooklibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.InputStream;
import java.util.List;

public class ListAllBooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Book> books;
    private Context context;

    public ListAllBooksAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_book, parent, false);
        return new ItemBooksViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Book book = books.get(position);
        final String bookCoverLink = book.getThumbnailLink();
        final String title = book.getTitle();
        final int rating = book.getRatingStars().length();
        final String author = book.getAuthors().toString();

        new DownloadImageTask(((ItemBooksViewHolder) holder).bookCover).execute(bookCoverLink);
        ((ItemBooksViewHolder) holder).txtTitle.setText(title);
        ((ItemBooksViewHolder) holder).txtAuthor.setText(author);
        ((ItemBooksViewHolder) holder).ratingBar.setRating((float) rating);
        ((ItemBooksViewHolder) holder).favourite.setChecked(false);
        ((ItemBooksViewHolder) holder).favourite.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favourite_gray));
        ((ItemBooksViewHolder) holder).favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    ((ItemBooksViewHolder) holder).favourite.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favourite_yellow));
                    // add to user favourite

                } else {
                    ((ItemBooksViewHolder) holder).favourite.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favourite_gray));
                    // remove from user favourite
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (books != null) {
            return books.size();
        }
        return 0;
    }

    class ItemBooksViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtAuthor;
        ImageView bookCover;
        RatingBar ratingBar;
        ToggleButton favourite;
        private Context context;

        ItemBooksViewHolder(Context context, View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            bookCover = itemView.findViewById(R.id.bookCover);
            ratingBar = itemView.findViewById(R.id.ratingbar);
            favourite = itemView.findViewById(R.id.favourite_button);
            this.context = context;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}


