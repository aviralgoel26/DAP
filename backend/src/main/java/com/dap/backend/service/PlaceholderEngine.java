package com.dap.backend.service;

import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class PlaceholderEngine {

    public String replacePlaceholders(
            String text,
            Map<String, String> placeholders) {

        if (text == null || text.isEmpty()) {
            return text;
        }

        String updatedText = text;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {

            updatedText = updatedText.replace(
                    "{{" + entry.getKey() + "}}",
                    entry.getValue()
            );

        }

        return updatedText;

    }
    

}