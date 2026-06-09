package com.app.model.viewmodel;

import com.app.model.theme.ThemeColor;

public class StatItem {
    private String label;
    private int percentage; // ou value, selon comment tu calcules
    private String colorClass; // On stocke la classe CSS générée par ton Enum

    public StatItem() {
    }

    public StatItem(String label, int percentage, ThemeColor theme) {
        this.label = label;
        this.percentage = percentage;
        // On extrait directement la chaîne "primary", "secondary", etc.
        this.colorClass = theme.getCssClass();
    }

    // --- Getters ---
    public String getLabel() {
        return label;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getColorClass() {
        return colorClass;
    }

    // --- Setters ---
    public void setLabel(String label) {
        this.label = label;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public void setColorClass(String colorClass) {
        this.colorClass = colorClass;
    }
}
