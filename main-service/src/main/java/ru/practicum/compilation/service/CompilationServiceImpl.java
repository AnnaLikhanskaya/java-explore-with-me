package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventShortDto;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        if (from < 0) {
            throw new BadRequestException("Параметр запроса 'from' должен быть больше 0, текущее значение from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Параметр запроса 'size' должен быть больше 0, текущее значение size=" + size);
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilationList = compilationRepository.findAllCompilations(pinned, pageable);
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        if (!compilationList.isEmpty()) {
            compilationDtoList = compilationList.stream().map(CompilationMapper::fromCompilationToCompilationDto).toList();
            for (CompilationDto compilationDto : compilationDtoList) {
                List<Event> eventList = eventRepository.findEventsByCompilationId(compilationDto.getId());
                List<EventShortDto> eventShortDtoList = eventList.stream().map(EventMapper::fromEventToEventShortDto).toList();
                compilationDto.setEvents(eventShortDtoList);
            }
        }
        return compilationDtoList;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        if (compId < 0) {
            throw new BadRequestException("Идентификатор подборки должен быть больше 0, текущее значение compId=" + compId);
        }

        CompilationDto compilationDto = compilationRepository.findCompilationById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id=" + compId + " не найдена"));
        List<Event> eventList = eventRepository.findEventsByCompilationId(compId);
        List<EventShortDto> eventShortDtoList = eventList.stream().map(EventMapper::fromEventToEventShortDto).toList();
        compilationDto.setEvents(eventShortDtoList);
        log.info("Получена подборка с id={}: {}", compId, compilationDto);
        return compilationDto;

    }

}