package org.ppg.model;

import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

public class PPGSchedulerTest {
    
    @Test
    public void testSchedule() throws PPGSchedulerException {
        PPGScheduler scheduler = new PPGScheduler();
        ArrayList<Batch> batches = new ArrayList<>();
        batches.add(new Batch(12, "VDWBBC", "VDW", "A-RXX3359-DD", 12, LocalDate.now(), LocalDate.now(), LocalDate.now(), Statuses.FINALIZADO, "", Types.PISC, 6, 45));
        //scheduler.insert(batches);
        scheduler.schedule();
    }
}