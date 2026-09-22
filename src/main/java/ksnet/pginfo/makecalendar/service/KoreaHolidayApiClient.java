package ksnet.pginfo.makecalendar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KoreaHolidayApiClient {
    @Value("${ksnet.pginfo.data-go-kr.api.url}") private String apiUrl;

    private final WebClientService webClientService;
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Holiday> getHolidays(int year) {
        List<Holiday> holidays = new ArrayList<>();
        try {
            String url = String.format(apiUrl, year);
            log.info("Fetching public holidays from API: {}", url);

            String responseBody = webClientService.callApi(url);
            //log.info("responseBody : {}", responseBody);
            if (!StringUtils.hasText(responseBody)) {
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
}
