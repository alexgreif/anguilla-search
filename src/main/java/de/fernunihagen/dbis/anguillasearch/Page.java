package de.fernunihagen.dbis.anguillasearch;

import java.io.IOException;

import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class Page {
    final String url;
    final String title;
    final String headings;
    final String paragraphs;
    private CountMap<String> incomingLinks;
    private CountMap<String> outgoingLinks;
    
    // Constructor that scrapes from url
    public Page(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        this.url = url;
        this.title = doc.title();
        this.headings = doc.select("h1, h2, h3, h4, h5, h6").text();
        this.paragraphs = doc.select("p").text();
        this.incomingLinks = new CountMap<>();
        this.outgoingLinks = new CountMap<>();

        for (Element link : doc.select("a[href]")) {
            this.outgoingLinks.add(link.attr("abs:href"));
        }
    }

    // Constructor for test pages jsonObjects
    public Page(JsonObject jsonObject) {
        this.url = jsonObject.get("url").getAsString();
        this.title = jsonObject.get("title").getAsString();
        this.headings = jsonObject.get("headings").getAsString();
        this.paragraphs = jsonObject.get("paragraphs").getAsString();
        for (JsonElement link : jsonObject.get("outgoingLinks").getAsJsonArray()) {
            this.outgoingLinks.add(link.getAsString());
        }
        for (JsonElement link : jsonObject.get("incomingLinks").getAsJsonArray()) {
            this.incomingLinks.add(link.getAsString());
        }
    }

    public void addIncomingLink(String url) {
        incomingLinks.add(url);
    }

    public Set<String> getIncomingLinks() {
        return this.incomingLinks.getKeys();
    }

    public Set<String> getOutgoingLinks() {
        return this.outgoingLinks.getKeys();
    }

    public int nIncomingLinks(boolean distinct) {
        return distinct ? this.incomingLinks.nDistinct() : this.incomingLinks.totalCount();
    }

    public int nOutgoingLinks(boolean distinct) {
        return distinct ? this.outgoingLinks.nDistinct() : this.outgoingLinks.totalCount();
    }

    public String getRawTokens() {
        return (this.title + " " + this.headings + " " + this.paragraphs);
    }
}

