package com.aurelio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LeaderboardEntry {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH);

    private final LocalDate date;
    private final int score;

    public LeaderboardEntry(LocalDate date, int score) {
        this.date = date;
        this.score = score;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getScore() {
        return score;
    }

    public String getFormattedDate() {
        return date.format(DISPLAY_DATE_FORMAT);
    }
}