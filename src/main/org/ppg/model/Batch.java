package org.ppg.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Batch implements Comparable<Batch> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final int nBatch;
    private final int quantity;
    private final LocalDate needDate;
    private String planningClass;
    private String plant;
    private String item;
    private LocalDate startDate;
    private LocalDate endDate;
    private Statuses status;
    private String description;
    private Types type;
    private int delay;
    private int dilutor;
    private int duration;
    private boolean isLocked;

    //Constructors
    public Batch(int nBatch, String planningClass, String plant, String item, int quantity, LocalDate startDate, LocalDate endDate, LocalDate needDate, Statuses status, String description, Types type, int dilutor, int duration) {
        this.nBatch = nBatch;
        this.planningClass = planningClass;
        this.plant = plant;
        this.item = item;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.needDate = needDate;
        this.status = status;
        this.description = description;
        this.type = type;
        this.dilutor = dilutor;
        this.duration = duration;
    }

    public Batch(int nBatch, String planningClass, String plant, String item, int quantity, String description, Types type, LocalDate needDate) {
        this.nBatch = nBatch;
        this.planningClass = planningClass;
        this.plant = plant;
        this.item = item;
        this.quantity = quantity;
        this.description = description;
        this.type = type;
        this.needDate = needDate;
        this.endDate = needDate;
    }

    public Batch(int nBatch, String planningClass, String plant, String item, int quantity, LocalDate needDate, Types type, String description) {
        this.nBatch = nBatch;
        this.planningClass = planningClass;
        this.plant = plant;
        this.item = item;
        this.quantity = quantity;
        this.needDate = needDate;
        this.type = type;
        this.description = description;
        this.endDate = needDate;
    }

    public Batch(int nBatch, int duration, LocalDate needDate, int quantity, boolean isLocked) {
        this.quantity = quantity;
        this.duration = duration;
        this.needDate = needDate;
        this.delay = 0;
        this.isLocked = isLocked;
        this.nBatch = nBatch;
        this.endDate = needDate;
    }

    //Getters
    public StringProperty[] getProperties() {
        StringProperty[] properties = new StringProperty[9];
        properties[0] = new SimpleStringProperty(Integer.toString(nBatch));
        properties[1] = new SimpleStringProperty(planningClass);
        properties[2] = new SimpleStringProperty(plant);
        properties[3] = new SimpleStringProperty(item);
        properties[4] = new SimpleStringProperty(Integer.toString(quantity));
        properties[5] = new SimpleStringProperty(startDate.format(formatter));
        properties[6] = new SimpleStringProperty(needDate.format(formatter));
        properties[7] = new SimpleStringProperty(status.getValue());
        properties[8] = new SimpleStringProperty(description);
        return properties;
    }

    public int getnBatch() {
        return nBatch;
    }

    public int getQuantity() {
        return quantity;
    }

    public Types getType() {
        return this.type;
    }

    public String getPlant() {
        return this.plant;
    }

    public String getPlanningClass() {
        return this.planningClass;
    }

    public Statuses getStatus() {
        return status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }

    public String getItem() {
        return this.item;
    }

    public int getId() {
        return this.nBatch;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate fechaInicio) {
        this.startDate = fechaInicio;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate fechaFin) {
        this.endDate = fechaFin;
    }

    public LocalDate getNeedDate() {
        return this.needDate;
    }

    public int getDilutorId() {
        return dilutor;
    }

    public long getDuration() {
        return this.duration;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        if (!this.isLocked) {
            this.delay = delay;
        }
    }

    public String getDescription() {
        return this.description;
    }

    //Setter
    public void setDilutor(int dilutor) {
        this.dilutor = dilutor;
    }

    //Methods
    /**
     * Obtiene la fecha de inicio del lote.
     *
     * @return La fecha calculada como (b() - duración).
     */
    public LocalDate startDate() {
        return endDate().minusDays(this.duration);
    }

    /**
     * Obtiene la fecha actualizada requerida del lote, considerando el retraso.
     *
     * @return La fecha requerida más el retraso.
     */
    public LocalDate endDate() {
        return this.needDate.plusDays(this.delay);
    }

    //Override
    @Override
    public Batch clone() {
        Batch cloned = new Batch(nBatch, duration, needDate, quantity, isLocked);
        cloned.delay = this.delay;
        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Batch b)) {
            return false;
        }
        return Objects.equals(this.nBatch, b.nBatch);
    }

    @Override
    public int compareTo(Batch b) {
        if (this.endDate.isAfter(b.endDate)) {
            return 1;
        } else if (this.endDate.isBefore(b.endDate)) {
            return -1;
        }
        if (this.duration > b.duration) {
            return 1;
        }
        if (this.nBatch == b.nBatch) {
            return 0;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "BatchTemp{" + "nBatch=" + nBatch + ", planningClass='" + planningClass + '\'' + ", plant='" + plant + '\'' + ", item='" + item + '\'' + ", quantity=" + quantity + ", startDate=" + startDate + ", endDate=" + endDate + ", needDate=" + needDate + ", status=" + status + ", description='" + description + '\'' + ", type=" + type + ", dilutor=" + dilutor + ", duration=" + duration + '}';
    }
}
