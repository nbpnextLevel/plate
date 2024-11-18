## AI 검증 비즈니스 프로젝트 ##
Spring 심화과정의 첫 프로젝트로 주문 관리 플랫폼을 모놀리식 아키텍처로 개발하는 것을 목표로 한 서비스에 생성형 인공지능 서비스를 연동하여 구현하였습니다.

----
### 📖 프로젝트 소개(Plate) ###
![image](https://github.com/user-attachments/assets/e765c67d-001c-41e1-a349-a1485323e853)

**Plate는 음식 배달의 중심!**

즉, 접시처럼 모든 음식 주문과 관련된 활동을 하나의 플랫폼에서 관리할 수 있는 공간을 제공합니다.
- #### 프로젝트 목표 ####
  - 추후 확장성을 고려한 배달 및 포장 음식 주문 관리 서비스 개발
  - AI 기능을 통해 상품의 정보에 대한 제안 받기
  - 기획자의 요구사항을 확인하여 프론트엔드 개발자와 협업에 문제 없도록 API 문서 작성
  - 설계부터 배포까지 여러가지를 시도해보며 학습하기
- #### 프로젝트 상세 ####
  - CUSTOMER, OWNER, MANAGER, MASTER의 권한으로 관리하여 진행
  - 유저, 가게, 상품, 주문, 결제, 리뷰 도메인으로 구성
  - 이력 관리가 필요한 부분은 물리삭제가 아닌 논리삭제(is_deleted) 컬럼 사용
  - 모든 테이블에 Audit 필드 추가하여 데이터 감사로그 기록 
----
### 👩‍💻 팀원 역할 분담 ###
 - **성은정 :** 팀장, 결제 및 리뷰 담당
 - **안재희 :**
   - Spring Security&JWT 다중토큰&Redis 활용한 로그인 기능 구현
   - 공통응답객체 설계 및 적용
   - 유저, 가게 도메인 CRUD 담당
   - aws ec2 배포 
 - **임지은 :** 주문 담당
 - **한미수 :** 상품 및 AI 담당
   - 상품, 상품 이력, 상품 이미지, AI 요청 기록 검색 시 queryDsl 적용
   - 이미지 s3 업로드 기능 구현
----
### 🔧 개발환경 ###
- **Framework  :** Spring Boot 3.3.5
- **Database :** PostgreSQL
- **Build Tool :** Gradle
- **IDE :** IntelliJ
- **ORM :** JPA
- **VCS :** GitHub(Git Flow 전략)
----
### ⚙️ 기술스택 ###
- **Server :** AWS VPC, AWS EC2
- **Database :** AWS RDS, AWS S3, Redis
- **API documentation:** Swagger
----
### 📝 ERD ###
![image](https://github.com/user-attachments/assets/b185937c-f778-46b3-8c54-0b4abb0be860)

----
### 📚 프로젝트 아키텍처 ###
![image](https://github.com/user-attachments/assets/9ed1c358-0350-405b-a94f-c2c4a985158b)

----
### 🖥️ API ###
- **API 명세:** https://teamsparta.notion.site/API-29ccff47988d4ae1a02c643a32393543
- **Swagger:** http://43.203.245.199/swagger-ui/index.html
### 🖥️ 테스트시 주의사항 ###
- 로그인, 로그아웃, 토큰 재발급 API는 swagger 적용이 어려워 Postman으로 테스트 요청드립니다. 
- 상품 등록 API 및 상품 이미지 수정 및 관리 API는 파일 업로드, JSON 요청 본문, 복잡한 쿼리 파라미터 조합 등은
  Swagger나 Postman에서는 다소 제한적일 수 있어 **HTTP Client**를 사용해 수행 부탁드립니다.
- 테스트 파일 경로(/resoucres/http/createProductTest.http)
----
### ⚙️서비스 구성 및 실행 방법 ###
- **AWS VPC (Virtual Private Cloud)**
  - 전체 인프라가 보안 경계 내에서 실행되는 네트워크
- **EC2 인스턴스**
  - Spring 애플리케이션 서버가 배포되어 있는 컴퓨팅 자원
  - 사용자는 EC2 서버를 통해 애플리케이션에 접근
- **Spring 프레임워크**
  - 사용자 요청을 처리하고 데이터베이스 및 외부 API와의 상호작용을 담당
- **Amazon RDS (Relational Database Service)**
  - PostgreSQL 데이터베이스를 호스팅하여 애플리케이션의 데이터 저장을 담당
- **Redis**
  - JWT 토큰 기반 인증 시스템 보안 강화를 위해 활용
- **Amazon S3**
  - 사용자가 업로드한 이미지나 기타 파일들을 저장하여 사용
- **Google AI API**
  - 외부 AI 서비스와 통합되어 기능을 확장
  - 사용자는 Spring 애플리케이션을 통해 Google AI API와 상호작용 가능
----
### ✒️기술 Issue 해결 과정 ###
----
