package net.regnology.lucy.service.helper.differenceView;

import java.util.List;
import java.util.stream.Collectors;

public class Operations<T> {

    /**
     * Get the intersection of two lists.
     *
     * @param a First list
     * @param b Second list
     * @return A new list with elements from the intersection.
     */
    public List<T> intersection(List<T> a, List<T> b) {
        return a.stream().filter(b::contains).collect(Collectors.toList());
    }

    /**
     *
     * @param a First list
     * @param b Second list
     * @return A new list with elements from the difference.
     */
    public List<T> difference(List<T> a, List<T> b) {
        return a.stream().filter(element -> !b.contains(element)).collect(Collectors.toList());
    }
}
