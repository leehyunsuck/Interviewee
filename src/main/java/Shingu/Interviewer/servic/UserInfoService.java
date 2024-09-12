package Shingu.Interviewer.servic;

import Shingu.Interviewer.dto.UserInfoForm;
import Shingu.Interviewer.entity.UserInfo;
import Shingu.Interviewer.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(path = "/save/userInfo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> createQuestions(@ModelAttribute UserInfoForm form) {
        UserInfo userInfo = form.toEntity();

        userInfoRepository.save(userInfo);
        return ResponseEntity.ok().body("Questions saved successfully");
    }
}