package com.app.model.icon;

public enum Icons {
    DOWNLOAD,
    PLUS,
    USERS,
    TRENDING_UP,
    FILE_CERTIFICATE,
    CLIPBOARD_LIST,
    TRENDING_DOWN,
    CURRENCY_FRANC,
    POINT_FILLED,
    ARROW_RIGHT,
    USER_PLUS,
    FILE_PLUS,
    CASH,
    CALENDAR_PLUS,
    USER_CHECK,
    CREDIT_CARD,
    EYE,
    SEARCH,
    X,
    FILE_TEXT,
    EDIT;

    @Override
    public String toString() {
        return "ti ti-" + name().toLowerCase().replace('_', '-'); // ti ti-download
    }
}
