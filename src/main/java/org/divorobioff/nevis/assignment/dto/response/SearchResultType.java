package org.divorobioff.nevis.assignment.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SearchResultType {
    CLIENT("client"),
    DOCUMENT("document");

    private final String value;

    SearchResultType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
