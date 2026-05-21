package com.skilltask.base;

/**
 * Central place for URLs, demo credentials, and timing.
 * Change values here when running against a different environment.
 */
public final class TestConfig {

    private TestConfig() { }   // utility class — no instances

    public static final String BASE_URL = "http://localhost:4200";

    // ── Demo accounts (seeded by DataInitializer) ─────────────────
    public static final String MANAGER_EMAIL = "manager@demo.com";
    public static final String MANAGER_PW    = "password123";

    public static final String ALICE_EMAIL = "alice@demo.com";
    public static final String ALICE_PW    = "password123";

    public static final String BOB_EMAIL = "bob@demo.com";
    public static final String BOB_PW    = "password123";

    // ── Waits (seconds) ───────────────────────────────────────────
    public static final int IMPLICIT_WAIT = 3;
    public static final int EXPLICIT_WAIT = 10;
}
