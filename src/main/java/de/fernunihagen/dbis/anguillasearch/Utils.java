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

/**
 * This is a utility class that provides some static helper methods for various other components.
 */
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