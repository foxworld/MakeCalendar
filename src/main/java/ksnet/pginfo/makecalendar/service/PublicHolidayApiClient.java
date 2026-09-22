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

    private final WebClientService webClientService;
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

            String responseBody = webClientService.callApi(url);
            //log.info("responseBody : {}", responseBody);
            if (!StringUtils.hasText(responseBody)) {
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
}
