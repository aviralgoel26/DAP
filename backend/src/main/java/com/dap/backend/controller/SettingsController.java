package com.dap.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dap.backend.model.AppSettings;
import com.dap.backend.service.SettingsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public AppSettings getSettings() {

        return settingsService.getSettings();

    }

    @PutMapping
    public AppSettings saveSettings(

            @RequestBody AppSettings request

    ) {

        return settingsService.saveSettings(request);

    }

}