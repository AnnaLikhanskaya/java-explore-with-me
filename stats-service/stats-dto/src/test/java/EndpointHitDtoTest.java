import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class EndpointHitDtoTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNoArgsConstructor() {
        EndpointHitDto dto = new EndpointHitDto();
        assertThat(dto).isNotNull();
    }

    @Test
    public void testAllArgsConstructor() {
        Long id = 1L;
        String app = "testApp";
        String uri = "/testUri";
        String ip = "127.0.0.1";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        EndpointHitDto dto = new EndpointHitDto(id, app, uri, ip, timestamp);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getApp()).isEqualTo(app);
        assertThat(dto.getUri()).isEqualTo(uri);
        assertThat(dto.getIp()).isEqualTo(ip);
        assertThat(dto.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    public void testSettersAndGetters() {
        EndpointHitDto dto = new EndpointHitDto();
        Long id = 1L;
        String app = "testApp";
        String uri = "/testUri";
        String ip = "127.0.0.1";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        dto.setId(id);
        dto.setApp(app);
        dto.setUri(uri);
        dto.setIp(ip);
        dto.setTimestamp(timestamp);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getApp()).isEqualTo(app);
        assertThat(dto.getUri()).isEqualTo(uri);
        assertThat(dto.getIp()).isEqualTo(ip);
        assertThat(dto.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    public void testValidation() {
        EndpointHitDto dto = new EndpointHitDto();
        Set<ConstraintViolation<EndpointHitDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(4);
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("app"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("uri"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("ip"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("timestamp"))).isTrue();
    }

    @Test
    public void testValidDto() {
        String app = "testApp";
        String uri = "/testUri";
        String ip = "127.0.0.1";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        EndpointHitDto dto = new EndpointHitDto(null, app, uri, ip, timestamp);
        Set<ConstraintViolation<EndpointHitDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
