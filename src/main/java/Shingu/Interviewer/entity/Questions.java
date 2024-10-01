package Shingu.Interviewer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "questions")
public class Questions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    private String keyword;

    @Column(columnDefinition = "TEXT")
    private String question1;

    @Column(columnDefinition = "TEXT")
    private String answers1;

    @Column(columnDefinition = "TEXT")
    private String feedback1;

    @Column(columnDefinition = "TEXT")
    private String question2;

    @Column(columnDefinition = "TEXT")
    private String answers2;

    @Column(columnDefinition = "TEXT")
    private String feedback2;

    @Column(columnDefinition = "TEXT")
    private String question3;

    @Column(columnDefinition = "TEXT")
    private String answers3;

    @Column(columnDefinition = "TEXT")
    private String feedback3;

    @Column(columnDefinition = "TEXT")
    private String question4;

    @Column(columnDefinition = "TEXT")
    private String answers4;

    @Column(columnDefinition = "TEXT")
    private String feedback4;

    @Column(columnDefinition = "TEXT")
    private String question5;

    @Column(columnDefinition = "TEXT")
    private String answers5;

    @Column(columnDefinition = "TEXT")
    private String feedback5;

    @Column(columnDefinition = "TEXT")
    private String question6;

    @Column(columnDefinition = "TEXT")
    private String answers6;

    @Column(columnDefinition = "TEXT")
    private String feedback6;

    @Column(columnDefinition = "TEXT")
    private String question7;

    @Column(columnDefinition = "TEXT")
    private String answers7;

    @Column(columnDefinition = "TEXT")
    private String feedback7;

    @Column(columnDefinition = "TEXT")
    private String question8;

    @Column(columnDefinition = "TEXT")
    private String answers8;

    @Column(columnDefinition = "TEXT")
    private String feedback8;

    @Column(columnDefinition = "TEXT")
    private String question9;

    @Column(columnDefinition = "TEXT")
    private String answers9;

    @Column(columnDefinition = "TEXT")
    private String feedback9;

    @Column(columnDefinition = "TEXT")
    private String question10;

    @Column(columnDefinition = "TEXT")
    private String answers10;

    @Column(columnDefinition = "TEXT")
    private String feedback10;
}
