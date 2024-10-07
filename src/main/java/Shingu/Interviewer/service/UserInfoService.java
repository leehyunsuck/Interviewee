package Shingu.Interviewer.service;

import Shingu.Interviewer.entity.UserInfo;
import Shingu.Interviewer.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoService {
    //DB에 있으면 true
    public boolean isEmailExists(String email) {
        return userInfoRepository.findByEmail(email) != null;
    }

    //DB에 있으면 true
    public boolean isAccountExists(String email, String password) {
        return userInfoRepository.findByEmailAndPassword(email, password) != null;
    }

    public UserInfo saveUser(UserInfo user) {
        return userInfoRepository.save(user);
    }

    public UserInfo findByEmail(String email) {
        return userInfoRepository.findByEmail(email);
    }

    @Autowired
    private UserInfoRepository userInfoRepository;
}