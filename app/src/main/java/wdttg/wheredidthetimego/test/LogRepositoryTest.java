package wdttg.wheredidthetimego.test;

import android.test.AndroidTestCase;

import java.util.List;

import wdttg.wheredidthetimego.history.LogEntry;
import wdttg.wheredidthetimego.history.LogRepository;

/**
 * A test for log repository functionality
 *
 * Created by Matthew on 11/9/2014.
 */
public class LogRepositoryTest extends AndroidTestCase {

    private LogRepository repository;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        repository = new LogRepository(getContext());
        repository.clearTable();
    }

    @Override
    public void tearDown() throws Exception {

        repository.clearTable();
        repository.close();
        super.tearDown();

    }

    public void testInsert() throws  Exception {

        LogEntry entry = repository.createLogEntry(1000, 1005, null);

        LogEntry returned = repository.getLogEntry(entry.getId());

        assertEquals(entry, returned);

    }

    public void testGetTimeSpan() throws Exception {

        repository.createLogEntry(100, 200, null);
        repository.createLogEntry(300, 400, null);
        repository.createLogEntry(250, 260, 0.5);

        List<LogEntry> returned =  repository.getEntriesBetween(230, 260);

        assertEquals(1, returned.size());

        assertEquals(0.5, returned.get(0).getProductivity(), 0.0001);

    }

    public void testSimpleAverage() throws Exception {

        repository.createLogEntry(10, 10, 0.0);
        repository.createLogEntry(10, 10, 1.0);

        assertEquals(0.5, repository.getAverageProductivityBetween(0, 15), 0.0001);

    }

    public void testSkipNullAverage() throws Exception {

        repository.createLogEntry(10, 10, 0.0);
        repository.createLogEntry(10, 10, null);
        repository.createLogEntry(10, 10, 1.0);

        assertEquals(0.5, repository.getAverageProductivityBetween(0, 15), 0.0001);


    }

    public void testFindNotFilleds() throws Exception {

        repository.createLogEntry(10, 10, 0.5);
        repository.createLogEntry(10, 10, 0.7);
        LogEntry unfilled = repository.createLogEntry(10, 10, null);

        List<LogEntry> unfilledList = repository.unfilledEntriesBetween(5, 15);

        assertEquals(1, unfilledList.size());
        assertEquals(unfilled, unfilledList.get(0));


    }
}
