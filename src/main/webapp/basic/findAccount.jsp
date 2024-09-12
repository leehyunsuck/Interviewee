<%--
  Created by IntelliJ IDEA.
  User: 5talk
  Date: 2024-04-30
  Time: 오후 2:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>Shingu-Project</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
    <meta name="description" content="" />
    <meta name="keywords" content="" />
    <link rel="stylesheet" href="../basic/assets/css/main.css" />

</head>
<body class="is-preload">

<!-- Header -->
<header id="header">
    <a class="logo" href="index.jsp">취업완</a>
</header>

<!-- Banner -->
<section id="banner">
    <div class="inner">
        <h2>비밀번호 변경</h2>
        <%
        String loginEmail = (String) session.getAttribute("loginEmail");

        if (loginEmail != null) {
            response.sendRedirect("/basic/index.jsp");
            return;
        }

        String email = (String) request.getSession().getAttribute("email");
        Integer code = (Integer) request.getSession().getAttribute("code");
        boolean invalidEmail = request.getSession().getAttribute("invalidEmail") != null;
        boolean notFoundEmail = request.getSession().getAttribute("notFoundEmail") != null;
        boolean invalidCode = request.getSession().getAttribute("invalidCode") != null;
        boolean check = request.getSession().getAttribute("check") != null;
        boolean notEqualsPassword = request.getSession().getAttribute("notEqualsPassword") != null;
        boolean passwordLength = request.getSession().getAttribute("passwordLength") != null;
        %>

        <% if (check) { %>
        <form method="post" action="/updatePassword">
            <% if (notEqualsPassword) { %>
            <p style="color: red;">비밀번호가 동일하지 않습니다.</p>
            <% } %>
            <% if (passwordLength) { %>
            <p style="color: red;">비밀번호는 7자 이상, 30자 이하로 입력해주세요.</p>
            <% } else { %>
            <p style="color: cornflowerblue;">비밀번호는 7자 이상, 30자 이하로 입력해주세요.</p>
            <% } %>
            <div class="col-6 col-12-xsmall">
                <input type="email" name="email" value="<%=email%>" readonly/>
            </div>
            <div class="col-6 col-12-xsmall">
                <input type="password" name="password" value="" placeholder="변경할 패스워드" />
            </div>
            <div class="col-6 col-12-xsmall">
                <input type="password" name="checkPassword" value="" placeholder="변경할 패스워드 확인" />
            </div>
            <div class="col-12 col-12-xsmall">
                <input type="submit" value="updatePassword" class="primary" />
            </div>
        </form>

        <% } else if (code == null || notFoundEmail || invalidEmail) { %>
        <form method="post" action="/emailSendUpdatePassword">
            <% if (notFoundEmail) { %>
            <p style="color: red">등록되지 않은 이메일 입니다.</p>
            <% } %>
            <% if (invalidEmail) { %>
            <p style="color: red">옳바르지 않은 이메일 입니다.</p>
            <% } %>
            <div class="col-6 col-12-xsmall">
                <input type="text" name="email" value="" placeholder="이메일" />
            </div>
            <div class="col-12 col-12-xsmall">
                <input type="submit" value="sendCode" class="primary"  id="submitBtn"/>
            </div>
        </form>

        <% } else { %>
        <form method="post" action="/checkCodeUpdatePassword">
            <% if (invalidCode) { %>
            <p style="color: red;">유효하지 않은 인증번호 입니다.</p>
            <% } else { %>
            <p style="color:deepskyblue">이메일로 전송된 인증번호를 입력해주세요</p>
            <% } %>
            <div class="col-6 col-12-xsmall">
                <input type="email" name="email" value="<%=email%>" readonly/>
            </div>
            <div class="col-6 col-12-xsmall">
                <input type="text" name="inputCode" value="" placeholder="인증번호" />
            </div>
            <div class="col-12 col-12-xsmall">
                <input type="submit" value="checkCode" class="primary" />
            </div>
        </form>
        <% } %>


        <a href="login.jsp">로그인</a>
        <a href="register.jsp">회원가입</a>
    </div>
    <video autoplay loop muted playsinline src="../basic/images/banner.mp4"></video>
</section>

<!-- Footer -->
<footer id="footer">
    <div class="inner">
        <div class="copyright">
            <a>
                Industrious by TEMPLATED
                templated.co @templatedco
                Released for free under the Creative Commons Attribution 3.0 license (templated.co/license)
            </a>
        </div>
    </div>
</footer>

<!-- Scripts -->
<script src="../basic/assets/js/jquery.min.js"></script>
<script src="../basic/assets/js/browser.min.js"></script>
<script src="../basic/assets/js/breakpoints.min.js"></script>
<script src="../basic/assets/js/util.js"></script>
<script src="../basic/assets/js/main.js"></script>
<!-- 이메일로 인증코드 전송이 버튼 비활성화-->
<script>
    document.querySelector('form').addEventListener('submit', function() {
        document.getElementById('submitBtn').disabled = true;
    });
</script>

</body>
</html>
