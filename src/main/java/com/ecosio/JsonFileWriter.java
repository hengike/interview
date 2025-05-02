package com.ecosio;

import com.ecosio.dto.Link;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class to write {@link Link} objects to a JSON file with timestamped filenames.
 */
public class JsonFileWriter {

    public static String writeLinksToJsonFile(List<Link> links, String baseFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = baseFilename + "_" + timestamp + ".json";
        String jsonContent = toJson(links);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(jsonContent);
            System.out.println("Links written to " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to write JSON: " + e.getMessage());
        }
        return filePath;
    }

    /** Converts a list of links to a JSON array string. */
    public static String toJson(List<Link> links) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[\n");

        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            jsonBuilder.append("  {\n")
                    .append("    \"label\": \"").append(escapeJson(link.getShortLabel())).append("\",\n")
                    .append("    \"url\": \"").append(escapeJson(link.getUrl())).append("\"\n")
                    .append("  }");

            if (i < links.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }

        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    /** Escapes backslashes and quotes for JSON safety. */
    static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}