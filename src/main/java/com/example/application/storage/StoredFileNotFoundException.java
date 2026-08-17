package com.example.application.storage;

public class StoredFileNotFoundException extends RuntimeException {

    public StoredFileNotFoundException() {
        super("Stored file was not found");
    }
}
