<%@ page import="Shingu.Interviewer.entity.Questions" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: 5talk
  Date: 2024-04-30
  Time: 오후 2:46
  To change this template use File | Settings | File Templates.
--%>

<%--
여기 오면 이메일, 인덱스페이지 에러 메시지 외에는 세션 값 날리기
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    // 한 페이지에 표시할 질문 수
    int questionsPerPage = 3;
    // 현재 페이지
    int currentPage = (request.getParameter("page") != null) ? Integer.parseInt(request.getParameter("page")) : 1;

    // 모든 질문 목록
    List<Questions> history = (List<Questions>) request.getAttribute("history");
%>

<html>
<head>
    <title>Shingu-Project</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
    <meta name="description" content="" />
    <meta name="keywords" content="" />
    <link rel="stylesheet" href="../../basic/assets/css/main.css" />
</head>
<body class="is-preload">

<!-- Header -->
<header id="header">
    <a class="logo" href="../../basic/index.jsp">취업완</a>
    <%
    String loginEmail = (String) session.getAttribute("loginEmail");
    String mainErrorMsg = (String) session.getAttribute("mainErrorMsg");
    String notQuestionsErrorMsg = (String) session.getAttribute("notQuestionsErrorMsg");
    session.invalidate();
    session = request.getSession();

    if (loginEmail == null) {
    %>
        <a class="logo" href="../../basic/login.jsp">로그인</a>
    <%
    } else {
        session.setAttribute("loginEmail", loginEmail);
    %>
        <a class="logo" href="/logout">로그아웃</a>
    <%
    }
    %>
</header>

<section class="wrapper">
    <div class="inner">
        <header class="special">
            <h2>History</h2>
        </header>

        <% if (currentPage > 1) { %>
        <a href="?page=<%= currentPage - 1 %>" style="
    display: inline-block;
    padding: 10px 20px;
    margin: 5px;
    font-size: 14px;
    color: white;
    background-color: #007bff;
    border: none;
    border-radius: 5px;
    text-decoration: none;
    cursor: pointer;
    transition: background-color 0.3s;
">이전</a>
        <% } %>
        <% if (history.size() > currentPage * questionsPerPage) { %>
        <a href="?page=<%= currentPage + 1 %>" style="
    display: inline-block;
    padding: 10px 20px;
    margin: 5px;
    font-size: 14px;
    color: white;
    background-color: #007bff;
    border: none;
    border-radius: 5px;
    text-decoration: none;
    cursor: pointer;
    transition: background-color 0.3s;
">다음</a>
        <% } %>

        <div class="highlights">
            <!-- 각 질문에 대한 섹션을 반복하여 출력 -->
            <c:forEach var="question" begin="<%= (currentPage - 1) * questionsPerPage %>"
                       end="<%= Math.min(currentPage * questionsPerPage - 1, history.size() - 1) %>" items="${history}">
                <section>
                    <div class="content">
                        <header>
                            <h3>${question.keyword}</h3> <!-- 키워드 출력 -->
                        </header>
                        <!-- 각 질문, 답변, 피드백을 순차적으로 출력 -->
                        <c:if test="${not empty question.getQuestion1()}">
                            <p style="font-weight: bold">${question.getQuestion1()}</p>
                            <p>${question.getAnswers1()}</p>
                            <p>${question.getFeedback1()}</p>
                            <hr>
                        </c:if>

                        <c:if test="${not empty question.getQuestion2()}">
                            <p style="font-weight: bold">${question.getQuestion2()}</p>
                            <p>${question.getAnswers2()}</p>
                            <p>${question.getFeedback2()}</p>
                            <hr>
                        </c:if>

                        <c:if test="${not empty question.getQuestion3()}">
                            <p style="font-weight: bold">${question.getQuestion3()}</p>
                            <p>${question.getAnswers3()}</p>
                            <p>${question.getFeedback3()}</p>
                            <hr>
                        </c:if>

                        <c:if test="${not empty question.getQuestion4()}">
                            <p style="font-weight: bold">${question.getQuestion4()}</p>
                            <p>${question.getAnswers4()}</p>
                            <p>${question.getFeedback4()}</p>
                            <hr>
                        </c:if>

                        <c:if test="${not empty question.getQuestion5()}">
                            <p style="font-weight: bold">${question.getQuestion5()}</p>
                            <p>${question.getAnswers5()}</p>
                            <p>${question.getFeedback5()}</p>
                            <hr>
                        </c:if>

                        <c:if test="${not empty question.getQuestion6()}">
                            <p style="font-weight: bold">${question.getQuestion6()}</p>
                            <p>${question.getAnswers6()}</p>
                            <p>${question.getFeedback6()}</p>
                            <hr>
                        </c:if>

                        <c:if test="${not empty question.getQuestion7()}">
                            <p style="font-weight: bold">${question.getQuestion7()}</p>
                            <p>${question.getAnswers7()}</p>
                            <p>${question.getFeedback7()}</p>
                            <hr>
                        </c:if>

                        <c:if test="${not empty question.getQuestion8()}">
                            <p style="font-weight: bold">${question.getQuestion8()}</p>
                            <p>${question.getAnswers8()}</p>
                            <p>${question.getFeedback8()}</p>
                            <hr>
                        </c:if>

                        <c:if test="${not empty question.getQuestion9()}">
                            <p style="font-weight: bold">${question.getQuestion9()}</p>
                            <p>${question.getAnswers9()}</p>
                            <p>${question.getFeedback9()}</p>
                            <hr>
                        </c:if>

                        <c:if test="${not empty question.getQuestion10()}">
                            <p style="font-weight: bold">${question.getQuestion10()}</p>
                            <p>${question.getAnswers10()}</p>
                            <p>${question.getFeedback10()}</p>
                            <hr>
                        </c:if>

                    </div>
                </section>
            </c:forEach>
        </div>
        <!-- 페이지 네비게이션 버튼 -->
        <% if (currentPage > 1) { %>
        <a href="?page=<%= currentPage - 1 %>" style="
    display: inline-block;
    padding: 10px 20px;
    margin: 5px;
    font-size: 14px;
    color: white;
    background-color: #007bff;
    border: none;
    border-radius: 5px;
    text-decoration: none;
    cursor: pointer;
    transition: background-color 0.3s;
">이전</a>
        <% } %>
        <% if (history.size() > currentPage * questionsPerPage) { %>
        <a href="?page=<%= currentPage + 1 %>" style="
    display: inline-block;
    padding: 10px 20px;
    margin: 5px;
    font-size: 14px;
    color: white;
    background-color: #007bff;
    border: none;
    border-radius: 5px;
    text-decoration: none;
    cursor: pointer;
    transition: background-color 0.3s;
">다음</a>
        <% } %>
    </div>
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
<script src="../../basic/assets/js/jquery.min.js"></script>
<script src="../../basic/assets/js/browser.min.js"></script>
<script src="../../basic/assets/js/breakpoints.min.js"></script>
<script src="../../basic/assets/js/util.js"></script>
<script src="../../basic/assets/js/main.js"></script>
<script>
    document.querySelector('form').addEventListener('submit', function() {
        document.getElementById('submitBtn').disabled = true;
    });
</script>

</body>
</html>