package ksnet.pginfo.makecalendar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ksnet.pginfo.makecalendar.utils.TimeAndDateScrapCountryCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PublicHolidayApiClient {
    @Value("${ksnet.pginfo.date-nager-at.url}") private String apiUrl;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Fetch public holidays from Nager.Date API (https://date.nager.at).
     * Returns an empty list if the API doesn't return 200 or parsing fails.
     */
    public List<Holiday> getHolidays(String countryAlpha3, int year) {
        List<Holiday> holidays = new ArrayList<>();
        try {
            TimeAndDateScrapCountryCode code = TimeAndDateScrapCountryCode.fromCode(countryAlpha3);
            String iso2 = code.getIso2();
            String url = apiUrl + year + "/" + iso2;
            log.info("Fetching public holidays from API: {}", url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "MakeCalendar/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.warn("PublicHoliday API returned {} for {}", response.statusCode(), url);
                return holidays;
            }

            JsonNode arr = mapper.readTree(response.body());
            if (!arr.isArray()) return holidays;

            for (JsonNode node : arr) {
                String dateIso = node.path("date").asText(null); // yyyy-MM-dd
                if (dateIso == null || dateIso.isBlank()) continue;
                String name="";
                if(code.getCode().equals("KOR")) {
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
