package ru.practicum.compilation.mapper;

import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.NewCompilationDto;

public class CompilationMapper {

    public static CompilationDto fromCompilationToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .events(null)
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .build();
    }

    public static Compilation fromNewCompilationDtoToCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .id(null)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }
}