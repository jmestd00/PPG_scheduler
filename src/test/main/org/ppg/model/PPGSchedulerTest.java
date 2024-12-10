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
        PPGScheduler scheduler = new PPGScheduler(operationCompleted);
        ArrayList<Batch> batches = new ArrayList<>();

        batches.add(new Batch(0, "VDWBBC", "VDW", "A-RXX3359-DD", 2000, LocalDate.of(2024, 12, 15), Types.PISC, "PISC"));
        scheduler.insert(batches);
    }
}