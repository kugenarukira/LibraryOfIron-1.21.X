package net.ironedge.libraryofiron.capability;

public enum CapabilitySyncPolicy {
    NONE,       // Never sync
    SERVER_TO_CLIENT, // Server sends updates
    CLIENT_TO_SERVER, // Client sends updates
    BIDIRECTIONAL     // Both directions
}