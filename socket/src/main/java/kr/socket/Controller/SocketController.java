package kr.socket.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
//REST API 다른 시스템간의 데이터 전송처리로직 받는쪽임
@RestControllerAdvice
public class SocketController {
			
			
static final Logger logger = LoggerFactory.getLogger(SocketController.class);
	   
	   @PostMapping("/postdata")
	   public int postsms(@RequestBody ModelMap modelMap)throws Exception {
		return 0;
	    //sendServiceImpl.HistoryInsert(modelMap);
	   }

}
