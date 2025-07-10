package ksnet.pginfo.makecalendar.controller;

import ksnet.pginfo.makecalendar.servicee.MakeCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MakeCalendarController implements ApplicationRunner {
    @Value("${ksnet.pginfo.country_code}") String countryCode;
    @Value("${ksnet.pginfo.year}") int year;

    private final MakeCalendarService service;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("PG Make Calendar!!!");

        if (countryCode.equals("ALL")) {
            service.makeCalendar(year);
        } else {
            service.makeCalendar(countryCode, year);
        }
    }
}
