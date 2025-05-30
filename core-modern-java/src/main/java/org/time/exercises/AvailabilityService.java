package org.time.exercises;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AvailabilityService {

    private static final Set<LocalDate> FERIADOS_FIXOS = Set.of(
            LocalDate.of(2025, 12, 25) // Natal
    );

    private static final Map<String, Map<DayOfWeek, List<LocalTime>>> AGENDA_USUARIOS = Map.of(
            "user123", Map.of(
                    DayOfWeek.MONDAY, List.of(LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0)),
                    DayOfWeek.TUESDAY, List.of(LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0)),
                    DayOfWeek.WEDNESDAY, List.of(LocalTime.of(9, 0), LocalTime.of(13, 0), LocalTime.of(15, 0)),
                    DayOfWeek.THURSDAY, List.of(LocalTime.of(9, 0), LocalTime.of(13, 0), LocalTime.of(15, 0)),
                    DayOfWeek.FRIDAY, List.of(LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0)) // parcial
            )
    // outros usuários podem ser adicionados aqui
    );

    public static boolean isFeriado(LocalDate date) {
        return FERIADOS_FIXOS.contains(date);
    }

    public static boolean isDiaUtil(LocalDate date) {
        return !(date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY);
    }

    public List<TimeSlot> getAvailableSlots(String user, LocalDate date, ZoneId zone1, ZoneId zone2) {
        if (user == null || user.isBlank())
            throw new IllegalArgumentException("Usuário inválido");

        if (isFeriado(date))
            return Collections.emptyList();

        Map<DayOfWeek, List<LocalTime>> agenda = AGENDA_USUARIOS.get(user);
        if (agenda == null)
            return Collections.emptyList();

        List<LocalTime> horarios = agenda.get(date.getDayOfWeek());
        if (horarios == null)
            return Collections.emptyList();

        return horarios.stream()
                .map(hora -> ZonedDateTime.of(date, hora, zone1))
                .map(TimeSlot::new)
                .sorted(Comparator.comparing(TimeSlot::start))
                .toList();
    }

    public Optional<List<TimeSlot>> getOptionalAvailability(String user, LocalDate localDate) {

        if (!isDiaUtil(localDate))
            return Optional.empty();
        return Optional.of(getAvailableSlots(user, localDate, ZoneId.systemDefault(), ZoneId.systemDefault()));
    }

    String getDayOfWeekName(DayOfWeek dayOfWeek, Locale locale) {

        return dayOfWeek.getDisplayName(TextStyle.FULL, locale);

    }

    public String getLocalizedSummary(String string, LocalDate date, Locale locale) {

        StringBuilder builder = new StringBuilder();

        builder.append("day: ");
        builder.append(getDayOfWeekName(date.getDayOfWeek(), locale));

        return builder.toString();

    }

}
