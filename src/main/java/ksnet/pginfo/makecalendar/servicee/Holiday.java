package ksnet.pginfo.makecalendar.servicee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Slf4j
@Data
@AllArgsConstructor
public class Holiday {
    private String date;
    private String name;
    private String type;

    public static String toYyyyMMdd(String input) {
        //input = input.trim().replaceAll("\\s+", "");
        String[] patterns = {
                "d MMMM yyyy",        // 1 January 2025
                "MMMM d yyyy",        // January 1 2025
                "d MMM yyyy",         // 1 Jan 2025
                "MMM d yyyy",         // Jan 1 2025
                "yyyy년 M월 d일",     // 2025년 1월 1일
                "M월 d일 yyyy",       // 1월 1일 2025
                "d일 M월 yyyy년",     // 1일 1월 2025년
                "yyyy-M-d",           // 2025-1-1
                "yyyy/M/d",           // 2025/1/1
                "yyyyMMdd"            // 20250101
        };

        for (String pattern : patterns) {
            try {
                Locale locale = pattern.contains("월") || pattern.contains("년") ? Locale.KOREAN : Locale.ENGLISH;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
                LocalDate date = LocalDate.parse(input, formatter);
                return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            } catch (DateTimeParseException e) {
                // 다음 포맷 시도
            }
        }

        return null;
    }
}
