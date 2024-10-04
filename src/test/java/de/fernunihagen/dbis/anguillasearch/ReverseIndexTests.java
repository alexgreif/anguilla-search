package de.fernunihagen.dbis.anguillasearch;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.fernunihagen.dbis.anguillasearch.ReverseIndex.TfIdfMap;


/**
 * Unit tests for the reverse index.
 */
class ReverseIndexTests {

    static List<JsonObject> testPages;
    static JsonObject correctReverseIdex;

    static ReverseIndex ri;
    static List<Page> pages;

    @BeforeAll
    static void setUp() throws IOException {

        testPages = Utils.parseAllJSONFiles(java.util.Optional.of("src/test/resources/tf-idf/pages"));
        correctReverseIdex = Utils.parseJSONFile("src/test/resources/tf-idf/index.json");

        // Add your code here to create your reverse index
        pages = new ArrayList<>();
        for (JsonObject page : testPages) {
            pages.add(new Page(page));
        }
        ForwardIndex forwardIndex = new ForwardIndex(pages);
        ri = new ReverseIndex(forwardIndex);
    }

        


    @Test
    void reverseIdexTFIDF() {

        for (Entry<String, JsonElement> entry : correctReverseIdex.entrySet()) {
            // The token of the reverse index
            String token = entry.getKey();
            JsonObject pagesMap= entry.getValue().getAsJsonObject();
            for (Entry<String, JsonElement> pageEntry : pagesMap.entrySet()) {

                // The URL of the page
                String url = pageEntry.getKey();
                // The TF-IDF value of the token in the page
                Double tfidf = pageEntry.getValue().getAsDouble();


                // Add your code here to compare the TF-IDF values of your reverse index with the correct values


                // Check if the reverse index contains the token
                assertTrue(ri.containsToken(token));

                // Get the map of pages for the token
                TfIdfMap tfIdfMap = ri.getPagesContaining(token);
        
                // Check if the URL exists for that token
                assertTrue(tfIdfMap.containsPage(url));

                // Get the TF-IDF value for the URL from your reverse index
                Double indexTfidf = tfIdfMap.getTfIdfOf(url);
                // Check if the TF-IDF value is correct
                assertTrue(Math.abs(tfidf - indexTfidf) < 0.0001);

                // Remove the following line after adding your code

            }
        }
    }
}