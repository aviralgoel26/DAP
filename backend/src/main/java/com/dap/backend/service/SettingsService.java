package com.dap.backend.service;

import org.springframework.stereotype.Service;

import com.dap.backend.model.AppSettings;

@Service
public class SettingsService {

    private AppSettings settings =
            new AppSettings(

                    "",

                    "",

                    "",

                    true,

                    true

            );

    public AppSettings getSettings() {

        return settings;

    }

    public AppSettings saveSettings(

            AppSettings request

    ) {

        settings = request;

        return settings;

    }

}