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
        return userReviewRepository.findByEmail(email);  // 이메일 조회
    }
    public List<UserReview> findEmailIsNullReviews() {
        return userReviewRepository.findByEmailIsNull(); //null인 이메일 조회
    }

    public UserReview addReview(String email, String pros, String cons, String feedback) {
        UserReview userReview = new UserReview(email, pros, cons, feedback);
        return userReviewRepository.save(userReview);  // 리뷰 저장
    }
}
