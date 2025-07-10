package ksnet.pginfo.makecalendar.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class CountryCodeTest {

    @Test
    void CountryCodeList() {
        log.info("{}", CountryCode.countryCodeAll());
    }

    @Test
    void CountryCodeLoop() {
        for(CountryCode c : CountryCode.countryCodeAll()) {
            log.info("{}:{}:{}", c.name(), c.getAlpha2Code(), c.getEnglishName());
        }
    }
}