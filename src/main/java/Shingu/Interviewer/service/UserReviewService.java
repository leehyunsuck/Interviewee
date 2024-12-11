package Shingu.Interviewer.service;

import Shingu.Interviewer.entity.UserReview;
import Shingu.Interviewer.repository.UserReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserReviewService {

    private final UserReviewRepository userReviewRepository;
    private final ReviewStatisticsService reviewStatisticsService;

    @Autowired
    public UserReviewService(UserReviewRepository userReviewRepository, ReviewStatisticsService reviewStatisticsService) {
        this.userReviewRepository = userReviewRepository;
        this.reviewStatisticsService = reviewStatisticsService;
    }

    public UserReview findReviewByEmail(String email) {
        return userReviewRepository.findByEmail(email);  // email 조회
    }

    public void addReview(String email, String pros, String cons, Integer review) {
        UserReview userReview = userReviewRepository.findByEmail(email);

        if (userReview.getEmail().equals(email)) {   // 기존 email이 존재할 경우
            userReview.setPros(pros);
            userReview.setCons(cons);
            userReview.setReview(review);
        } else {
            userReview = new UserReview(email, pros, cons, review);
        }
        userReviewRepository.save(userReview);
        reviewStatisticsService.calcReview();
    }
}
