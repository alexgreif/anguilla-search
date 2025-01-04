package de.fernunihagen.dbis.anguillasearch;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
/**
 * Unit tests for the search.
 */
class SearchTests {

    @Test
    void findCorrectURLs() throws IOException {
        JsonObject testJSON = Utils.parseJSONFile("intranet/cheesy1-f126d0d3.json");
        // Extract the seed URLs from the JSON file
        String[] seedUrls = new Gson().fromJson(testJSON.get("Seed-URLs"), String[].class);
        // Extract the query from the JSON file
        String[] query = new Gson().fromJson(testJSON.get("Query-Token"), String[].class);
        // Extract the expected URLs from the JSON file
        String[] expectedURLs = new Gson().fromJson(testJSON.get("Query-URLs"), String[].class);
        // Execute a search with the given query in the given network via the seed URLs
        Index index = new Index(seedUrls);

        // Place your code here to execute the search
        Index.SimpleSearchResult resultTfIdf = index.tfIdfSearch(String.join(", ", query));
        Index.SimpleSearchResult resultCosine = index.vectorSearch(String.join(", ", query));
        List<String> foundUrlsTfIdf = resultTfIdf.getUrls(); 
        List<String> foundUrlsCosine = resultCosine.getUrls();

        // Print results
        System.out.println("Expected\n" + Arrays.asList(expectedURLs) + "\n");
        System.out.println("TfIdf");
        resultTfIdf.prettyPrint();
        System.out.println(resultTfIdf);
        System.out.println("\nCosine Similarity");
        resultCosine.prettyPrint();
        System.out.println(resultCosine);

        // Verify that the found URLs are correct, i.e. the same as stated in the JSON file
        // Uncomment the following line once you have implemented the search
        assertTrue(foundUrlsTfIdf.containsAll(Arrays.asList(expectedURLs)));
        assertTrue(foundUrlsCosine.containsAll(Arrays.asList(expectedURLs)));


        // Remove the following line after adding your code!
    }
}