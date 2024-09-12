package Shingu.Interviewer.controller;

import Shingu.Interviewer.servic.UserInfoService;
import Shingu.Interviewer.tool.JobCompletionEncode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@WebServlet(name = "Login", value = "/login")
public class UserInfoLoginController extends HttpServlet {
    private final UserInfoService userInfoService;

    public UserInfoLoginController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        String hashPassword = JobCompletionEncode.encode(email, password);

        req.getSession().setAttribute("loginEmail", null);
        req.getSession().setAttribute("notFoundEmail", null);
        req.getSession().setAttribute("wrongPassword", null);

        if (!userInfoService.isEmailExists(email)) {
            req.getSession().setAttribute("notFoundEmail", true);
            resp.sendRedirect("/basic/login.jsp");
            return;
        }

        if (!userInfoService.isAccountExists(email, hashPassword)) {
            req.getSession().setAttribute("wrongPassword", true);
            resp.sendRedirect("/basic/login.jsp");
            return;
        }

        req.getSession().setAttribute("loginEmail", email);
        resp.sendRedirect("/basic/index.jsp");
    }
}