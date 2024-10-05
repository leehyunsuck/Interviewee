package Shingu.Interviewer.repository;

import Shingu.Interviewer.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> { // CrudRepository -> JpaRepository
    UserInfo findByEmail(String email);
    UserInfo findByEmailAndPassword(String email, String password);
}
