package com.example.space.virtualbooklibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookData {

    private List<Book> bookList = new ArrayList<>();

    public BookData(List<Book> bookList) {
        if (bookList != null) {
            this.bookList.addAll(bookList);
        }
    }

    public List<Book> getAllBooks() {
        return this.bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    public List<Book> getCategoryFilteredBooks(List<String> category, List<Book> bookList) {
        List<Book> tempList = new ArrayList<>();
        for (Book book : bookList) {
            for (String g : category) {
                if (book.getCategories() != null) {
                    if (book.getCategories().get(0).equalsIgnoreCase(g)) {
                        tempList.add(book);
                    }
                }
            }
        }
        return tempList;
    }

    /*public List<Book> getYearFilteredBooks(List<String> yearStr, List<Book> bookList) {
        List<Book> tempList = new ArrayList<>();
        for(Book book: bookList){
            for(String y : yearStr){
                if(book.getYear() == Integer.parseInt(y)){
                    tempList.add(book);
                }
            }
        }
        return tempList;
    }*/

    public List<Book> getRatingFilteredBooks(List<String> rating, List<Book> bookList) {
        List<Book> tempList = new ArrayList<>();
        for (Book book : bookList) {
            for (String r : rating) {
                if (book.getRatingStars() != null) {
                    if (book.getRatingStars().length() >= Integer.parseInt(r.replaceAll("> ", ""))) {
                        tempList.add(book);
                    }
                }

            }
        }
        return tempList;
    }

    public List<String> getUniqueCategoryKeys() {
        List<String> categories = new ArrayList<>();
        for (Book book : this.bookList) {

            if (book.getCategories() != null) {
                if (!categories.contains(book.getCategories().get(0))) {
                    categories.add(book.getCategories().get(0));
                }
            }
        }
        Collections.sort(categories);
        return categories;
    }

    /*public List<String> getUniqueYearKeys(){
        List<String> years = new ArrayList<>();
        for(Book book: this.bookList){
            if(!years.contains(book.getYear() + "")){
                years.add(book.getYear() + "");
            }
        }
        Collections.sort(years);
        return years;
    }*/

    public List<String> getUniqueRatingKeys() {
        List<String> ratings = new ArrayList<>();
        for (Book book : this.bookList) {
            if (book.getRatingStars() != null) {
                String rate = "> " + book.getRatingStars().length();
                if (!ratings.contains(rate)) {
                    ratings.add(rate);
                }
            }

        }
        Collections.sort(ratings);
        return ratings;
    }
}
