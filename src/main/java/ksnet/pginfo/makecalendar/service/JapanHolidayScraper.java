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
public class JapanHolidayScraper {
    public List<Holiday> getHolidays(int year) throws Exception {
        String url = "https://www.timeanddate.com/holidays/japan/"+year;
        Document doc = Jsoup.connect(url).get();

        List<Holiday> holidays = new ArrayList<>();

        Element table = doc.selectFirst("table.table--left.table--inner-borders-rows");
        if (table == null) throw new Exception("Holiday table not found");

        Elements rows = table.select("tbody tr");

        for (Element row : rows) {
            String date = null;
            Elements ths = row.select("th");
            if (!ths.isEmpty()) {
                date = Holiday.toYyyyMMdd(ths.get(0).text().trim()+ " " + year);
            }
            Elements tds = row.select("td");
            if (tds.size() >= 3) {
                String name = tds.get(1).text();      // 휴일명
                String type = tds.get(2).text();      // 유형 (공휴일, 관측일 등)
                if (type.toLowerCase().contains("national holiday")) {
                    holidays.add(new Holiday(date, name, type));
                }
            }
        }
        return holidays;
    }
}
