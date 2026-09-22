package ksnet.pginfo.makecalendar.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(PgCal02Key.class)
@Table(name="pgcal02")
public class PgCal02 {

    @Id
    private String countryCode;
    @Id
    @Column(name="trd_date")
    private String tradeDate;
    private String dayOfWeek;
    private String legalHoliday;
}
