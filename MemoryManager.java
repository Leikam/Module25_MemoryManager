package module_25.MemoryManager;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

public class MemoryManager implements IMemoryManager {

    private final int[] memory;
    private final int capacity;
    private final Stack<MemoryRecord> allocatedStack = new Stack<>();
    private final Stack<MemoryRecord> freeStack = new Stack<>();

    public MemoryManager(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Память не может быть меньше 1 байта");
        }
        this.capacity = capacity;
        this.memory = new int[capacity];
        freeStack.push(new MemoryRecord(0, capacity));
    }

    static class MemoryRecord {
        private int start;  // included
        private int end;    // excluded
        private int length;

        public MemoryRecord(int start, int length) {
            this.start = start;
            this.length = length;
            this.end = start + length;
        }

        @Override
        public String toString() {
            return "Record {" + start + ".." + end + ", size=" + length + "}";
        }
    }


    @Override
    public int malloc(int n) throws NotEnoughMemoryException {
        if (n > capacity) {
            throw new NotEnoughMemoryException();
        }

        int index = -1;

        final MemoryRecord topFreeRecord = freeStack.peek();
        if (topFreeRecord != null && topFreeRecord.length >= n) {
            final MemoryRecord slice = freeStack.pop();
            final int start = index = slice.start;
            allocateInner(start, n);
            final int diff = slice.length - n;
            if (diff > 0) {
                freeInner(start + n, diff);
            }
        }

        System.out.println("\nmalloc: " + n);
        return index;
    }

    @Override
    public int free(int i) {
        System.out.println("\nfree from: " + i);
        int index = -1;

        if (!allocatedStack.isEmpty()) {
            final Iterator<MemoryRecord> itr = allocatedStack.iterator();
            while (itr.hasNext()) {
                final MemoryRecord next = itr.next();
                if (next.start == i) {
                    index = i;
                    itr.remove();
                    markSweepMemory(next);
                    freeInner(next.start, next.length);
                    break;
                }
            }
        }

        return index;
    }

    private void markSweepMemory(MemoryRecord slice) {
        boolean leftMerge = false;
        boolean rightMerge = false;

        final Iterator<MemoryRecord> itr = freeStack.iterator();
        while (itr.hasNext()) {
            final MemoryRecord next = itr.next();
            boolean merged = false;

            if (!leftMerge && next.end == slice.start) {
                slice.start = next.start;
                merged = leftMerge = true;
            } else if (!rightMerge && next.start == slice.end) {
                slice.end = next.end;
                merged = rightMerge = true;
            }

            if (merged) {
                slice.length += next.length;
                itr.remove();
            }

            if (leftMerge && rightMerge) {
                break;
            }
        }
    }

    private void allocateInner(int start, int length) {
        markMemory(start, length, true);
        allocatedStack.push(new MemoryRecord(start, length));
    }

    private void markMemory(int start, int length, boolean write) {
        for (int i = start; i < start + length; i++) {
            memory[i] = write ? 1 : 0;
        }
    }

    private void freeInner(int start, int length) {
        markMemory(start, length, false);
        freeStack.push(new MemoryRecord(start, length));
    }


    @Override
    public String toString() {
        return "MemoryManager{" +
               "capacity=" + capacity +
               ", allocatedMemStack=" + allocatedStack +
               ", freeMemStack=" + freeStack +
               "; \nmemory=" + Arrays.toString(memory) +
               '}';
    }
}
