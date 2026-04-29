package com.aurelio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeaderboardStore {
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private final Path filePath;

    public LeaderboardStore() {
        this(Path.of("leaderboard.txt"));
    }

    public LeaderboardStore(Path filePath) {
        this.filePath = filePath;
    }

    public List<LeaderboardEntry> loadTop(int limit) {
        List<LeaderboardEntry> entries = loadAll();
        entries.sort(
                Comparator.comparingInt(LeaderboardEntry::getScore).reversed()
                        .thenComparing(LeaderboardEntry::getDate, Comparator.reverseOrder())
        );

        if (entries.size() <= limit) {
            return entries;
        }

        return new ArrayList<>(entries.subList(0, limit));
    }

    public void addScore(int score) {
        List<LeaderboardEntry> entries = loadAll();
        entries.add(new LeaderboardEntry(LocalDate.now(), score));
        saveAll(entries);
    }

    private List<LeaderboardEntry> loadAll() {
        ensureFileExists();

        List<LeaderboardEntry> entries = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length != 2) {
                    continue;
                }

                try {
                    LocalDate date = LocalDate.parse(parts[0], FILE_DATE_FORMAT);
                    int score = Integer.parseInt(parts[1]);
                    entries.add(new LeaderboardEntry(date, score));
                } catch (RuntimeException ignored) {
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read leaderboard file.", e);
        }

        return entries;
    }

    private void saveAll(List<LeaderboardEntry> entries) {
        ensureFileExists();

        List<String> lines = new ArrayList<>();

        for (LeaderboardEntry entry : entries) {
            lines.add(
                    entry.getDate().format(FILE_DATE_FORMAT) + "|" +
                            entry.getScore()
            );
        }

        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write leaderboard file.", e);
        }
    }

    private void ensureFileExists() {
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create leaderboard file.", e);
        }
    }
}