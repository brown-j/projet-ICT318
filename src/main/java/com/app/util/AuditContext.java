package com.app.util;

import com.app.jpa.model.OfficierEtatCivil;

public class AuditContext {
    private static final ThreadLocal<OfficierEtatCivil> currentOfficier = new ThreadLocal<>();
    private static final ThreadLocal<String> currentIp = new ThreadLocal<>();

    public static void set(OfficierEtatCivil officier, String ip) {
        currentOfficier.set(officier);
        currentIp.set(ip);
    }

    public static OfficierEtatCivil getOfficier() {
        return currentOfficier.get();
    }

    public static String getIp() {
        return currentIp.get();
    }

    public static void clear() {
        currentOfficier.remove();
        currentIp.remove();
    }
}