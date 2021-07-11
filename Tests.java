package module_25.MemoryManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    @Test
    public void testCore() throws NotEnoughMemoryException {
        final MemoryManager memManager = new MemoryManager(20);
        assertEquals(0, memManager.malloc(10));
        System.out.println(memManager);
        assertEquals(10, memManager.malloc(5));
        System.out.println(memManager);
        assertEquals(-1, memManager.free(4));
        System.out.println(memManager);
        assertEquals(-1, memManager.malloc(15));
        System.out.println(memManager);
    }

    @Test
    public void testMemoryConjunction() throws NotEnoughMemoryException {
        final MemoryManager memManager = new MemoryManager(20);

        System.out.println("malloc memory chunks sizes: 3, 5, 5");
        assertEquals(0, memManager.malloc(3));
        assertEquals(3, memManager.malloc(5));
        assertEquals(8, memManager.malloc(5));
        System.out.println(memManager);

        System.out.println("free memory 2 chunks: at 3 and 0, expect free chunk merge: 0..8");
        assertEquals(3, memManager.free(3));
        assertEquals(0, memManager.free(0));
        System.out.print("Try allocate 10 = false: cant allocate > size of last emptied chunk (8)");
        assertEquals(-1, memManager.malloc(10));
        System.out.print("Try allocate 4 = true");
        assertEquals(0, memManager.malloc(4));
        System.out.println(memManager);


    }

}
