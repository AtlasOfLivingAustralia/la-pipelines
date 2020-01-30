package au.org.ala.pipelines.beam;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility to show that singleton objects work with both Direct and embedded spark runners.
 * (For a distributed Spark, a cache would be created per executor)
 *
 * This creates an input of a list of integers 1,2,3...100.
 * It then calls a singleton object simulating a cache lookup.
 * On completion it then reports how often a lookup was reported on the cache.
 *
 * To run Direct:
 *   java -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.ExampleSingleton
 *
 * To run Spark:
 *   java -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.ExampleSingleton --runner=SparkRunner
 *
 */
public class ExampleSingleton {

  public static void main(String[] args) {
    PipelineOptions options = PipelineOptionsFactory.fromArgs(args).create();
    Pipeline p = Pipeline.create(options);

    // create a list of integers
    List<Integer> range = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());
    PCollection<Integer> input = p.apply(Create.of(range).withCoder(BigEndianIntegerCoder.of()));
    PCollection<Integer> output = input.apply(ParDo.of(new Lookup()));

    p.run().waitUntilFinish();

    System.out.println("Number of times the cache was called: " + Cache.getInstance().getCount());
  }

  // use a singleton cache
  static class Lookup extends DoFn<Integer, Integer> {
    private transient Cache cache;

    @Setup
    public void setup() {
      System.out.println("Setup");
      if (cache == null) cache = Cache.getInstance();
    }

    @StartBundle
    public void startBundle() {
      System.out.println("Start bundle");
    }

    @ProcessElement
    public void processElement(@Element Integer in, OutputReceiver<Integer> out) {
      out.output(cache.lookup(in));
    }

    @FinishBundle
    public void finishBundle() {
      System.out.println("Finish bundle");
    }

    @Teardown
    public void teardown() {
      System.out.println("Teardown");
    }

  }

  // fake a singleton cache
  static class Cache {

    private static volatile Cache instance;
    private AtomicInteger counter = new AtomicInteger();

    static synchronized Cache getInstance() {
      if (instance == null) {
        instance = new Cache();
      }
      return instance;
    }

    private Cache () {}

    int lookup(int value) {
      // don't lookup but return the input and increment the number of times we've been called
      counter.addAndGet(1);
      System.out.println(value + " counter: " + counter.get());
      return value;
    }


    int getCount() {
      return counter.get();
    }

  }

}
