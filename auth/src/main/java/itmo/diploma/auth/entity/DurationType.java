package itmo.diploma.auth.entity;

public enum DurationType {
    MONTH(1),
    SIX_MONTHS(6),
    YEAR(12);

    private final int months;

    DurationType(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }
}
