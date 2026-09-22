package ksnet.pginfo.makecalendar.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PgCal02Key implements Serializable {
    private static final long serialVersionUID = 1L;

    private String countryCode;
    private String tradeDate;

}
