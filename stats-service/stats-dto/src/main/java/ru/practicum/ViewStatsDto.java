package ru.practicum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {
    @NotNull
    private String app;
    @NotNull
    private String uri;
    @NotNull
    private Long hits;
}