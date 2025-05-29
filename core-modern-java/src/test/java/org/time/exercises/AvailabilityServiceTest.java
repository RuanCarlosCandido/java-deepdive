package org.time.exercises;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AvailabilityServiceTest {

    private AvailabilityService service;
    private ZoneId zoneNY;
    private ZoneId zoneTokyo;

    @BeforeEach
    void setup() {
        service = new AvailabilityService();
        zoneNY = ZoneId.of("America/New_York");
        zoneTokyo = ZoneId.of("Asia/Tokyo");
    }

    @Test
    void shouldReturnAvailableSlotsConsideringTimeZones() {
        LocalDate date = LocalDate.of(2025, 6, 10); // Terça-feira
        List<TimeSlot> slots = service.getAvailableSlots("user123", date, zoneNY, zoneTokyo);
        assertFalse(slots.isEmpty(), "Slots devem existir ao considerar fusos NY -> Tóquio");
    }

    @Test
    void shouldHandleUnavailableDaysGracefullyUsingOptional() {
        LocalDate domingo = LocalDate.of(2025, 6, 8); // Domingo
        Optional<List<TimeSlot>> maybeSlots = service.getOptionalAvailability("user123", domingo);
        assertTrue(maybeSlots.isEmpty(), "Domingo não deveria retornar disponibilidade");
    }

    @Test
    void shouldRespectUserLocaleForDayNames() {
        Locale br = Locale.of("pt", "BR");
        String tuesdayPt = service.getDayOfWeekName(DayOfWeek.TUESDAY, br);
        assertEquals("terça-feira", tuesdayPt);
    }

    @Test
    void shouldFormatAvailabilitySummaryLocalized() {
        Locale localeUs = Locale.US;
        Locale localeFr = Locale.FRANCE;
        LocalDate date = LocalDate.of(2025, 6, 11);

        String summaryUs = service.getLocalizedSummary("user123", date, localeUs);
        String summaryFr = service.getLocalizedSummary("user123", date, localeFr);

        assertTrue(summaryUs.contains("Wednesday"));
        assertTrue(summaryFr.contains("mercredi"));
    }

    @Test
    void shouldExcludeHolidaysFromAvailability() {
        LocalDate holiday = LocalDate.of(2025, 12, 25);
        List<TimeSlot> slots = service.getAvailableSlots("user123", holiday, zoneNY, zoneNY);
        assertTrue(slots.isEmpty(), "Feriado deve ter agenda vazia");
    }

    @Test
    void shouldSupportPartialAvailabilityUsingLambdas() {
        LocalDate sexta = LocalDate.of(2025, 6, 13); // Sexta-feira
        List<TimeSlot> slots = service.getAvailableSlots("user123", sexta, zoneNY, zoneNY);

        boolean allMorning = slots.stream()
            .allMatch(slot -> slot.start().getHour() < 12);

        assertTrue(allMorning, "Na sexta-feira, o usuário só atende de manhã");
    }

    @Test
    void shouldHandleEmptyUserGracefully() {
        assertThrows(IllegalArgumentException.class, () ->
            service.getAvailableSlots(null, LocalDate.now(), zoneNY, zoneNY)
        );
    }

    @Test
    void shouldSortSlotsChronologically() {
        LocalDate date = LocalDate.of(2025, 6, 12);
        List<TimeSlot> slots = service.getAvailableSlots("user123", date, zoneNY, zoneNY);

        List<ZonedDateTime> startTimes = slots.stream()
                .map(TimeSlot::start)
                .toList();

        List<ZonedDateTime> sorted = new ArrayList<>(startTimes);
        sorted.sort(Comparator.naturalOrder());

        assertEquals(sorted, startTimes, "Os horários devem estar ordenados cronologicamente");
    }

    @Test
    void shouldHandleLeapYearDatesCorrectly() {
        LocalDate leapDay = LocalDate.of(2028, 2, 29);
        Optional<List<TimeSlot>> slots = service.getOptionalAvailability("user123", leapDay);
        assertTrue(slots.isPresent(), "Leap day deve ser tratado corretamente como dia válido");
    }

    @Test
    void shouldRespectDSTTransitions() {
        // Horário de verão termina em 3 de novembro de 2025 nos EUA
        LocalDate dstDate = LocalDate.of(2025, 11, 3);
        List<TimeSlot> slots = service.getAvailableSlots("user123", dstDate, zoneNY, zoneNY);
        assertFalse(slots.isEmpty(), "DST não deve impedir cálculo correto de slots");
    }
}
