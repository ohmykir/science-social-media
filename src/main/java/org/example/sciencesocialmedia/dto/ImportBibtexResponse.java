package org.example.sciencesocialmedia.dto;

import lombok.Data;

import java.util.List;

@Data
public class ImportBibtexResponse {
    private int successCount;
    private int failedCount;
    private List<String> errors;

    public ImportBibtexResponse(int successCount, int failedCount, List<String> errors) {
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.errors = errors;
    }
}
