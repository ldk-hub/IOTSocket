# IOT_Socket
Sensing Data Control MiddleWare  
개발 컨셉 : HW의 센싱된 데이터를 특정 포트와 전문규칙에 의거하여 규칙에 합당 데이터만 받아 DB에 저장할 수 있도록 구성  

TMI - 개별의 USIM이 내장되어있어 고유번호로 데이터 값을 전달함. 하지만 통신사 중계기에서 발신되는 IP값은 고정이 아니기 때문에 서버측에서 센서별로 IP를 지정할 수 없었음. (보안취약)
실습 단계에서는 UDP센서로 구성해보았음. 이러한 센서에서 발신된 정보중 IMEI <<핸드폰에서 사용되는 단말의 고유 번호라보면됨. 미들웨어 단에서 IMEI 값을 체크하여 서버의 불필요한 접근은 차단해야함.


## 소켓 시스템 정의
 - 센서에서 보내주는 소켓 전문(16진수) 데이터값을 받아 정상적인 값인지 식별한다.  
 - 정상적인 코드값일 경우 해당 코드에 맞춰 10진수의 값으로 변환  
 - 10진수로 사람이 식별할 수 있도록 연산처리함.  
 - 지정한 테이블로 데이터가 쌓일 수있도록 처리  
 - 정상코드 외 데이터 진입시 익셉션처리하여 서비스 종료되지 않도록  
 - 센서에서 데이터를 받기전 수동으로 데이터를 가공하여 던져볼수 있는(테스트목적) 방법  


## curl 테스트코드
```
//실제 센싱 테스트 하기 전 crul로 가데이터를 임의적으로 발송하여 테스트 해볼 수 있다.
//1. 리눅스에서 Curl 전송 
curl -i  '{"imei_cd":"456754675467","seq":"1","center_nm":"aaaa","center_cd":"T1","name":"bbb","phone":"12312312"}' -H "Content-Type: application/json" -X POST http://localhost:8123/postfire

//2. 윈도우 환경에서 cmd에서 CURL로 데이터 전송시 
curl -i  -H "Content-Type: application/json" -d "{\"imei_cd\":\"test\",\"seq\":\"1\",\"center_nm\":\"test\",\"center_cd\":\"S2\",\"name\":\"test\",\"phone\":\"435643564356\"}" -X POST http://localhost:9090/postsms
```
