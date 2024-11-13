package com.sparta.plate.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageableUtil {
    public static Pageable createPageable(Integer pageNumber, Integer pageSize) {
        int page = (pageNumber != null) ? pageNumber - 1 : 0;
        int size = (pageSize != null) ? pageSize : 10;
        return PageRequest.of(page, size);
    }
}