package sqlitejviewer;

import java.io.File;
import javax.swing.table.TableModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class SQLiteDatabaseGUIInteractorTest {

    private SQLiteDatabaseGUIInteractor dbInteractor;

    @Before
    public void setUp() {
        File f = new File("test2.db");

        try {
            dbInteractor = new SQLiteDatabaseGUIInteractor(f, true);
        } catch (Exception ex) {
            fail("Failed to instantiate SQLiteDatabaseGUIInteractor: " + ex);
        }
    }

    @After
    public void tearDown() {
        try {
            dbInteractor.disconnect();
        } catch (Exception ex) {
            fail("Could not disconnect: " + ex);
        }
    }

    /**
     * Test of getDatabaseTables method, of class SQLiteDatabaseGUIInteractor.
     */
    @Test
    public void testGetDatabaseTables() {
        System.out.println("getDatabaseTables");
        DatabaseTableDTO[] result = dbInteractor.getDatabaseTables();
        assertNotNull(result);
        assertTrue(result.length > 0);
        System.out.println("tables=" + result.length);
    }

    /**
     * Test of getDatabaseIndexes method, of class SQLiteDatabaseGUIInteractor.
     */
    @Test
    public void testGetDatabaseIndexes() {
        System.out.println("getDatabaseIndexes");
        DatabaseIndexDTO[] result = dbInteractor.getDatabaseIndexes();
        assertNotNull(result);
        assertTrue(result.length > 0);
        System.out.println("indexes=" + result.length);
    }

    /**
     * Test of getDatabaseViews method, of class SQLiteDatabaseGUIInteractor.
     */
    @Test
    public void testGetDatabaseViews() {
        System.out.println("getDatabaseViews");
        DatabaseViewDTO[] result = dbInteractor.getDatabaseViews();
        assertNotNull(result);
        assertTrue(result.length > 0);
        System.out.println("views=" + result.length);
    }

    /**
     * Test of getAllData method, of class SQLiteDatabaseGUIInteractor.
     */
    @Test
    public void testGetAllData() {
        System.out.println("getAllData");

        try {
            TableModel result = dbInteractor.getAllData("users");
            assertNotNull(result);
            assertTrue(result.getRowCount() > 0);
            System.out.println("rows=" + result.getRowCount());
        } catch (Exception ex) {
            fail(ex.toString());
        }
    }

    /**
     * Test of getDatabaseTriggers method, of class SQLiteDatabaseGUIInteractor.
     */
    @Test
    public void testGetDatabaseTriggers() {
        System.out.println("getDatabaseTriggers");
        DatabaseTriggerDTO[] result = dbInteractor.getDatabaseTriggers();
        assertNotNull(result);
        assertTrue(result.length > 0);
        System.out.println("triggers=" + result.length);
    }

    /**
     * Test of dumpDatabase method, of class SQLiteDatabaseGUIInteractor.
     */
    @Test
    public void testDumpDatabase() {
        System.out.println("dumpDatabase");
        String result = dbInteractor.dumpDatabase();
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    /**
     * Test of getDatabaseFile method, of class SQLiteDatabaseGUIInteractor.
     */
    @Test
    public void testGetDatabaseFile() {
        System.out.println("getDatabaseFile");
        File result = dbInteractor.getDatabaseFile();
        assertNotNull(result);
        assertTrue(result.isFile());
    }

    /**
     * Test of getRowCount method, of class SQLiteDatabaseGUIInteractor.
     */
    @Test
    public void testGetRowCount() {
        System.out.println("getRowCount");
        int result = dbInteractor.getRowCount("users");
        assertTrue(result >= 0);
        System.out.println("users.rowcount=" + result);
    }
}