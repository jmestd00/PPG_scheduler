package org.ppg.model;

import java.util.ArrayList;

public class Solution {
    private ArrayList<Batch> solutionBatches;
    
    public Solution(ArrayList<Batch> batches) {
        this.solutionBatches = new ArrayList<>();
        for (Batch batch : batches) {
            this.solutionBatches.add(batch.clone()); // Copia profunda
        }
    }

    public ArrayList<Batch> getBatches() {
        return this.solutionBatches;
    }

    @Override
    public String toString() {
        return "Solution {lotes = " + solutionBatches + "}";
    }
}
