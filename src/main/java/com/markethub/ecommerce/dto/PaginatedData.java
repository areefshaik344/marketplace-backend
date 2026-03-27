package com.markethub.ecommerce.dto;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaginatedData<T> {
    private List<T> items;
    private long total;
    private int page;
    private int pageSize;
    private int totalPages;
}
