import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.DatabaseException;
import ru.practicum.exception.GeneralException;
import ru.practicum.model.Stats;
import ru.practicum.repository.StatsRepository;
import ru.practicum.service.StatsServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StatsServiceImplTest {

    @Mock
    private StatsRepository statsRepository;

    @InjectMocks
    private StatsServiceImpl statsService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testGetStatsWithUniqueAndUris() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/testUri");
        boolean unique = true;

        List<ViewStatsDto> expectedStats = List.of(new ViewStatsDto("testApp", "/testUri", 1L));

        when(statsRepository.findAllStatsWithUniqueAndUris(any(LocalDateTime.class), any(LocalDateTime.class), any(List.class)))
                .thenReturn(expectedStats);

        List<ViewStatsDto> actualStats = statsService.getStats(start, end, uris, unique);

        assertThat(actualStats).isEqualTo(expectedStats);
        verify(statsRepository).findAllStatsWithUniqueAndUris(any(LocalDateTime.class), any(LocalDateTime.class), any(List.class));
    }

    @Test
    public void testGetStatsWithUnique() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = null;
        boolean unique = true;

        List<ViewStatsDto> expectedStats = List.of(new ViewStatsDto("testApp", "/testUri", 1L));

        when(statsRepository.findAllStatsWithUnique(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedStats);

        List<ViewStatsDto> actualStats = statsService.getStats(start, end, uris, unique);

        assertThat(actualStats).isEqualTo(expectedStats);
        verify(statsRepository).findAllStatsWithUnique(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testGetStatsWithoutUnique() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = null;
        boolean unique = false;

        List<ViewStatsDto> expectedStats = List.of(new ViewStatsDto("testApp", "/testUri", 1L));

        when(statsRepository.findAllStats(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedStats);

        List<ViewStatsDto> actualStats = statsService.getStats(start, end, uris, unique);

        assertThat(actualStats).isEqualTo(expectedStats);
        verify(statsRepository).findAllStats(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testGetStatsWithUris() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/testUri");
        boolean unique = false;

        List<ViewStatsDto> expectedStats = List.of(new ViewStatsDto("testApp", "/testUri", 1L));

        when(statsRepository.findAllStatsWithUris(any(LocalDateTime.class), any(LocalDateTime.class), any(List.class)))
                .thenReturn(expectedStats);

        List<ViewStatsDto> actualStats = statsService.getStats(start, end, uris, unique);

        assertThat(actualStats).isEqualTo(expectedStats);
        verify(statsRepository).findAllStatsWithUris(any(LocalDateTime.class), any(LocalDateTime.class), any(List.class));
    }

    @Test
    public void testGetStatsException() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/testUri");
        boolean unique = true;

        when(statsRepository.findAllStatsWithUniqueAndUris(any(LocalDateTime.class), any(LocalDateTime.class), any(List.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseException.class, () -> statsService.getStats(start, end, uris, unique));
    }

    @Test
    public void testPostHit() {
        EndpointHitDto hitDto = new EndpointHitDto(null, "testApp", "/testUri", "127.0.0.1", LocalDateTime.now().format(formatter));

        statsService.postHit(hitDto);

        verify(statsRepository).save(any(Stats.class));
    }

    @Test
    public void testPostHitException() {
        EndpointHitDto hitDto = new EndpointHitDto(null, "testApp", "/testUri", "127.0.0.1", LocalDateTime.now().toString());

        doThrow(new RuntimeException("Database error")).when(statsRepository).save(any(Stats.class));

        assertThrows(GeneralException.class, () -> statsService.postHit(hitDto));
    }
}
