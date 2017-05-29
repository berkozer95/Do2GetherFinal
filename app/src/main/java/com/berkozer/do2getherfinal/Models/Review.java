package com.berkozer.do2getherfinal.Models;


public class Review {
    public String author, lang, text, dateInfo;
    public int rating;

    public String getDateInfo() {
        return dateInfo;
    }

    public Review setDateInfo(String dateInfo) {
        this.dateInfo = dateInfo;
        return this;
    }

    public Review() {
    }

    public String getAuthor() {
        return author;
    }

    public Review setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getLanguage() {
        return lang;
    }

    public Review setLanguage(String lang) {
        this.lang = lang;
        return this;
    }

    public String getText() {
        return text;
    }

    public Review setText(String text) {
        this.text = text;
        return this;
    }

    public int getRating() {
        return rating;
    }

    public Review setRating(int rating) {
        this.rating = rating;
        return this;
    }

}
