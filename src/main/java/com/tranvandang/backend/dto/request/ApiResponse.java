package com.tranvandang.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// Bo qua nhung cai bi null
@JsonInclude(JsonInclude.Include.NON_NULL)
// Chuan hoa api
public class ApiResponse<T> {
    @Builder.Default
    int code = 1000;

    String message;
    T result;
}
