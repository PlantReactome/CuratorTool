package test.org.gk.qualityCheck;

import java.io.FileReader;
import java.util.Properties;

import org.gk.persistence.MySQLAdaptor;

public class TestUtil {

    /**
     * Acquires the test MySQL data source.
     * 
     * The data source requires a file <code>test/resources/auth.properties</code>
     * with entries <code>host</code> (default <code>localhost</code>),
     * <code>database</code>, <code>user</code> and <code>password</code>.
     * 
     * @throws FileNotFoundException if the test database authorization
     *      file is missing
     */
    public static MySQLAdaptor getDataSource() throws Exception {
        Properties props = new Properties();
        FileReader reader = new FileReader("test/resources/auth.properties");
        props.load(reader);
        String host = props.getProperty("host", "localhost");
        MySQLAdaptor dba = new MySQLAdaptor(
                host,
                props.getProperty("database"),
                props.getProperty("user"),
                props.getProperty("password"));
        return dba;
    }

}
