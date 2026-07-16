package com.dap.backend.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dap.backend.model.HistoryItem;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Value("${generated.storage.path}")
    private String generatedPath;

    @GetMapping
    public List<HistoryItem> history() {

        File folder = new File(generatedPath);

        List<HistoryItem> items = new ArrayList<>();

        File[] files = folder.listFiles();

        if (files == null)
            return items;

        for (File file : files) {

            if (!file.isFile())
                continue;

            items.add(

                    new HistoryItem(

                            file.getName(),

                            file.length(),

                            file.lastModified()

                    )

            );

        }

        items.sort(

                Comparator.comparing(

                        HistoryItem::getLastModified

                ).reversed()

        );

        return items;

    }

}