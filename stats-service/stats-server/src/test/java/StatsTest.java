import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class StatsTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNoArgsConstructor() {
        Stats stats = new Stats();
        assertThat(stats).isNotNull();
    }

    @Test
    public void testAllArgsConstructor() {
        Long id = 1L;
        String app = "testApp";
        String uri = "/testUri";
        String ip = "127.0.0.1";
        LocalDateTime timestamp = LocalDateTime.now();

        Stats stats = new Stats(id, app, uri, ip, timestamp);

        assertThat(stats.getId()).isEqualTo(id);
        assertThat(stats.getApp()).isEqualTo(app);
        assertThat(stats.getUri()).isEqualTo(uri);
        assertThat(stats.getIp()).isEqualTo(ip);
        assertThat(stats.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    public void testBuilder() {
        Long id = 1L;
        String app = "testApp";
        String uri = "/testUri";
        String ip = "127.0.0.1";
        LocalDateTime timestamp = LocalDateTime.now();

        Stats stats = Stats.builder()
                .id(id)
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp)
                .build();

        assertThat(stats.getId()).isEqualTo(id);
        assertThat(stats.getApp()).isEqualTo(app);
        assertThat(stats.getUri()).isEqualTo(uri);
        assertThat(stats.getIp()).isEqualTo(ip);
        assertThat(stats.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    public void testSettersAndGetters() {
        Stats stats = new Stats();
        Long id = 1L;
        String app = "testApp";
        String uri = "/testUri";
        String ip = "127.0.0.1";
        LocalDateTime timestamp = LocalDateTime.now();

        stats.setId(id);
        stats.setApp(app);
        stats.setUri(uri);
        stats.setIp(ip);
        stats.setTimestamp(timestamp);

        assertThat(stats.getId()).isEqualTo(id);
        assertThat(stats.getApp()).isEqualTo(app);
        assertThat(stats.getUri()).isEqualTo(uri);
        assertThat(stats.getIp()).isEqualTo(ip);
        assertThat(stats.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    public void testValidation() {
        Stats stats = new Stats();
        Set<ConstraintViolation<Stats>> violations = validator.validate(stats);

        assertThat(violations).hasSize(4);
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("app"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("uri"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("ip"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("timestamp"))).isTrue();
    }

    @Test
    public void testValidStats() {
        String app = "testApp";
        String uri = "/testUri";
        String ip = "127.0.0.1";
        LocalDateTime timestamp = LocalDateTime.now();

        Stats stats = Stats.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp)
                .build();

        Set<ConstraintViolation<Stats>> violations = validator.validate(stats);

        assertThat(violations).isEmpty();
    }
}