# IOT_Socket
Sensing Data Control MiddleWare

## 소켓 시스템 정의
센서에서 보내주는 소켓 전문(16진수) 데이터값을 받아 정상적인 값인지 식별한다.
정상적인 코드값일 경우 해당 코드에 맞춰 10진수의 값으로 변환
10진수로 사람이 식별할 수 있도록 연산처리함.
지정한 테이블로 데이터가 쌓일 수있도록 처리
정상코드 외 데이터 진입시 익셉션처리하여 서비스 종료되지 않도록
센서에서 데이터를 받기전 수동으로 데이터를 가공하여 던져볼수 있는(테스트목적) 방법



```
//1. 리눅스에서 Curl 전송 
curl -i  '{"imei_cd":"456754675467","seq":"1","center_nm":"aaaa","center_cd":"T1","name":"bbb","phone":"12312312"}' -H "Content-Type: application/json" -X POST http://localhost:8123/postfire

//2. 윈도우 환경에서 cmd에서 CURL로 데이터 전송시 
curl -i  -H "Content-Type: application/json" -d "{\"imei_cd\":\"test\",\"seq\":\"1\",\"center_nm\":\"test\",\"center_cd\":\"S2\",\"name\":\"test\",\"phone\":\"435643564356\"}" -X POST http://localhost:9090/postsms
```
프로그램 내부에서 처리하기 힘든 부분을 DB 프로시저로 대신 처리하는 방안

센싱된 데이터를 수집하여 insert 시킬때 해당 테이블에 트리거를 설정하여 다음 동작을 취할 수 있도록 프로세스화 시키는 방법

```
--트리거 및 펑션 샘플 예시
1. 테이블 생성
create table t1(c1 integer, c2 date);
create table t2 (like t1); -- t1과 같은 구조를 t2테이블을 생성
create table t3 (like t2); -- t2와 같은 구조를 t3테이블을 생성

2. 트리거 생성 (t1테이블의 insert되는 데이터를 t2테이블에 insert하는 트리거)

CREATE TRIGGER t1_trg
after insert on t1
--referencing new table as new_table
FOR EACH ROW
EXECUTE PROCEDURE insert_t1_tr(); -- t1에 insert되는 데이터가 있으면 Row마다 insert_t1_tr이라고 하는 트리거 프로시저를 실행한다.

3. 함수 생성 (t2테이블에 insert된 데이터 중 키 값에 부합하는 t2테이블의 데이터를 t3에 insert하는 함수 생성)
CREATE OR REPLACE FUNCTION insert_t1_tr() RETURNS TRIGGER 
AS $$
DECLARE
 v_c1 int4 DEFAULT 0;
 v_c2 date;

BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO t2 VALUES(NEW.*); -- T1테이블에 Insert된 변경 데이터를 T2테이블로 Insert

SELECT c1, c2 INTO v_c1, v_c2
FROM t2
WHERE c1 = NEW.c1; -- T2테이블에 Insert된 데이터를 NEW라고 하는 객체에 저장하고 있으며, 그 값 중 C1컬럼에 해당하는 값을 조건으로 T2테이블을 조회하여 v_c1, v_c2변수에 저장

INSERT INTO t3 (c1, c2)
VALUES (v_c1, v_c2);

END IF; -- 위에서 저장된 v_c1, v_c2변수값을 t3테이블에 저장
RETURN NULL;

END

$$ LANGUAGE plpgsql;
### DB안 테이블에 대해 select할 수 있는 계정 생성
--user생성
create user iot_select password 'test_user';

--search_path 변경
alter user test_user search_path to '퍼블릭명',"$user",public;

--스키마에 대한 권한 부여
grant usage on schema iot_dev to test_user;

--해당 유저(test_public2)로 접속 한 후에 test_user user에게 권한 부여
grant select on all tables in schema "test_public2" to "test_user";

--해당 권한 부여시점 이후에 테이블에 적용될 수 있도록 권한 부여
ALTER DEFAULT PRIVILEGES IN SCHEMA "test_public2" GRANT select on tables TO "test_user"
```

위의 예시는 Table 및 View에 대한 권한이며, 
Function 및 Sequence, Tablespace에 대한 권한은 따로 부여해야됨.

※참고URL:  https://www.postgresql.org/docs/10/sql-createrole.html



센서 개별의 USIM이 내장되어있어 고유번호로 데이터 값을 전달함.

하지만 통신사중계기에서 발신되는 IP값은 고정이 아니기 때문에 서버측에서 센서별로 IP를 지정할 수 없었음. (보안취약)

이러한 센서에서 발신된 정보중 IMEI <<핸드폰에서 사용되는 단말의 고유 번호라보면됨.
미들웨어 단에서 IMEI 값을 체크하여 서버의 불필요한 접근은 차단해야함.
