package com.example.space.virtualbooklibrary.model;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {

    private static final long serialVersionUID = 123L;
    private int ratingsCount;
    private String Id = "",
            description = "",
            thumbnailLink = "",
            title = "",
            maturityRating = "",
            ratingStars = "",
            googleEbooksPrice = "",
            message = "",
            link = "",
            mainCategory = "";
    private List<String> authors, categories;

    public Book(int ratingsCount, String Id, String description, String thumbnailLink, String title,
                String maturityRating, String ratingStars, String googleEbooksPrice, String message,
                String link, String mainCategory, List<String> authors,
                List<String> categories) {
        this.Id = Id;
        this.link = link;
        this.title = title;
        this.authors = authors;
        this.message = message;
        this.categories = categories;
        this.ratingStars = ratingStars;
        this.description = description;
        this.ratingsCount = ratingsCount;
        this.mainCategory = mainCategory;
        this.thumbnailLink = thumbnailLink;
        this.maturityRating = maturityRating;
        this.googleEbooksPrice = googleEbooksPrice;
    }

    // The count of people who rated the book.
    public int getRatingsCount() {
        return ratingsCount;
    }

    // The ISBN of the book.
    public String getId() {
        return Id;
    }

    // Returns the book's description.
    public String getDescription() {
        return description;
    }

    // The link to the book's thumbnail.
    public String getThumbnailLink() {
        return thumbnailLink;
    }

    // The book's title.
    public String getTitle() {
        return title;
    }

    // The book's maturity rating.
    public String getMaturityRating() {
        return maturityRating;
    }

    // The book's rating as stars out of 5.
    public String getRatingStars() {
        return ratingStars;
    }

    // The book's price at Google ebooks.
    public String getGoogleEbooksPrice() {
        return googleEbooksPrice;
    }

    // A message denoting whether the book is available at the given link or just
    // a preview of it.
    public String getMessage() {
        return message;
    }

    // The link to the book's preview or the book on google ebooks.
    public String getLink() {
        return link;
    }

    // Returns the book's main category.
    public String getMainCategory() {
        return mainCategory;
    }

    // Returns the book's authors.
    public java.util.List<String> getAuthors() {
        return authors;
    }

    // Returns the categories under which the books falls.
    public java.util.List<String> getCategories() {
        return categories;
    }
}