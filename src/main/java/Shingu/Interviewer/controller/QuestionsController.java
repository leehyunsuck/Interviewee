//package Shingu.Interviewer.controller;
//
//import Shingu.Interviewer.dto.QuestionsForm;
//import Shingu.Interviewer.entity.Questions;
//import Shingu.Interviewer.repository.QuestionsRepository;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.http.HttpStatus;
//import java.net.URI;
//
//
//import java.lang.reflect.Method;
//
//@RestController
//public class QuestionsController {
//
//    @Autowired
//    private QuestionsRepository questionsRepository;
//
//    @GetMapping(path = "/save/questions")
//    @PostMapping(path = "/save/questions", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public ResponseEntity<?> createQuestions(@ModelAttribute QuestionsForm form, HttpSession session) throws Exception {
//        String loginEmail = (String) session.getAttribute("loginEmail");
//        String[] questions = (String[]) session.getAttribute("questions");
//        String[] answers = (String[]) session.getAttribute("answers");
//        String[] feedbakcs = (String[]) session.getAttribute("feedbacks");
//
//        String keyword = (String) session.getAttribute("keyword");
//
//        if (loginEmail == null || keyword == null || questions[0] == null) {
//            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/basic/index.jsp")).build();
//        }
//
//        form.setKeyword(keyword);
//        form.setEmail(loginEmail);
//        for (int i = 0; i < questions.length; i++) {
//            String questionMethodName = "setQuestion" + (i + 1);
//            String answerMethodName = "setAnswers" + (i + 1);
//            String feedbackMethodName = "setFeedback" + (i + 1);
//
//            Method questionMethod = form.getClass().getMethod(questionMethodName, String.class);
//            Method answerMethod = form.getClass().getMethod(answerMethodName, String.class);
//            Method feedbackMethod = form.getClass().getMethod(feedbackMethodName, String.class);
//
//            questionMethod.invoke(form, questions[i]);
//            answerMethod.invoke(form, answers[i]);
//            feedbackMethod.invoke(form, feedbakcs[i]);
//        }
//
//        Questions questionsEntity = form.toEntity();
//        questionsRepository.save(questionsEntity);
//
//        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/basic/showResult.jsp")).build();
//    }
//}
