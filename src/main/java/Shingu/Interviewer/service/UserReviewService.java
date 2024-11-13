package Shingu.Interviewer.service;

import Shingu.Interviewer.entity.UserReview;
import Shingu.Interviewer.repository.UserReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserReviewService {

    private final UserReviewRepository userReviewRepository;

    @Autowired
    public UserReviewService(UserReviewRepository userReviewRepository) {
        this.userReviewRepository = userReviewRepository;
    }

    public UserReview findReviewByEmail(String email) {
        return userReviewRepository.findByEmail(email);  // email 조회
    }

    public List<UserReview> findEmailIsNullReviews() {
        return userReviewRepository.findByEmailIsNull(); // null인 이메일 조회
    }

    public UserReview addReview(String email, String pros, String cons, Integer review) {
        UserReview usedReview = userReviewRepository.findByEmail(email);

        if (usedReview != null) {      // 기존 email이 존재할 경우
            usedReview.setPros(pros);
            usedReview.setCons(cons);
            usedReview.setReview(review);
            return userReviewRepository.save(usedReview);
        } else {
            UserReview newReview = new UserReview(email, pros, cons, review);
            return userReviewRepository.save(newReview);
        }
    }
}
