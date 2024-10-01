package Shingu.Interviewer.repository;

import Shingu.Interviewer.entity.UserInfo;
import org.springframework.data.repository.CrudRepository;

public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
    UserInfo findByEmail(String email);
    UserInfo findByEmailAndPassword(String email, String password);
}
