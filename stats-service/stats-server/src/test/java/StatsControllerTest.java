import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsServer;
import ru.practicum.ViewStatsDto;
import ru.practicum.controller.StatsController;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
@ContextConfiguration(classes = StatsServer.class)
@ExtendWith(MockitoExtension.class)
public class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testPostHit() throws Exception {
        EndpointHitDto hitDto = new EndpointHitDto(null, "testApp", "/testUri", "127.0.0.1", LocalDateTime.now().format(formatter));

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hitDto)))
                .andExpect(status().isCreated());

        verify(statsService).postHit(any(EndpointHitDto.class));
    }

    @Test
    public void testGetStats() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/testUri");
        Boolean unique = true;

        List<ViewStatsDto> expectedStats = List.of(new ViewStatsDto("testApp", "/testUri", 1L));

        when(statsService.getStats(any(LocalDateTime.class), any(LocalDateTime.class), any(List.class), any(Boolean.class)))
                .thenReturn(expectedStats);

        mockMvc.perform(get("/stats")
                        .param("start", start.format(formatter))
                        .param("end", end.format(formatter))
                        .param("uris", "/testUri")
                        .param("unique", "true"))
                .andExpect(status().isOk());

        verify(statsService).getStats(any(LocalDateTime.class), any(LocalDateTime.class), any(List.class), any(Boolean.class));
    }
}