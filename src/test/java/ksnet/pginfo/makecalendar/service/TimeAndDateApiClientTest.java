package ksnet.pginfo.makecalendar.service;

import ksnet.pginfo.makecalendar.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TimeAndDateApiClientTest {

    @Autowired TimeAndDateApiClient timeAndDateApiClient;

    @Test
    void testGetHolidays() {
        int year = Integer.parseInt(DateTimeUtils.now(DateTimeUtils.YYYY));
        String countryCode = "USA";
        log.info("Testing getHolidays for year {} and country {}", year, countryCode);
        try {
            var holidays = timeAndDateApiClient.getHolidays(countryCode, year);
            assertNotNull(holidays, "Holidays list should not be null");
            assertFalse(holidays.isEmpty(), "Holidays list should not be empty");

            for (Holiday holiday : holidays) {
                log.info("Holiday: {} on {} type {}", holiday.getName(), holiday.getDate(), holiday.getType());
                assertNotNull(holiday.getName(), "Holiday name should not be null");
                assertNotNull(holiday.getDate(), "Holiday date should not be null");
            }
        } catch (Exception e) {
            fail("Exception occurred while fetching holidays: " + e.getMessage());
        }
    }

}