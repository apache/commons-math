package org.apache.commons.math4.genetics.listener;

import java.lang.reflect.Field;
import java.util.List;
import org.apache.commons.math4.genetics.ListPopulation;
import org.apache.commons.math4.genetics.Population;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.junit.Assert;
import org.junit.Test;

public class ConvergenceListenerRegistryTest {

    @Test
    public void testRegister() {
        try {
            reset();
            ConvergenceListenerRegistry<String> registry = ConvergenceListenerRegistry.<String>getInstance();
            ConvergenceListener<String> convergenceListener = new ConvergenceListener<String>() {

                @Override
                public void notify(int generation, Population<String> population) {
                    // No op
                }
            };
            registry.addConvergenceListener(convergenceListener);
            Field listenersField = registry.getClass().getDeclaredField("listeners");
            listenersField.setAccessible(true);
            List<ConvergenceListener<String>> listeners = (List<ConvergenceListener<String>>) listenersField
                    .get(registry);
            Assert.assertTrue(listeners.get(0) == convergenceListener);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new GeneticException(e);
        } catch (IllegalArgumentException e) {
            throw new GeneticException(e);
        } catch (IllegalAccessException e) {
            throw new GeneticException(e);
        }
    }

    private void reset()
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        ConvergenceListenerRegistry<String> registry = ConvergenceListenerRegistry.<String>getInstance();
        Field listenersField = registry.getClass().getDeclaredField("listeners");
        boolean accessible = listenersField.canAccess(registry);
        if (!accessible) {
            listenersField.setAccessible(true);
        }
        List<ConvergenceListener<String>> listeners = (List<ConvergenceListener<String>>) listenersField
                .get(ConvergenceListenerRegistry.getInstance());
        listeners.clear();
        listenersField.setAccessible(accessible);
    }

    @Test(expected = GeneticException.class)
    public void testNotifyAll() {
        try {
            reset();
            ConvergenceListenerRegistry<String> registry = ConvergenceListenerRegistry.<String>getInstance();
            ConvergenceListener<String> convergenceListener = new ConvergenceListener<String>() {

                @Override
                public void notify(int generation, Population<String> population) {
                    throw new GeneticException("Test Notify");
                }
            };
            registry.addConvergenceListener(convergenceListener);
            registry.notifyAll(0, new ListPopulation<>(10));
            Assert.assertTrue(false);
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            throw new GeneticException(e);
        }
    }

}
