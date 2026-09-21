package ksnet.pginfo.makecalendar.service;

import ksnet.pginfo.makecalendar.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ChinaHolidayApiClientTest {

    @Autowired ChinaHolidayApiClient chinaHolidayApiClient;

    @Test
    void testGetHolidays() {
        int year = Integer.parseInt(DateTimeUtils.now(DateTimeUtils.YYYY));
        String countryCode = "KOR";
        log.info("Testing getHolidays for year: {} and country: {}", year, countryCode);
        var holidays = chinaHolidayApiClient.getHolidays(year);
        assertNotNull(holidays, "Holidays list should not be null");
        assertFalse(holidays.isEmpty(), "Holidays list should not be empty");

        for (Holiday holiday : holidays) {
            log.info("Holiday: {} on {}", holiday.getName(), holiday.getDate());
            assertNotNull(holiday.getName(), "Holiday name should not be null");
            assertNotNull(holiday.getDate(), "Holiday date should not be null");
        }
    }



}