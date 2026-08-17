package com.example.application.storage;

public class StoredObjectNotAvailableException extends RuntimeException {

    public StoredObjectNotAvailableException() {
        super("Stored object is not available");
    }
}
