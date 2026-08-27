package com.example.claims.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String resource;
    private final String id;

    public ResourceNotFoundException(String resource, String id) {
        super(resource + " '" + id + "' not found");
        this.resource = resource;
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public String getId() {
        return id;
    }
}
