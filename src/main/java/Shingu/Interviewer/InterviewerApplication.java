package Shingu.Interviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class InterviewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewerApplication.class, args);
	}

}
