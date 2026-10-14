package de.fernunihagen.dbis.anguillasearch.searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.fernunihagen.dbis.anguillasearch.Page;

/**
 * Tests the combined search engine independently of the course intranet.
 */
class SearchEngineTests {
    @Test
    void retainsDistinctPagesWithIdenticalScores() {
        String firstUrl = "http://first.example";
        String secondUrl = "http://second.example";
        Page first = new Page(firstUrl, "ricotta", "", "", Map.of(secondUrl, 1));
        Page second = new Page(secondUrl, "ricotta", "", "", Map.of(firstUrl, 1));
        first.addIncomingLink(secondUrl);
        second.addIncomingLink(firstUrl);
        SearchEngine engine = new SearchEngine(List.of(first, second));

        assertEquals(2, engine.search("ricotta", false).size());
    }
}
