package ksnet.pginfo.makecalendar.controller;

import ksnet.pginfo.makecalendar.service.MakeCalendarService;
import ksnet.pginfo.makecalendar.utils.CountryCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MakeCalendarController implements ApplicationRunner {
    @Value("${COUNTRY-CODE:}") String countryCode;
    @Value("${YEAR:}") String year;

    private final MakeCalendarService service;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("PG Make Calendar!!!");

        if(!StringUtils.hasText(countryCode)) {
            log.info("COUNTRY-CODE 값이 없습니다. 종료합니다.");
            return;
        }

        if(!StringUtils.hasText(year)) {
            log.info("YEAR 값이 없습니다. 종료합니다.");
            return;
        }

        int parsedYear;
        try {
            parsedYear = Integer.parseInt(year.trim());
        } catch (NumberFormatException e) {
            log.info("YEAR 값이 올바르지 않습니다. YEAR:{}, 종료합니다.", year);
            return;
        }

        if (parsedYear <= 0) {
            log.info("YEAR 값이 유효하지 않습니다. YEAR:{}, 종료합니다.", parsedYear);
            return;
        }

        switch (countryCode.toUpperCase()) {
            case "TEST" -> log.info("TEST!!");
            case "ALL" -> service.makeCalendar(parsedYear);
            default -> {
                try {
                    CountryCode selectedCountry = CountryCode.valueOf(countryCode.toUpperCase());
                    service.makeCalendar(selectedCountry.name(), parsedYear);
                } catch (IllegalArgumentException e) {
                    log.info("미지원하는 국가코드 입니다. 국가코드:{}", countryCode);
                }
            }
        }
    }
}
