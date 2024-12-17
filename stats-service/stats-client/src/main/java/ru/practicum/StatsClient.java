package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.exception.GeneralException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient {

    public RestTemplate rest;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public void postHit(String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHitDto hit = new EndpointHitDto();
        hit.setApp(app);
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setTimestamp(timestamp.format(formatter));
        try {
            rest.postForEntity("/hit", hit, EndpointHitDto.class);
        } catch (Exception e) {
            throw new GeneralException("Ошибка при отправке запроса: " + e.getMessage());
        }
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "uris", uris,
                "unique", unique
        );
        try {
            return rest.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ViewStatsDto>>() {
                    }, parameters).getBody();
        } catch (Exception e) {
            throw new GeneralException("Ошибка при получении статистики: " + e.getMessage());
        }
    }

    public List<ViewStatsDto> getStats(List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", LocalDateTime.now().minusYears(5).format(formatter),
                "end", LocalDateTime.now().plusYears(5).format(formatter),
                "uris", uris,
                "unique", unique
        );
        try {
            return rest.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ViewStatsDto>>() {
                    }, parameters).getBody();
        } catch (Exception e) {
            throw new GeneralException("Ошибка при получении статистики: " + e.getMessage());
        }
    }
}
