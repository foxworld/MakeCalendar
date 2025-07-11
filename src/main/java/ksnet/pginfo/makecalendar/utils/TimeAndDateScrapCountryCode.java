package ksnet.pginfo.makecalendar.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum TimeAndDateScrapCountryCode {
    KRW("KRW", "South Korea", "south-korea"),
    JPN("JPN", "Japan", "japan"),
    HKG("HKG", "Hong Kong", "hong-kong"),
    SGP("SGP", "Singapore", "singapore"),
    USA("USA", "United States", "us"),
    CHN("CHN", "China", "china"),
    TWN("TWN", "Taiwan", "taiwan"),
    GBR("GBR", "United Kingdom", "uk");

    private final String code;
    private final String fullName;
    private final String urlPath;

    public static TimeAndDateScrapCountryCode fromCode(String code) {
        for (TimeAndDateScrapCountryCode c : values()) {
            if (c.code.equalsIgnoreCase(code)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown country code: " + code);
    }
}
