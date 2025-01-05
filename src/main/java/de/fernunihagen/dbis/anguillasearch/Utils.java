package de.fernunihagen.dbis.anguillasearch;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Optional;

import java.util.Map;
import java.util.stream.Collectors;

// This is a utility class that provides some helper methods for the tests
public class Utils {
    
    private Utils() {}

    // This method parses a JSON file and returns the parsed object
    public static JsonObject parseJSONFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        // Read the file content using try-with-resources
        String content = Files.readString(path);
        // Parse the JSON content
        return new Gson().fromJson(content, JsonObject.class);
    }

    public static List<JsonObject> parseAllJSONFiles(Optional<String> folderName) throws IOException {
        String fN = folderName.orElse("intranet");
        List<JsonObject> jsonObjects = new ArrayList<>();
        // Get all files in the folder
        File folder = new File(fN);
        File[] files = folder.listFiles();
        // Iterate over all files
        for (File file : files) {
            // Parse the JSON file
            jsonObjects.add(parseJSONFile(file.getAbsolutePath()));
        }
        return jsonObjects;
    }

    /**
     * Counts the occurence of all elements in a {@code List}.
     * Use cases:
     *  1. In StringProcessing class to count the occurence of tokens on a web page or in a query text.
     *  2. In Page class to count occurence of links on or to a web page.
     * 
     * @param <T> type of {@code List} elements
     * @param list {@code List} of elements to be counted
     * @return {@code Map} from elements to counts
     */
    public static <T> Map<T, Integer> listToCounts(List<T> list) {
        return list.stream().collect(Collectors.groupingBy(e -> e, Collectors.reducing(0, e -> 1, Integer::sum)));
    }

    /**
     * Counts distinct keys or sums values of a {@code Map} from any type to {@code Integer}.
     * 
     * @param <T>
     * @param map
     * @param distinct
     * @return
     */
    public static <T> int nElems(Map<T, Integer> map, boolean distinct) {
        if (distinct) {
            return map.size();
        } else {
            int sum = 0;
            for (int count : map.values()) {
                sum += count;
            }
            return sum;
        }
    }
}