package shop.chaekmate.api.common.dto;

import java.time.LocalDateTime;

// 공통 응답 포맷 적용 DTO
public record CommonResponse<T>(LocalDateTime timestamp, String code, T data) {
}
