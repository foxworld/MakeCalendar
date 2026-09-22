package ksnet.pginfo.makecalendar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ksnet.pginfo.makecalendar.utils.CountryCode;
import ksnet.pginfo.makecalendar.utils.DateTimeUtils;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
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

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Holiday> getHolidays(String countryAlpha3, int year) throws Exception {
        List<Holiday> holidays = new ArrayList<>();

        try {
            String url = getApiUrl(countryAlpha3, year); // URL 생성 및 로깅
            log.info("Fetching public holidays from API: {}", url);
            String responseBody = callApi(url);
            log.info("Received response from API: {}", responseBody);
            if (!StringUtils.hasText(responseBody) || responseBody.trim().isEmpty()) {
                return holidays;
            }
            JsonNode rootNode = mapper.readTree(responseBody);
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

    private String getApiUrl(String countryCode, int year) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
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
        String encodedTimestamp = URLEncoder.encode(zuluTimestamp, String.valueOf(StandardCharsets.UTF_8));
        String encodedSignature = URLEncoder.encode(signature, String.valueOf(StandardCharsets.UTF_8));

        // 4. timestamp 파라미터를 포함한 최종 URL 구성
        String url = String.format(apiUrl, ACCESS_KEY, encodedSignature, alpha2.getAlpha2Code(), year, encodedTimestamp);
        log.debug("Requesting URL: {}", url);

        return url;
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
