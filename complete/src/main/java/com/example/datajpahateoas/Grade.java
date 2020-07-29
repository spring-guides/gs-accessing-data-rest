package com.example.datajpahateoas;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Grade {

    GOLD("GOLD", "Gold"),
    BRONZE("BRONZE", "Bronze");

    private final String key;
    private final String title;
}
