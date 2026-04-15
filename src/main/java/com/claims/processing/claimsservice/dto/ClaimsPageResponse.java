package com.claims.processing.claimsservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClaimsPageResponse {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLastPage;
    private List<ClaimsProcessingResponse> content;


}
