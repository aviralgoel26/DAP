package com.dap.backend.service;

import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Stateless engine for replacing {@code {{key}}} placeholder tokens within a text string.
 */
@Service
public class PlaceholderEngine {

    /**
     * Replaces all {@code {{key}}} tokens in the given text with the corresponding values
     * from the placeholders map. Keys not present in the map are left unchanged.
     *
     * @param text         the source text containing zero or more placeholder tokens
     * @param placeholders a map of placeholder keys to their replacement values
     * @return the text with all matching placeholders substituted, or the original text
     *         if it is {@code null} or empty
     */
    public String replacePlaceholders(String text, Map<String, String> placeholders) {

        if (text == null || text.isEmpty()) {
            return text;
        }

        String updatedText = text;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            updatedText = updatedText.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return updatedText;
    }
}