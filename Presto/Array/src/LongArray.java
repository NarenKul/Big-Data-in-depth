package io.prestosql.array;

import io.airlift.slice.SizeOf;
import org.openjdk.jol.info.ClassLayout;

import java.util.Arrays;

import static io.airlift.slice.SizeOf.sizeOfLongArray;
import static io.prestosql.array.BigArrays.INITIAL_SEGMENTS;
import static io.prestosql.array.BigArrays.SEGMENT_SIZE;
import static io.prestosql.array.BigArrays.offset;
import static io.prestosql.array.BigArrays.segment;

public final class LongBigArray
{
    private static final int INSTANCE_SIZE = ClassLayout.parseClass(LongBigArray.class).instanceSize();
    private static final long SIZE_OF_SEGMENT = sizeOfLongArray(SEGMENT_SIZE);

    private final long initialValue;

    private long[][] array;
    private int capacity;
    private int segments;

    /**
     * Creates a new big array containing one initial segment
     */
    public LongBigArray()
    {
        this(0L);
    }

    /**
     * Creates a new big array containing one initial segment filled with the specified default value
     */
    public LongBigArray(long initialValue)
    {
        this.initialValue = initialValue;
        array = new long[INITIAL_SEGMENTS][];
        allocateNewSegment();
    }

    /**
     * Returns the size of this big array in bytes.
     */
    public long sizeOf()
    {
        return INSTANCE_SIZE + SizeOf.sizeOf(array) + (segments * SIZE_OF_SEGMENT);
    }

    /**
     * Returns the element of this big array at specified index.
     *
     * @param index a position in this big array.
     * @return the element of this big array at the specified position.
     */
    public long get(long index)
    {
        return array[segment(index)][offset(index)];
    }

    /**
     * Sets the element of this big array at specified index.
     *
     * @param index a position in this big array.
     */
    public void set(long index, long value)
    {
        array[segment(index)][offset(index)] = value;
    }

    /**
     * Increments the element of this big array at specified index.
     *
     * @param index a position in this big array.
     */
    public void increment(long index)
    {
        array[segment(index)][offset(index)]++;
    }

    /**
     * Adds the specified value to the specified element of this big array.
     *
     * @param index a position in this big array.
     * @param value the value
     */
    public void add(long index, long value)
    {
        array[segment(index)][offset(index)] += value;
    }

    /**
     * Ensures this big array is at least the specified length.  If the array is smaller, segments
     * are added until the array is larger then the specified length.
     */
    public void ensureCapacity(long length)
    {
        if (capacity > length) {
            return;
        }

        grow(length);
    }

    private void grow(long length)
    {
        // how many segments are required to get to the length?
        int requiredSegments = segment(length) + 1;

        // grow base array if necessary
        if (array.length < requiredSegments) {
            array = Arrays.copyOf(array, requiredSegments);
        }

        // add new segments
        while (segments < requiredSegments) {
            allocateNewSegment();
        }
    }

    private void allocateNewSegment()
    {
        long[] newSegment = new long[SEGMENT_SIZE];
        if (initialValue != 0) {
            Arrays.fill(newSegment, initialValue);
        }
        array[segments] = newSegment;
        capacity += SEGMENT_SIZE;
        segments++;
    }
}
