package net.regnology.lucy.domain.statistics;

import java.util.Deque;

public class Series {

    private String name;
    private Deque<CountOccurrences> series;

    public Series(String name, Deque<CountOccurrences> series) {
        this.name = name;
        this.series = series;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Deque<CountOccurrences> getSeries() {
        return series;
    }

    public void setSeries(Deque<CountOccurrences> series) {
        this.series = series;
    }

    /**
     * Add a new {@link CountOccurrences entry} for the series at the beginning.
     *
     * @param name Name for the new CountOccurrences object
     * @param value Value for the new CountOccurrences object
     */
    public void addSeriesEntry(String name, long value) {
        series.addFirst(new CountOccurrences(name, value));
    }
}
