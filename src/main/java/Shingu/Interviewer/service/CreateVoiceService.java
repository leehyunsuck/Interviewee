package Shingu.Interviewer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class CreateVoiceService {
    @Autowired  //TTS
    private TextToSpeechService textToSpeechService;
    private static final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public List<String> createVoice(List<String> questions, String ip) {
        ip = ip.replace(":", "_");

        // File Path를 담을 리스트
        List<String> audioFilePaths = new ArrayList<>();

        try {
            for (int idx = 0; idx < questions.size(); idx++) {
                // 음성 파일 생성
                String filePath = "./mp3/" + ip + idx + ".mp3";
                textToSpeechService.synthesizeText(questions.get(idx), filePath);

                //System.out.println("파일 생성");

                // 기존 예약 작업 있으면 취소
                if (scheduledTasks.containsKey(filePath)) scheduledTasks.get(filePath).cancel(false);

                // 삭제 작업 예약 -> 각 질문 최대 시간마다 삭제
                ScheduledFuture<?> scheduledTask = scheduler.schedule(() -> {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }, ((idx + 1) * 3L), TimeUnit.MINUTES);

                // 예약 작업 저장
                scheduledTasks.put(filePath, scheduledTask);

                // 오디오 파일 경로 리스트에 추가
                audioFilePaths.add("/mp3/" + ip + idx + ".mp3");
            }
            return audioFilePaths;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
