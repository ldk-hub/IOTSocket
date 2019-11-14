package kr.socket.Controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

//여기가 프로세스의 시작점
@SpringBootApplication
//@EnableScheduling

public class MiddleWareApplication {

	static final Logger logger = LoggerFactory.getLogger(MiddleWareApplication.class);
	int number = 0;
	//지정해준 포트로 유입된 데이터를 가공처리함 포트지정 위치
	static int port1  = 9080;
	static int port2  = 9081;

//	@Autowired
//	private FireSensorOccurMapper fireSensorOccurMapper;
//	private FireOccurRepository fireOccurRepository;	

	public static void main(String[] args) {
		
		//logger.info("start MiddlepostgreApplication ");
		try {
			//각포트의 정보를 전달
			new UDPServer(port1,"fire").start();
			new UDPServer(port2,"temp").start();
			//정해진 포트외 접근시 발생되는 익셉션 처리
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
		
		SpringApplication.run(MiddleWareApplication.class, args);
	}

//	@Scheduled(fixedRate = 10000)
//	public void functiondemo() {
//	
//       
//	}

}
