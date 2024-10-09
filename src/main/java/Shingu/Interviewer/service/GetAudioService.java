package Shingu.Interviewer.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import java.io.File;

@Service
public class GetAudioService {
    public ResponseEntity<Resource> getAudioFile(String fileName) {
        try {
            File audioFile = new File("./mp3/" + fileName); // 실제 파일 경로

            if (audioFile.exists()) {
                org.springframework.core.io.Resource resource = new FileSystemResource(audioFile);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg"); // MP3 파일 MIME 타입 설정
                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build(); // 파일이 없으면 404 반환
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 서버 에러 처리
        }
    }
}
