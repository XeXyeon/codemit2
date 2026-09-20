# Todo REST API

Spring Boot와 Spring Data JPA로 만든 할 일(ToDo) REST API 서버입니다.
회원 가입과 로그인 없이 할 일을 생성, 조회, 수정, 삭제할 수 있습니다.

## 실행 방법

### 필요 환경

- JDK 25
- Gradle Wrapper

### 실행 명령

```bash
./gradlew bootRun
```

서버는 `8082` 포트에서 실행됩니다.

```text
http://localhost:8082
```

헬스 체크:

```bash
curl http://localhost:8082/health
```

응답:

```text
OK
```

## DB 선택

DB는 H2 인메모리 데이터베이스를 사용했습니다.
별도의 DB 설치 없이 저장소를 받은 사람이 바로 서버를 실행할 수 있게 하기 위해 선택했습니다.

## API 명세

### 할 일 생성

```http
POST /todos
```

요청 본문:

```json
{
  "title": "Spring Boot 공부"
}
```

성공 응답: `201 Created`

```json
{
  "id": 1,
  "title": "Spring Boot 공부",
  "completed": false,
  "createdAt": "2026-09-20T22:00:00",
  "updatedAt": "2026-09-20T22:00:00"
}
```

### 할 일 목록 조회

```http
GET /todos
```

성공 응답: `200 OK`

```json
[
  {
    "id": 1,
    "title": "Spring Boot 공부",
    "completed": false,
    "createdAt": "2026-09-20T22:00:00",
    "updatedAt": "2026-09-20T22:00:00"
  }
]
```

### 할 일 단건 조회

```http
GET /todos/{id}
```

성공 응답: `200 OK`

```json
{
  "id": 1,
  "title": "Spring Boot 공부",
  "completed": false,
  "createdAt": "2026-09-20T22:00:00",
  "updatedAt": "2026-09-20T22:00:00"
}
```

### 할 일 수정

```http
PATCH /todos/{id}
```

요청 본문:

```json
{
  "title": "Spring Boot 복습",
  "completed": true
}
```

`title` 또는 `completed` 중 일부만 보낼 수 있습니다.

성공 응답: `200 OK`

```json
{
  "id": 1,
  "title": "Spring Boot 복습",
  "completed": true,
  "createdAt": "2026-09-20T22:00:00",
  "updatedAt": "2026-09-20T22:05:00"
}
```

### 할 일 삭제

```http
DELETE /todos/{id}
```

성공 응답: `204 No Content`

응답 본문은 없습니다.

## 오류 응답

모든 오류 응답은 같은 형태를 사용합니다.

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "제목을 작성해주세요."
}
```

### 입력값 검증 실패

상태 코드: `400 Bad Request`

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "제목을 작성해주세요."
}
```

### 존재하지 않는 할 일

상태 코드: `404 Not Found`

```json
{
  "status": 404,
  "code": "TODO_NOT_FOUND",
  "message": "할 일을 찾을 수 없습니다. id=999"
}
```

## 입력 검증 규칙

- 생성 시 제목은 필수입니다.
- 제목은 비어 있거나 공백뿐일 수 없습니다.
- 제목은 최대 30자까지 허용합니다.
- 수정 시 제목을 보내지 않는 것은 허용합니다.
- 수정 시 제목을 보냈다면 공백뿐인 값은 허용하지 않습니다.

## 설계 설명

### URL과 HTTP 메서드

할 일은 하나의 리소스이므로 기본 경로를 `/todos`로 정했습니다.

- `POST /todos`: 새 할 일 생성
- `GET /todos`: 할 일 목록 조회
- `GET /todos/{id}`: 특정 할 일 조회
- `PATCH /todos/{id}`: 특정 할 일 일부 수정
- `DELETE /todos/{id}`: 특정 할 일 삭제

수정에는 `PATCH`를 사용했습니다.
제목과 완료 여부 중 필요한 값만 일부 수정할 수 있기 때문입니다.

### 상태 코드

- 생성 성공은 새 리소스가 만들어졌으므로 `201 Created`를 사용했습니다.
- 조회와 수정 성공은 응답 본문에 결과를 반환하므로 `200 OK`를 사용했습니다.
- 삭제 성공은 반환할 본문이 없으므로 `204 No Content`를 사용했습니다.
- 입력값이 잘못된 경우는 클라이언트 요청 문제이므로 `400 Bad Request`를 사용했습니다.
- 존재하지 않는 id를 조회, 수정, 삭제하려는 경우는 리소스를 찾을 수 없으므로 `404 Not Found`를 사용했습니다.

### 계층 구조와 DTO

Controller, Service, Repository로 역할을 나눴습니다.

- Controller: HTTP 요청과 응답 처리
- Service: 할 일 생성, 조회, 수정, 삭제 로직 처리
- Repository: Spring Data JPA를 통한 DB 접근

요청과 응답에는 JPA 엔티티를 직접 사용하지 않고 DTO를 사용했습니다.
이를 통해 DB 구조와 API 응답 구조가 강하게 묶이지 않도록 했습니다.

## 실행 결과 예시

### 1. 할 일 생성

요청:

```bash
curl -i -X POST http://localhost:8082/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Spring Boot 공부"}'
```

응답:

```http
HTTP/1.1 201 Created
```

```json
{
  "id": 1,
  "title": "Spring Boot 공부",
  "completed": false,
  "createdAt": "2026-09-20T22:00:00",
  "updatedAt": "2026-09-20T22:00:00"
}
```

### 2. 목록 조회

요청:

```bash
curl -i http://localhost:8082/todos
```

응답:

```http
HTTP/1.1 200 OK
```

```json
[
  {
    "id": 1,
    "title": "Spring Boot 공부",
    "completed": false,
    "createdAt": "2026-09-20T22:00:00",
    "updatedAt": "2026-09-20T22:00:00"
  }
]
```

### 3. 완료 처리

요청:

```bash
curl -i -X PATCH http://localhost:8082/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"completed":true}'
```

응답:

```http
HTTP/1.1 200 OK
```

```json
{
  "id": 1,
  "title": "Spring Boot 공부",
  "completed": true,
  "createdAt": "2026-09-20T22:00:00",
  "updatedAt": "2026-09-20T22:05:00"
}
```

### 4. 삭제

요청:

```bash
curl -i -X DELETE http://localhost:8082/todos/1
```

응답:

```http
HTTP/1.1 204 No Content
```

### 5. 400 오류

요청:

```bash
curl -i -X POST http://localhost:8082/todos \
  -H "Content-Type: application/json" \
  -d '{"title":""}'
```

응답:

```http
HTTP/1.1 400 Bad Request
```

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "제목을 작성해주세요."
}
```

### 6. 404 오류

요청:

```bash
curl -i http://localhost:8082/todos/999
```

응답:

```http
HTTP/1.1 404 Not Found
```

```json
{
  "status": 404,
  "code": "TODO_NOT_FOUND",
  "message": "할 일을 찾을 수 없습니다. id=999"
}
```
