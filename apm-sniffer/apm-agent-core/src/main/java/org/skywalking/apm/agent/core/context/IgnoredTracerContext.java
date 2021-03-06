package org.skywalking.apm.agent.core.context;

import java.util.LinkedList;
import java.util.List;
import org.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.skywalking.apm.agent.core.context.trace.NoopSpan;

/**
 * The <code>IgnoredTracerContext</code> represent a context should be ignored.
 * So it just maintains the stack with an integer depth field.
 *
 * All operations through this will be ignored, and keep the memory and gc cost as low as possible.
 *
 * @author wusheng
 */
public class IgnoredTracerContext implements AbstractTracerContext {
    private static final NoopSpan NOOP_SPAN = new NoopSpan();

    private int stackDepth;

    public IgnoredTracerContext() {
        this.stackDepth = 0;
    }

    @Override
    public void inject(ContextCarrier carrier) {

    }

    @Override
    public void extract(ContextCarrier carrier) {

    }

    @Override public ContextSnapshot capture() {
        return new ContextSnapshot(null, -1, null);
    }

    @Override public void continued(ContextSnapshot snapshot) {

    }

    @Override
    public String getGlobalTraceId() {
        return "[Ignored Trace]";
    }

    @Override
    public AbstractSpan createEntrySpan(String operationName) {
        stackDepth++;
        return NOOP_SPAN;
    }

    @Override
    public AbstractSpan createLocalSpan(String operationName) {
        stackDepth++;
        return NOOP_SPAN;
    }

    @Override
    public AbstractSpan createExitSpan(String operationName, String remotePeer) {
        stackDepth++;
        return NOOP_SPAN;
    }

    @Override
    public AbstractSpan activeSpan() {
        return NOOP_SPAN;
    }

    @Override
    public void stopSpan(AbstractSpan span) {
        stackDepth--;
        if (stackDepth == 0) {
            ListenerManager.notifyFinish(this);
        }
    }

    public static class ListenerManager {
        private static List<IgnoreTracerContextListener> LISTENERS = new LinkedList<IgnoreTracerContextListener>();

        /**
         * Add the given {@link IgnoreTracerContextListener} to {@link #LISTENERS} list.
         *
         * @param listener the new listener.
         */
        public static synchronized void add(IgnoreTracerContextListener listener) {
            LISTENERS.add(listener);
        }

        /**
         * Notify the {@link IgnoredTracerContext.ListenerManager} about the given {@link IgnoredTracerContext} have
         * finished. And trigger {@link IgnoredTracerContext.ListenerManager} to notify all {@link #LISTENERS} 's {@link
         * IgnoreTracerContextListener#afterFinished(IgnoredTracerContext)}
         *
         * @param ignoredTracerContext
         */
        static void notifyFinish(IgnoredTracerContext ignoredTracerContext) {
            for (IgnoreTracerContextListener listener : LISTENERS) {
                listener.afterFinished(ignoredTracerContext);
            }
        }

        /**
         * Clear the given {@link IgnoreTracerContextListener}
         */
        public static synchronized void remove(IgnoreTracerContextListener listener) {
            LISTENERS.remove(listener);
        }
    }
}
