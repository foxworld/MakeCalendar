package ksnet.pginfo.makecalendar.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class MakeCalendarServiceTest {
    @Autowired
    private MakeCalendarService service;

    @Test
    void testMakeCalendar() throws Exception {
        log.info("Running MakeCalendarServiceTest for all countries...");
        service.makeCalendar(2026);
    }

    @Test
    void testMakeCalendarWithCountryCode() throws Exception {
        log.info("Running MakeCalendarServiceTest for a specific country code...");
        service.makeCalendar("KOR", 2026);
    }

    @Test
    void testMakeCalendarOnlyHoliday() throws Exception {
        log.info("Running MakeCalendarServiceTest with only holiday processing...");
        service.makeCalendar(2026, true);
    }

    @Test
    void testMakeCalendarWithCountryCodeOnlyHoliday() throws Exception {
        log.info("Running MakeCalendarServiceTest for a specific country code with only holiday processing...");
        service.makeCalendar("KOR", 2026, true);
    }
}