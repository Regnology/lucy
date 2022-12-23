package net.regnology.lucy.domain.statistics;

public class CountOccurrences {

    private String name;

    private long value;

    public CountOccurrences(String name, long counter) {
        this.name = name;
        this.value = counter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
