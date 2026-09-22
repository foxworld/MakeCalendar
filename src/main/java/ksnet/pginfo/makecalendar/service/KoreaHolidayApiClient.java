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
public class KoreaHolidayApiClient {
    @Value("${ksnet.pginfo.data-go-kr.api.url}") private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Holiday> getHolidays(int year) {
        List<Holiday> holidays = new ArrayList<>();
        try {
            String url = String.format(apiUrl, year);
            log.info("Fetching public holidays from API: {}", url);

            String responseBody = callApi(url);
            //log.info("responseBody : {}", responseBody);
            if (!StringUtils.hasText(responseBody) || responseBody.trim().isEmpty()) {
                return holidays;
            }

            JsonNode rootNode = mapper.readTree(responseBody);
            // response -> body -> items -> item 경로 탐색
            JsonNode arr = rootNode.path("response").path("body").path("items").path("item");
            //log.info("arr : {}", arr.toString());

            if (!arr.isArray()) return holidays;

            for (JsonNode node : arr) {
                String dateIso = node.path("locdate").asText(null); // yyyy-MM-dd
                if (dateIso == null || dateIso.trim().isEmpty()) continue;
                String name = node.path("dateName").asText(null);
                String dateFormatted = Holiday.toYyyyMMdd(dateIso);
                holidays.add(new Holiday(dateFormatted, name, "public holiday"));
            }

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
}
