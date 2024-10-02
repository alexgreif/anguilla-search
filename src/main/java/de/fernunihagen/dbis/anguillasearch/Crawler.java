package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
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
        this.crawl();
    }

    private void setSeedUrls(String[] seedUrls) {
        this.seedUrls = Arrays.asList(seedUrls);
    }

    public void crawl() {
        this.findPages();
        this.setInGoingLinks();
    }

    public Set<String> getUrls() {
        return this.pages.keySet();
    }

    public int nPages() {
        return this.pages.size();
    }

    public int nLinks() {
        int results = 0;
        for (Page page : this.getPages()) {
            results += page.nIncomingLinks(false);
        }
        return results;
    }

    private Collection<Page> getPages() {
        return this.pages.values();
    }

    private void findPages() {
        this.pages = new HashMap<>();
        Queue<String> Q = new LinkedList<>(this.seedUrls);

        while (!Q.isEmpty()) {
            String url = Q.remove();
            Page page;
            try {
                page = new Page(url);
            } catch (IOException e) {
                continue;
            }

            pages.put(url, page);
            for (String link : page.getOutgoingLinks()) {
                if (!pages.containsKey(link)) {
                    Q.add(link);
                }
            }
        }
    }

    private void setInGoingLinks() {
        for (Page page : this.pages.values()) {
            for (String outLink : page.getOutgoingLinks()) {
                this.pages.get(outLink).addIncomingLink(page.url);
            }
        }
    }
}


