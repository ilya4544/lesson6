package com.lyamkin.rss;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RssParser extends DefaultHandler {
    private List<FeedItem> feedItems = null;
    private String currentTitle, currentDescription, currentLink;
    private StringBuilder sb = new StringBuilder();
    private FeedParsedCallback feedParsedCallback;

    interface FeedParsedCallback {
         void onFeedParsed(List<FeedItem> feedItems);
    }

    private static HashSet<String> win1251Hosts = new HashSet<>();
    static {
        win1251Hosts.add("bash");
    }

    class GetFeedTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(URI.create(strings[0]));
                HttpResponse response = client.execute(request);
                HttpEntity e = response.getEntity();
                String encoding = "UTF-8";
                for (String host : win1251Hosts)
                if (strings[0].contains(host)) {
                    encoding = "Windows-1251";
                    break;
                }
                Reader r = new InputStreamReader(e.getContent(), encoding);
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                SAXParser newSAXParser = saxParserFactory.newSAXParser();
                XMLReader xmlReader = newSAXParser.getXMLReader();
                xmlReader.setContentHandler(RssParser.this);
                xmlReader.parse(new InputSource(r));
            } catch (IOException | SAXException | ParserConfigurationException ignored) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            feedParsedCallback.onFeedParsed(feedItems);
        }
    }

    public RssParser(FeedParsedCallback feedParsedCallback, String url) {
        this.feedParsedCallback = feedParsedCallback;
        new GetFeedTask().execute(url);
    }

    @Override
    public void startDocument() throws SAXException {
        feedItems = new ArrayList<>();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        sb.setLength(0);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("item")) {
            feedItems.add(new FeedItem(currentTitle, currentDescription, currentLink));
        } else if (localName.equalsIgnoreCase("title")) {
            currentTitle = sb.toString();
        } else if (localName.equalsIgnoreCase("description")) {
            currentDescription = sb.toString();
        } else if (localName.equalsIgnoreCase("link")) {
            currentLink = sb.toString();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String tempString = new String(ch, start, length);
        sb.append(tempString);
    }
}