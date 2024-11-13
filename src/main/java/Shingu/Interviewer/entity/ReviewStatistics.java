package Shingu.Interviewer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "review_statistics")
public class ReviewStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "average_review", nullable = false)
    private Double averageReview;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount;


    public ReviewStatistics(Double averageReview, Integer reviewCount) {
        this.averageReview = averageReview;
        this.reviewCount = reviewCount;
    }

    protected ReviewStatistics() {
    }

    // Getter와 Setter
    public Integer getId() {
        return id;
    }

    public Double getAverageReview() {
        return averageReview;
    }

    public void setAverageReview(Double averageReview) {
        this.averageReview = averageReview;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
}
