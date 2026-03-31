# Desktop Dictionary

`desktop-dictionary`는 교내 IT 지원 서비스를 위한 백엔드 서버 프로젝트입니다.

질문/답변 게시판, 일반 게시판, 중고거래 게시판, 댓글, 채팅, 주문 및 결제, 사용자 인증 기능을 포함하고 있습니다.

## 소개

- 교내 IT 지원 서비스 백엔드 프로젝트
- Java 21, Spring Boot 기반 서버
- 게시판, Q&A, 중고거래, 채팅, 인증, 주문 도메인 중심 구조
- REST API와 WebSocket 기반 실시간 채팅 기능 포함

## 주요 기능

- 회원가입, 로그인, 이메일 인증, 사용자 정보 관리
- 질문/답변 게시판 및 일반 게시판 CRUD
- 중고거래 게시글 및 댓글 기능
- 실시간 채팅방 생성 및 메시지 전송
- 주문 생성, 결제 검증, 주문 상태 관리
- Swagger 기반 API 문서화

## 기술 스택

- Backend: Java, Spring Boot, Spring Security
- Database: H2, MariaDB, Redis
- Communication: REST API, WebSocket, STOMP
- Tools: Gradle, Swagger

## 참고

- 민감한 설정값과 운영 시크릿은 저장소에 포함하지 않았습니다.
- 결제, 메일, 이미지 업로드 등 외부 연동 값은 환경변수 또는 별도 설정으로 관리합니다.
