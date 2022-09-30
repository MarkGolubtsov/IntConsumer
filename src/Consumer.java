import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

public class Consumer {
    private final int DELAY_MINUTES = 5;

    // Multithreading: Replace TreeMap -> ConcurrentSkipListMap or Collections.synchronizedMap (TreeMap)
    // int[] -> Collections.synchronizedList (List)
    private final TreeMap<Long, int[]> storage = new TreeMap<>();

    public void accept(int number) {
        clearRedundantElements();

        long now = new Date().getTime();

        if (storage.containsKey(now)) {
            storage.replace(now, addNumberToArray(number, storage.get(now)));
        } else {
            storage.put(now, new int[]{number});
        }
    }

    private int[] addNumberToArray(int number, int[] prevArray) {
        int prevArrayLength = prevArray.length;

        int [] numbersCopy = new int[prevArrayLength + 1];
        System.arraycopy(prevArray, 0, numbersCopy, 0, prevArrayLength);
        numbersCopy[prevArrayLength] = number;

        return numbersCopy;
    }

    public double mean() {
        clearRedundantElements();
        return getAverageFromMap(storage);
    }

    private void clearRedundantElements() {
        Date olderRangeDate = getOlderRangeDate();
        storage.headMap(olderRangeDate.getTime());
    }

    private Date getOlderRangeDate() {
       return new Date(System.currentTimeMillis() - (long) this.DELAY_MINUTES * 60 * 1000);
    }

    private double getAverageFromMap(Map<Long, int[]> dataMap) {
        return dataMap.values().stream()
                .flatMapToInt(IntStream::of)
                .average()
                .orElse(0.0);
    }
}
