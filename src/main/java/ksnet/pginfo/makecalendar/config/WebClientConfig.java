package ksnet.pginfo.makecalendar.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.List;

/**
 * WebClient 통신시 TLS 1.2를 명시적으로 적용하고, 요청과 응답을 통합적으로 로깅하는 설정 클래스입니다.
 * - TLS 1.2 적용: Netty HttpClient를 사용하여 SSLContext를 구성하고 WebClient에 적용합니다.
 * - 통합 로깅: 요청과 응답 모두에서 헤더를 포함한 상세 정보를 로그로 출력하며, 민감한 정보는 maskingKeys 리스트에 따라 마스킹 처리합니다.
 * - maskingKeys 프로퍼티: application.properties에서 "ksnet.pginfo.logging.masking-keys"로 설정된 키워드를 기준으로 헤더명을 검사하여 마스킹 여부를 결정합니다. 기본값은 "password"입니다.
 * - 로그 레벨: 디버그 레벨로 요청과 응답의 상세 정보를 출력하므로, 실제 운영 환경에서는 로그 레벨을 적절히 조정하여 사용하시기 바랍니다.
 * - 예외 처리: SSLException이 발생할 수 있으므로, WebClient 빈 생성 시 예외 처리를 고려해야 합니다.
 * - 확장성: 필요에 따라 로깅 필터를 확장하여 요청 바디나 응답 바디도 로깅할 수 있도록 개선할 수 있습니다.
 */

@Slf4j
@Configuration
public class WebClientConfig {
    // 프로퍼티에서 리스트 형태로 주입 (값이 없을 경우를 대비해 기본값 설정)
    @Value("${ksnet.pginfo.logging.masking-keys:password}") private List<String> maskingKeys;
    @Value("${spring.profiles.active:local}") private String env;

    @Bean
    public WebClient webClient(WebClient.Builder builder) throws SSLException {
        // 1. TLS 1.2 설정을 포함한 SslContext 생성
        SslContext sslContext = SslContextBuilder.forClient()
                .protocols("TLSv1.2") // TLS 1.2 명시적 지정
                .build();

        // 2. Netty HttpClient 설정 (TLS 적용)
        HttpClient httpClient = HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

        // 3. WebClient 구성 및 로깅 필터 추가
        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logFilter()) // 요청/응답 통합 로깅 필터
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB로 확장
                .build();
    }

    private ExchangeFilterFunction logFilter() {
        return (request, next) -> {
            // [요청 로그]
            log.debug(">>>> [WebClient Request] {} {}", request.method(), request.url());
            request.headers().forEach((name, values) ->
                    values.forEach(value -> {
                        String lowerName = name.toLowerCase();

                        // maskingKeys 리스트 중 하나라도 헤더명에 포함되어 있는지 확인
                        boolean isSensitive = !"dev".equals(env) && maskingKeys.stream()
                                .anyMatch(key -> lowerName.contains(key.trim().toLowerCase()));
                        if (isSensitive) {
                            log.debug(">>>> [WebClient Request] Header: {} = ********", name);
                        } else {
                            log.debug(">>>> [WebClient Request] Header: {} = {}", name, value);
                        }
                    })
            );
            return next.exchange(request).doOnNext(response -> {
                // [응답 로그]
                log.debug("<<<< [WebClient Response] Status: {} URL: {}", response.statusCode(), request.url());
                response.headers().asHttpHeaders().forEach((name, values) ->
                        values.forEach(value -> log.debug("<<<< [WebClient Response] Response Header: {}={}", name, value)));
            });
        };
    }
}
