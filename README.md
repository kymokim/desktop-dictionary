# Desktop Dictionary

`Desktop Dictionary`는 교내 IT 지원 서비스를 위한 백엔드 프로젝트입니다.

게시판, Q&A, 중고거래, 실시간 채팅, 주문 및 결제, 이메일 인증 기능을 하나의 Spring Boot 애플리케이션으로 구성했습니다.

## 소개

- 교내 커뮤니티 및 IT 지원 서비스 성격의 백엔드 프로젝트
- Java 21, Spring Boot 기반 서버
- REST API와 WebSocket 기반 실시간 채팅 기능 제공
- Redis, 결제, 메일, 이미지 업로드 연동 구조 포함

## 주요 기능

- 회원가입, 로그인, 이메일 인증, 사용자 정보 관리
- 일반 게시판 및 질문/답변 게시판 CRUD
- 중고거래 게시글 및 댓글 기능
- WebSocket/STOMP 기반 실시간 채팅방 생성 및 메시지 전송
- 주문 생성, 결제 검증, 주문 상태 관리
- Swagger 기반 API 문서 제공

## 기술 스택

- Backend: Java, Spring Boot
- Data: MariaDB, Redis
- Realtime: WebSocket, STOMP
- Tools: Gradle, Swagger

## 참고

- 민감정보와 운영용 비밀값은 저장소에 포함하지 않습니다.
- 결제, 메일, JWT, 이미지 업로드 관련 값은 환경변수 또는 로컬 설정으로 관리합니다.
- 채팅방과 메시지 이력은 Redis를 사용해 관리합니다.
