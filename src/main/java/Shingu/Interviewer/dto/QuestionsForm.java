package Shingu.Interviewer.dto;

import Shingu.Interviewer.entity.Questions;
import lombok.Data;

@Data
public class QuestionsForm {
    private String email;
    private String keyword;
    private String question1;
    private String answers1;
    private String feedback1;
    private String question2;
    private String answers2;
    private String feedback2;
    private String question3;
    private String answers3;
    private String feedback3;
    private String question4;
    private String answers4;
    private String feedback4;
    private String question5;
    private String answers5;
    private String feedback5;
    private String question6;
    private String answers6;
    private String feedback6;
    private String question7;
    private String answers7;
    private String feedback7;
    private String question8;
    private String answers8;
    private String feedback8;
    private String question9;
    private String answers9;
    private String feedback9;
    private String question10;
    private String answers10;
    private String feedback10;

    public Questions toEntity() {
        return Questions.builder()
                .email(email)
                .keyword(keyword)
                .question1(question1)
                .answers1(answers1)
                .feedback1(feedback1)
                .question2(question2)
                .answers2(answers2)
                .feedback2(feedback2)
                .question3(question3)
                .answers3(answers3)
                .feedback3(feedback3)
                .question4(question4)
                .answers4(answers4)
                .feedback4(feedback4)
                .question5(question5)
                .answers5(answers5)
                .feedback5(feedback5)
                .question6(question6)
                .answers6(answers6)
                .feedback6(feedback6)
                .question7(question7)
                .answers7(answers7)
                .feedback7(feedback7)
                .question8(question8)
                .answers8(answers8)
                .feedback8(feedback8)
                .question9(question9)
                .answers9(answers9)
                .feedback9(feedback9)
                .question10(question10)
                .answers10(answers10)
                .feedback10(feedback10)
                .build();
    }
}
