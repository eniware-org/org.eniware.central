/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 * $Id$
 * ==================================================================
 */

package org.eniware.central.dao;

import java.io.Serializable;
import java.util.List;

import org.eniware.central.domain.Entity;
import org.eniware.central.domain.SortDescriptor;

/**
 * Generic DAO API.
 * 
 * <p>Based in part on 
 * http://www-128.ibm.com/developerworks/java/library/j-genericdao.html.</p>
 * 
 * @param <T> the domain object type
 * @param <PK> the primary key type
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public interface GenericDao<T extends Entity<PK>, PK extends Serializable> {

	/**
	 * Get the class supported by this Dao.
	 * 
	 * @return class
	 */
	Class<? extends T> getObjectType();

   /**
     * Persist the domainObject object into database, 
     * creating or updating as appropriate.
     * 
     * @param domainObject the domain object so store
     * @return the primary key of the stored object
     */
    PK store(T domainObject);

    /** 
     * Get a persisted domain object by its primary key.
     * @param id the primary key to retrieve
     * @return the domain object
     */
    T get(PK id);

    /**
     * Get a list of persisted domain objects, optionally sorted in some way.
     * 
     * <p>The <code>sortDescriptors</code> parameter can be <em>null</em>, in
     * which case the sort order is not defined and implementation specific.</p>
     * 
     * @param sortDescriptors list of sort descriptors to sort the results by
     * @return list of all persisted domain objects, or empty list if none available
     * @since 1.2
     */
    List<T> getAll(List<SortDescriptor> sortDescriptors);
    
    /** 
     * Remove an object from persistent storage in the database.
     * @param domainObject the domain object to delete
     */
    void delete(T domainObject);
    
}
