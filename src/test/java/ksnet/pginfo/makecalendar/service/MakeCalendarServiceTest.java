package ksnet.pginfo.makecalendar.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Slf4j
@Transactional
@Rollback(false)
class MakeCalendarServiceTest {
    @Autowired
    private MakeCalendarService service;

    @Test
    void testMakeCalendar() throws Exception {
        log.info("Running MakeCalendarServiceTest for all countries...");
        service.makeCalendar(2026);
    }

    @Test
    void testMakeCalendarWithKoreaCode() throws Exception {
        log.info("Running MakeCalendarServiceTest for a specific Korea code...");
        service.makeCalendar("KOR", 2026);
    }

    @Test
    void testMakeCalendarWithKoreaCodeOnlyHoliday() throws Exception {
        log.info("Running MakeCalendarServiceTest for a specific Korea code with only holiday processing...");
        service.makeCalendar("KOR", 2026, true);
    }

    @Test
    void testMakeCalendarOnlyHoliday() throws Exception {
        log.info("Running MakeCalendarServiceTest with only holiday processing...");
        service.makeCalendar(2026, true);
    }

    @Test
    void testMakeCalendarWithChina() throws Exception {
        log.info("Running MakeCalendarServiceTest for a specific china code...");
        service.makeCalendar("CHN", 2026);
    }

    @Test
    void testMakeCalendarWithChinaCodeOnlyHoliday() throws Exception {
        log.info("Running MakeCalendarServiceTest for a specific china code with only holiday processing...");
        service.makeCalendar("CHN", 2026, true);
    }

}