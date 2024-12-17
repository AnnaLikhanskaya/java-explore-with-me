import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.ViewStatsDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewStatsDtoTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNoArgsConstructor() {
        ViewStatsDto dto = new ViewStatsDto();
        assertThat(dto).isNotNull();
    }

    @Test
    public void testAllArgsConstructor() {
        String app = "testApp";
        String uri = "/testUri";
        Long hits = 10L;

        ViewStatsDto dto = new ViewStatsDto(app, uri, hits);

        assertThat(dto.getApp()).isEqualTo(app);
        assertThat(dto.getUri()).isEqualTo(uri);
        assertThat(dto.getHits()).isEqualTo(hits);
    }

    @Test
    public void testBuilder() {
        String app = "testApp";
        String uri = "/testUri";
        Long hits = 10L;

        ViewStatsDto dto = ViewStatsDto.builder()
                .app(app)
                .uri(uri)
                .hits(hits)
                .build();

        assertThat(dto.getApp()).isEqualTo(app);
        assertThat(dto.getUri()).isEqualTo(uri);
        assertThat(dto.getHits()).isEqualTo(hits);
    }

    @Test
    public void testSettersAndGetters() {
        ViewStatsDto dto = new ViewStatsDto();
        String app = "testApp";
        String uri = "/testUri";
        Long hits = 10L;

        dto.setApp(app);
        dto.setUri(uri);
        dto.setHits(hits);

        assertThat(dto.getApp()).isEqualTo(app);
        assertThat(dto.getUri()).isEqualTo(uri);
        assertThat(dto.getHits()).isEqualTo(hits);
    }

    @Test
    public void testValidation() {
        ViewStatsDto dto = new ViewStatsDto();
        Set<ConstraintViolation<ViewStatsDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(3);
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("app"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("uri"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("hits"))).isTrue();
    }

    @Test
    public void testValidDto() {
        String app = "testApp";
        String uri = "/testUri";
        Long hits = 10L;

        ViewStatsDto dto = new ViewStatsDto(app, uri, hits);
        Set<ConstraintViolation<ViewStatsDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}