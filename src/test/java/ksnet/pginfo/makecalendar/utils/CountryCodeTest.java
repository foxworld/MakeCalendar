package ksnet.pginfo.makecalendar.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
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