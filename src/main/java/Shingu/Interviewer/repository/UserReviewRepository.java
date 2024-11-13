package Shingu.Interviewer.repository;

import Shingu.Interviewer.entity.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReviewRepository extends JpaRepository<UserReview, Integer> {
    UserReview findByEmail(String email);
}
