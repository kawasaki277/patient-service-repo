package com.clinicbooking.patient.domain;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public record AttendanceSummary(long total, long attended, long noShows, long cancelled, LocalDate lastVisit) {

    public static AttendanceSummary from(List<AttendanceRecord> records) {
        long attended = count(records, AttendanceStatus.ATTENDED);
        long noShows = count(records, AttendanceStatus.NO_SHOW);
        long cancelled = count(records, AttendanceStatus.CANCELLED);
        LocalDate lastVisit = records.stream()
                .filter(r -> r.status() == AttendanceStatus.ATTENDED)
                .map(AttendanceRecord::date)
                .max(Comparator.naturalOrder())
                .orElse(null);
        return new AttendanceSummary(records.size(), attended, noShows, cancelled, lastVisit);
    }

    private static long count(List<AttendanceRecord> records, AttendanceStatus status) {
        return records.stream().filter(r -> r.status() == status).count();
    }
}
