package Shingu.Interviewer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//사용자 또는 AI의 메시지를 정의 (각 메시지는 역할과, 내용을 가짐)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String role;
    private String content;
}
