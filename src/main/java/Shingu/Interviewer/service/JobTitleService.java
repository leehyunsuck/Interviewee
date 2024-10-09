package Shingu.Interviewer.service;
import Shingu.Interviewer.entity.JobTitle;
import Shingu.Interviewer.repository.JobTitleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobTitleService {
    private final JobTitleRepository jobTitleRepository;
    public JobTitleService(JobTitleRepository jobTitleRepository) {
        this.jobTitleRepository = jobTitleRepository;
    }
    public List<JobTitle> getAllJobTitles() {
        return jobTitleRepository.findAll();
    }
    public List<JobTitle> getJobTitlesByCategoryId(Long categoryId) {
        return jobTitleRepository.findByCategoryId(categoryId);
    }
}