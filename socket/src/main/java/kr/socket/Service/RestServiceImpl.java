package kr.socket.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.socket.vo.LogErrorVO;
import kr.socket.vo.SensorOccurVO;
import kr.socket.vo.SensorRestoreVO;
import kr.socket.vo.TempSensorVO;

//String 데이터를 파싱하여 DB 로 전달 

@Service
public class RestServiceImpl implements MiddleService {

	static final Logger logger = LoggerFactory.getLogger(RestServiceImpl.class);
	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	/*@Autowired
    private TempSensorService tempSensorService;
	@Autowired
    private FireSensorOccurService fireSensorOccurService;
	@Autowired
    private FireSensorRestoreService fireSensorRestoreService;
	@Autowired
    private LogErrorService logErrorService;*/
//	
	
//	@Autowired
//	private TempRepository tempRepository = new TempRepository();
//
//	@Autowired
//	private FireOccurRepository fireOccurRepository = new FireOccurRepository();
//	
//	@Autowired
//	private FireRestoreRepository fireRestoreRepository = new FireRestoreRepository();

	@Override
	public boolean inserttempSensor(String sensor) {
		boolean ret = false;

//      |                                 | Battery                           | Temperature                       | Humidity                          | Parity Check | 
//      | IMEI               | Device ID  | Tag-ID_1 | Length_1 | Tag-Value_1 | Tag-ID_2 | Length_2 | Tag-Value_2 | Tag-ID_3 | Length_3 | Tag-Value_3 | Parity Check |
//      | 8 Byte             | 4 Byte     | 2 byte   | 1 byte   | 2 byte      | 2 byte   | 1 byte   | 4 byte      | 2 byte   | 1 byte   | 3 byte      | 1 Byte       |
//      | 0x3593690811103440 | 0x22991234 | 0x0001   | 0x02     | 0x1816      | 0x0002   | 0x04     | 0xFF002291  | 0x0003   | 0x03     | 0x001205    | 0xFF         | 
		logger.warn("temp " + sensor);
//		logger.warn("lenght " + sensor.length());

		int temp_seq = 0;
		LocalDateTime current = LocalDateTime.now();
		current = current.plusHours(9);
		String cdate = current.format(format);
//		logger.warn("cdate " + cdate);

		if(sensor.length() < 62)
		{
			logger.warn("temp length : " + sensor.length() + "  is samller than  62 ");
			LogErrorVO logErrorVO = new LogErrorVO(temp_seq,sensor, cdate, "length error", "temp");
			logErrorService.save(logErrorVO);
			return ret;
		}
		
		if(!checksum(sensor))		
		{
			logger.warn("checksum error " + sensor);
			LogErrorVO logErrorVO = new LogErrorVO(temp_seq,sensor, cdate, "checksum error", "temp");
			logErrorService.save(logErrorVO);
			return ret;
		}
		
		
		
		int imei_len = 8;
		int stringE = 0;
		int stringS = stringE;
		stringE += imei_len * 2;
		String imei = sensor.substring(stringS, stringE);
//		logger.warn("imei " + imei);

		int deviceid_len = 4;
		stringS = stringE;
		stringE += deviceid_len * 2;
		String deviceid = sensor.substring(stringS, stringE);
//		logger.warn("deviceid " + deviceid);

		int batteryid_len = 2;
		stringS = stringE;
		stringE += batteryid_len * 2;
		String batteryid = sensor.substring(stringS, stringE);
//		logger.warn("batteryid " + batteryid);

		int batterylen_len = 1;
		stringS = stringE;
		stringE += batterylen_len * 2;
		int batterylen = Integer.parseInt(sensor.substring(stringS, stringE), 16);
//		logger.warn("batterylen " + batterylen);

//		int batteryval_len = batterylen;
		int batteryval_len = 2;
		stringS = stringE;
		stringE += batteryval_len * 2;
		String batteryval = sensor.substring(stringS, stringE);
//		logger.warn("batteryval " + batteryval);
		String dbbatteryval = batteryval.substring(0, 1) + "." + batteryval.substring(1, 4);
//		logger.warn("dbbatteryval " + dbbatteryval);

		int tempid_len = 2;
		stringS = stringE;
		stringE += tempid_len * 2;
		String tempid = sensor.substring(stringS, stringE);
//		logger.warn("tempid " + tempid);

		int templen_len = 1;
		stringS = stringE;
		stringE += templen_len * 2;
		int templen = Integer.parseInt(sensor.substring(stringS, stringE), 16);
//		logger.warn("templen " + templen);

//		int tempval_len = templen;
		int tempval_len = 4;
		stringS = stringE;
		stringE += tempval_len * 2;
		String tempval = sensor.substring(stringS, stringE);
//		logger.warn("tempval " + tempval);

		String dbtempval = "";
		if (tempval.substring(0, 2).equals("FF")) {
			dbtempval = "-";
		}
		dbtempval += Integer.parseInt(tempval.substring(2, 6)) + "." + tempval.substring(6, 8);
//		logger.warn("dbtempval " + dbtempval);

		int humid_len = 2;
		stringS = stringE;
		stringE += humid_len * 2;
		String humid = sensor.substring(stringS, stringE);
//		logger.warn("humid " + humid);

		int humlen_len = 1;
		stringS = stringE;
		stringE += humlen_len * 2;
		int humlen = Integer.parseInt(sensor.substring(stringS, stringE), 16);
//		logger.warn("humlen " + humlen);

//		int humval_len = humlen;
		int humval_len = 3;
		stringS = stringE;
		stringE += humval_len * 2;
		String humval = sensor.substring(stringS, stringE);
//		logger.warn("humval " + humval);
		String dbhumval = "";
		if (humval.substring(0, 2).equals("01")) {
			dbhumval = "100";
		} else {
			dbhumval += Integer.parseInt(humval.substring(2, 4)) + "." + humval.substring(4, 6);
		}
//		logger.warn("dbhumval " + dbhumval);

		int parity_len = 1;
		stringS = stringE;
		stringE += parity_len * 2;
		String parity = sensor.substring(stringS, stringE);
//		logger.warn("parity " + parity);

		
	    Date today = new Date();	    
		LocalDate localDate = LocalDate.now();
		
		String imei_cd = imei;
		String temp_val = dbtempval;
		String humi_val = dbhumval;
		String battery_chk = dbbatteryval;
		String comm_yn = parity;
		String push_date = cdate;

//		logger.warn(imei_cd + " " + temp_val + " " + humi_val + " " + battery_chk + " " + comm_yn + " " + push_date);
		TempSensorVO tempSensorVO = new TempSensorVO(temp_seq, imei_cd, temp_val, humi_val, battery_chk, comm_yn,
				push_date);
		
//		TempRepository tempRepository = new  TempRepository();
//		tempRepository.save(tempSensorVO);
		
		//tempSensorService.save(tempSensorVO);
		
//		TempSensorService tempSensorService  = new TempSensorServiceImpl();
//		tempSensorService.save(tempSensorVO);
		
		ret = true;
		return ret;
	}
	
