/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.webproxy.beans.http;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.lob.LobHandler;

/**
 * Expects a table named WP_STATE_STORE with two fields. STATE_KEY is a VARCHAR and STATE is a BLOB. STATE_KEY is
 * the primary key
 * 
 * 
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class JdbcStateStore extends JdbcDaoSupport implements StateStore {
    private StateExistsQuery stateExistsQuery;
    private StateInsert stateInsert;
    private StateUpdate stateUpdate;
    private StateQuery stateQuery;
    private StateDelete stateDelete;
    private AllStatesDelete allStatesDelete;
    
    private LobHandler lobHandler;
    
    
    /**
     * @return Returns the lobHandler.
     */
    public LobHandler getLobHandler() {
        return this.lobHandler;
    }

    /**
     * @param lobHandler The lobHandler to set.
     */
    public void setLobHandler(LobHandler lobHandler) {
        this.lobHandler = lobHandler;
    }

    /**
     * @see org.springframework.jdbc.core.support.JdbcDaoSupport#initDao()
     */
    protected void initDao() throws Exception {
        this.stateExistsQuery = new StateExistsQuery(this.getDataSource());
        this.stateInsert = new StateInsert(this.getDataSource());
        this.stateUpdate = new StateUpdate(this.getDataSource());
        this.stateQuery = new StateQuery(this.getDataSource());
        this.stateDelete = new StateDelete(this.getDataSource());
        this.allStatesDelete = new AllStatesDelete(this.getDataSource());
    }

    /**
     * @see edu.wisc.my.webproxy.beans.http.StateStore#storeState(java.lang.String, edu.wisc.my.webproxy.beans.http.State)
     */
    public void storeState(String key, State state) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null.");
        }
        if (state == null) {
            throw new IllegalArgumentException("state cannot be null.");
        }
        
        //TODO ensure State can be re-created from bean properties and use those instead of serialization (faster?)
        final byte[] serializedState;
        try {
            serializedState = SerializationUtils.serialize(state);
        }
        catch (SerializationException se) {
            this.logger.error("Could not serialize State='" + state + "' for key='" + key + "'", se);
            return;
        }
        
        //Check to see if the State exists in the DB already
        final List results = this.stateExistsQuery.execute(key);
        
        //Does not exist, do an insert
        if (results.size() == 0) {
            final Object[] args = {
                    key, 
                    new SqlLobValue(serializedState, this.lobHandler)};
            
            final int rowsCreated = this.stateInsert.update(args);
            
            if (rowsCreated != 1) {
                this.logger.warn("Inserting State='" + state + "' for key='" + key + "' resulted in " + rowsCreated + " rows created. Expected 1 row created.");
            }
        }
        //Exists, update the row
        else {
            final Object[] args = {
                    new SqlLobValue(serializedState, this.lobHandler),
                    key};
            
            final int rowsUpdated = this.stateUpdate.update(args);
            
            if (rowsUpdated != 1) {
                this.logger.warn("Updating State='" + state + "' for key='" + key + "' resulted in " + rowsUpdated + " rows updated. Expected 1 row updated.");
            }
        }
    }

    /**
     * @see edu.wisc.my.webproxy.beans.http.StateStore#getState(java.lang.String)
     */
    public State getState(String key) {
        final List results = this.stateQuery.execute(key);
        
        final byte[] serializedState = (byte[])DataAccessUtils.uniqueResult(results);
        
        if (serializedState == null) {
            return null;
        }
        
        final State state;
        try {
            state = (State)SerializationUtils.deserialize(serializedState);
        }
        catch (SerializationException se) {
            this.logger.error("Could not deserialize State for key='" + key + "'", se);
            return null;
        }
        
        this.purgeCookies(state);
        return state;
    }

    /**
     * @see edu.wisc.my.webproxy.beans.http.StateStore#deleteState(java.lang.String)
     */
    public void deleteState(String key) {
        final int rowsDeleted = this.stateDelete.update(key);
        
        if (rowsDeleted != 1) {
            this.logger.warn("Deleting State for key='" + key + "' resulted in " + rowsDeleted + " rows deleted. Expected 1 row deleted.");
        }
    }

    /**
     * @see edu.wisc.my.webproxy.beans.http.StateStore#clearAll()
     */
    public void clearAll() {
        final int rowsDeleted = this.allStatesDelete.update();
        
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Deleting all states resulted in " + rowsDeleted + " rows deleted.");
        }
    }
    
    /**
     * Remove expired cookies from the state
     */
    protected void purgeCookies(final State state) {
        final Cookie[] cookies = state.getCookies();
        if (cookies != null) {
            final List<Cookie> validCookies = new ArrayList<Cookie>(cookies.length);
            for (Cookie cookie : cookies) {
                //Only keep cookies that have an expiration date and is not expired
                if (cookie.getExpiryDate() != null && !cookie.isExpired()) {
                    validCookies.add(cookie);
                }
            }
            state.clearCookies();

            if (validCookies.size() > 0) {
                state.addCookies(validCookies.toArray(new Cookie[validCookies.size()]));
            }
         }
     }
    
    private class StateExistsQuery extends MappingSqlQuery {
        private static final String SQL = 
            "SELECT STATE_KEY " +
            "FROM WP_STATE_STORE " +
            "WHERE STATE_KEY = ?";
        
        public StateExistsQuery(DataSource ds) {
            super(ds, SQL);
            
            this.declareParameter(new SqlParameter("STATE_KEY", Types.VARCHAR));
            
            this.compile();
        }

        /**
         * @see org.springframework.jdbc.object.MappingSqlQuery#mapRow(java.sql.ResultSet, int)
         */
        protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("STATE_KEY");
        }
    }
    
    private class StateInsert extends SqlUpdate {
        private static final String SQL = 
            "INSERT INTO WP_STATE_STORE (STATE_KEY, STATE) " +
            "VALUES (?, ?)";

        public StateInsert(DataSource ds) {
            super(ds, SQL);
            
            this.declareParameter(new SqlParameter("STATE_KEY", Types.VARCHAR));
            this.declareParameter(new SqlParameter("STATE", Types.BLOB));
            
            this.compile();
        }
    }
    
    private class StateUpdate extends SqlUpdate {
        private static final String SQL = 
            "UPDATE WP_STATE_STORE " +
            "SET STATE = ? " +
            "WHERE STATE_KEY = ?";

        public StateUpdate(DataSource ds) {
            super(ds, SQL);
            
            this.declareParameter(new SqlParameter("STATE", Types.BLOB));
            this.declareParameter(new SqlParameter("STATE_KEY", Types.VARCHAR));
            
            this.compile();
        }
    }
    
    private class StateQuery extends MappingSqlQuery {
        private static final String SQL = 
            "SELECT STATE " +
            "FROM WP_STATE_STORE " +
            "WHERE STATE_KEY = ?";
        
        public StateQuery(DataSource ds) {
            super(ds, SQL);

            this.declareParameter(new SqlParameter("STATE_KEY", Types.VARCHAR));
            
            this.compile();
        }

        /**
         * @see org.springframework.jdbc.object.MappingSqlQuery#mapRow(java.sql.ResultSet, int)
         */
        protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            final int colIndex = rs.findColumn("STATE"); //TODO upgrade spring to get rid of this line
            final byte[] serializedState = JdbcStateStore.this.lobHandler.getBlobAsBytes(rs, colIndex);
            
            return serializedState;
        }
    }
    
    private class StateDelete extends SqlUpdate {
        private static final String SQL = 
            "DELETE FROM WP_STATE_STORE " +
            "WHERE STATE_KEY = ?";

        public StateDelete(DataSource ds) {
            super(ds, SQL);
            
            this.declareParameter(new SqlParameter("STATE_KEY", Types.VARCHAR));
            
            this.compile();
        }
    }
    
    private class AllStatesDelete extends SqlUpdate {
        private static final String SQL = 
            "DELETE FROM WP_STATE_STORE";

        public AllStatesDelete(DataSource ds) {
            super(ds, SQL);
            
            this.compile();
        }
    }
}
