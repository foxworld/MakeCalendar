package ksnet.pginfo.makecalendar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ksnet.pginfo.makecalendar.utils.ChinaHolidayCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChinaHolidayApiClient {
    @Value("${ksnet.pginfo.timor-tech.url}") private String apiUrl;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Holiday> getHolidays(int year) {
        List<Holiday> holidays = new ArrayList<>();
        try {
            String url = apiUrl + year + "/";
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

            JsonNode root = mapper.readTree(response.body());
            JsonNode holidayMap = root.path("holiday");
            if (holidayMap == null || holidayMap.isMissingNode() || !holidayMap.isObject()) {
                return holidays;
            }

            Iterator<Map.Entry<String, JsonNode>> fields = holidayMap.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode node = entry.getValue();
                if (node == null || node.isNull() || node.isMissingNode()) {
                    continue;
                }

                boolean isHoliday = node.path("holiday").asBoolean(false);
                if (!isHoliday) {
                    continue;
                }

                String dateIso = node.path("date").asText(null);
                if (!StringUtils.hasText(dateIso)) {
                    continue;
                }

                String name = node.path("name").asText(null);
                if (!StringUtils.hasText(name)) {
                    continue;
                }

                String target = node.path("target").asText(null);;
                String translateName = ChinaHolidayCode.translateToKorean(name, target);

                holidays.add(new Holiday(Holiday.toYyyyMMdd(dateIso), translateName, "public holiday"));
            }

            return holidays;
        } catch (Exception ex) {
            log.warn("Failed to fetch/parse PublicHoliday API: {}", ex.getMessage());
            return holidays;
        }
    }
}
