package Shingu.Interviewer.repository;

import Shingu.Interviewer.entity.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserReviewRepository extends JpaRepository<UserReview, Integer> {
    UserReview findByEmail(String email);

    List<UserReview> findByEmailIsNull();
}
