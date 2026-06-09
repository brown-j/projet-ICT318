package com.app.model.theme;

public enum ThemeColor {
    PRIMARY("primary"),
    SECONDARY("secondary"),
    ACCENT("accent"),
    SUCCESS("success"),
    INFO("info"),
    WARNING("warning"),
    ERROR("error"),
    NEUTRAL("neutral");

    private final String cssClass;

    // Le constructeur de l'enum lie la constante à sa chaîne de caractères
    ThemeColor(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String toString() {
        return cssClass;
    }
}