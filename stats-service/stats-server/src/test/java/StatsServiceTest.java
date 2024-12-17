import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ViewStatsDto;
import ru.practicum.repository.StatsRepository;
import ru.practicum.service.StatsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {

    @Mock
    private StatsRepository statsRepository;

    @InjectMocks
    private StatsServiceImpl statsService;

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

}