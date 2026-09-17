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
    @Value("${ONLY-HOLIDAY:false}") boolean onlyHoliday;

    private final MakeCalendarService service;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("PG Make Calendar!!!");

        if (!hasValidCountryCode()) {
            return;
        }

        Integer parsedYear = parseYear();
        if (parsedYear == null) {
            return;
        }

        switch (countryCode.toUpperCase()) {
            case "TEST" -> log.info("TEST!!");
            case "ALL" -> service.makeCalendar(parsedYear, onlyHoliday);
            default -> {
                try {
                    CountryCode selectedCountry = CountryCode.valueOf(countryCode.toUpperCase());
                    service.makeCalendar(selectedCountry.name(), parsedYear, onlyHoliday);
                } catch (IllegalArgumentException e) {
                    log.info("미지원하는 국가코드 입니다. 국가코드:{}", countryCode);
                }
            }
        }
    }

    private boolean hasValidCountryCode() {
        if (!StringUtils.hasText(countryCode)) {
            log.info("COUNTRY-CODE 값이 없습니다. 종료합니다.");
            printUsage();
            return false;
        }
        return true;
    }

    private Integer parseYear() {
        if (!StringUtils.hasText(year)) {
            log.info("YEAR 값이 없습니다. 종료합니다.");
            printUsage();
            return null;
        }

        try {
            int parsedYear = Integer.parseInt(year.trim());
            if (parsedYear <= 0) {
                log.info("YEAR 값이 유효하지 않습니다. YEAR:{}, 종료합니다.", parsedYear);
                printUsage();
                return null;
            }
            return parsedYear;
        } catch (NumberFormatException e) {
            log.info("YEAR 값이 올바르지 않습니다. YEAR:{}, 종료합니다.", year);
            printUsage();
            return null;
        }
    }

    private void printUsage() {
        log.info("Usage: java -jar MakeCalendar-0.0.1.jar --COUNTRY-CODE=ALL --YEAR=2026 --ONLY-HOLIDAY=true|false");
        log.info("Allowed COUNTRY-CODE: ALL, TEST, KOR, USA, JPN, SGP, HKG, CHN");
    }
}
