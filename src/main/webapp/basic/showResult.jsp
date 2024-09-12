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
    String loginEmail = (String) session.getAttribute("loginEmail");

    String[] questions = (String[]) session.getAttribute("questions");
    String[] answers = (String[]) session.getAttribute("answers");
    String[] feedbacks = (String[]) session.getAttribute("feedbacks");

    if (loginEmail == null || questions == null) {
        response.sendRedirect("/basic/index.jsp");
        return;
    } else { %>
        <p style="color: white;"><%=loginEmail%>님 환영합니다.</p>
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

            <%
            for (int i = 0; i < questions.length; i++) {
            %>
                <div style="align-self: flex-start; background-color: #717171; color: white; max-width: 20rem; border-radius: 10px; padding: 12px 16px; font-size: 0.875rem;">
                    <%=questions[i]%>
                </div>
                <div style="align-self: flex-end; background-color: #007bff; color: white; max-width: 20rem; border-radius: 10px; padding: 12px 16px; font-size: 0.875rem;">
                    <%=answers[i]%>
                </div>
                <div style="align-self: flex-start; background-color: #f0e68c; color: black; max-width: 20rem; border-radius: 10px; padding: 12px 16px; font-size: 0.875rem;">
                    <%=feedbacks[i]%>
                </div>
                <br>
            <%
            }
            %>
        </div>
        <div style="padding: 16px; border-top: 1px solid #ccc;">
            <form method="POST" style="display: flex; gap: 8px;" action="/basic/index.jsp">
                <input placeholder="고생하셨습니다. 위 모의 기술면접이 도움이 되었으면 좋겠습니다."
                    style="flex: 1; padding: 8px; border: 1px solid #ccc; border-radius: 10px; font-size: 0.875rem;"
                    readonly
                    type="text"
                    />
                <input type="submit" value="메인으로" class="primary" id="submitBtn"/>
            </form>
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
<script>
    document.querySelector('form').addEventListener('submit', function() {
        document.getElementById('submitBtn').disabled = true;
    });

</script>

</body>
</html>