package ksnet.pginfo.makecalendar.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeAndDateScrapCountryCode {
    KOR("KOR", "South Korea", "south-korea", "KR"),
    JPN("JPN", "Japan", "japan", "JP"),
    HKG("HKG", "Hong Kong", "hong-kong", "HK"),
    SGP("SGP", "Singapore", "singapore", "SG"),
    USA("USA", "United States", "us", "US"),
    CHN("CHN", "China", "china", "CN"),
    TWN("TWN", "Taiwan", "taiwan", "TW"),
    GBR("GBR", "United Kingdom", "uk", "GB");

    private final String code;
    private final String fullName;
    private final String urlPath;
    private final String iso2;

    public static TimeAndDateScrapCountryCode fromCode(String code) {
        for (TimeAndDateScrapCountryCode c : values()) {
            if (c.code.equalsIgnoreCase(code)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown country code: " + code);
    }
}
