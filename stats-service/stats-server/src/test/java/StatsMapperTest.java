import org.junit.jupiter.api.Test;
import ru.practicum.EndpointHitDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StatsMapperTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testToStats() {
        String app = "testApp";
        String uri = "/testUri";
        String ip = "127.0.0.1";
        String timestamp = LocalDateTime.now().format(formatter);

        EndpointHitDto dto = new EndpointHitDto(null, app, uri, ip, timestamp);
        Stats stats = StatsMapper.toStats(dto);

        assertThat(stats.getApp()).isEqualTo(app);
        assertThat(stats.getUri()).isEqualTo(uri);
        assertThat(stats.getIp()).isEqualTo(ip);
        assertThat(stats.getTimestamp()).isEqualTo(LocalDateTime.parse(timestamp, formatter));
    }

    @Test
    public void testToStatsWithNullValues() {
        String app = null;
        String uri = null;
        String ip = null;
        String timestamp = null;

        EndpointHitDto dto = new EndpointHitDto(null, app, uri, ip, timestamp);

        assertThatThrownBy(() -> StatsMapper.toStats(dto))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testToStatsWithInvalidTimestamp() {
        String app = "testApp";
        String uri = "/testUri";
        String ip = "127.0.0.1";
        String invalidTimestamp = "invalid-timestamp";

        EndpointHitDto dto = new EndpointHitDto(null, app, uri, ip, invalidTimestamp);

        assertThatThrownBy(() -> StatsMapper.toStats(dto))
                .isInstanceOf(DateTimeParseException.class);
    }
}