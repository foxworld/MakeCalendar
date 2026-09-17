package ksnet.pginfo.makecalendar.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Getter
@AllArgsConstructor
@Slf4j
public enum ChinaHolidayCode {
    // 7대 법정 공휴일
    NEW_YEAR("元旦", "New Year's Day", "신정"),
    SPRING_FESTIVAL("春节", "Spring Festival", "춘절"),
    TOMB_SWEEPING_DAY("清明节", "Tomb Sweeping Day", "청명절"),
    LABOR_DAY("劳动节", "Labor Day", "노동절"),
    DRAGON_BOAT_FESTIVAL("端午节", "Dragon Boat Festival", "단오절"),
    MID_AUTUMN_FESTIVAL("中秋节", "Mid-Autumn Festival", "중추절"),
    NATIONAL_DAY("国庆节", "National Day", "국경절"),

    // 춘절 연휴 세부 세시풍속 명칭 (API 응답 대응)
    EVE_OF_SPRING_FESTIVAL("除夕", "Chinese New Year's Eve", "제석(음력 믐날)"),
    CHUNI("初一", "Spring Festival Day 1", "춘절 1일"),
    CHUER("初二", "Spring Festival Day 2", "춘절 2일"),
    CHUSAN("初三", "Spring Festival Day 3", "춘절 3일"),
    CHUSI("初四", "Spring Festival Day 4", "춘절 4일"),
    CHUWU("初五", "Spring Festival Day 5", "춘절 5일"),
    CHULIU("初六", "Spring Festival Day 6", "춘절 6일"),
    CHUQI("初七", "Spring Festival Day 7", "춘절 7일"),

    UNKNOWN("", "Unknown Holiday", "미지정 공휴일");

    private final String chineseName;
    private final String englishName;
    private final String koreanName;

    /**
     * API name 필드(rawName) 및 대상(target) 기준으로 ChinaHolidayCode 매핑
     */
    public static ChinaHolidayCode of(String rawName, String target) {
        if (rawName == null || rawName.trim().isEmpty()) {
            return UNKNOWN;
        }
        String searchKey = (target != null && !target.isEmpty()) ? target : rawName;

        return Arrays.stream(values())
                .filter(h -> !h.chineseName.isEmpty() && searchKey.contains(h.chineseName))
                .findFirst()
                .orElse(UNKNOWN);
    }

    /**
     * 영문 명칭 변환 (대체근무일 구분 포함)
     */
    public static String translateToEnglish(String rawName, String target) {
        ChinaHolidayCode holiday = of(rawName, target);
        if (isWorkday(rawName)) {
            boolean isAfter = rawName.contains("后");
            String timing = isAfter ? "Post" : "Pre";
            return holiday.getEnglishName() + " (" + timing + "-Adjusted Working Day)";
        }
        return holiday.getEnglishName();
    }

    /**
     * 한국어 명칭 변환 (대체근무일 구분 포함)
     */
    public static String translateToKorean(String rawName, String target) {
        ChinaHolidayCode holiday = of(rawName, target);
        if (isWorkday(rawName)) {
            boolean isAfter = rawName.contains("后");
            String timing = isAfter ? "후" : "전";
            return holiday.getKoreanName() + " " + timing + " 대체근무일";
        }
        return holiday.getKoreanName();
    }

    /**
     * 대체근무일(补班/调休) 여부 확인
     */
    private static boolean isWorkday(String rawName) {
        return rawName != null && (rawName.contains("补班") || rawName.contains("调休"));
    }
}