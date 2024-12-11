package Shingu.Interviewer.service;

import Shingu.Interviewer.entity.ReviewStatistics;
import Shingu.Interviewer.entity.UserReview;
import Shingu.Interviewer.repository.ReviewStatisticsRepository;
import Shingu.Interviewer.repository.UserReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewStatisticsService {

    private final UserReviewRepository userReviewRepository;
    private final ReviewStatisticsRepository reviewStatisticsRepository;

    @Autowired
    public ReviewStatisticsService(UserReviewRepository userReviewRepository, ReviewStatisticsRepository reviewStatisticsRepository) {
        this.userReviewRepository = userReviewRepository;
        this.reviewStatisticsRepository = reviewStatisticsRepository;
    }

    @Transactional
    public void calcReview() {
        List<UserReview> reviews = userReviewRepository.findAll();

        double average = reviews.stream()
                .filter(r -> r.getReview() != null)
                .mapToInt(UserReview::getReview)
                .average()
                .orElse(0.0);

        long reviewCount = reviews.stream()
                .filter(r -> r.getReview() != null)
                .count();

        // 기존 값가 있으면 업데이트 없으면 새로 저장
        List<ReviewStatistics> statisticsList = reviewStatisticsRepository.findAll();
        if (statisticsList.isEmpty()) {  // 새로 저장
            ReviewStatistics newStatistics = new ReviewStatistics(average, (int) reviewCount);
            reviewStatisticsRepository.save(newStatistics);
        } else {  // 업데이트
            ReviewStatistics statistics = statisticsList.get(0);
            statistics.setAverageReview(average);
            statistics.setReviewCount((int) reviewCount);
            reviewStatisticsRepository.save(statistics);
        }
    }
}
