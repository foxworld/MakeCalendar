package ksnet.pginfo.makecalendar.utils;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

@Slf4j
public class Utils {
	public static String getToday(String type) {
		// yyyy : 연도
		// MM : 월
		// dd : 일자
		// hh : 시
		// mm : 분
		// ss : 초
		if (type == null || type.isEmpty()) {
			type = "yyyyMMddhhmmss";
		}
		GregorianCalendar calendar = new GregorianCalendar();
		SimpleDateFormat dateFormat = new SimpleDateFormat(type);
        return dateFormat.format(calendar.getTime());
	}
	
	
	public static String getNextMonth(String workMon) {
		int year = Integer.parseInt(workMon.substring(0, 4));
		int mon = Integer.parseInt(workMon.substring(4, 6));

		mon = mon + 1;
		if (mon == 13) {
			mon = 1;
			year = year + 1;
		}

		return String.format("%04d%02d", year, mon);
	}

	/**
	 * PG거래번호로 거래일자 찾기
	 * 
	 * @param pgDealNumb PG거래번호
	 * @return 거래일자(yyyyMMdd) 또는 null
	 */
	public static String getPgDealNumbToDate(String pgDealNumb) {
		if(!StringUtils.hasText(pgDealNumb) || pgDealNumb.length()!=12) return null;

		log.debug("pgDealNumb={}", pgDealNumb);
		int dateCount = Integer.parseInt(pgDealNumb.substring(1, 5));
		// 기준일자.
		int initDate  = Integer.parseInt(getToday("yyyyMMdd"));

		GregorianCalendar gregorianCalendar = null;
		if (initDate <= 20270515) {
			gregorianCalendar = new GregorianCalendar(1999, Calendar.DECEMBER, 29);
		} else {
			gregorianCalendar = new GregorianCalendar(2027, Calendar.MAY, 16);
		}

		GregorianCalendar targetDate = gregorianCalendar;
		targetDate.add(Calendar.DAY_OF_MONTH, dateCount);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		return dateFormat.format(targetDate.getTime());
	}
	
	
	public static String getNextDate(String srcDate , int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();

		try {
			calendar.setTime(sdf.parse(srcDate));
			calendar.add(Calendar.DATE, days);

			return sdf.format(calendar.getTime());
			
		} catch (ParseException | IllegalArgumentException e) {
			log.error("nextDate Exception: ", e);
			return null;
		}
    }
	
	public static String trim(String value) {
		if(!StringUtils.hasText(value)) {
			return null;
		}
		return value.trim();
	}
	
	public static LocalDateTime stringToLocalDateTime(String dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        //log.info("Date={}", datetime.toString());
		return LocalDateTime.parse(dateTime, formatter);
	}
	
