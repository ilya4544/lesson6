package com.lyamkin.rss;

/**
 * Created by Ilya on 04.01.2015.
 */
class FeedItem {
    String title, description, link;

    public FeedItem(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    @Override
    public String toString() {
        return title;
    }
}
