package com.example.space.virtualbooklibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookData {

    private List<Book> bookList;

    public BookData(List<Book> bookList) {
        this.bookList = bookList;
    }

    public List<Book> getAllBooks() {
        return this.bookList;
    }

    public List<Book> getCategoryFilteredBooks(List<String> category, List<Book> bookList) {
        List<Book> tempList = new ArrayList<>();
        for (Book book : bookList) {
            for (String g : category) {
                if (book.getMainCategory().equalsIgnoreCase(g)) {
                    tempList.add(book);
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
                if (Integer.parseInt(book.getRatingStars()) >= Integer.parseInt(r.replace(">", ""))) {
                    tempList.add(book);
                }
            }
        }
        return tempList;
    }

    public List<String> getUniqueCategoryKeys() {
        List<String> categories = new ArrayList<>();
        for (Book book : this.bookList) {
            if (!categories.contains(book.getMainCategory())) {
                categories.add(book.getMainCategory());
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
            String rate = "> " + book.getRatingStars();
            if (!ratings.contains(rate)) {
                ratings.add(rate);
            }
        }
        Collections.sort(ratings);
        return ratings;
    }
}
