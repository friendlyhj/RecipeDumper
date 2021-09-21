package cn.mcmod.recipedumper.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import org.apache.commons.lang3.ArrayUtils;

import java.util.NoSuchElementException;

/**
 * @author youyihj
 */
public class IntIntersectionHelper {
    private final IntList list;

    public IntIntersectionHelper(int... ints) {
        this.list = new IntArrayList(ints);
    }

    public void addOperatedInts(int... ints) {
        IntListIterator iterator = list.iterator();
        while (iterator.hasNext()) {
            if (!ArrayUtils.contains(ints, iterator.nextInt())) {
                iterator.remove();
            }
        }
    }

    public int getFirstIntersection() {
        try {
            return list.getInt(0);
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }
}
