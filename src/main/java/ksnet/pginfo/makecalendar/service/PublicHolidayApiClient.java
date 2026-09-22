package ksnet.pginfo.makecalendar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ksnet.pginfo.makecalendar.utils.CountryCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicHolidayApiClient {
    @Value("${ksnet.pginfo.date-nager-at.url}") private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Fetch public holidays from Nager.Date API (https://date.nager.at).
     * Returns an empty list if the API doesn't return 200 or parsing fails.
     */
    public List<Holiday> getHolidays(String countryAlpha3, int year) {
        List<Holiday> holidays = new ArrayList<>();
        try {
            CountryCode countryCode = CountryCode.fromAlpha3(countryAlpha3);
            String url = apiUrl + year + "/" + countryCode.getAlpha2Code();
            log.info("Fetching public holidays from API: {}", url);

            String responseBody = callApi(url);
            if (!StringUtils.hasText(responseBody) || responseBody.trim().isEmpty()) {
                return holidays;
            }

            JsonNode arr = mapper.readTree(responseBody);
            if (!arr.isArray()) return holidays;

            for (JsonNode node : arr) {
                String dateIso = node.path("date").asText(null); // yyyy-MM-dd
                if (dateIso == null || dateIso.trim().isEmpty()) continue;
                String name="";
                if(countryCode.equals(CountryCode.KOR)) {
                    name = node.path("localName").asText(node.path("name").asText(null));
                } else {
                    name = node.path("name").asText(node.path("localName").asText(null));
                }
                boolean global = node.path("global").asBoolean(false);
                String type = global ? "public holiday" : "local holiday";
                String dateFormatted = Holiday.toYyyyMMdd(dateIso);
                holidays.add(new Holiday(dateFormatted, name, type));
            }

            return holidays;
        } catch (IllegalArgumentException iae) {
            log.warn("Unknown country code for API lookup: {}", countryAlpha3);
            return holidays;
        } catch (Exception ex) {
            log.warn("Failed to fetch/parse PublicHoliday API: {}", ex.getMessage());
            return holidays;
        }
    }

    private String callApi(String url) {
        // 1. URI 빌드 (Map에 담긴 파라미터를 자동으로 쿼리 스트링으로 변환)
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        URI uri = builder.build().encode().toUri();

        try {
            // 동기 처리
            return webClient.get()
                    .uri(uri)
                    .header("User-Agent", "MakeCalendar/1.0")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .exchangeToMono(response -> {
                        if (!response.statusCode().is2xxSuccessful()) {
                            log.error("PublicHoliday API returned {} for {}", response.statusCode(), url);
                            return response.releaseBody().then(Mono.empty());
                        }
                        return response.bodyToMono(String.class);
                    })
                    .block();
        } catch (Exception ex) {
            log.error("Failed to call PublicHoliday API: {}", ex.getMessage());
            return null;
        }
    }

    private List<Holiday> makeHoliday(String responseBody, CountryCode countryCode) throws Exception {
        List<Holiday> holidays = new ArrayList<>();

        JsonNode arr = mapper.readTree(responseBody);
        if (!arr.isArray()) return holidays;

        for (JsonNode node : arr) {
            String dateIso = node.path("date").asText(null); // yyyy-MM-dd
            if (dateIso == null || dateIso.trim().isEmpty()) continue;
            String name="";
            if(countryCode.equals(CountryCode.KOR)) {
                name = node.path("localName").asText(node.path("name").asText(null));
            } else {
                name = node.path("name").asText(node.path("localName").asText(null));
            }
            boolean global = node.path("global").asBoolean(false);
            String type = global ? "public holiday" : "local holiday";
            String dateFormatted = Holiday.toYyyyMMdd(dateIso);
            holidays.add(new Holiday(dateFormatted, name, type));
        }

        return holidays;
    }
}
