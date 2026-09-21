package ksnet.pginfo.makecalendar.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    // 기본 날짜
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    // 기본 날짜·시간
    public static final String YYYYMMDD_HHMMSS = "yyyyMMddHHmmss";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    // ISO 형식
    public static final String ISO_LOCAL = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ISO_LOCAL_MILLIS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String ISO_OFFSET = "yyyy-MM-dd'T'HH:mm:ssXXX"; // +09:00 포함
    public static final String ISO_INSTANT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; // UTC Z

    // 시간만
    public static final String HHMMSS = "HHmmss";
    public static final String HH_MM_SS = "HH:mm:ss";

    // 년월
    public static final String YYYY = "yyyy";
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYY_MM = "yyyy-MM";

    // 년월일 + 밀리초
    public static final String YYYYMMDD_HHMMSS_SSS = "yyyyMMddHHmmssSSS";


    /**
     * KST 시간 문자열을 UTC Zulu 시간 문자열로 변환
     *
     * @param localDateTime KST 시간 문자열 (예: "2024-06-01T15:30:00")
     * @return UTC Zulu 시간 문자열 (예: "2024-06-01T06:30:00Z")
     */
    public static String kstToUtcZ(String localDateTime) {
        LocalDateTime ldt = LocalDateTime.parse(localDateTime);
        ZonedDateTime kst = ldt.atZone(ZoneId.of("Asia/Seoul"));
        ZonedDateTime utc = kst.withZoneSameInstant(ZoneOffset.UTC);
        return utc.format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * "yyyyMM" 형식의 연월 문자열을 주어진 오프셋만큼 이동시킨 후 다시 "yyyyMM" 형식으로 반환
     *
     * @param yearMonth "yyyyMM" 형식의 연월 문자열 (예: "202602")
     * @param offset 이동할 개월 수 (양수: 미래, 음수: 과거)
     * @return 이동된 연월을 "yyyyMM" 형식으로 반환
     */
    public static String calcYearMonth(String yearMonth, int offset) {
        // "202602" → YearMonth 파싱
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(YYYYMM);
        YearMonth ym = YearMonth.parse(yearMonth, fmt);

        // offset 만큼 이동
        YearMonth result = ym.plusMonths(offset);

        // 다시 "yyyyMM" 형태로 반환
        return result.format(fmt);
    }

    /**
     * 현재 시간을 지정된 패턴으로 포맷팅하여 문자열로 반환
     *
     * @param pattern 날짜/시간 패턴 (예: "yyyy-MM-dd HH:mm:ss")
     * @return 포맷팅된 현재 시간 문자열
     */
    public static String now(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return now.format(formatter);
    }

    /**
     * from Long to Timestamp
     * @param timestamp  일시 long형
     * @return "yyyy-MM-dd'T'HH:mm:ss" 형식의 문자열로 변환된 타임스탬프
     */
    public static String formatTimestamp(long timestamp) {
        // 1. 밀리초 타임스탬프를 LocalDateTime으로 변환
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault() // 시스템 기본 시간대(Asia/Seoul) 적용
        );

        // 2. 원하는 포맷(yyyy-MM-dd'T'HH:mm:ss)으로 변환
        return dateTime.format(DateTimeFormatter.ofPattern(ISO_LOCAL));
    }

    public static String nowZulu() {
        return LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .withZoneSameInstant(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(ISO_INSTANT));
    }
}
