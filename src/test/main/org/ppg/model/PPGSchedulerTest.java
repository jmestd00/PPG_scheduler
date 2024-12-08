package org.ppg.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

public class PPGSchedulerTest {
    
    @Test
    public void testSchedule() throws PPGSchedulerException, CantAddException {
        BooleanProperty operationCompleted = new SimpleBooleanProperty();
        operationCompleted.setValue(false);
        PPGScheduler scheduler = new PPGScheduler(/*operationCompleted*/);
        ArrayList<Batch> batches = new ArrayList<>();

        batches.add(new Batch(777, 3, LocalDate.of(2025, 2, 25), 20, false));
        batches.add(new Batch(888, 3, LocalDate.of(2025, 3, 7), 20, false));
        batches.add(new Batch(999, 3, LocalDate.of(2025, 4, 3), 20, false));
        scheduler.insert(batches);
    }
}