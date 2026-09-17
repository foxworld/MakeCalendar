package ksnet.pginfo.makecalendar.service;

import ksnet.pginfo.makecalendar.domain.PgCal02;
import ksnet.pginfo.makecalendar.domain.PgCal03;
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


    public void setHoliday(CountryCode countryCode, int year) throws Exception {

        List<Holiday> holidayList = new ArrayList<>();
        if(countryCode == CountryCode.CHN) {
            holidayList = chinaHolidayApiClient.getHolidays(year);
        }else {
            holidayList = publicHolidayApiClient.getHolidays(countryCode.name(), year);
        }
        for(Holiday holiday : holidayList) {
            pgCal02Repository.setHoliday(countryCode.name(), holiday.getDate(), "Y");
            pgCal03Repository.setHoliday(countryCode.name(), holiday.getDate(), "Y", holiday.getName());
        }
    }

    private void makeDateLoop(CountryCode countryCode, int year) {
        LocalDate startDate = LocalDate.parse(year+"0101", DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate endDate = LocalDate.parse(year+"1231", DateTimeFormatter.ofPattern("yyyyMMdd"));

        int i=0;
        while (!startDate.isAfter(endDate)) {
            PgCal02 pgCal02 = new PgCal02(
                    countryCode.name(),
                    startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    Integer.toString(startDate.getDayOfWeek().getValue()-1),
                    (startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY?"Y":"N")
            );

            pgCal02Repository.save(pgCal02);

            PgCal03 pgCal03 = new PgCal03(
                    countryCode.name(),
                    startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    Integer.toString(startDate.getDayOfWeek().getValue()-1),
                    (startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY?"Y":"N")
            );
            pgCal03Repository.save(pgCal03);

            startDate = startDate.plusDays(1); // 하루 증가
        }
    }

}
