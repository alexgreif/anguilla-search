package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;

import java.io.IOException;

public class Crawler {
    private List<String> seedUrls;
    private Map<String, Page> pages;
    
    public Crawler(String[] seedUrls) {
        this.setSeedUrls(seedUrls);
    }

    private void setSeedUrls(String[] seedUrls) {
        this.seedUrls = Arrays.asList(seedUrls);
    }

    public void crawl() {
        this.findPages();
        this.setIncomingLinks();
    }

    public void crawl(int maxPages) {
        this.findPages(maxPages);
        this.setIncomingLinks();
    }

    public Set<String> getUrls() {
        return this.pages.keySet();
    }

    public int nPages() {
        return this.pages.size();
    }

    public int nLinks(boolean distinct) {
        int results = 0;
        for (Page page : this.getPages()) {
            results += page.nIncomingLinks(distinct);
        }
        return results;
    }

    public List<Link> getAllLinks() {
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

    public List<Link> getLinksWithin() {
        List<Link> links = new ArrayList<>();
        for (Page target : this.getPages()) {
            for (String source : target.getIncomingLinks()) {
                links.add(new Link(source, target.url));
            }
        }
        return links;
    } 

    private Collection<Page> getPages() {
        return this.pages.values();
    }

    private void findPages() {
        this.pages = new HashMap<>();
        Queue<String> Q = new LinkedList<>(this.seedUrls);

        while (!Q.isEmpty()) {
            // Dequeue first URL, scrape page and add to this.pages
            String url = Q.remove();
            Page page;
            try {
                page = new Page(url);
            } catch (IOException e) {
                continue;
            }
            pages.put(url, page);
            
            // Append URLs from outgoing links to queue
            for (String link : page.getOutgoingLinks()) {
                if (!pages.containsKey(link)) {
                    Q.add(link);
                }
            }   
        }
    }

    private void findPages(int maxPages) {
        // Same as findPages but stops after maxPages have been found.
        // Repeats a lot of same code for efficiency
        // Otherwise there would be two unnecessary comparisons for each site when using INFINITY
        this.pages = new HashMap<>();
        Queue<String> Q = new LinkedList<>(this.seedUrls);

        while (!Q.isEmpty()) {
            // Dequeue first URL, scrape page and add to this.pages
            String url = Q.remove();
            Page page;
            try {
                page = new Page(url);
            } catch (IOException e) {
                continue;
            }
            pages.put(url, page);
            
            // Append URLs from outgoing links to queue if less than maxPages have been found so far
            if (this.nPages() + Q.size() < maxPages) {
                for (String link : page.getOutgoingLinks()) {
                    if (!pages.containsKey(link) && this.nPages() + Q.size() < maxPages) {
                        Q.add(link);
                    }
                }
            }
        }
    }

    private void setIncomingLinks() {
        for (Page page : this.pages.values()) {
            for (String outLink : page.getOutgoingLinks()) {
                if (this.pages.containsKey(outLink)) {
                    this.pages.get(outLink).addIncomingLink(page.url);
                }
            }
        }
    }

    public class Link {
        final String source;
        final String target;

        Link(String source, String target) {
            this.source = source;
            this.target = target;
        }
    }
}


