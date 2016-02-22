package org.codingmatters.ufc.ead.m1.nosql.data.generators.util;

import org.codingmatters.ufc.ead.m1.nosql.data.utils.Randomizer;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vagrant on 2/20/16.
 */
public class RandomizerTest {
    @Test
    public void testNextGaussianInTable() throws Exception {
        Randomizer randomizer = new Randomizer(System.currentTimeMillis());
        Integer [] table = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        HashMap<Integer, AtomicLong> counts = new HashMap<>();
        try {
            for (int i = 0; i < 10000; i++) {
                Integer x = randomizer.nextGaussianFromTable(table);
                if (!counts.containsKey(x)) {
                    counts.put(x, new AtomicLong(0));
                }
                counts.get(x).incrementAndGet();
            }
        } finally {
            System.out.println(counts);
        }
    }
}