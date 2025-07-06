package Shingu.Interviewer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;

@Service
public class TextToSpeechService {

    // Flask TTS 서버 URL (application.properties 등에 설정 가능)
    @Value("${flask.tts.url}")
    private String ttsServerUrl;

    // 파일들이 저장될 디렉토리 경로를 설정
    @Value("${file.storage.path}")
    private String fileStoragePath;

    @PostConstruct
    public void deleteAllFilesOnStartup() {
        File directory = new File(fileStoragePath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        try {
                            file.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void synthesizeText(String text, String outputFileName) throws Exception {
        File directory = new File(fileStoragePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.valueOf("audio/mpeg")));

        String requestJson = "{\"text\":\"" + text.replace("\"", "\\\"") + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        // Flask 서버에 POST 요청하여 음성(mp3) 데이터 바이트 받기
        ResponseEntity<byte[]> response = restTemplate.exchange(ttsServerUrl, HttpMethod.POST, entity, byte[].class);

        byte[] audioBytes = response.getBody();

        if (audioBytes == null || audioBytes.length == 0) {
            throw new RuntimeException("Empty audio content received from TTS server");
        }

        try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
            fos.write(audioBytes);
        }
    }
}

/*
package Shingu.Interviewer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Collections;

@Service
public class TextToSpeechService {

    @Value("${gcp.api.key}")
    private String apiKey;

    // 파일들이 저장될 디렉토리 경로를 설정
    @Value("${file.storage.path}")
    private String fileStoragePath;

    // 서버 시작 시 파일 삭제 로직
    @PostConstruct
    public void deleteAllFilesOnStartup() {
        File directory = new File(fileStoragePath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        try {
                            file.delete();
                        } catch (Exception e) {
                            // 예외 처리
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void synthesizeText(String text, String outputFileName) throws Exception {
        File directory = new File("./mp3");
        if (!directory.exists()) {
            directory.mkdirs(); // 디렉토리 생성
        }

        String url = "https://texttospeech.googleapis.com/v1/text:synthesize?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String requestJson = "{"
                + "\"input\": {\"text\": \"" + text + "\"},"
                + "\"voice\": {\"languageCode\": \"ko-KR\", \"name\": \"ko-KR-Neural2-A\"},"
                + "\"audioConfig\": {\"audioEncoding\": \"MP3\"}"
                + "}";

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        String audioContent = response.getBody().split("\"audioContent\": \"")[1].split("\"")[0];
        byte[] audioBytes = Base64.getDecoder().decode(audioContent);

        try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
            fos.write(audioBytes);
            //System.out.println("Audio content written to file " + outputFileName);
        }
    }
}
*/