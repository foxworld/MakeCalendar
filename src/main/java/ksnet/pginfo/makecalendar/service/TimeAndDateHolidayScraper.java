package ksnet.pginfo.makecalendar.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
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

        String url = timeAndDateUrl.replaceAll("/$", "") + "/" + code.getUrlPath().replaceAll("^/", "") + "/" + year;
        Document doc;
        List<Holiday> holidays = new ArrayList<>();
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(10_000)
                    .followRedirects(true)
                    .get();
        } catch (org.jsoup.HttpStatusException e) {
            if (e.getStatusCode() == 403) {
                log.warn("Access blocked fetching {} (status 403). Attempting Playwright fallback.", url);
                try (Playwright playwright = Playwright.create();
                     Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
                     BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                             .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"));
                     Page page = context.newPage()) {
                    page.navigate(url, new Page.NavigateOptions().setTimeout(30_000));
                    page.waitForSelector("table.table--left.table--inner-borders-rows",
                            new Page.WaitForSelectorOptions().setTimeout(15_000));
                    doc = Jsoup.parse(page.content(), url);
                } catch (Exception ex) {
                    log.warn("Playwright fallback failed for {}: {}. Returning empty holiday list.", url, ex.getMessage());
                    return holidays;
                }
            }
            log.error("Failed to fetch holidays from {} (status {})", url, e.getStatusCode());
            throw new Exception("Failed to fetch holidays", e);
        }

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
