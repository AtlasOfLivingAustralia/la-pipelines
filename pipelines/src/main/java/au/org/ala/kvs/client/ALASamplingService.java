package au.org.ala.kvs.client;

import java.io.Closeable;
import java.util.Map;

public interface ALASamplingService extends Closeable {

    Map<String,String> sample(ALASamplingRequest request);
}