	public static LocalDateTime stringToLocalDateTime(String dateTime, String type) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(type);
        //log.info("Date={}", datetime.toString());
		return LocalDateTime.parse(dateTime, formatter);
	}

	public static String setMonths(String yyyymm, int cnt) {

		try {
			String calMonth;

			SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMM", Locale.KOREAN);
			Calendar cal = Calendar.getInstance();
			Date sMonth = sdf.parse(yyyymm);

			cal.setTime(sMonth);
			cal.add(Calendar.MONTH, cnt);

			calMonth = sdf.format(cal.getTime());
			return calMonth;
		} catch (ParseException e) {
			log.error("ParseException : ", e);
			return "";
		}
	}

	public static String getLastMonth(String yyyymm){ 

		String lastMonth = "";

		try {
			SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMM", Locale.KOREAN);
			Calendar cal = Calendar.getInstance();
			Date sMonth = sdf.parse(yyyymm);
			
			cal.setTime(sMonth);
			cal.add(Calendar.MONTH, -1);
			
			lastMonth = sdf.format(cal.getTime());
		} catch (ParseException e) {
			log.error("ParseException : ", e);
		}
		return lastMonth;
	}	
	
	public static String getClientIpAddr(HttpServletRequest request) {

		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			log.trace("Proxy-Client-IP={}", ip);
		} 
		
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			log.trace("WL-Proxy-Client-IP={}", ip);
		}

		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
			log.trace("HTTP_CLIENT_IP={}", ip);
		}

		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			log.trace("HTTP_X_FORWARDED_FOR={}", ip);
		}

		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			log.trace("RemoteAddrIp={}", ip);
		}

		return ip;
	}
	
  	// 자기 IP 가져오기

	/**
	 * 자기 자신의 IP를 가져오는 함수
	 * @return 자기 자신의 IP 주소, 가져오지 못하면 빈 문자열 반환
	 */
  	public static String getMyIp(){
  	    try {
  	    	return InetAddress.getLocalHost().getHostAddress();
  	    } catch (UnknownHostException e) {
  	    	log.error("UnknownHostException getMyIP: ", e);
  	    	return "";
  	    }
 
  	}

    /**
     * 문자열 을 encode 확인
     * @param messageBody 문자열
     */
	public static void encodeData(String messageBody)  {
		try {
			log.debug("utf-8(1) : {}", new String(messageBody.getBytes(StandardCharsets.UTF_8), "euc-kr"));
			log.debug("utf-8(2) : {}", new String(messageBody.getBytes(StandardCharsets.UTF_8), "ksc5601"));
			log.debug("utf-8(3) : {}", new String(messageBody.getBytes(StandardCharsets.UTF_8), "x-windows-949"));
			log.debug("utf-8(4) : {}", new String(messageBody.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
			 
			log.debug("iso-8859-1(1) : {}", new String(messageBody.getBytes(StandardCharsets.ISO_8859_1), "euc-kr"));
			log.debug("iso-8859-1(2) : {}", new String(messageBody.getBytes(StandardCharsets.ISO_8859_1), "ksc5601"));
			log.debug("iso-8859-1(3) : {}", new String(messageBody.getBytes(StandardCharsets.ISO_8859_1), "x-windows-949"));
			log.debug("iso-8859-1(4) : {}", new String(messageBody.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
			 
			log.debug("euc-kr(1) : {}", new String(messageBody.getBytes("euc-kr"), "ksc5601"));
			log.debug("euc-kr(2) : {}", new String(messageBody.getBytes("euc-kr"), StandardCharsets.UTF_8));
			log.debug("euc-kr(3) : {}", new String(messageBody.getBytes("euc-kr"), "x-windows-949"));
			log.debug("euc-kr(4) : {}", new String(messageBody.getBytes("euc-kr"), StandardCharsets.ISO_8859_1));
			 
			log.debug("ksc5601(1) : {}", new String(messageBody.getBytes("ksc5601"), "euc-kr"));
			log.debug("ksc5601(2) : {}", new String(messageBody.getBytes("ksc5601"), StandardCharsets.UTF_8));
			log.debug("ksc5601(3) : {}", new String(messageBody.getBytes("ksc5601"), "x-windows-949"));
			log.debug("ksc5601(4) : {}", new String(messageBody.getBytes("ksc5601"), StandardCharsets.ISO_8859_1));
			 
			log.debug("x-windows-949(1) : {}", new String(messageBody.getBytes("x-windows-949"), "euc-kr"));
			log.debug("x-windows-949(2) : {}", new String(messageBody.getBytes("x-windows-949"), StandardCharsets.UTF_8));
			log.debug("x-windows-949(3) : {}", new String(messageBody.getBytes("x-windows-949"), "ksc5601"));
			log.debug("x-windows-949(4) : {}", new String(messageBody.getBytes("x-windows-949"), StandardCharsets.ISO_8859_1));
	
			byte[] BOM = new byte[4];
			BOM = messageBody.getBytes();
			if( (BOM[0] & 0xFF) == 0xEF && (BOM[1] & 0xFF) == 0xBB && (BOM[2] & 0xFF) == 0xBF ) {
				log.debug("UTF-8");
			} else if( (BOM[0] & 0xFF) == 0xFE && (BOM[1] & 0xFF) == 0xFF ) {
				log.debug("UTF-16BE");
			} else if( (BOM[0] & 0xFF) == 0xFF && (BOM[1] & 0xFF) == 0xFE ) {
				log.debug("UTF-16LE");
			} else if( (BOM[0] & 0xFF) == 0x00 && (BOM[1] & 0xFF) == 0x00 &&
			         (BOM[0] & 0xFF) == 0xFE && (BOM[1] & 0xFF) == 0xFF ) {
				log.debug("UTF-32BE");
			} else if( (BOM[0] & 0xFF) == 0xFF && (BOM[1] & 0xFF) == 0xFE &&
			         (BOM[0] & 0xFF) == 0x00 && (BOM[1] & 0xFF) == 0x00 ) {
				log.debug("UTF-32LE");
			} else {
				log.debug("EUC-KR");
			}
		} catch(IOException e) {
			log.error("IOException: ", e);
		}
	}

    /**
     * 파일의 레코드 갯수를 리턴
     * @param path 패스포함 파일명
     * @return Jar파일 디렉토리
     */
	public static long fileDataCount(String path) {
		try {
			// LineNumberReader 생성
			LineNumberReader reader = new LineNumberReader(new FileReader(path));
			// 라인수 세기
			while(reader.readLine() != null) ;

			return reader.getLineNumber();
		} catch (IOException e) {
			log.error("IOException: ", e);

			return 0L;
		}
	}

    /**
     * 자기 자신의 jar 파일명
     * @return Jar파일명
     */
    public static String getJarName() {
        try {
            String command = System.getProperty("sun.java.command");
            if (command != null) {
                String jarName = command.split(" ")[0];
                return new File(jarName).getName();
            }
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * 자기 자신의 jar 파일명 포함 디렉토로 조회
     * @return Jar파일 디렉토리
     */
    public static String getJarPath() {
        try {
            String command = System.getProperty("sun.java.command");
            if (command != null) {
                // "BatchCodeCreate-0.0.1.jar --CARD-CODE=..." 형태
                String jarName = command.split(" ")[0];
                File jarFile = new File(jarName);

                // 절대 경로로 변환
                return jarFile.getAbsolutePath();
            }
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * 자기 자신의 jar 파일의 디렉토로 조회
     * @return Jar파일 디렉토리
     */
    public static String getJarDirectory() {
        try {
            String command = System.getProperty("sun.java.command");
            if (command != null) {
                String jarName = command.split(" ")[0];
                File jarFile = new File(jarName);

                // 부모 디렉토리 경로
                return jarFile.getAbsoluteFile().getParent();
            }
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

	/**
	 * 문자열에서 따옴표, 콤마 제거 및 EUC-KR에서 깨지는 문자 제거
	 * @param input 원본 문자열
	 * @return 정제된 문자열
	 */
    public static String cleanText(String input) {
        if (!StringUtils.hasText(input)) return null;

        // 1. 따옴표 및 콤마 제거
        String cleaned = input.replace("\"", "")
                .replace("'", "")
                .replace(",", "")
                .replaceAll("[\\r\\n]+", "");

        // 2. EUC-KR에서 깨지는 문자 제거
        // 한글 완성형 + 자모 + 영문 + 숫자 + 공백 + 특수문자만 허용
        cleaned = cleaned.replaceAll("[^\\uAC00-\\uD7A3\\u3131-\\u318E\\p{Punct}\\s\\w]", "");

        return cleaned;
    }


     /**
     * EUC-KR 인코딩 기준으로 깨진 문자를 제거하고 정상적인 문자만 반환
     * @param input 원본 문자열
     * @return 정상적인 문자만 포함된 문자열
     */
    public static String extractValidCharacters(String input) {
        if (input == null) return "";

        try {
            // EUC-KR 디코더 생성 (깨진 문자 무시 설정)
            CharsetDecoder decoder = Charset.forName("EUC-KR")
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.IGNORE)
                    .onUnmappableCharacter(CodingErrorAction.IGNORE);

            // 문자열을 EUC-KR 바이트로 인코딩 후 다시 디코딩
            byte[] bytes = input.getBytes("EUC-KR");
            return decoder.decode(java.nio.ByteBuffer.wrap(bytes)).toString();

        } catch (Exception e) {
            // 예외 발생 시 원본 문자열 반환
            return input;
        }
    }

    /**
     * 디렉토리가 존재하지 않으면 생성하는 함수
     * @param path 디렉토리 또는 파일 경로
     * @param isFilePath true: 파일 경로로 간주하고 부모 디렉토리 생성 / false: 디렉토리 경로 생성
     */
    public static void makeDirectory(String path, boolean isFilePath) {
        if (!StringUtils.hasText(path)) {
            throw new IllegalArgumentException("경로가 비어 있습니다.");
        }

        File target = new File(path);
        File directoryToCreate = isFilePath ? target.getParentFile() : target;

        if (directoryToCreate == null) {
            throw new RuntimeException("유효하지 않은 경로입니다: " + path);
        }

        if (directoryToCreate.exists()) {
            if (!directoryToCreate.isDirectory()) {
                throw new RuntimeException("경로에 파일이 존재합니다: " + directoryToCreate.getAbsolutePath());
            }
            // 이미 디렉토리 존재 → 아무 작업 안 함
            return;
        }

        boolean created = directoryToCreate.mkdirs();
        if (!created) {
            throw new RuntimeException("디렉토리 생성 실패: " + directoryToCreate.getAbsolutePath());
        }
    }

}
