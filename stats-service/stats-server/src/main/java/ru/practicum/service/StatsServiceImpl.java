package ru.practicum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.DatabaseException;
import ru.practicum.exception.GeneralException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Stats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {

    private static final Logger log = LoggerFactory.getLogger(StatsServiceImpl.class);

    @Autowired
    private StatsRepository statsRepository;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Получение статистики с параметрами: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        try {
            if (unique) {
                if (uris != null && !uris.isEmpty()) {
                    return statsRepository.findAllStatsWithUniqueAndUris(start, end, uris);
                } else {
                    return statsRepository.findAllStatsWithUnique(start, end);
                }
            } else {
                if (uris != null && !uris.isEmpty()) {
                    return statsRepository.findAllStatsWithUris(start, end, uris);
                } else {
                    return statsRepository.findAllStats(start, end);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при получении статистики: {}", e.getMessage());
            throw new DatabaseException("Ошибка при получении статистики: " + e.getMessage());
        }
    }

    @Override
    public void postHit(EndpointHitDto hitDto) {
        try {
            Stats hit = StatsMapper.toStats(hitDto);
            log.info("Сохранение информации о посещении: {}", hit);
            statsRepository.save(hit);
        } catch (Exception e) {
            log.error("Ошибка при сохранении информации о посещении: {}", e.getMessage());
            throw new GeneralException("Ошибка при сохранении информации о посещении: " + e.getMessage());
        }
    }
}