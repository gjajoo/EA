package eachecks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.openjdk.jmh.annotations.Mode.AverageTime;

@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ArrayLoop {
    private static Random s_r = new Random();

    private static int next() { return s_r.nextInt() % 1000; }

    // simple loop based sum, can be any variation - for(i:arr), for(i=0; i<... etc...
    static int loop(int... arr) {
        int sum = 0;
        for (int i = arr.length - 1; i >= 0; sum += arr[i--]) { ; }
        return sum;
    }

    @Benchmark
    public void loopSum(Blackhole bh) {
        bh.consume(loop(next(), next()));
    }

    // silly manually unrolled loop
    static int unrolled(int... arr) {
        int sum = 0;
        switch (arr.length) {
            default: for (int i = arr.length - 1; i >= 4; sum += arr[i--]) { ; }
            case 4: sum += arr[3];
            case 3: sum += arr[2];
            case 2: sum += arr[1];
            case 1: sum += arr[0];
        }
        return sum;
    }

    @Benchmark
    public void unrolledSum(Blackhole bh) {
        bh.consume(unrolled(next(), next()));
    }

    class ArrayWrapper {
        final int[] arr;
        ArrayWrapper(int... many) { arr = many; }
        int loopSum() { return ArrayLoop.loop(arr); }
        int unrolledSum() { return ArrayLoop.unrolled(arr); }
    }

    @Benchmark
    public void wrappedUnrolledSum(Blackhole bh) {
        bh.consume(new ArrayWrapper(next(), next()).unrolledSum());
    }

    @Benchmark
    public void wrappedLoopSum(Blackhole bh) {
        bh.consume(new ArrayWrapper(next(), next()).loopSum());
    }
}
