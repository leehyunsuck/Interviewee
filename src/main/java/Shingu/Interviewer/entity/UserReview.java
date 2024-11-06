package Shingu.Interviewer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_review")
public class UserReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String pros;

    @Column(columnDefinition = "TEXT")
    private String cons;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    public UserReview(String email, String pros, String cons, String feedback) {
        this.email = email;
        this.pros = pros;
        this.cons = cons;
        this.feedback = feedback;
    }

    protected UserReview() {
    }
}
