package com.dap.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dap.backend.model.HistoryItem;
import com.dap.backend.repository.GenerationHistoryRepository;

/**
 * REST controller for document generation history.
 * <p>
 * Previously scanned the filesystem {@code generated/} directory on every request.
 * Now reads persisted records from MongoDB for reliability across restarts.
 * </p>
 * <p>
 * The existing API endpoint ({@code GET /api/history}) and the
 * {@link HistoryItem} response format ({@code filename}, {@code size}, {@code lastModified})
 * are completely unchanged — no frontend modifications required.
 * </p>
 */
@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final GenerationHistoryRepository historyRepository;

    public HistoryController(GenerationHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * Returns all generation history records, ordered newest-first.
     * Maps MongoDB documents to the existing {@link HistoryItem} DTO:
     * {@code generatedAt.toEpochMilli()} → {@code lastModified}.
     *
     * @return list of {@link HistoryItem} objects; empty if no history exists
     */
    @GetMapping
    public List<HistoryItem> history() {
        return historyRepository.findAllByOrderByGeneratedAtDesc()
                .stream()
                .map(doc -> new HistoryItem(
                        doc.getFilename(),
                        doc.getSize(),
                        doc.getGeneratedAt().toEpochMilli()
                ))
                .collect(Collectors.toList());
    }
}