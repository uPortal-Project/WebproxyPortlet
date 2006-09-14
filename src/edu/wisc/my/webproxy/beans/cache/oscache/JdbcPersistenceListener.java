/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.webproxy.beans.cache.oscache;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Set;

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

import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;
import com.opensymphony.oscache.base.persistence.PersistenceListener;

/**
 * Expects a table named WP_CACHE_STORE with three fields. CACHE_KEY is a VARCHAR(2000), OBJECT_TYPE is a VARCHAR(1), CACHE_OBJECT is a BLOB.
 * CACHE_KEY & OBJECT_TYPE make up the primary key.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class JdbcPersistenceListener extends JdbcDaoSupport implements PersistenceListener {
    private ObjectExistsQuery objectExistsQuery;
    private AllObjectsDelete allObjectsDelete;
    private ObjectDelete objectDelete;
    private ObjectQuery objectQuery;
    private ObjectInsert objectInsert;
    private ObjectUpdate objectUpdate;
    
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
        this.objectExistsQuery = new ObjectExistsQuery(this.getDataSource());
        this.allObjectsDelete = new AllObjectsDelete(this.getDataSource());
        this.objectDelete = new ObjectDelete(this.getDataSource());
        this.objectQuery = new ObjectQuery(this.getDataSource());
        this.objectInsert = new ObjectInsert(this.getDataSource());
        this.objectUpdate = new ObjectUpdate(this.getDataSource());
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isStored(java.lang.String)
     */
    public boolean isStored(String key) throws CachePersistenceException {
        return this.isStored(key, StoredObjectType.OBJECT);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isGroupStored(java.lang.String)
     */
    public boolean isGroupStored(String groupName) throws CachePersistenceException {
        return this.isStored(groupName, StoredObjectType.GROUP);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#clear()
     */
    public void clear() throws CachePersistenceException {
        this.allObjectsDelete.update();
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#configure(com.opensymphony.oscache.base.Config)
     */
    public PersistenceListener configure(Config config) {
        return this;
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#remove(java.lang.String)
     */
    public void remove(String key) throws CachePersistenceException {
        this.remove(key, StoredObjectType.OBJECT);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws CachePersistenceException {
        this.remove(groupName, StoredObjectType.GROUP);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieve(java.lang.String)
     */
    public Object retrieve(String key) throws CachePersistenceException {
        return this.retrieve(key, StoredObjectType.OBJECT);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieveGroup(java.lang.String)
     */
    public Set retrieveGroup(String groupName) throws CachePersistenceException {
        return (Set)this.retrieve(groupName, StoredObjectType.GROUP);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#store(java.lang.String, java.lang.Object)
     */
    public void store(String key, Object obj) throws CachePersistenceException {
        this.store(key, StoredObjectType.OBJECT, obj);

    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#storeGroup(java.lang.String, java.util.Set)
     */
    public void storeGroup(String groupName, Set keys) throws CachePersistenceException {
        this.store(groupName, StoredObjectType.GROUP, keys);
    }

    
    //***************
    //
    // Typed methods shared by the Object and Group persistence methods
    //
    //***************
    
    
    protected boolean isStored(String key, StoredObjectType type) {
        final Object[] args = {key, type.toString()};
        final List results = this.objectExistsQuery.execute(args);

        return results.size() > 0;
    }
    
    protected void remove(String key, StoredObjectType type) {
        final Object[] args = {key, type.toString()};
        final int rowsDeleted = this.objectDelete.update(args);
        
        if (rowsDeleted != 1) {
            this.logger.warn("Deleting Object of type='" + type + "' for key='" + key + "' resulted in " + rowsDeleted + " rows deleted. Expected 1 row deleted.");
        }
    }
    
    protected Object retrieve(String key, StoredObjectType type) {
        final Object[] args = {key, type.toString()};
        final List results = this.objectQuery.execute(args);
        
        final byte[] serializedObject = (byte[])DataAccessUtils.uniqueResult(results);
        
        if (serializedObject == null) {
            return null;
        }
        
        final Object obj;
        
        try {
            obj = SerializationUtils.deserialize(serializedObject);
        }
        catch (SerializationException se) {
            this.logger.error("Could not deserialize Object for key='" + key + "' and type='" + type + "'", se);
            return null;
        }
        
        return obj;
    }
    
    protected void store(String key, StoredObjectType type, Object obj) {
        final byte[] serializedObject;
        if (obj != null) {
            try {
                serializedObject = SerializationUtils.serialize((Serializable)obj);
            }
            catch (SerializationException se) {
                this.logger.error("Could not serialize Object='" + obj + "' for key='" + key + "' and type='" + type + "'", se);
                return;
            }
        }
        else {
            serializedObject = null;
        }
        
        //Check to see if the Object exists in the DB already
        final boolean exists = this.isStored(key, type);
        
        //Does not exist, do an insert
        if (!exists) {
            final Object[] args = {
                    key,
                    type.toString(),
                    new SqlLobValue(serializedObject, this.lobHandler)};
            
            final int rowsCreated = this.objectInsert.update(args);
            
            if (rowsCreated != 1) {
                this.logger.warn("Inserting Object='" + obj + "' for key='" + key + "' and type='" + type + "' resulted in " + rowsCreated + " rows created. Expected 1 row created.");
            }
        }
        //Exists, update the row
        else {
            final Object[] args = {
                    new SqlLobValue(serializedObject, this.lobHandler),
                    key,
                    type.toString()};
            
            final int rowsUpdated = this.objectUpdate.update(args);
            
            if (rowsUpdated != 1) {
                this.logger.warn("Updating Object='" + obj + "' for key='" + key + "' and type='" + type + "' resulted in " + rowsUpdated + " rows updated. Expected 1 row updated.");
            }
        }
    }
    
    
    
    
    
    /**
     * Strongly typed enumeration for use with typed storage methods
     */
    public static class StoredObjectType {
        public static final StoredObjectType OBJECT = new StoredObjectType('O');
        public static final StoredObjectType GROUP  = new StoredObjectType('G');

        private final char name;
        private final Character nameObj;
        
        private StoredObjectType(char typeName) {
            this.name = typeName;
            this.nameObj = new Character(this.name);
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof StoredObjectType)) {
                return false;
            }
            
            final StoredObjectType other = (StoredObjectType)obj;
            
            return this.name == other.name;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return this.nameObj.hashCode();
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return this.nameObj.toString();
        }
    }
    
    
    
    //***************
    //
    // Spring Query classes
    //
    //***************
    
    private class ObjectExistsQuery extends MappingSqlQuery {
        private static final String SQL = 
            "SELECT CACHE_KEY " +
            "FROM WP_CACHE_STORE " +
            "WHERE CACHE_KEY = ? and OBJECT_TYPE = ?";
        
        public ObjectExistsQuery(DataSource ds) {
            super(ds, SQL);
            
            this.declareParameter(new SqlParameter("CACHE_KEY", Types.VARCHAR));
            this.declareParameter(new SqlParameter("OBJECT_TYPE", Types.CHAR));
            
            this.compile();
        }

        /**
         * @see org.springframework.jdbc.object.MappingSqlQuery#mapRow(java.sql.ResultSet, int)
         */
        protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("CACHE_KEY");
        }
    }
    
    
    private class AllObjectsDelete extends SqlUpdate {
        private static final String SQL = 
            "DELETE FROM WP_CACHE_STORE";

        public AllObjectsDelete(DataSource ds) {
            super(ds, SQL);
            
            this.compile();
        }
    }
    
    
    private class ObjectDelete extends SqlUpdate {
        private static final String SQL = 
            "DELETE FROM WP_CACHE_STORE " +
            "WHERE CACHE_KEY = ? and OBJECT_TYPE = ?";

        public ObjectDelete(DataSource ds) {
            super(ds, SQL);
            
            this.declareParameter(new SqlParameter("CACHE_KEY", Types.VARCHAR));
            this.declareParameter(new SqlParameter("OBJECT_TYPE", Types.CHAR));
            
            this.compile();
        }
    }
    
    
    private class ObjectQuery extends MappingSqlQuery {
        private static final String SQL = 
            "SELECT CACHE_OBJECT " +
            "FROM WP_CACHE_STORE " +
            "WHERE CACHE_KEY = ? and OBJECT_TYPE = ?";
        
        public ObjectQuery(DataSource ds) {
            super(ds, SQL);
            
            this.declareParameter(new SqlParameter("CACHE_KEY", Types.VARCHAR));
            this.declareParameter(new SqlParameter("OBJECT_TYPE", Types.CHAR));
            
            this.compile();
        }

        /**
         * @see org.springframework.jdbc.object.MappingSqlQuery#mapRow(java.sql.ResultSet, int)
         */
        protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            final int colIndex = rs.findColumn("CACHE_OBJECT"); //TODO upgrade spring to get rid of this line
            final byte[] serializedObject = JdbcPersistenceListener.this.lobHandler.getBlobAsBytes(rs, colIndex);
            
            return serializedObject;
        }
    }

    
    private class ObjectInsert extends SqlUpdate {
        private static final String SQL = 
            "INSERT INTO WP_CACHE_STORE (CACHE_KEY, OBJECT_TYPE, CACHE_OBJECT) " +
            "VALUES (?, ?, ?)";

        public ObjectInsert(DataSource ds) {
            super(ds, SQL);
            
            this.declareParameter(new SqlParameter("CACHE_KEY", Types.VARCHAR));
            this.declareParameter(new SqlParameter("OBJECT_TYPE", Types.CHAR));
            this.declareParameter(new SqlParameter("CACHE_OBJECT", Types.BLOB));
            
            this.compile();
        }
    }
    
    
    private class ObjectUpdate extends SqlUpdate {
        private static final String SQL = 
            "UPDATE WP_CACHE_STORE " +
            "SET CACHE_OBJECT = ? " +
            "WHERE CACHE_KEY = ? and OBJECT_TYPE = ?";

        public ObjectUpdate(DataSource ds) {
            super(ds, SQL);
            
            this.declareParameter(new SqlParameter("CACHE_OBJECT", Types.BLOB));
            this.declareParameter(new SqlParameter("CACHE_KEY", Types.VARCHAR));
            this.declareParameter(new SqlParameter("OBJECT_TYPE", Types.CHAR));
            
            this.compile();
        }
    }
}
