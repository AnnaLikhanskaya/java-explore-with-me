package ru.practicum.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationDto;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c " +
            "FROM Compilation c " +
            "WHERE (:pinned IS NULL OR c.pinned = :pinned) " +
            "ORDER BY c.id")
    List<Compilation> findAllCompilations(@Param("pinned") Boolean pinned, Pageable pageable);

    @Query("SELECT new ru.practicum.compilation.model.CompilationDto(c.id, c.pinned, c.title) " +
            "FROM Compilation c " +
            "WHERE c.id = :compId")
    Optional<CompilationDto> findCompilationById(@Param("compId") Long compId);

}
