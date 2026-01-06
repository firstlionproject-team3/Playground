## 왜 `t = e;`로 시작해서 `t = t.getCause();`로 내려가나?

### 1. 핵심 결론
Spring/JPA 환경에서 **DB 예외는 여러 계층으로 감싸져(wrapping) 전달**된다.  
따라서 **겉 예외만 보면 원인을 알 수 없고**,  
`getCause()`를 따라 **안쪽(진짜 원인)까지 내려가며 검사해야 한다.**

---

### 2. 예외가 “겹겹이 포장되는” 이유

Spring은 DB, JPA, JDBC, 드라이버 등 **서로 다른 기술 계층의 예외를 통일된 형태로 감싸서** 던진다.

이를 **예외 래핑(Exception Wrapping)** 이라고 한다.

---

### 3. 현실적인 예외 체인 구조 예시

```text
DataIntegrityViolationException  ← Spring
└─ cause: ConstraintViolationException  ← Hibernate
   └─ cause: SQLIntegrityConstraintViolationException  ← JDBC
      └─ cause: MysqlDataTruncation / SQLStateException  ← DB Driver
```
```
private boolean isNicknameDuplicate(Throwable e) {
    return hasConstraint(e, UK_NICKNAME);
}

private boolean hasConstraint(Throwable e, String constraint) {
    Throwable t = e;
    while (t != null) {
        String msg = t.getMessage();
        if (msg != null && msg.contains(constraint)) {
            return true;
        }
        t = t.getCause();
    }
    return false;
}

```

=====================================================================

**ConstraintViolationException** 은
→ “DB의 어떤 규칙이 깨졌는지”를 정확히 아는 예외이고

**DataIntegrityViolationException** 은
→ “데이터 무결성에 문제가 생겼다”라고 Spring이 포괄적으로 감싸서 던지는 예외야.

둘은 경쟁 관계가 아니라, **포장 관계**야.

```
메시지 기반은 “개발환경에서는 잘 되는데 운영에서 제약명 못 찾는” 상황이 실제로 자주 나옵니다.

DB가 바뀌거나(H2 → MySQL)

드라이버 버전이 바뀌거나

메시지 로케일이 바뀌면
contains("uk_user_nickname")가 실패할 수 있습니다.
```

ConstraintViolationException 기반은 이런 문제를 크게 줄입니다.

### ConstraintViolationException을 직접 catch하지 않는다.

왜냐면:

- Hibernate에 종속되기 때문

- Spring의 추상화 계층을 깨기 때문

대신 이렇게 한다.

#### catch는 DataIntegrityViolationException으로 한다
(Spring이 보장하는 안정적인 인터페이스)

- 내부 원인을 살펴본다

- 원인 중에 ConstraintViolationException이 있는지 확인

- 있으면 거기서 제약 이름을 꺼내 쓴다

```
“DB 유니크 제약을 최종 판단 기준으로 삼았고,
Hibernate의 ConstraintViolationException에서 제공하는
constraintName을 1차로 사용합니다.

다만 환경 차이로 해당 정보가 없는 경우를 대비해
메시지 기반 파싱을 fallback으로 두어
운영 안정성을 확보했습니다.”
```