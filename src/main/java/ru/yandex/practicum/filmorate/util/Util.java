package ru.yandex.practicum.filmorate.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Util {
    public final static LocalDate VALIDATION_DATE = LocalDate.of(1895, 12, 28);
    public final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
