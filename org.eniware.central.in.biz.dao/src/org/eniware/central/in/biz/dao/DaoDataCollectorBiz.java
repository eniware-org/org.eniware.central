/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.biz.dao;

import java.util.ArrayList;
import java.util.List;

import org.eniware.central.biz.EniwareEdgeMetadataBiz;
import org.eniware.central.dao.PriceLocationDao;
import org.eniware.central.dao.EniwareLocationDao;
import org.eniware.central.dao.EniwareEdgeDao;
import org.eniware.central.dao.WeatherLocationDao;
import org.eniware.central.datum.biz.DatumMetadataBiz;
import org.eniware.central.datum.dao.GeneralLocationDatumDao;
import org.eniware.central.datum.dao.GeneralEdgeDatumDao;
import org.eniware.central.datum.domain.ConsumptionDatum;
import org.eniware.central.datum.domain.Datum;
import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.datum.domain.DayDatum;
import org.eniware.central.datum.domain.GeneralLocationDatum;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.GeneralLocationDatumPK;
import org.eniware.central.datum.domain.GeneralEdgeDatum;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.GeneralEdgeDatumPK;
import org.eniware.central.datum.domain.LocationDatum;
import org.eniware.central.datum.domain.EdgeDatum;
import org.eniware.central.datum.domain.PowerDatum;
import org.eniware.central.datum.domain.PriceDatum;
import org.eniware.central.datum.domain.WeatherDatum;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.LocationMatch;
import org.eniware.central.domain.EniwareEdge;
import org.eniware.central.domain.EniwareEdgeMetadataFilter;
import org.eniware.central.domain.EniwareEdgeMetadataFilterMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.domain.SourceLocation;
import org.eniware.central.domain.SourceLocationMatch;
import org.eniware.central.in.biz.DataCollectorBiz;
import org.eniware.central.security.AuthenticatedEdge;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityException;
import org.eniware.central.security.AuthorizationException.Reason;
import org.eniware.domain.GeneralDatumMetadata;
import org.eniware.util.ClassUtils;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link DataCollectorBiz} using {@link GeneralEdgeDatumDao}
 * and {@link GeneralLocationDatumDao} APIs to persist the data.
 * 
 * <p>
 * This service expects all calls into {@link #postGeneralEdgeDatum(Iterable)}
 * and {@link #postGeneralLocationDatum(Iterable)} to provide a
 * {@link AuthenticatedEdge} via the normal Spring Security
 * {@link SecurityContextHolder} API. Any attempt to post data for a Edge
 * different from the currently authenticated Edge will result in a
 * {@link SecurityException}. If a {@link EdgeDatum} is posted with a
 * <em>null</em> {@link EdgeDatum#getEdgeId()} value, this service will set the
 * Edge ID to the authenticated Edge ID automatically.
 * </p>
 *
 * @version 2.1
 */
public class DaoDataCollectorBiz implements DataCollectorBiz {

	private EniwareEdgeDao eniwareEdgeDao = null;
	private PriceLocationDao priceLocationDao = null;
	private WeatherLocationDao weatherLocationDao = null;
	private EniwareLocationDao eniwareLocationDao = null;
	private EniwareEdgeMetadataBiz eniwareEdgeMetadataBiz;
	private GeneralEdgeDatumDao generalEdgeDatumDao = null;
	private GeneralLocationDatumDao generalLocationDatumDao = null;
	private DatumMetadataBiz datumMetadataBiz = null;
	private int filteredResultsLimit = 250;
	private GeneralDatumMapper datumMapper = null;

	/** A class-level logger. */
	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	private Integer limitFilterMaximum(Integer requestedMaximum) {
		if ( requestedMaximum == null || requestedMaximum.intValue() > filteredResultsLimit
				|| requestedMaximum.intValue() < 1 ) {
			return filteredResultsLimit;
		}
		return requestedMaximum;
	}

	private Integer limitFilterOffset(Integer requestedOffset) {
		if ( requestedOffset == null || requestedOffset.intValue() < 0 ) {
			return 0;
		}
		return requestedOffset;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	@Deprecated
	public <D extends Datum> D postDatum(D datum) {
		if ( datum == null ) {
			throw new IllegalArgumentException("Datum must not be null");
		}

		// verify Edge ID with security
		AuthenticatedEdge authEdge = getAuthenticatedEdge();
		if ( authEdge == null ) {
			throw new AuthorizationException(Reason.ANONYMOUS_ACCESS_DENIED, null);
		}
		if ( datum instanceof EdgeDatum ) {
			EdgeDatum nd = (EdgeDatum) datum;
			if ( nd.getEdgeId() == null ) {
				if ( log.isDebugEnabled() ) {
					log.debug("Setting EdgeId property to authenticated Edge ID " + authEdge.getEdgeId()
							+ " on datum " + datum);
				}
				BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(nd);
				wrapper.setPropertyValue("EdgeId", authEdge.getEdgeId());
			} else if ( !nd.getEdgeId().equals(authEdge.getEdgeId()) ) {
				if ( log.isWarnEnabled() ) {
					log.warn("Illegal datum post by Edge " + authEdge.getEdgeId() + " as Edge "
							+ nd.getEdgeId());
				}
				throw new AuthorizationException(Reason.ACCESS_DENIED, nd.getEdgeId());
			}
		}

		Long entityId = null;
		try {
			if ( datum instanceof LocationDatum
					&& !(datum instanceof PowerDatum || datum instanceof ConsumptionDatum) ) {
				GeneralLocationDatum g = preprocessLocationDatum((LocationDatum) datum);
				GeneralLocationDatum entity = checkForLocationDatumByDate(g.getLocationId(),
						g.getCreated(), g.getSourceId());
				GeneralLocationDatumPK pk;
				if ( entity == null ) {
					pk = generalLocationDatumDao.store(g);
				} else {
					pk = entity.getId();
				}
				entityId = ((pk.getLocationId().longValue() & 0x7FFFFF) << 40)
						| ((pk.getSourceId().hashCode() & 0xFF) << 32)
						| (pk.getCreated().minusYears(40).getMillis() & 0xFFFFFFFF);
			} else {
				GeneralEdgeDatum g = preprocessDatum(datum);
				GeneralEdgeDatum entity = checkForEdgeDatumByDate(g.getEdgeId(), g.getCreated(),
						g.getSourceId());
				GeneralEdgeDatumPK pk;
				if ( entity == null ) {
					pk = generalEdgeDatumDao.store(g);
				} else {
					pk = entity.getId();
				}
				entityId = ((pk.getEdgeId().longValue() & 0x7FFFFF) << 40)
						| ((pk.getSourceId().hashCode() & 0xFF) << 32)
						| (pk.getCreated().minusYears(40).getMillis() & 0xFFFFFFFF);
			}
		} catch ( DataIntegrityViolationException e ) {
			if ( log.isDebugEnabled() ) {
				log.debug("DataIntegretyViolation on store of " + datum.getClass().getSimpleName() + ": "
						+ ClassUtils.getBeanProperties(datum, null), e);
			} else if ( log.isWarnEnabled() ) {
				log.warn("DataIntegretyViolation on store of " + datum.getClass().getSimpleName() + ": "
						+ ClassUtils.getBeanProperties(datum, null));
			}
			throw new org.eniware.central.RepeatableTaskException(e);
		} catch ( RuntimeException e ) {
			// log this
			log.error("Unable to store " + datum.getClass().getSimpleName() + ": "
					+ ClassUtils.getBeanProperties(datum, null));
			throw e;
		}

		// now get numeric ID for datum and return
		PropertyAccessor bean = PropertyAccessorFactory.forBeanPropertyAccess(datum);
		bean.setPropertyValue("id", entityId);

		return datum;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	@Deprecated
	public List<Datum> postDatum(Iterable<Datum> datums) {
		List<Datum> results = new ArrayList<Datum>();
		for ( Datum d : datums ) {
			Datum entity = postDatum(d);
			results.add(entity);
		}
		return results;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void postGeneralEdgeDatum(Iterable<GeneralEdgeDatum> datums) {
		if ( datums == null ) {
			return;
		}
		// verify Edge ID with security
		AuthenticatedEdge authEdge = getAuthenticatedEdge();
		if ( authEdge == null ) {
			throw new AuthorizationException(Reason.ANONYMOUS_ACCESS_DENIED, null);
		}
		for ( GeneralEdgeDatum d : datums ) {
			if ( d.getEdgeId() == null ) {
				d.setEdgeId(authEdge.getEdgeId());
			} else if ( !d.getEdgeId().equals(authEdge.getEdgeId()) ) {
				if ( log.isWarnEnabled() ) {
					log.warn("Illegal datum post by Edge " + authEdge.getEdgeId() + " as Edge "
							+ d.getEdgeId());
				}
				throw new AuthorizationException(Reason.ACCESS_DENIED, d.getEdgeId());
			}
			generalEdgeDatumDao.store(d);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void postGeneralLocationDatum(Iterable<GeneralLocationDatum> datums) {
		if ( datums == null ) {
			return;
		}
		// verify Edge ID with security
		AuthenticatedEdge authEdge = getAuthenticatedEdge();
		if ( authEdge == null ) {
			throw new AuthorizationException(Reason.ANONYMOUS_ACCESS_DENIED, null);
		}
		for ( GeneralLocationDatum d : datums ) {
			if ( d.getLocationId() == null ) {
				throw new IllegalArgumentException(
						"A locationId value is required for GeneralLocationDatum");
			}
			generalLocationDatumDao.store(d);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void addGeneralEdgeDatumMetadata(Long EdgeId, final String sourceId,
			final GeneralDatumMetadata meta) {
		if ( sourceId == null || meta == null
				|| ((meta.getTags() == null || meta.getTags().isEmpty())
						&& (meta.getInfo() == null || meta.getInfo().isEmpty())
						&& (meta.getPropertyInfo() == null || meta.getPropertyInfo().isEmpty())) ) {
			return;
		}

		// verify Edge ID with security
		AuthenticatedEdge authEdge = getAuthenticatedEdge();
		if ( authEdge == null ) {
			throw new AuthorizationException(Reason.ANONYMOUS_ACCESS_DENIED, null);
		}
		if ( EdgeId == null ) {
			EdgeId = authEdge.getEdgeId();
		} else if ( EdgeId.equals(authEdge.getEdgeId()) == false ) {
			if ( log.isWarnEnabled() ) {
				log.warn("Illegal datum metadata post by Edge " + authEdge.getEdgeId() + " as Edge "
						+ EdgeId);
			}
			throw new AuthorizationException(Reason.ACCESS_DENIED, EdgeId);
		}
		datumMetadataBiz.addGeneralEdgeDatumMetadata(EdgeId, sourceId, meta);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void addEniwareEdgeMetadata(Long EdgeId, GeneralDatumMetadata meta) {
		if ( meta == null || ((meta.getTags() == null || meta.getTags().isEmpty())
				&& (meta.getInfo() == null || meta.getInfo().isEmpty())
				&& (meta.getPropertyInfo() == null || meta.getPropertyInfo().isEmpty())) ) {
			return;
		}

		// verify Edge ID with security
		AuthenticatedEdge authEdge = getAuthenticatedEdge();
		if ( authEdge == null ) {
			throw new AuthorizationException(Reason.ANONYMOUS_ACCESS_DENIED, null);
		}
		if ( EdgeId == null ) {
			EdgeId = authEdge.getEdgeId();
		} else if ( EdgeId.equals(authEdge.getEdgeId()) == false ) {
			if ( log.isWarnEnabled() ) {
				log.warn("Illegal Edge metadata post by Edge " + authEdge.getEdgeId() + " as Edge "
						+ EdgeId);
			}
			throw new AuthorizationException(Reason.ACCESS_DENIED, EdgeId);
		}
		eniwareEdgeMetadataBiz.addEniwareEdgeMetadata(EdgeId, meta);
	}

	private EniwareEdgeMetadataFilter eniwareEdgeMetadataCriteriaForcedToAuthenticatedEdge(
			final EniwareEdgeMetadataFilter criteria) {
		// verify Edge ID with security
		AuthenticatedEdge authEdge = getAuthenticatedEdge();
		if ( authEdge == null ) {
			throw new AuthorizationException(Reason.ANONYMOUS_ACCESS_DENIED, null);
		}
		if ( criteria.getEdgeId() != null && authEdge.getEdgeId().equals(criteria.getEdgeId()) ) {
			return criteria;
		}
		if ( !(criteria instanceof DatumFilterCommand) ) {
			throw new AuthorizationException(Reason.ANONYMOUS_ACCESS_DENIED, null);
		}
		DatumFilterCommand dfc = (DatumFilterCommand) criteria;
		dfc.setEdgeId(authEdge.getEdgeId());
		return dfc;
	}

	@Override
	public FilterResults<EniwareEdgeMetadataFilterMatch> findEniwareEdgeMetadata(
			EniwareEdgeMetadataFilter criteria, final List<SortDescriptor> sortDescriptors,
			final Integer offset, final Integer max) {
		return eniwareEdgeMetadataBiz.findEniwareEdgeMetadata(
				eniwareEdgeMetadataCriteriaForcedToAuthenticatedEdge(criteria), sortDescriptors, offset,
				max);
	}

	private GeneralEdgeDatumMetadataFilter metadataCriteriaForcedToAuthenticatedEdge(
			final GeneralEdgeDatumMetadataFilter criteria) {
		// verify Edge ID with security
		AuthenticatedEdge authEdge = getAuthenticatedEdge();
		if ( authEdge == null ) {
			throw new AuthorizationException(Reason.ANONYMOUS_ACCESS_DENIED, null);
		}
		if ( criteria.getEdgeId() != null && authEdge.getEdgeId().equals(criteria.getEdgeId()) ) {
			return criteria;
		}
		if ( !(criteria instanceof DatumFilterCommand) ) {
			throw new AuthorizationException(Reason.ANONYMOUS_ACCESS_DENIED, null);
		}
		DatumFilterCommand dfc = (DatumFilterCommand) criteria;
		dfc.setEdgeId(authEdge.getEdgeId());
		return dfc;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<GeneralEdgeDatumMetadataFilterMatch> findGeneralEdgeDatumMetadata(
			final GeneralEdgeDatumMetadataFilter criteria, final List<SortDescriptor> sortDescriptors,
			final Integer offset, final Integer max) {
		return datumMetadataBiz.findGeneralEdgeDatumMetadata(
				metadataCriteriaForcedToAuthenticatedEdge(criteria), sortDescriptors, offset, max);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<GeneralLocationDatumMetadataFilterMatch> findGeneralLocationDatumMetadata(
			final GeneralLocationDatumMetadataFilter criteria,
			final List<SortDescriptor> sortDescriptors, final Integer offset, final Integer max) {
		return datumMetadataBiz.findGeneralLocationDatumMetadata(criteria, sortDescriptors, offset, max);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public List<SourceLocationMatch> findPriceLocations(final SourceLocation criteria) {
		FilterResults<SourceLocationMatch> matches = findPriceLocations(criteria, null, null, null);
		List<SourceLocationMatch> resultList = new ArrayList<SourceLocationMatch>(
				matches.getReturnedResultCount());
		for ( SourceLocationMatch m : matches.getResults() ) {
			resultList.add(m);
		}
		return resultList;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<SourceLocationMatch> findPriceLocations(SourceLocation criteria,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max) {
		return priceLocationDao.findFiltered(criteria, sortDescriptors, limitFilterOffset(offset),
				limitFilterMaximum(max));
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public List<SourceLocationMatch> findWeatherLocations(SourceLocation criteria) {
		FilterResults<SourceLocationMatch> matches = findWeatherLocations(criteria, null, null, null);
		List<SourceLocationMatch> resultList = new ArrayList<SourceLocationMatch>(
				matches.getReturnedResultCount());
		for ( SourceLocationMatch m : matches.getResults() ) {
			resultList.add(m);
		}
		return resultList;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<SourceLocationMatch> findWeatherLocations(SourceLocation criteria,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max) {
		return weatherLocationDao.findFiltered(criteria, sortDescriptors, limitFilterOffset(offset),
				limitFilterMaximum(max));
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public List<LocationMatch> findLocations(Location criteria) {
		FilterResults<LocationMatch> matches = eniwareLocationDao.findFiltered(criteria, null,
				limitFilterOffset(null), limitFilterMaximum(null));
		List<LocationMatch> resultList = new ArrayList<LocationMatch>(matches.getReturnedResultCount());
		for ( LocationMatch m : matches.getResults() ) {
			resultList.add(m);
		}
		return resultList;
	}

	private AuthenticatedEdge getAuthenticatedEdge() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ( auth != null ) {
			Object principal = auth.getPrincipal();
			if ( principal instanceof AuthenticatedEdge ) {
				return (AuthenticatedEdge) principal;
			}
		}
		return null;
	}

	private GeneralLocationDatum preprocessLocationDatum(LocationDatum datum) {
		GeneralLocationDatum result = null;
		if ( datum instanceof DayDatum ) {
			result = preprocessDayDatum((DayDatum) datum);
		} else if ( datum instanceof PriceDatum ) {
			result = preprocessPriceDatum((PriceDatum) datum);
		} else if ( datum instanceof WeatherDatum ) {
			result = preprocessWeatherDatum((WeatherDatum) datum);
		}
		return result;
	}

	private GeneralEdgeDatum preprocessDatum(Datum datum) {
		GeneralEdgeDatum result = getGeneralDatumMapper().mapDatum(datum);
		return result;
	}

	private GeneralLocationDatum preprocessDayDatum(DayDatum datum) {
		// fill in location ID if not provided
		if ( datum.getLocationId() == null ) {
			EniwareEdge Edge = eniwareEdgeDao.get(datum.getEdgeId());
			if ( Edge != null ) {
				datum.setLocationId(Edge.getWeatherLocationId());
			}
		}
		GeneralLocationDatum g = getGeneralDatumMapper().mapLocationDatum(datum);
		return g;
	}

	private GeneralLocationDatum preprocessPriceDatum(PriceDatum datum) {
		GeneralLocationDatum g = getGeneralDatumMapper().mapLocationDatum(datum);
		return g;
	}

	private GeneralLocationDatum preprocessWeatherDatum(WeatherDatum datum) {
		// fill in location ID if not provided
		if ( datum.getLocationId() == null ) {
			EniwareEdge Edge = eniwareEdgeDao.get(datum.getEdgeId());
			if ( Edge != null ) {
				datum.setLocationId(Edge.getWeatherLocationId());
			}
		}
		GeneralLocationDatum g = getGeneralDatumMapper().mapLocationDatum(datum);
		return g;
	}

	private GeneralEdgeDatum checkForEdgeDatumByDate(Long EdgeId, DateTime date, String sourceId) {
		GeneralEdgeDatumPK pk = new GeneralEdgeDatumPK();
		pk.setCreated(date);
		pk.setEdgeId(EdgeId);
		pk.setSourceId(sourceId == null ? "" : sourceId);
		GeneralEdgeDatum entity = generalEdgeDatumDao.get(pk);
		return entity;
	}

	private GeneralLocationDatum checkForLocationDatumByDate(Long locationId, DateTime date,
			String sourceId) {
		GeneralLocationDatumPK pk = new GeneralLocationDatumPK();
		pk.setCreated(date);
		pk.setLocationId(locationId);
		pk.setSourceId(sourceId == null ? "" : sourceId);
		GeneralLocationDatum entity = generalLocationDatumDao.get(pk);
		return entity;
	}

	private GeneralDatumMapper getGeneralDatumMapper() {
		if ( datumMapper != null ) {
			return datumMapper;
		}
		GeneralLocationDatumDao locationDao = getGeneralLocationDatumDao();
		if ( locationDao == null ) {
			throw new UnsupportedOperationException(
					"A GeneralLocationDatumDao is required to use GeneralDatumMapper");
		}
		GeneralDatumMapper mapper = new GeneralDatumMapper();
		mapper.setGeneralLocationDatumDao(locationDao);
		datumMapper = mapper;
		return mapper;
	}

	public EniwareEdgeDao getEniwareEdgeDao() {
		return eniwareEdgeDao;
	}

	/**
	 * Set the {@link EniwareEdgeDao} so location information can be added to
	 * {@link DayDatum} and {@link WeatherDatum} objects if they are missing
	 * that information when passed to {@link #postDatum(Datum)}.
	 * 
	 * @param eniwareEdgeDao
	 *        the DAO to use
	 */
	public void setEniwareEdgeDao(EniwareEdgeDao eniwareEdgeDao) {
		this.eniwareEdgeDao = eniwareEdgeDao;
	}

	public PriceLocationDao getPriceLocationDao() {
		return priceLocationDao;
	}

	public void setPriceLocationDao(PriceLocationDao priceLocationDao) {
		this.priceLocationDao = priceLocationDao;
	}

	public WeatherLocationDao getWeatherLocationDao() {
		return weatherLocationDao;
	}

	public void setWeatherLocationDao(WeatherLocationDao weatherLocationDao) {
		this.weatherLocationDao = weatherLocationDao;
	}

	public EniwareLocationDao getEniwareLocationDao() {
		return eniwareLocationDao;
	}

	public void setEniwareLocationDao(EniwareLocationDao eniwareLocationDao) {
		this.eniwareLocationDao = eniwareLocationDao;
	}

	public int getFilteredResultsLimit() {
		return filteredResultsLimit;
	}

	public void setFilteredResultsLimit(int filteredResultsLimit) {
		this.filteredResultsLimit = filteredResultsLimit;
	}

	public GeneralEdgeDatumDao getGeneralEdgeDatumDao() {
		return generalEdgeDatumDao;
	}

	public void setGeneralEdgeDatumDao(GeneralEdgeDatumDao generalEdgeDatumDao) {
		this.generalEdgeDatumDao = generalEdgeDatumDao;
	}

	public DatumMetadataBiz getDatumMetadataBiz() {
		return datumMetadataBiz;
	}

	public void setDatumMetadataBiz(DatumMetadataBiz datumMetadataBiz) {
		this.datumMetadataBiz = datumMetadataBiz;
	}

	public GeneralLocationDatumDao getGeneralLocationDatumDao() {
		return generalLocationDatumDao;
	}

	public void setGeneralLocationDatumDao(GeneralLocationDatumDao generalLocationDatumDao) {
		this.generalLocationDatumDao = generalLocationDatumDao;
	}

	/**
	 * Get the configured Edge metadata biz.
	 * 
	 * @return the service, or {@literal null} if not configured
	 * @since 2.1
	 */
	public EniwareEdgeMetadataBiz getEniwareEdgeMetadataBiz() {
		return eniwareEdgeMetadataBiz;
	}

	/**
	 * Set the Edge metadata biz to use.
	 * 
	 * @param eniwareEdgeMetadataBiz
	 *        the service to set
	 * @since 2.1
	 */
	public void setEniwareEdgeMetadataBiz(EniwareEdgeMetadataBiz eniwareEdgeMetadataBiz) {
		this.eniwareEdgeMetadataBiz = eniwareEdgeMetadataBiz;
	}

}
