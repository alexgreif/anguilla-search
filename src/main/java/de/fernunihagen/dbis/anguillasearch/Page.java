package de.fernunihagen.dbis.anguillasearch;

import java.io.IOException;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


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
    private Map<String, Integer> incomingLinks;
    private Map<String, Integer> outgoingLinks;

    /**
     * Instantiates representation of a web page.
     * Saves relevant content of web page but does not add incoming link.
     * If required, incoming links need to be added after instantiation using the addIncomingLink method.
     * This method is used during crawling to save the scraped contents.
     * 
     * @param url URL of web page
     * @param title title of web page
     * @param headings single {@code String} containing all headings of web page
     * @param paragraphs single {@code String} containing all paragraphs of web page
     * @param outgoingLinks {@code Map} from link to count of occurence of that link
     */
    public Page(String url, String title, String headings, String paragraphs, Map<String, Integer> outgoingLinks) {
        this.url = url;
        this.title = title;
        this.headings = headings;
        this.paragraphs = paragraphs;
        this.incomingLinks = new HashMap<>();
        this.outgoingLinks = outgoingLinks;
    }

    /**
     * Adds information about existing incoming link from a given web page to the web page of this Page instance.
     * This method is used to add incoming links for all pages at the end of the crawling process after all web pages have been found.
     * 
     * @param url URL of web page that links to web page of this Page instance
     */
    public void addIncomingLink(String url) {
        this.incomingLinks.merge(url, 1, Integer::sum);
    }

    /**
     * Returns URLs of web pages that link to this Page with the number of those links.
     * 
     * @return {@code Map} from URL to count of links
     */
    public Map<String, Integer> getIncomingLinksWithCounts() {
        return this.incomingLinks;
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
     * Returns URLs of web pages that this Page links to with the number of those links.
     * 
     * @return {@code Map} from URL to count of links
     */
    public Map<String, Integer> getOutgoingLinksWithCounts() {
        return this.outgoingLinks;
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
        return Utils.nElems(this.incomingLinks, distinct);
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
        return Utils.nElems(this.outgoingLinks, distinct);
    }

    /**
     * Returns title, headings and paragraphs of page, seperated by newlines.
     * 
     * @return title, headings and paragraphs, concatenated and seperated by newlines.
     */
    public String rawText() {
        return this.title + "\n" + this.headings + "\n" + this.paragraphs;
    }
}

