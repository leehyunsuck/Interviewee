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

    @Column(name = "review", nullable = true)
    private Integer review;

    public UserReview(String email, String pros, String cons, Integer review) {
        this.email = email;
        this.pros = pros;
        this.cons = cons;
        this.review = review;
    }

    protected UserReview() {
    }
}