	@Override
	public boolean insertfireSensor(String sensor) {
		boolean ret = false;

//      |                                 | External sensor                   | Parity Check | 
//      | IMEI               | Device ID  | Tag-ID_1 | Length_1 | Tag-Value_1 | Parity Check |
//      | 8 Byte             | 4 Byte     | 2 byte   | 1 byte   | 1 byte      | 1 Byte       |
//      | 0x3593690811103440 | 0x22991234 | 0x0004   | 0x01     | 0x01        | 0xFF         | 
//		    3539333639303831     30353235     3533     0D         30            32

		logger.error("fire " + sensor);
//		logger.error("lenght " + sensor.length());
		
		int alarm_seq = 0;
		LocalDateTime current = LocalDateTime.now();
		current = current.plusHours(9);
		String cdate = current.format(format);
//		logger.warn("cdate " + cdate);
		
		if(sensor.length() < 34)
		{
			logger.error("fire length " + sensor.length() + "  is samller than 34 ");
			LogErrorVO logErrorVO = new LogErrorVO(alarm_seq,sensor, cdate, "length error", "fire");
			logErrorService.save(logErrorVO);
			return ret;
		}
		
		if(!checksum(sensor))		
		{
			logger.error("checksum error ");
			LogErrorVO logErrorVO = new LogErrorVO(alarm_seq,sensor, cdate, "checksum error", "fire");
			logErrorService.save(logErrorVO);
			return ret;
		}
		
		int imei_len = 8;
		int stringE = 0;
		int stringS = stringE;
		stringE += imei_len * 2;
		String imei = sensor.substring(stringS, stringE);
//		logger.error("imei " + imei);

		int deviceid_len = 4;
		stringS = stringE;
		stringE += deviceid_len * 2;
		String deviceid = sensor.substring(stringS, stringE);
//		logger.error("deviceid " + deviceid);

		int extsensorid_len = 2;
		stringS = stringE;
		stringE += extsensorid_len * 2;
		String extsensorid = sensor.substring(stringS, stringE);
//		logger.error("extsensorid " + extsensorid);

		int extsensorlen_len = 1;
		stringS = stringE;
		stringE += extsensorlen_len * 2;
		int extsensorlen = Integer.parseInt(sensor.substring(stringS, stringE), 16);
//		logger.error("extsensorlen " + extsensorlen);

//		int extsensorval_len = extsensorlen;
		int extsensorval_len = 1;
		stringS = stringE;
		stringE += extsensorval_len * 2;
		String extsensorval = sensor.substring(stringS, stringE);
//		logger.error("extsensorval " + extsensorval);

		int parity_len = 1;
		stringS = stringE;
		stringE += parity_len * 2;
		String parity = sensor.substring(stringS, stringE);
//		logger.error("parity " + parity);

		if (extsensorval.equals("01")) {
			logger.error("event occur ");
			String imei_cd = imei;
			String tag1_val = extsensorval;
			String push_date = cdate;
			String comm_yn = parity;
			String restore_info = "";
			
//			logger.error(imei_cd + " " + tag1_val + " " + push_date + " " + comm_yn);
			SensorOccurVO fireSensorOccurVO = new SensorOccurVO(alarm_seq, imei_cd, tag1_val, push_date, comm_yn,
					restore_info);

//			fireOccurRepository.save(fireSensorOccurVO);
			SensorOccurService.save(fireSensorOccurVO);				
			SensorOccurService.sendsmsinfo(imei_cd);			
			

		} else {
			logger.error("event restore ");
			String imei_cd = imei;
			String tag1_val = extsensorval;
			String push_date = cdate;

//			logger.error(imei_cd + " " + tag1_val + " " + push_date);
			SensorRestoreVO fireSensorRestoreVO = new SensorRestoreVO(alarm_seq, imei_cd, tag1_val, push_date);
			
//			fireRestoreRepository.save(fireSensorRestoreVO);
			SensorRestoreService.save(fireSensorRestoreVO);

		}

		ret = true;
		return ret;
	}

	public boolean checksum(String buf)
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
		}
//		System.out.format("sum   %02X \n", add);
		add = add & 0xFF;
//		System.out.format("last  %02X \n", add);

//		String str = Integer.toBinaryString(add);
//		System.out.println(str + "    //  bytes");

		String expected = "11111111"; // expected result of a ^ b
		int xorInt = Integer.parseInt(expected, 2);
		byte aByte = (byte) add;
		byte xorByte = (byte) xorInt;
		byte xor = (byte) (0xff & ((int) aByte) ^ ((int) xorByte));
//         System.out.println(xor + "    // a ^ b as bytes");
		
//		String str1 = Integer.toBinaryString(xor);
//		System.out.println(str1 + "    //  bytes");
		
//		System.out.format("xor  %02X \n", xor);
		xor += 1;
//		System.out.format("xor +1  %02X \n", xor);
//		System.out.format("data[data.length]  %02X \n", );

		int last = data[data.length-1] & 0xFF;
//		System.out.format("last  %02X \n",last );
		
		if((data[data.length-1] & 0xFF) == (xor & 0xFF))
		{
			ret = true; 
		}		
		return ret;
	}

}