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
<body class="is-preload">

<!-- Header -->
<header id="header">
    <a class="logo" href="index.jsp">취업완</a>
    <%
    String loginEmail = session.getAttribute("loginEmail") == null ? null : (String) session.getAttribute("loginEmail");
    String mainErrorMsg = session.getAttribute("mainErrorMsg") == null ? null : (String) session.getAttribute("mainErrorMsg");
    String notQuestionsErrorMsg = session.getAttribute("notQuestionsErrorMsg") == null ? null : (String) session.getAttribute("notQuestionsErrorMsg");
    session.invalidate();
    session = request.getSession();

    if (loginEmail == null) {
    %>
        <a class="logo" href="login.jsp">로그인</a>
    <%
    } else {
        session.setAttribute("loginEmail", loginEmail);
    %>
        <a class="logo" href="/show/history"><%=loginEmail%> 님 환영합니다.</a>
    <%
    }
    %>
</header>

<!-- Banner -->
<section id="banner">
    <div class="inner">
        <%  if (loginEmail != null) { %>
        <a style="display: block; text-align: right;">우측 상단 환영합니다. 클릭시 면접 질문 기록 확인, 로그아웃 가능</a>
        <%  } %>
        <h2>취업완</h2>
        <form method="post" action="/GPTCall">
            <div class="col-3 col-12-xsmall">
                <a>면접 질문 개수</a>
                <select name="count">
                    <option value="3">3개</option>
                    <option value="5">5개</option>
                    <option value="7">7개</option>
                    <option value="10">10개</option>
                </select>
            </div>
            <div class="col-3 col-12-xsmall">
                <a>언어</a>
                <select name="lang">
                    <option value="Korean">한글</option>
                    <option value="English">English</option>
                </select>
            </div>
            <div class="col-3 col-12-xsmall">
                <a>난이도</a>
                <select name="diff">
                    <option value="false">normal</option>
                    <option value="true">hard</option>
                </select>
            </div>

            <div class="col-9 col-12-xsmall">
                <% String tempKeyword = mainErrorMsg == null ? "제공받을 기술 면접의 기술 입력란 (자바, 포토샵 등)" : mainErrorMsg;%>
                <input type="text" name="keyword" value="" placeholder="<%=tempKeyword%>" />
            </div>

            <div class="col-9 col-12-xsmall">
                <% String tempNotQuestions = notQuestionsErrorMsg == null ? "받지않을 면접 질문 입력란 (빈칸 가능)" : notQuestionsErrorMsg;%>
                <input type="text" name="notQuestion" value="" placeholder="<%=tempNotQuestions%>" />
            </div>

            <br>
            <div class="col-3 col-12-xsmall">
                <input type="submit" value="Send Message" class="primary" id="submitBtn"/>
            </div>
            <a>비로그인 유저는 하루 3번 / 로그인 유저 하루 6번 질문 가능</a>

        </form>
    </div>
    <video autoplay loop muted playsinline src="../basic/images/banner.mp4"></video>
</section>

<!-- Highlights -->
<section class="wrapper">
    <div class="inner">
        <header class="special">
            <h2>면접 질문 예시</h2>
            <!--						<p>content</p>-->
        </header>
        <div class="highlights">
            <section>
                <div class="content">
                    <header>
                        <!--									<a class="icon fa-vcard-o"></a>-->
                        <h3>자바 (Normal)</h3>
                    </header>
                    <p>자바에서 상속과 인터페이스의 차이점은 무엇인가요?</p>
                    <p>자바에서 'static' 키워드의 사용 용도는 무엇인가요?</p>
                    <p>어떤 프로젝트에서 가장 어려운 문제를 해결한 적이 있나요?</p>
                </div>
            </section>
            <section>
                <div class="content">
                    <header>
                        <!--									<a class="icon fa-files-o"></a>-->
                        <h3>유아교육 (Hard)</h3>
                    </header>
                    <p>유아교육에서 효과적인 학습 환경을 조성하기 위해 노력하는 방법은 무엇인가요?</p>
                    <p>유아의 역량 발달을 위해 가정에서 보완해야 할 부분은 무엇인가요?</p>
                    <p>유아교육자로서 해결하기 어려운 문제에 부딪혔을 때 어떻게 대처하시겠습니까?</p>
                </div>
            </section>
            <section>
                <div class="content">
                    <header>
                        <!--									<a class="icon fa-floppy-o"></a>-->
                        <h3>원자력공학과 (Normal)</h3>
                    </header>
                    <p>핵분열 반응의 원리에 대해 설명해주세요.</p>
                    <p>원자력발전소에서 안전을 위해 어떤 조치들이 필요한지 알려주세요.</p>
                    <p>원자력공학과 직업과 관련된 자질은 무엇이라고 생각하십니까?</p>
                </div>
            </section>
        </div>
    </div>
</section>

<!-- CTA -->
<section id="cta" class="wrapper">
    <div class="inner">
        <h2>취업완</h2>
        <p>해당 사이트를 통해 면접 준비 시간을 단축하여 <br>
            더 많은걸 준비할 수 있으면 좋겠습니다! <br>
            모두 그림에 있는 행복한 곰 처럼 취업에 성공하여 행복하길 기원합니다.
        </p>
    </div>
</section>

<!-- Testimonials -->
<section class="wrapper">
    <div class="inner">
        <header class="special">
            <h2>제작 동기</h2>
            <p>지금 당장 저에게 필요한게 무엇일까 고민하였습니다. <br>
                전 대학교 졸업예정자로 기술 면접을 준비해야하는 학생입니다. <br>
                추 후 이걸 활용해서 준비하면 편하지 않을까? 생각하여 만들게 되었습니다.
            </p>
        </header>
    </div>
</section>

<!-- Footer -->
<footer id="footer">
    <div class="inner">
        <div class="content">
            <section>
                <h3>제작자 (신구대학교)</h3>
                <p>이현석 (leehyunsuck01@gmail.com) </p>
            </section>
        </div>
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