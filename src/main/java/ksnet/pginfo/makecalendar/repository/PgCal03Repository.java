package ksnet.pginfo.makecalendar.repository;

import javax.transaction.Transactional;
import ksnet.pginfo.makecalendar.domain.PgCal03;
import ksnet.pginfo.makecalendar.domain.PgCal03Key;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class PgCal03Repository {

    private final JpaPgCal03Repository repository;

    public void save(PgCal03 pgCal03) {
        repository.deleteById(new PgCal03Key(pgCal03.getCountryCode(), pgCal03.getTradeDate()));
        repository.save(pgCal03);
    }

    public Optional<PgCal03> findById(PgCal03Key key) {
        return repository.findById(key);
    }

    public PgCal03 setHoliday( String countryCode, String tradeDate, String legalHoliday, String description) {
        PgCal03 pgCal03 = repository.findById(new PgCal03Key(countryCode, tradeDate)).orElse(null);
        if (pgCal03 == null) {
            throw new IllegalArgumentException("PgCal03 not found for country code: " + countryCode + ", trade date: " + tradeDate);
        }
        pgCal03.setLegalHoliday(legalHoliday);
        pgCal03.setDescription(description);
        repository.save(pgCal03);

        return pgCal03;
    }

}
