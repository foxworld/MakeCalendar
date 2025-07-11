package ksnet.pginfo.makecalendar.service;

import ksnet.pginfo.makecalendar.utils.TimeAndDateScrapCountryCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TimeAndDateHolidayScraper {
    @Value("${ksnet.pginfo.time-and-date.holiday.url}") String timeAndDateUrl;


    public List<Holiday> getHolidays(String countryCode, int year) throws Exception {

        TimeAndDateScrapCountryCode code = TimeAndDateScrapCountryCode.fromCode(countryCode);

        String url = timeAndDateUrl+"/"+code.getUrlPath()+"/" + year;
        Document doc = Jsoup.connect(url).get();

        List<Holiday> holidays = new ArrayList<>();

        Element table = doc.selectFirst("table.table--left.table--inner-borders-rows");
        if (table == null) throw new Exception("Holiday table not found");

        Elements rows = table.select("tbody tr");

        for (Element row : rows) {
            String date = null;
            Elements ths = row.select("th");
            if (!ths.isEmpty()) {
                date = Holiday.toYyyyMMdd(ths.get(0).text().trim() + " " + year);
            }

            Elements tds = row.select("td");
            if (tds.size() >= 3) {
                String name = tds.get(1).text();      // 휴일명
                String type = tds.get(2).text();      // 유형 (공휴일, 관측일 등)
                switch (type.toLowerCase()) {
                    case "public holiday", "federal holiday", "national holiday" -> {
                    }
                    default -> {
                        continue;
                    }
                }
                holidays.add(new Holiday(date, name, type));
            }
        }
        return holidays;
    }
}
