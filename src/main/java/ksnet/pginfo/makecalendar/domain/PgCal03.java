package ksnet.pginfo.makecalendar.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(PgCal03Key.class)
@Table(name="pgcal03")
public class PgCal03 {
    @Id
    private String countryCode;
    @Id
    @Column(name="trd_date")
    private String tradeDate;
    private String dayOfWeek;
    private String legalHoliday;
    private String description;

    public PgCal03(String countryCode, String tradeDate, String dayOfWeek, String legalHoliday) {
        this.countryCode = countryCode;
        this.tradeDate = tradeDate;
        this.dayOfWeek = dayOfWeek;
        this.legalHoliday = legalHoliday;
        this.description = "";
    }

}
