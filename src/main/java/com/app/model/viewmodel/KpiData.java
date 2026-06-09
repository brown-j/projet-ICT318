package com.app.model.viewmodel;

import com.app.model.icon.Icons;
import com.app.model.theme.ThemeColor;

public class KpiData {
    // On garde un String ici pour que le Getter fonctionne parfaitement avec la JSP
    private String type;
    private String icon;
    private String value;
    private String label;
    private String trendType;
    private String trendIcon;
    private String trendValue;

    public KpiData() {
    }

    // Le premier paramètre prend maintenant ThemeColor au lieu de String
    public KpiData(ThemeColor theme, String icon, String value, String label, boolean isUp, String trendValue) {
        // On extrait automatiquement la classe CSS (ex: "primary")
        this.type = theme.getCssClass();
        this.icon = icon;
        this.value = value;
        this.label = label;
        this.trendValue = trendValue;
        setTrendTypeAndIcon(isUp);
    }

    // Setters
    public void setType(String type) {
        this.type = type;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setTrendValue(String trendValue) {
        this.trendValue = trendValue;
    }

    public void setTrendTypeAndIcon(boolean isUp) {
        this.trendType = isUp ? "up" : "down";
        this.trendIcon = isUp ? Icons.TRENDING_UP.toString() : Icons.TRENDING_DOWN.toString();
    }

    // Getters
    public String getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public String getTrendType() {
        return trendType;
    }

    public String getTrendIcon() {
        return trendIcon;
    }

    public String getTrendValue() {
        return trendValue;
    }
}
