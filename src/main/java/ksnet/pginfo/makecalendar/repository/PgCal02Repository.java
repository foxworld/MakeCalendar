package ksnet.pginfo.makecalendar.repository;

import jakarta.transaction.Transactional;
import ksnet.pginfo.makecalendar.domain.PgCal02;
import ksnet.pginfo.makecalendar.domain.PgCal02Key;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class PgCal02Repository {

    private final JpaPgCal02Repository repository;

    public void save(PgCal02 pgCal02) {
        repository.deleteById(new PgCal02Key(pgCal02.getCountryCode(), pgCal02.getTradeDate()));
        repository.save(pgCal02);
    }

    public Optional<PgCal02> findById(PgCal02Key key) {
        return repository.findById(key);
    }

    public PgCal02 setHoliday( String countryCode, String tradeDate, String legalHoliday) {
        Optional<PgCal02> findPgCal02 = repository.findById(new PgCal02Key(countryCode, tradeDate));
        PgCal02 pgCal02 = findPgCal02.orElseThrow(() ->
                new IllegalArgumentException("PgCal02 not found for country code: " + countryCode + ", trade date: " + tradeDate));
        pgCal02.setLegalHoliday(legalHoliday);
        repository.save(pgCal02);

        return pgCal02;
    }

}
