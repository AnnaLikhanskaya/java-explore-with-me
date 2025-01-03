package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.Stats;

public class StatsMapper {
    public static Stats fromEndpointHitDtoToEndpointHit(EndpointHitDto endpointHitDto) {
        return Stats.builder()
                .id(null)
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .app(endpointHitDto.getApp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }

    public static EndpointHitDto fromEndpointHitToEndpointHitDto(Stats stats) {
        return EndpointHitDto.builder()
                .uri(stats.getUri())
                .ip(stats.getIp())
                .app(stats.getApp())
                .timestamp(stats.getTimestamp())
                .build();
    }
}