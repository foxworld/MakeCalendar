package ksnet.pginfo.makecalendar.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DateTimeUtilsTest {

    @Test
    void testZuluDateTime() {

        // KST 시간 문자열을 UTC Zulu 시간 문자열로 변환 테스트
        String kstTime = "2024-06-01T15:30:00";
        String expectedUtcZulu = "2024-06-01T06:30:00Z";
        String actualUtcZulu = DateTimeUtils.kstToUtcZ(kstTime);
        log.info("KST Time: {}, Expected UTC Zulu: {}, Actual UTC Zulu: {}", kstTime, expectedUtcZulu, actualUtcZulu);

        // 현재시간 UTC Zulu 일자
        String zuluDateTime = DateTimeUtils.nowZulu(); // Call the private method for testing purposes (if accessible)
        log.info("localDateTime: {}, zuluDateTime: {}", DateTimeUtils.now(DateTimeUtils.ISO_LOCAL), zuluDateTime);

        // 현재시간 KST 일자 Zulu 변환
        String kstDateTime = DateTimeUtils.now(DateTimeUtils.ISO_LOCAL);
        String convertedZuluDateTime = DateTimeUtils.kstToUtcZ(kstDateTime);
        log.info("KST DateTime: {}, Converted Zulu DateTime: {}", kstDateTime, convertedZuluDateTime);

        assertEquals(expectedUtcZulu, actualUtcZulu, "The UTC Zulu conversion did not match the expected value.");
    }

}