package ksnet.pginfo.makecalendar.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="pgcal01")
public class PgCal01 {
    @Id
    @Column(name="trd_date")
    private String tradeDate;
    private String holiDate;
    private String dayOfWeek;
}
