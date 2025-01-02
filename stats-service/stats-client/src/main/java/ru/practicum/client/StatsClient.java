package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {
    @Value("${stats.server.url}")
    private String serverUrl;
    public RestTemplate restTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void postHit(EndpointHitDto hit) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl).path("/hit").toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(hit, headers);

        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl).path("/stats")
                .queryParam("start", start.format(formatter))
                .queryParam("end", end.format(formatter))
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .queryParam("ss", start)
                .toUriString();
        ResponseEntity<List<ViewStatsDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {
                });

        return responseEntity.getBody();
    }
}
