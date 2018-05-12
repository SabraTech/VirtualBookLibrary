package com.example.space.virtualbooklibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.List;
import java.util.Set;

public class BooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Book> books;
    private Set<String> bookIdSet;
    private Context context;
    private String addFavouriteURL, serverIp, sessionToken, username;
    Picasso picasso;

    public BooksAdapter(Context context, List<Book> books, Set<String> bookIdSet, Picasso picasso, String serverIp, String sessionToken, String username) {
        this.context = context;
        this.books = books;
        this.bookIdSet = bookIdSet;
        this.picasso = picasso;
        this.serverIp = serverIp;
        this.sessionToken = sessionToken;
        this.username = username;
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
        final String price = book.getGoogleEbooksPrice();
        final int rating = book.getRatingStars().length();
        final String author = book.getAuthors().toString();
        final String id = book.getId();
        final String bookUri = book.getLink();


        picasso.load(bookCoverLink).placeholder(android.R.color.darker_gray).config(Bitmap.Config.RGB_565).into(((ItemBooksViewHolder) holder).bookCover);
        ((ItemBooksViewHolder) holder).bookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(bookUri));
                context.startActivity(intent);
            }
        });
        ((ItemBooksViewHolder) holder).txtTitle.setText(title);
        ((ItemBooksViewHolder) holder).txtAuthor.setText(author);
        ((ItemBooksViewHolder) holder).txtPrice.setText(price);
        ((ItemBooksViewHolder) holder).ratingBar.setRating((float) rating);

        // check if the book in the list of favourite
        if (bookIdSet.contains(id)) {
            ((ItemBooksViewHolder) holder).favourite.setChecked(true);
            ((ItemBooksViewHolder) holder).favourite.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favourite_yellow));
        } else {
            ((ItemBooksViewHolder) holder).favourite.setChecked(false);
            ((ItemBooksViewHolder) holder).favourite.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favourite_gray));
        }


        ((ItemBooksViewHolder) holder).favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    ((ItemBooksViewHolder) holder).favourite.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favourite_yellow));
                    addFavouriteURL = "http://" + serverIp + ":8080/user/" + username + "/favourite?ISBN="
                            + id + "&title=" + title;
                    new FavouriteQuery().execute();
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

    private void addBookToFavourite() {
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(addFavouriteURL);
        try {
            request.setEntity(new StringEntity(this.sessionToken));
            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                // accept

            } else {
                // wrong
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ItemBooksViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtAuthor, txtPrice;
        ImageView bookCover;
        RatingBar ratingBar;
        ToggleButton favourite;
        private Context context;

        ItemBooksViewHolder(Context context, View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            bookCover = itemView.findViewById(R.id.bookCover);
            ratingBar = itemView.findViewById(R.id.ratingbar);
            favourite = itemView.findViewById(R.id.favourite_button);
            this.context = context;
        }
    }

    private class FavouriteQuery extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            addBookToFavourite();
            return null;
        }

    }
}


