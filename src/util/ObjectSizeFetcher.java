package util;

/**
 * Created by CAndres on 5/15/2016.
 */
import java.lang.instrument.Instrumentation;

public class ObjectSizeFetcher {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }
}
