package de.fernunihagen.dbis.anguillasearch;

import java.io.IOException;

import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.fernunihagen.dbis.anguillasearch.index.StringProcessing;


/**
 * A Page instance holds all relevant raw information about a single web page.
 */
public class Page {
    /**
     * The web page's URL
     */
    public final String url; // not private to avoid overhead of function call when accessing url (frequent operation during crawling)
    private final String title;
    private final String headings;
    private final String paragraphs;
    private CountMap<String> incomingLinks;
    private CountMap<String> outgoingLinks;
    
    /**
     * A constructor that scrapes the required contents from a given URL.
     * Does not add incoming links.
     * If required, incoming links need to be added after instantiation using the addIncomingLink method.
     * This method is used during crawling to scrape the content of web pages in the queue.
     * 
     * @param url the URL of the web page to be scraped
     * @throws IOException
     */
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

    /**
     * A Constructor that creates a page intance based on a jsonObject.
     * This constructor is only used in the ReverseIndexTests.
     * 
     * @param jsonObject a jsonObject that holds url, title, headings, paragraphs, outgoingLinks and incomingLinks of a web page
     */
    public Page(JsonObject jsonObject) {
        this.url = jsonObject.get("url").getAsString();
        this.title = jsonObject.get("title").getAsString();
        this.headings = jsonObject.get("headings").getAsString();
        this.paragraphs = jsonObject.get("paragraphs").getAsString();
        this.incomingLinks = new CountMap<>();
        this.outgoingLinks = new CountMap<>();

        for (JsonElement link : jsonObject.get("outgoingLinks").getAsJsonArray()) {
            this.outgoingLinks.add(link.getAsString());
        }
        for (JsonElement link : jsonObject.get("incomingLinks").getAsJsonArray()) {
            this.incomingLinks.add(link.getAsString());
        }
    }

    /**
     * Adds information about existing incoming link from a given web page to the web page of this Page instance.
     * This method is used to add incoming links for all pages at the end of the crawling process after all web pages have been found.
     * 
     * @param url URL of web page that links to web page of this Page instance
     */
    public void addIncomingLink(String url) {
        incomingLinks.add(url);
    }

    /**
     * Returns URLs of web pages that link to this Page.
     * This method is used for computing PageRank.
     * 
     * @return {@code Set} of URLs of web pages that link to this page
     */
    public Set<String> getIncomingLinks() {
        return this.incomingLinks.keySet();
    }

    /**
     * Return URLs of web pages that this Page links to.
     * 
     * @return {@code Set} of URLs of web pages that this Page links to
     */
    public Set<String> getOutgoingLinks() {
        return this.outgoingLinks.keySet();
    }

    /**
     * Returns number of incoming links.
     * Can either return number of distinct or number of total incoming links.
     * If no page contains more than one link to this Page, both are identical.
     * This method is used to compute total number of links found during crawling for CrawlerTest.
     * 
     * @param distinct flag whether distinct or total incoming links should be returned
     * @return number of distinct or total incoming links
     */
    public int nIncomingLinks(boolean distinct) {
        return distinct ? this.incomingLinks.nDistinct() : this.incomingLinks.totalCount();
    }

    /**
     * Returns number of outgoing links.
     * Can either return number of distinct or number of total outgoing links.
     * If no page contains more than one link to this Page, both are identical.
     * This method is used to compute PageRank.
     * 
     * @param distinct flag whether distinct or total outgoing links should be returned
     * @return number of distinct or total outgoing links
     */
    public int nOutgoingLinks(boolean distinct) {
        return distinct ? this.outgoingLinks.nDistinct() : this.outgoingLinks.totalCount();
    }

    /**
     * Returns tokens of this page.
     * This method is used to build the forward and reverse index.
     * 
     * @return map of tokens to their frequency
     */
    public CountMap<String> tokens() {
        String fullString = (this.title + " " + this.headings + " " + this.paragraphs);
        return StringProcessing.process(fullString);
    }
}

