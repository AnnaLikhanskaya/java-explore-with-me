import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class StatsClientTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    @Mock
    private RestTemplate restTemplate;

    private StatsClient statsClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        statsClient = new StatsClient("http://stats-server.url", new RestTemplateBuilder());
        statsClient.rest = restTemplate;
    }

    @Test
    public void testPostHit() {
        String app = "testApp";
        String uri = "/testUri";
        String ip = "127.0.0.1";
        LocalDateTime timestamp = LocalDateTime.now();

        when(restTemplate.postForEntity(eq("/hit"), any(EndpointHitDto.class), eq(EndpointHitDto.class)))
                .thenReturn(ResponseEntity.ok(null));

        statsClient.postHit(app, uri, ip, timestamp);

        verify(restTemplate, times(1)).postForEntity(eq("/hit"), any(EndpointHitDto.class), eq(EndpointHitDto.class));
    }

    @Test
    public void testGetStatsWithAllParameters() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/testUri");
        Boolean unique = true;

        List<ViewStatsDto> expectedStats = List.of(new ViewStatsDto("testApp", "/testUri", 1L));

        when(restTemplate.exchange(
                eq("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class),
                anyMap()
        )).thenReturn(ResponseEntity.ok(expectedStats));

        List<ViewStatsDto> actualStats = statsClient.getStats(start, end, uris, unique);

        assertEquals(expectedStats, actualStats);
        verify(restTemplate, times(1)).exchange(
                eq("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class),
                anyMap()
        );
    }

    @Test
    public void testGetStatsWithUrisAndUnique() {
        List<String> uris = List.of("/testUri");
        Boolean unique = true;

        List<ViewStatsDto> expectedStats = List.of(new ViewStatsDto("testApp", "/testUri", 1L));

        when(restTemplate.exchange(
                eq("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class),
                anyMap()
        )).thenReturn(ResponseEntity.ok(expectedStats));

        List<ViewStatsDto> actualStats = statsClient.getStats(uris, unique);

        assertEquals(expectedStats, actualStats);
        verify(restTemplate, times(1)).exchange(
                eq("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class),
                anyMap()
        );
    }
}