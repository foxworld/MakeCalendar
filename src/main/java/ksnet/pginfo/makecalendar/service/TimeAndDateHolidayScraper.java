package ksnet.pginfo.makecalendar.service;

import ksnet.pginfo.makecalendar.utils.CountryCode;
import ksnet.pginfo.makecalendar.utils.TimeAndDateScrapCountryCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
                log.warn("Access blocked fetching {} (status 403). Attempting Selenium fallback.", url);
                try {
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    // Use headless mode; adjust if you need to see the browser
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
                    options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");

                    WebDriver driver = new ChromeDriver(options);
                    try {
                        driver.get(url);
                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.table--left.table--inner-borders-rows")));
                        String pageSource = driver.getPageSource();
                        doc = Jsoup.parse(pageSource, url);
                    } finally {
                        try { driver.quit(); } catch (Exception ex) { log.debug("Error quitting webdriver: {}", ex.getMessage()); }
                    }
                } catch (Exception ex) {
                    log.warn("Selenium fallback failed for {}: {}. Returning empty holiday list.", url, ex.getMessage());
                    return holidays;
                }
            }
            log.error("Failed to fetch holidays from {} (status {})", url, e.getStatusCode());
            throw e;
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
