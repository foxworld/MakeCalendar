package ksnet.pginfo.makecalendar.repository;

import jakarta.transaction.Transactional;
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
        Optional<PgCal03> findPgCal03 = repository.findById(new PgCal03Key(countryCode, tradeDate));
        if(findPgCal03.isEmpty()) {
            return null;
        }
        findPgCal03.get().setLegalHoliday(legalHoliday);
        findPgCal03.get().setDescription(description);
        repository.save(findPgCal03.get());

        return findPgCal03.get();
    }

}
