package com.megait.myhome.util;

import lombok.Builder;
import lombok.Data;

@Data   // DTO
@Builder
public class EmailMessage {
    private String to;      // 수신인
    private String subject; // 제목
    private String message; // 내용
}
