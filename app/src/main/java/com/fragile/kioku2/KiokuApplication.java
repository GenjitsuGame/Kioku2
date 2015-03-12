package com.fragile.kioku2;

import fm.last.api.Session;

public class KiokuApplication {

    public static final String APPLICATION_NAME = "Kioku";

    private static Session session;

    public static void setSession(Session session) {
        KiokuApplication.session = session;
    }

    public static Session getSession() {
        return KiokuApplication.session;
    }

    public static boolean isSessionSet() { return session != null; }

}
