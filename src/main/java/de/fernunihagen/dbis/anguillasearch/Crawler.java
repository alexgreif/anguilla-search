package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Collection;

import java.io.IOException;

/**
 * Scrapes web pages based on seed URLs.
 * A Crawler is instantiated using a collection or array of seed URLs and provides methods to 
 */
public class Crawler {
    private Collection<String> seedUrls;
    private Map<String, Page> urlToPage;
    
    /**
     * Creates Crawler instanced based on {@code Array} of seed URLs.
     * 
     * @param seedUrls {@code Array} of seed URLs
     */
    public Crawler(String[] seedUrls) {
        this.seedUrls = Arrays.asList(seedUrls);
    }
  
    /**
     * Creates Crawler instanced based on {@code Collection} of seed URLs.
     * 
     * @param seedUrls {@code Collection} of seed URLs
     */
    public Crawler(Collection<String> seedUrls) {
        this.seedUrls = seedUrls;
    }

    /**
     * Crawls pages based on seed URLs.
     */
    public void crawl() {
        this.findPages();
        this.setIncomingLinks();
    }

    /**
     * Crawls pages based on seed URLs until maxPages pages (including seed pages) have been found.
     * If maxPages is smaller than the number of seed URLs, will scrape all seed URLs but nothing else.
     * 
     * @param maxPages maximum number of pages to be crawled (including seed pages)
     */
    public void crawl(int maxPages) {
        this.findPages(maxPages);
        this.setIncomingLinks();
    }

    /**
     * Retrieve URLs of all crawled pages.
     * This method is used to help create the Graph representation of links between pages.
     * 
     * @return {@code Set} of URLs of all crawled pages
     */
    public Set<String> getUrls() {
        return this.urlToPage.keySet();
    }

    /**
     * Retrieve total number of pages crawled.
     * This method is used for limiting the number of crawled pages, testing and providing information to the user.
     * 
     * @return number of pages crawled
     */
    public int nPages() {
        return this.urlToPage.size();
    }

    /**
     * Retrieves all links that have been found in crawled pages.
     * This includes links to pages that have not been crawled.
     * 
     * @return List of Links on crawled pages
     */
    public List<Link> getAllOutgoingLinks() {
        // Can include links to pages that are not in this.pages
        // For example when 
        List<Link> links = new ArrayList<>();
        for (Page source : this.getPages()) {
            for (String target : source.getOutgoingLinks()) {
                links.add(new Link(source.url, target));
            }
        }
        return links;
    } 

    /**
     * Retrieves all links between crawled pages.
     * Does not include links to pages that have not been crawled.
     * This method is used for constructing the Graph representation of links between pages.
     * 
     * @return List of Links between crawled pages
     */
    public List<Link> getLinksWithin() {
        List<Link> links = new ArrayList<>();
        for (Page target : this.getPages()) {
            for (String source : target.getIncomingLinks()) {
                links.add(new Link(source, target.url));
            }
        }
        return links;
    } 

    /**
     * Retrieves all crawled pages
     * 
     * @return {@code Collection} of crawled pages
     */
    public Collection<Page> getPages() {
        return this.urlToPage.values();
    }

    /**
     * Does actual crawling.
     * Finds pages and scrapes their contents. Starts crawling in this.seedUrls.
     */
    private void findPages() {
        this.urlToPage = new HashMap<>();
        Queue<String> queue = new LinkedList<>(this.seedUrls);

        while (!queue.isEmpty()) {
            // Dequeue first URL, scrape and save Page Object in URL to Page map
            String url = queue.remove();
            Page page;
            try {
                page = parsePage(url);
            } catch (IOException e) {
                System.out.println("Could not parse " + url);
                continue;
            }
            urlToPage.put(url, page);

            // Append URLs from outgoing links to queue
            for (String link : page.getOutgoingLinks()) {
                if (!urlToPage.containsKey(link)) {
                    queue.add(link);
                }
            }   
        }
    }

    /**
     * Does actual crawling.
     * Finds pages and scrapes their contents. Starts crawling in this.seedUrls.
     * Stops crawling after max(len(seedUrls), maxPages) have been added to this.pages.
     * Same as findPages() but stops after maxPages have been found.
     * Repeats a lot of code for efficiency. Otherwise there would be two unnecessary comparisons for each site when using INFINITY.
     * 
     * @param maxPages crawling will be stopped after this number of pages have been crawled   
     */
    private void findPages(int maxPages) {
        this.urlToPage = new HashMap<>();
        Queue<String> queue = new LinkedList<>(this.seedUrls);

        while (!queue.isEmpty()) {
            // Dequeue first URL, scrape page and add to this.pages
            String url = queue.remove();
            Page page;
            try {
                page = parsePage(url);
            } catch (IOException e) {
                continue;
            }
            urlToPage.put(url, page);
            
            // Append URLs from outgoing links to queue if less than maxPages have been found so far
            for (String link : page.getOutgoingLinks()) {
                if (this.nPages() + queue.size() >= maxPages) {
                    break;
                } else if (!urlToPage.containsKey(link)) {
                        queue.add(link);
                    }
            }
            
        }
    }

    /**
     * Parses a web pages contents given a URL
     * 
     * @param url url of page to be parsed
     * @return {@code Page} representing the web page
     * @throws IOException if no page can be found under url
     */
    private static Page parsePage(String url) throws IOException {
        Document doc;
        doc = Jsoup.connect(url).get();
        String title = doc.title();
        String headings = doc.select("h1, h2, h3, h4, h5, h6").text();
        String paragraphs = doc.select("p").text();
        Map<String, Integer> outgoingLinks = new HashMap<>();
        for (Element link : doc.select("a[href]")) {
            outgoingLinks.merge(link.attr("abs:href"), 1, Integer::sum);
        }
        return new Page(url, title, headings, paragraphs, outgoingLinks);
    }

    private void setIncomingLinks() {
        for (Page page : this.urlToPage.values()) {
            for (String outLink : page.getOutgoingLinks()) {
                if (this.urlToPage.containsKey(outLink)) {
                    this.urlToPage.get(outLink).addIncomingLink(page.url);
                }
            }
        }
    }

    /**
     * Link between two pages consisting of source and target.
     * Used to create Graph representation of links between pages.
     */
    public class Link {
        final String source;
        final String target;

        Link(String source, String target) {
            this.source = source;
            this.target = target;
        }
    }
}


