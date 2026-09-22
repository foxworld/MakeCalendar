package ksnet.pginfo.makecalendar.service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import ksnet.pginfo.makecalendar.domain.PgCal01;
import ksnet.pginfo.makecalendar.domain.PgCal02;
import ksnet.pginfo.makecalendar.domain.PgCal03;
import ksnet.pginfo.makecalendar.repository.JpaPgCal01Repository;
import ksnet.pginfo.makecalendar.repository.PgCal02Repository;
import ksnet.pginfo.makecalendar.repository.PgCal03Repository;
import ksnet.pginfo.makecalendar.utils.CountryCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MakeCalendarService {
    @Value("${spring.profiles.active:dev}") private String activeProfile;
    private boolean isProd;

    private final JpaPgCal01Repository jpaPgCal01Repository;
    private final PgCal02Repository pgCal02Repository;
    private final PgCal03Repository pgCal03Repository;
    private final PublicHolidayApiClient publicHolidayApiClient;
    private final ChinaHolidayApiClient chinaHolidayApiClient;
    private final KoreaHolidayApiClient koreaHolidayApiClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // @PostConstruct를 통해 객체 생성 및 주입이 완료된 후 isProd 값 세팅
    @PostConstruct
    private void init() {
        //this.isProd = "prod".equalsIgnoreCase(activeProfile);
        if(!StringUtils.hasText(activeProfile)) {
            log.warn("Active profile is not set. Defaulting to 'dev'.");
            activeProfile = "dev";
            this.isProd = false;
        } else if ("prod".equalsIgnoreCase(activeProfile) /* || "test".equalsIgnoreCase(activeProfile)*/) {
            log.warn("Active profile '{}' is not recognized. Defaulting to 'dev'.", activeProfile);
            this.isProd = true;
        }
        else {
            this.isProd = false;
        }
    }

    public void makeCalendar(String countryCode, int year) throws Exception {
        makeCalendar(countryCode, year, false);
    }

    @Transactional
    public void makeCalendar(String countryCode, int year, boolean onlyHoliday) throws Exception {
        CountryCode code = CountryCode.fromAlpha3(countryCode);
        if (!onlyHoliday) {
            makeDateLoop(code, year);
        }
        setHoliday(code, year);
    }

    public void makeCalendar(int year) throws Exception {
        makeCalendar(year, false);
    }

    @Transactional
    public void makeCalendar(int year, boolean onlyHoliday) throws Exception {
        for (CountryCode countryCode : CountryCode.countryCodeAll()) {
            if (!onlyHoliday) {
                makeDateLoop(countryCode, year);
            }
            setHoliday(countryCode, year);
        }
    }

    private void setHoliday(CountryCode countryCode, int year) throws Exception {

        List<Holiday> holidayList = new ArrayList<>();
        if(countryCode == CountryCode.CHN) {
            holidayList = chinaHolidayApiClient.getHolidays(year);
        } else if(countryCode == CountryCode.KOR) {
            holidayList = koreaHolidayApiClient.getHolidays(year);
        } else {
            holidayList = publicHolidayApiClient.getHolidays(countryCode.name(), year);
        }
        for(Holiday holiday : holidayList) {
            if(countryCode.equals(CountryCode.KOR)) {
                jpaPgCal01Repository.setHoliday(holiday.getDate(),"0");
            }
            pgCal02Repository.setHoliday(countryCode.getCurrencyNumericCode(), holiday.getDate(), "Y");

            /*개발만 동작한다 */
            if(!isProd) {
                pgCal03Repository.setHoliday(countryCode.name(), holiday.getDate(), "Y", holiday.getName());
            }
        }
    }

    private void makeDateLoop(CountryCode countryCode, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        int i=0;
        while (!startDate.isAfter(endDate)) {
            String trdDate = startDate.format(DATE_FORMATTER);
            String dayOfWeek = Integer.toString(startDate.getDayOfWeek().getValue() - 1);
            boolean isWeekend = (startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY);

            String isHolidayPgCal01 = isWeekend ? "0" : "1";
            String isHoliday = isWeekend ? "Y" : "N";

            if(countryCode.equals(CountryCode.KOR)) {
                jpaPgCal01Repository.save(new PgCal01(trdDate, isHolidayPgCal01, dayOfWeek));
            }
            pgCal02Repository.save(new PgCal02(countryCode.getCurrencyNumericCode(), trdDate, dayOfWeek, isHoliday));

            /*개발만 동작한다 */
            if(!isProd) {
                pgCal03Repository.save(new PgCal03(countryCode.name(), trdDate, dayOfWeek, isHoliday));
            }

            startDate = startDate.plusDays(1); // 하루 증가
        }
    }

}
