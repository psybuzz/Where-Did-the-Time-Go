package wdttg.wheredidthetimego.history;

/**
 * A history entry in our database
 *
 * Created by Matthew on 11/9/2014.
 */
public class LogEntry {

    /**
     * The ID of this log entry in the database, or null if it has not yet been stored.
     */
    private long id;

    /**
     * The time (in MS since 1970) at which this time period begins (inclusive)
     */
    private long startTime;

    /**
     * The time (in MS since 1970) at which this time period finishes (non inclusive)
     */
    private long endTime;

    /**
     * The productivity for this time period, on a scale of 0-1, or null if it has not yet been set
     */
    private Double productivity;

    /**
     * Constructs a new log entry
     * @param id
     * @param startTime
     * @param endTime
     * @param productivity
     */
    public LogEntry(long id, long startTime, long endTime, Double productivity) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productivity = productivity;
    }

    public long getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Double getProductivity() {
        return productivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEntry logEntry = (LogEntry) o;

        if (endTime != logEntry.endTime) return false;
        if (id != logEntry.id) return false;
        if (startTime != logEntry.startTime) return false;
        if (productivity != null ? !productivity.equals(logEntry.productivity) : logEntry.productivity != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        result = 31 * result + (productivity != null ? productivity.hashCode() : 0);
        return result;
    }
}
