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
public class HongKongHolidayScraper {

    public List<Holiday> getHolidays(int year) throws Exception {
        String url = "https://www.gov.hk/en/about/abouthk/holiday/" + year +".htm";

        Document doc = Jsoup.connect(url).get();

        List<Holiday> holidays = new ArrayList<>();

        // 휴일 정보가 담긴 표 선택 (예: <table> 내부 tr들)
        Elements rows = doc.select("table tbody tr");

        for (Element row : rows) {
            Elements cols = row.select("td");
            if (cols.size() >= 2) {
                if(cols.get(1).text().trim().isEmpty()) continue;
                String date = Holiday.toYyyyMMdd(cols.get(1).text().trim() + " " + year);
                if(date == null) continue;
                String name = cols.get(0).text().trim();
                holidays.add(new Holiday(date, name, ""));
            }
        }

        return holidays;
    }

}
