package com.mytconvert.datamanagement.dto;

import java.util.List;

public class PagedUserConversionStatsDTO {
    private List<UserConversionStatsDTO> content;
    private int totalPages;
    private long totalElements;
    
    public PagedUserConversionStatsDTO() {
    }
    
    public PagedUserConversionStatsDTO(List<UserConversionStatsDTO> content, int totalPages, long totalElements) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<UserConversionStatsDTO> getContent() {
        return content;
    }

    public void setContent(List<UserConversionStatsDTO> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}