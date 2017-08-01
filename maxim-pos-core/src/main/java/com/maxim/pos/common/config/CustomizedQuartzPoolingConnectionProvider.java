package com.maxim.pos.common.config;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.quartz.SchedulerException;
import org.quartz.utils.ConnectionProvider;

import com.maxim.util.EncryptionUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class CustomizedQuartzPoolingConnectionProvider implements ConnectionProvider {

    private String driver;
    private String URL;
    private String user;
    private String password;
    private int maxConnections;
    private int maxCachedStatementsPerConnection;
    private int maxIdleSeconds;
    private String validationQuery;
    private int idleConnectionValidationSeconds;
    private boolean validateOnCheckout;
    private String discardIdleConnectionsSeconds;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxCachedStatementsPerConnection() {
        return maxCachedStatementsPerConnection;
    }

    public void setMaxCachedStatementsPerConnection(int maxCachedStatementsPerConnection) {
        this.maxCachedStatementsPerConnection = maxCachedStatementsPerConnection;
    }

    public int getMaxIdleSeconds() {
        return maxIdleSeconds;
    }

    public void setMaxIdleSeconds(int maxIdleSeconds) {
        this.maxIdleSeconds = maxIdleSeconds;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public int getIdleConnectionValidationSeconds() {
        return idleConnectionValidationSeconds;
    }

    public void setIdleConnectionValidationSeconds(int idleConnectionValidationSeconds) {
        this.idleConnectionValidationSeconds = idleConnectionValidationSeconds;
    }

    public boolean isValidateOnCheckout() {
        return validateOnCheckout;
    }

    public void setValidateOnCheckout(boolean validateOnCheckout) {
        this.validateOnCheckout = validateOnCheckout;
    }

    public String getDiscardIdleConnectionsSeconds() {
        return discardIdleConnectionsSeconds;
    }

    public void setDiscardIdleConnectionsSeconds(String discardIdleConnectionsSeconds) {
        this.discardIdleConnectionsSeconds = discardIdleConnectionsSeconds;
    }

    public static final String DB_MAX_CACHED_STATEMENTS_PER_CONNECTION = "maxCachedStatementsPerConnection";

    private static final String K = "90206f7a4fc149b592a14b7629caad5e";

    /**
     * The database sql query to execute every time a connection is returned to
     * the pool to ensure that it is still valid.
     */
    public static final String DB_VALIDATION_QUERY = "validationQuery";

    /**
     * The number of seconds between tests of idle connections - only enabled if
     * the validation query property is set. Default is 50 seconds.
     */
    public static final String DB_IDLE_VALIDATION_SECONDS = "idleConnectionValidationSeconds";

    /**
     * Whether the database sql query to validate connections should be executed
     * every time a connection is retrieved from the pool to ensure that it is
     * still valid. If false, then validation will occur on check-in. Default is
     * false.
     */
    public static final String DB_VALIDATE_ON_CHECKOUT = "validateOnCheckout";

    /** Default maximum number of database connections in the pool. */
    public static final int DEFAULT_DB_MAX_CONNECTIONS = 10;

    /** Default maximum number of database connections in the pool. */
    public static final int DEFAULT_DB_MAX_CACHED_STATEMENTS_PER_CONNECTION = 120;

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Data members.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private ComboPooledDataSource datasource;

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Constructors.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public CustomizedQuartzPoolingConnectionProvider() {
        super();
    }

    private void initialize(String dbDriver, String dbURL, String dbUser, String dbPassword, int maxConnections,
            int maxStatementsPerConnection, String dbValidationQuery, boolean validateOnCheckout,
            int idleValidationSeconds, int maxIdleSeconds) throws SQLException, SchedulerException {
        if (dbURL == null) {
            throw new SQLException("DBPool could not be created: DB URL cannot be null");
        }

        if (dbDriver == null) {
            throw new SQLException(
                    "DBPool '" + dbURL + "' could not be created: " + "DB driver class name cannot be null!");
        }

        if (maxConnections < 0) {
            throw new SQLException(
                    "DBPool '" + dbURL + "' could not be created: " + "Max connections must be greater than zero!");
        }

        datasource = new ComboPooledDataSource();
        try {
            datasource.setDriverClass(dbDriver);
        } catch (PropertyVetoException e) {
            throw new SchedulerException("Problem setting driver class name on datasource: " + e.getMessage(), e);
        }
        datasource.setJdbcUrl(dbURL);
        datasource.setUser(dbUser);
        try {

            datasource.setPassword(EncryptionUtil.decrypt(dbPassword, K));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        datasource.setMaxPoolSize(maxConnections);
        datasource.setMinPoolSize(1);
        datasource.setMaxIdleTime(maxIdleSeconds);
        datasource.setMaxStatementsPerConnection(maxStatementsPerConnection);

        if (dbValidationQuery != null) {
            datasource.setPreferredTestQuery(dbValidationQuery);
            if (!validateOnCheckout)
                datasource.setTestConnectionOnCheckin(true);
            else
                datasource.setTestConnectionOnCheckout(true);
            datasource.setIdleConnectionTestPeriod(idleValidationSeconds);
        }
    }

    protected ComboPooledDataSource getDataSource() {
        return datasource;
    }

    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    public void shutdown() throws SQLException {
        datasource.close();
    }

    public void initialize() throws SQLException {
        // do nothing, already initialized during constructor call
        try {
            initialize(driver, URL, user, password, maxConnections, maxCachedStatementsPerConnection, validationQuery,
                    validateOnCheckout, idleConnectionValidationSeconds, maxIdleSeconds);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

}
