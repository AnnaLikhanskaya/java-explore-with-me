package ru.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stats")
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "app", nullable = false, length = 64)
    private String app;

    @NotBlank
    @Column(name = "uri", nullable = false, length = 256)
    private String uri;

    @NotBlank
    @Column(name = "ip", nullable = false, length = 64)
    private String ip;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}