package ksnet.pginfo.makecalendar.repository;

import ksnet.pginfo.makecalendar.domain.PgCal01;
import ksnet.pginfo.makecalendar.domain.PgCal02;
import ksnet.pginfo.makecalendar.domain.PgCal02Key;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPgCal01Repository extends JpaRepository<PgCal01, String> {

    default PgCal01 setHoliday(String trdDate, String isHoliday, String dayOfWeek) {
        Optional<PgCal01> findPgCal01 = findById(trdDate);
        PgCal01 pgCal01 = findPgCal01.orElseThrow(() ->
                new IllegalArgumentException("PgCal01 not found for trade date: " + trdDate));
        pgCal01.setHoliDate(isHoliday);
        return save(pgCal01);
    }
}
