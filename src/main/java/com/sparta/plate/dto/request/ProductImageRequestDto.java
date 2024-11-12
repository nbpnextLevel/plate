package com.sparta.plate.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequestDto {
    private MultipartFile[] files;
    private Integer primaryImageIndex;
    private List<UUID> deletedImageIds;

    public boolean hasFiles() {
        return files != null && files.length > 0;
    }
}