package kr.socket.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//테스트용 소스 IMEI데이터 생성해서 전달

@RestController
public class RestTestController {

	static final Logger logger = LoggerFactory.getLogger(RestTestController.class);

	@Autowired
	private MiddleService middleService;

	@RequestMapping("/testtemp")
	public String getTest() {
		logger.info("get /temp  ");

  //소켓통신 전달용 임의정의용 샘플 데이터
		String senddata = "3593690811103440";
		senddata += "22991234";
		senddata += "0001";
		senddata += "02";
		senddata += "1816";
		senddata += "0002";
		senddata += "04";
		senddata += "FF002291";
		senddata += "0003";
		senddata += "03";
		senddata += "001205";

//    	senddata += "0004";
//    	senddata += "01";
//    	senddata += "15";
//    	senddata += "0005";
//    	senddata += "15";
//    	senddata += "04105C340B0000000000B0040000000000001C7DD1";

		senddata += "FF";
		String senddata1 = "359369080981828025625810000002205400010400001855000203002983B3";
		middleService.inserttempSensor(senddata1);

		return "hahaha";
	}

	@RequestMapping("/testfireoccur")
	public String getTest1() {
		logger.info("get /testfireoccur  ");

		String senddata = "3593690811103440";
		senddata += "22991234";
		senddata += "0004";
		senddata += "01";
		senddata += "01";
		senddata += "FF";

		String senddata1 = "35936908099145402610760800040101EE";

		middleService.insertfireSensor(senddata1);

		return "hahaha";
	}

	@RequestMapping("/testfirerestore")
	public String getTest2() {
		logger.info("get /testfirerestore  ");

		String senddata = "3593690811103440";
		senddata += "22991234";
		senddata += "0004";
		senddata += "01";
		senddata += "00";
		senddata += "FF";

		middleService.insertfireSensor(senddata);

		return "hahaha";
	}
  
  //유효한 데이터 인지 체크썸하여 문제
	@RequestMapping("/testchecksumfire")
	public String testchecksumfire() {
		logger.info("get /testchecksumfire  ");

		byte[] buf2 = { (byte) 0x35, (byte) 0x93, (byte) 0x69, (byte) 0x08, (byte) 0x11, (byte) 0x07, (byte) 0x75,
				(byte) 0x30, (byte) 0x26, (byte) 0x10, (byte) 0x76, (byte) 0x71, (byte) 0x00, (byte) 0x04, (byte) 0x01,
				(byte) 0x01, (byte) 0x99 };

//		35 93 69 08 09 24 19 00 
//		26 10 76 65 00 04 01 00 6B
		byte[] buf1 = { (byte) 0x35, (byte) 0x93, (byte) 0x69, (byte) 0x08, (byte) 0x09, (byte) 0x24, (byte) 0x19,
				(byte) 0x00, (byte) 0x26, (byte) 0x10, (byte) 0x76, (byte) 0x65, (byte) 0x00, (byte) 0x04, (byte) 0x01,
				(byte) 0x00, (byte) 0x6B };

//		35 93 69 08 10 24 00 80
//		22 99 21 77 00 00 02 20 95 
//		00 01 04 00 00 24 80 00 02 
//		03 00 21 44 F6
		byte[] buf = { (byte) 0x35, (byte) 0x93, (byte) 0x69, (byte) 0x08, (byte) 0x10, (byte) 0x24, (byte) 0x00,
				(byte) 0x80, (byte) 0x22, (byte) 0x99, (byte) 0x21, (byte) 0x77, (byte) 0x00, (byte) 0x00, (byte) 0x02,
				(byte) 0x20, (byte) 0x95, (byte) 0x00, (byte) 0x01, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x24,
				(byte) 0x80, (byte) 0x00, (byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x21, (byte) 0x44,
				(byte) 0xf6 };

		int add = 0;
		for (int j = 0; j < buf.length - 1; j++) {
			add += buf[j] & 0xFF;
		}
		System.out.format("sum   %02X \n", add);
		add = add & 0xFF;
		System.out.format("last  %02X \n", add);

		String str = Integer.toBinaryString(add);
//		System.out.println(str + "    //  bytes");

		String expected = "11111111"; // expected result of a ^ b
		int xorInt = Integer.parseInt(expected, 2);
		byte aByte = (byte) add;
		byte xorByte = (byte) xorInt;
		byte xor = (byte) (0xff & ((int) aByte) ^ ((int) xorByte));

//         System.out.println(xor + "    // a ^ b as bytes");
		String str1 = Integer.toBinaryString(xor);
//		System.out.println(str1 + "    //  bytes");
		System.out.format("xor  %02X \n", xor);
		xor += 1;
		System.out.format("xor +1  %02X \n", xor);

		return "hahaha";
	}

	@RequestMapping("/testchecksumfire1")
	public String testchecksumfire1() {
		logger.info("get /testchecksumfire1  ");

//		35 93 69 08 09 24 19 00 
//		26 10 76 65 00 04 01 00 6B
		String buf2 = "359369080924190026107665000401006B";
		String buf = "35393336393038313035323535330D3032";
//					  35393336393038313035323535330D3032
//					  35393336393038313035323535330D3032
		
//		35 93 69 08 10 24 00 80
//		22 99 21 77 00 00 02 20 95 
//		00 01 04 00 00 24 80 00 02 
//		03 00 21 44 F6
		String buf1 = "359369081024008022992177000002209500010400002480000203002144F6";
		
		if(testchecksum(buf))
		{
			System.out.println("checksum same ");
		}
		else
		{
			System.out.println("checksum error ");
		}
		
//		if(testchecksum(buf1))
//		{
//			System.out.println("checksum same ");
//		}
//		else
//		{
//			System.out.println("checksum error ");
//		}
		return "hahaha";
	}
	
	public boolean testchecksum(String buf)
	{
		boolean ret = false;
		
		int len = buf.length();
	    byte[] data = new byte[len/2];
	    for (int i = 0; i < len; i += 2) {
	        data[i/2] = (byte) ((Character.digit(buf.charAt(i), 16) << 4)
	                             + Character.digit(buf.charAt(i+1), 16));
	    }
	    
	    int add = 0;
		for (int j = 0; j < data.length - 1; j++) {
			add += data[j] & 0xFF;
//			System.out.format("sum %02X add %02X \n", add,data[j] & 0xFF);
		}
		
		System.out.format("sum   %02X \n", add);
		add = add & 0xFF;
		System.out.format("last  %02X \n", add);

		String str = Integer.toBinaryString(add);
		System.out.println(str + "    //  bytes");

		String expected = "11111111"; // expected result of a ^ b
		int xorInt = Integer.parseInt(expected, 2);
		byte aByte = (byte) add;
		byte xorByte = (byte) xorInt;
		byte xor = (byte) (0xff & ((int) aByte) ^ ((int) xorByte));
         System.out.println(xor + "    // a ^ b as bytes");
		
		String str1 = Integer.toBinaryString(xor);
		System.out.println(str1 + "    //  bytes");
		
		System.out.format("xor  %02X \n", xor);
		xor += 1;
		System.out.format("xor +1  %02X \n", xor);

		int last = data[data.length-1] & 0xFF;
		System.out.format("last  %02X \n",last );
		
		if((data[data.length-1] & 0xFF) == (xor & 0xFF))
		{
			ret = true; 
		}		
		return ret;
	}

}
