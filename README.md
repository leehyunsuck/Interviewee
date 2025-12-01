# Interviewee

> **AI를 활용한 이력서 기반 모의 면접 서비스**

**Interviewee**는 사용자의 이력서를 분석하여 맞춤형 면접 질문을 생성하고, 실전과 같은 모의 면접 환경을 제공하는 웹 서비스입니다. OpenAI의 기술을 활용하여 깊이 있는 꼬리물기 질문과 피드백을 제공함으로써 구직자가 면접 역량을 강화할 수 있도록 돕습니다.

<br>

## Tech Stack (기술 스택)

### Backend
<img src="https://img.shields.io/badge/Java 21-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/JPA (Hibernate)-59666C?style=for-the-badge&logo=hibernate&logoColor=white">

### Database
<img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white">

### AI & API
<img src="https://img.shields.io/badge/OpenAI API-412991?style=for-the-badge&logo=openai&logoColor=white"> <img src="https://img.shields.io/badge/SMTP-D14836?style=for-the-badge&logo=gmail&logoColor=white">

### Infrastructure & Security
<img src="https://img.shields.io/badge/GCP-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white"> <img src="https://img.shields.io/badge/CertBot (SSL)-003A70?style=for-the-badge&logo=letsencrypt&logoColor=white"> <img src="https://img.shields.io/badge/SHA--256-4EAA25?style=for-the-badge&logo=security&logoColor=white">

### Tools & IDE
<img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white"> <img src="https://img.shields.io/badge/HeidiSQL-1572B6?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/MobaXterm-2C2C2C?style=for-the-badge&logo=linux&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white">

<br>

## Key Features (주요 기능)

* **이력서 기반 질문 생성**: OpenAI API를 연동하여 사용자가 등록한 이력서 내용을 분석, 직무 연관성이 높은 예상 질문을 자동으로 생성합니다.
* **실전 모의 면접**: 실제 면접장과 유사한 환경(타이머 등)에서 질문에 답변하는 연습을 할 수 있습니다.
* **보안 및 인증**:
    * **SHA-256** 암호화를 적용하여 사용자 비밀번호를 안전하게 관리합니다.
    * **SMTP**를 이용한 이메일 인증 시스템을 구축하여 계정 보안을 강화했습니다.
    * **SSL (CertBot)** 적용으로 안전한 데이터 전송(HTTPS)을 보장합니다.
* **클라우드 배포**: Google Cloud Platform(GCP) 상에 배포되어 언제 어디서든 접근 가능합니다.

<br>

## Contribution

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request
