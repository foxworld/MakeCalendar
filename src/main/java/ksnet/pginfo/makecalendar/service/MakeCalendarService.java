package ksnet.pginfo.makecalendar.service;

import ksnet.pginfo.makecalendar.domain.PgCal01;
import ksnet.pginfo.makecalendar.domain.PgCal02;
import ksnet.pginfo.makecalendar.domain.PgCal03;
import ksnet.pginfo.makecalendar.repository.JpaPgCal01Repository;
import ksnet.pginfo.makecalendar.repository.PgCal02Repository;
import ksnet.pginfo.makecalendar.repository.PgCal03Repository;
import ksnet.pginfo.makecalendar.utils.CountryCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MakeCalendarService {
    private final JpaPgCal01Repository jpaPgCal01Repository;
    private final PgCal02Repository pgCal02Repository;
    private final PgCal03Repository pgCal03Repository;
    private final PublicHolidayApiClient publicHolidayApiClient;
    private final ChinaHolidayApiClient chinaHolidayApiClient;

    public void makeCalendar(String countryCode, int year) throws Exception {
        makeCalendar(countryCode, year, false);
    }

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
        }else {
            holidayList = publicHolidayApiClient.getHolidays(countryCode.name(), year);
        }
        for(Holiday holiday : holidayList) {
            if(countryCode.equals(CountryCode.KOR)) {
                jpaPgCal01Repository.setHoliday(holiday.getDate(),"0");
            }
            pgCal02Repository.setHoliday(countryCode.getCurrencyNumericCode(), holiday.getDate(), "Y");
            pgCal03Repository.setHoliday(countryCode.name(), holiday.getDate(), "Y", holiday.getName());
        }
    }

    private void makeDateLoop(CountryCode countryCode, int year) {
        LocalDate startDate = LocalDate.parse(year+"0101", DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate endDate = LocalDate.parse(year+"1231", DateTimeFormatter.ofPattern("yyyyMMdd"));

        int i=0;
        while (!startDate.isAfter(endDate)) {
            String trdDate = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String dayOfWeek = Integer.toString(startDate.getDayOfWeek().getValue() - 1);
            String isHolidayPgCal01 = (startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY?"0":"1");
            String isHoliday = (startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY?"Y":"N");

            if(countryCode.equals(CountryCode.KOR)) {
                jpaPgCal01Repository.save(new PgCal01(trdDate, isHolidayPgCal01, dayOfWeek));
            }
            pgCal02Repository.save(new PgCal02(countryCode.getCurrencyNumericCode(), trdDate, dayOfWeek, isHoliday));
            pgCal03Repository.save(new PgCal03(countryCode.name(), trdDate, dayOfWeek, isHoliday));

            startDate = startDate.plusDays(1); // 하루 증가
        }
    }

}
