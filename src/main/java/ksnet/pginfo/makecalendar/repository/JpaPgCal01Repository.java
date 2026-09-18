package ksnet.pginfo.makecalendar.repository;

import ksnet.pginfo.makecalendar.domain.PgCal01;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPgCal01Repository extends JpaRepository<PgCal01, String> {

    default PgCal01 setHoliday(String trdDate, String isHoliday) {
        PgCal01 pgCal01 = findById(trdDate).orElseThrow(() ->
                new IllegalArgumentException("PgCal01 not found for trade date: " + trdDate));
        pgCal01.setHoliDate(isHoliday);
        return save(pgCal01);
    }
}
