<%--
  Created by IntelliJ IDEA.
  User: 5talk
  Date: 2024-04-30
  Time: 오후 2:46
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
<body class="is-preload" style="background-color: black">

<!-- Header -->
<header id="header">
    <a class="logo" href="index.jsp">취업완</a>
    <%
    String loginEmail, keyword;
    String[] questions, answers;
    int index;

    try {
        loginEmail = (String) session.getAttribute("loginEmail");

        questions = (String[]) session.getAttribute("questions");
        answers = (String[]) session.getAttribute("answers");
        keyword = (String) session.getAttribute("keyword");
        index = (int) session.getAttribute("index");
    } catch (Exception e) {
        String temp = session.getAttribute("loginEmail") == null ? null : (String) session.getAttribute("loginEmail");
        session.invalidate();
        session = request.getSession();
        if (temp != null) session.setAttribute("loginEmail", temp);
        response.sendRedirect("/basic/index.jsp");
        return;
    }

    String action = request.getParameter("action");
    String inputAnswer = request.getParameter("inputAnswer");

    if ("increase".equals(action)) {
        answers[index] = inputAnswer;
        index++;

        session.setAttribute("index", index);
        session.setAttribute("answers", answers);
    }

    if (loginEmail == null) {
        String showKeyword = keyword.length() > 10 ? keyword.substring(0, 10) + "..." : keyword;
    %>
    <p style="color: white;">키워드 : <%=showKeyword%></p>
    <% } else { %>
        <a class="logo" href="/show/history"><%=loginEmail%> 님 환영합니다.</a>
    <% } %>
</header>

<div style="width: 80%; margin: auto; background-color: #171c24; box-shadow: 0px 0px 10px 0px rgba(0,0,0,0.15); border-radius: 10px; overflow: hidden;">
    <div style="display: flex; flex-direction: column; height: 80%;">
        <div style="padding: 16px; border-bottom: 1px solid #ccc;">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <h2 style="font-size: 1.125rem; font-weight: 600; color: white;">
                    모의 면접
                </h2>
            </div>
        </div>
        <div style="flex: 1; height: 80%; padding: 16px; overflow-y: auto; display: flex; flex-direction: column; gap: 8px;" id="chatDisplay">
            <%-- 로그인 한 사용자 아니면 질문 보기 --%>
        <%
        if (loginEmail == null) {
            for (String question : questions) { %>
                <div style="align-self: flex-start; background-color: #717171; color: white; max-width: 20rem; border-radius: 10px; padding: 12px 16px; font-size: 0.875rem;">
                    <%=question%>
                </div>
        <%
            }
        %>
            </div>
            <div style="padding: 16px; border-top: 1px solid #ccc;">
                <form method="POST" style="display: flex; gap: 8px;" action="/basic/index.jsp">
                    <input placeholder="비로그인 유저는 문제만 확인 가능합니다"
                           style="flex: 1; padding: 8px; border: 1px solid #ccc; border-radius: 10px; font-size: 0.875rem;"
                           type="text"
                           name="inputAnswer"
                           readonly/>
                    <input type="submit" value="메인페이지로 이동" class="primary"/>
                </form>
            </div>
        <%
        } else {
            for (int i = 0; i < questions.length; i++) {
        %>
                <div style="align-self: flex-start; background-color: #717171; color: white; max-width: 20rem; border-radius: 10px; padding: 12px 16px; font-size: 0.875rem;">
                    <%=questions[i]%>
                </div>
        <%
                if (i == index) break;
        %>
                <div style="align-self: flex-end; background-color: #007bff; color: white; max-width: 20rem; border-radius: 10px; padding: 12px 16px; font-size: 0.875rem;">
                    <%=answers[i]%>
                </div>
        <%
            }
        %>
            </div>
            <div style="padding: 16px; border-top: 1px solid #ccc;">
            <%
            if (index != answers.length && answers[index] == null) {
            %>
                <form method="POST" style="display: flex; gap: 8px;">
                    <input type="hidden" name="action" value="increase">
                    <input placeholder="메시지를 입력하세요..."
                           style="flex: 1; padding: 8px; border: 1px solid #ccc; border-radius: 10px; font-size: 0.875rem; color: white;"
                           id="chatInput"
                           type="text"
                           name="inputAnswer"/>
                    <input type="submit" value="전송" class="primary" id="submitBtn"/>
                </form>
            <%
            } else {
            %>
<%--                /save/questions 대신에 테스트로 이거 넣음 GPTFeedBack--%>
                <form method="POST" style="display: flex; gap: 8px;" action="/GPTFeedBack">
                    <input placeholder="답변하느라 고생하셨습니다. 저장 클릭시 피드백을 보여드리며, 데이터를 저장합니다"
                       style="flex: 1; padding: 8px; border: 1px solid #ccc; border-radius: 10px; font-size: 0.875rem;"
                       type="text"
                       name="inputAnswer"
                       readonly/>
                    <input type="submit" value="저장" class="primary" id="submitBtnSave"/>
                </form>
            <%
                }
            }
            %>
        </div>
    </div>
</div>



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
<%--<script src="https://cdn.tailwindcss.com"></script>--%>
<script>
    document.querySelectorAll('form').forEach(function(form) {
        form.addEventListener('submit', function() {
            var submitButton = form.querySelector('input[type="submit"]');
            if (submitButton) {
                submitButton.disabled = true;
            }
        });
    });

    var div = document.getElementById('chatDisplay');
    div.scrollTop = div.scrollHeight;

    window.addEventListener('DOMContentLoaded', (event) => {
        var chatInput = document.getElementById('chatInput');
        if (chatInput) {
            chatInput.focus();
        }
    });
</script>

</body>
</html>