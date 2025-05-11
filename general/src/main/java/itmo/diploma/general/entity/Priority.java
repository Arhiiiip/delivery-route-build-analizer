package itmo.diploma.general.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Priority {

    PRICE,
    TIME;

    public static Priority[] getAllPriorities() {
        return values();
    }

    public static Priority fromString(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
