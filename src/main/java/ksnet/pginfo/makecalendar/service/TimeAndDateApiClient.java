package ksnet.pginfo.makecalendar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ksnet.pginfo.makecalendar.utils.CountryCode;
import ksnet.pginfo.makecalendar.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimeAndDateApiClient {
    @Value("${ksnet.pginfo.time-end-date.api.url}") private String apiUrl;
    @Value("${ksnet.pginfo.time-end-date.access-key}") private String ACCESS_KEY;
    @Value("${ksnet.pginfo.time-end-date.secret-key}") private String SECRET_KEY;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Holiday> getHolidays(String countryAlpha3, int year) throws Exception {
        List<Holiday> holidays = new ArrayList<>();

        try {
            String url = getApiUrl(countryAlpha3, year); // URL 생성 및 로깅
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

            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode arr = rootNode.path("holidays");
            //if (!arr.isArray()) return holidays;

            for (JsonNode item : arr) {
                // date.iso 추출 (ex: "2026-01-01")
                String dateIso = item.path("date").path("iso").asText();
                // name[0].text 추출 (ex: "New Year's Day")
                String nameText = item.path("name").path(0).path("text").asText();
                // subtype 추출 (ex: "public holiday")
                String subtype = item.path("subtype").asText("");

                // subtype이 없거나(null/공백), 소문자 변환 값이 "federal holiday"가 아니면 스킵
                if (!StringUtils.hasText(subtype) || !"federal holiday".equalsIgnoreCase(subtype.toLowerCase(Locale.ROOT).trim())) {
                    continue;
                }

                if (!dateIso.isEmpty() && !nameText.isEmpty()) {
                    String dateFormatted = Holiday.toYyyyMMdd(dateIso);
                    holidays.add(new Holiday(dateFormatted, nameText, subtype));
                }
                log.debug("Parsed holiday: {} on {} type {}", nameText, dateIso, subtype);
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

    private String getApiUrl(String countryCode, int year) throws NoSuchAlgorithmException, InvalidKeyException {
        CountryCode alpha2 = CountryCode.fromAlpha3(countryCode);

        // 1. 현재 UTC 타임스탬프 생성 (ISO-8601)
        String zuluTimestamp = DateTimeUtils.nowZulu();
        log.info("Generated UTC Zulu Timestamp: {}", zuluTimestamp);

        // 2. HMAC-SHA1 메시지 조합 (AccessKey + "holidays" + Timestamp)
        String message = ACCESS_KEY + "holidays" + zuluTimestamp;
        Mac sha1Hmac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        sha1Hmac.init(secretKey);

        byte[] rawHmac = sha1Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(rawHmac);

        // 3. URL 인코딩 처리 (특수문자 포함 대비)
        String encodedTimestamp = URLEncoder.encode(zuluTimestamp, StandardCharsets.UTF_8);
        String encodedSignature = URLEncoder.encode(signature, StandardCharsets.UTF_8);

        // 4. timestamp 파라미터를 포함한 최종 URL 구성
        String url = String.format(apiUrl, ACCESS_KEY, encodedSignature, alpha2.getAlpha2Code(), year, encodedTimestamp);
        log.debug("Requesting URL: {}", url);

        return url;
    }
}
