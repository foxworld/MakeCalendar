package ksnet.pginfo.makecalendar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientService {

    private final WebClient webClient;

    public String callApi(String url) {
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
