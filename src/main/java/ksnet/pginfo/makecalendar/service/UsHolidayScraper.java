package ksnet.pginfo.makecalendar.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class

UsHolidayScraper {
    public List<Holiday> getFederalHolidays(int year) throws Exception {
        String url = "https://www.timeanddate.com/holidays/us/" + year;
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        List<Holiday> holidays = new ArrayList<>();
        Element table = doc.selectFirst("table.table--left.table--inner-borders-rows");
        if (table == null) {
            throw new Exception("Holiday table not found");
        }

        for (Element row : table.select("tbody tr")) {
            String date = null;
            Elements ths = row.select("th");
            if (!ths.isEmpty()) {
                date = Holiday.toYyyyMMdd(year + "년 " + ths.get(0).text().trim());
                //log.info("{}:{}", ths.get(0).text().trim()+ " " + year, date);
                if(date == null) {
                    date = year + "년 " + ths.get(0).text().trim();
                }
            }

            Elements cols = row.select("td");
            if (cols.size() >= 3) {
                //date = cols.get(0).text();
                String name = cols.get(1).text();
                String type = cols.get(2).text();
                if ("Federal Holiday".equalsIgnoreCase(type)) {
                    holidays.add(new Holiday(date, name, type));
                }
            }
        }
        return holidays;
    }

}
