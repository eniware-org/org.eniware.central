/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.aop;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eniware.central.datum.domain.AggregateGeneralEdgeDatumFilter;
import org.eniware.central.datum.domain.DatumFilter;
import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.datum.domain.GeneralEdgeDatumFilter;
import org.eniware.central.datum.domain.EdgeDatumFilter;
import org.eniware.central.domain.Filter;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.query.biz.QueryBiz;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityPolicy;
import org.eniware.central.security.SecurityPolicyEnforcer;
import org.eniware.central.security.SecurityUtils;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.support.AuthorizationSupport;
import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;

/**
 * Security enforcing AOP aspect for {@link QueryBiz}.
 * 
 * @version 1.5
 */
@Aspect
public class QuerySecurityAspect extends AuthorizationSupport {

	public static final String FILTER_KEY_Edge_ID = "EdgeId";
	public static final String FILTER_KEY_Edge_IDS = "EdgeIds";

	private Set<String> EdgeIdNotRequiredSet;

	/**
	 * Constructor.
	 * 
	 * @param userEdgeDao
	 *        the UserEdgeDao
	 */
	public QuerySecurityAspect(UserEdgeDao userEdgeDao) {
		super(userEdgeDao);
		AntPathMatcher antMatch = new AntPathMatcher();
		antMatch.setCachePatterns(false);
		antMatch.setCaseSensitive(true);
		setPathMatcher(antMatch);
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.query.biz.*.getReportableInterval(..)) && args(EdgeId,sourceId,..)")
	public void EdgeReportableInterval(Long EdgeId, String sourceId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.query.biz.*.getAvailableSources(..)) && args(EdgeId,..)")
	public void EdgeReportableSources(Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.query.biz.*.getMostRecentWeatherConditions(..)) && args(EdgeId,..)")
	public void EdgeMostRecentWeatherConditions(Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.query.biz.*.findFiltered*(..)) && args(filter,..)")
	public void EdgeDatumFilter(Filter filter) {
	}

	@Around(value = "EdgeDatumFilter(filter)")
	public Object userEdgeFilterAccessCheck(ProceedingJoinPoint pjp, Filter filter) throws Throwable {
		final boolean isQueryBiz = (pjp.getTarget() instanceof QueryBiz);
		final SecurityPolicy policy = getActiveSecurityPolicy();

		if ( policy != null && policy.getSourceIds() != null && !policy.getSourceIds().isEmpty()
				&& filter instanceof GeneralEdgeDatumFilter
				&& ((GeneralEdgeDatumFilter) filter).getSourceId() == null ) {
			// no source IDs provided, but policy restricts source IDs.
			// restrict the filter to the available source IDs if using a DatumFilterCommand,
			// and let call to userEdgeAccessCheck later on filter out restricted values
			if ( isQueryBiz && filter instanceof DatumFilterCommand ) {
				QueryBiz target = (QueryBiz) pjp.getTarget();
				DatumFilterCommand f = (DatumFilterCommand) filter;
				Set<String> availableSources = target.getAvailableSources(f.getEdgeId(),
						f.getStartDate(), f.getEndDate());
				if ( availableSources != null && !availableSources.isEmpty() ) {
					f.setSourceIds(availableSources.toArray(new String[availableSources.size()]));
				}
			}
		}

		Filter f = userEdgeAccessCheck(filter);
		if ( f == filter ) {
			return pjp.proceed();
		}

		// if an aggregate was injected (enforced) on the filter, then the join point method
		// might need to change to an aggregate one, e.g. from findFilteredGeneralEdgeDatum
		// to findFilteredAggregateGeneralEdgeDatum. This _could_ break the calling code if
		// it is expecting a specific result type, but in many cases it is simply returning
		// the result as JSON to some HTTP client and the difference does not matter.
		if ( isQueryBiz && f instanceof AggregateGeneralEdgeDatumFilter
				&& ((AggregateGeneralEdgeDatumFilter) f).getAggregation() != null
				&& pjp.getSignature().getName().equals("findFilteredGeneralEdgeDatum") ) {
			// redirect this to findFilteredAggregateGeneralEdgeDatum
			QueryBiz target = (QueryBiz) pjp.getTarget();
			Object[] args = pjp.getArgs();
			@SuppressWarnings("unchecked")
			List<SortDescriptor> sorts = (List<SortDescriptor>) args[1];
			return target.findFilteredAggregateGeneralEdgeDatum((AggregateGeneralEdgeDatumFilter) f,
					sorts, (Integer) args[2], (Integer) args[3]);
		}
		Object[] args = pjp.getArgs();
		args[0] = f;
		return pjp.proceed(args);
	}

	/**
	 * Enforce Edge ID and source ID policy restrictions when requesting the
	 * available sources of a Edge.
	 * 
	 * First the Edge ID is verified. Then, for all returned source ID values,
	 * if the active policy has no source ID restrictions return all values,
	 * otherwise remove any value not included in the policy.
	 * 
	 * @param pjp
	 *        The join point.
	 * @param EdgeId
	 *        The Edge ID.
	 * @return The set of String source IDs.
	 * @throws Throwable
	 */
	@Around("EdgeReportableSources(EdgeId)")
	public Object reportableSourcesAccessCheck(ProceedingJoinPoint pjp, Long EdgeId) throws Throwable {
		// verify Edge ID
		requireEdgeReadAccess(EdgeId);

		// verify source IDs in result
		@SuppressWarnings("unchecked")
		Set<String> result = (Set<String>) pjp.proceed();
		if ( result == null || result.isEmpty() ) {
			return result;
		}
		SecurityPolicy policy = getActiveSecurityPolicy();
		if ( policy == null ) {
			return result;
		}
		Set<String> allowedSourceIds = policy.getSourceIds();
		if ( allowedSourceIds == null || allowedSourceIds.isEmpty() ) {
			return result;
		}
		Authentication authentication = SecurityUtils.getCurrentAuthentication();
		Object principal = (authentication != null ? authentication.getPrincipal() : null);
		SecurityPolicyEnforcer enforcer = new SecurityPolicyEnforcer(policy, principal, null,
				getPathMatcher());
		try {
			String[] resultSourceIds = enforcer
					.verifySourceIds(result.toArray(new String[result.size()]));
			result = new LinkedHashSet<String>(Arrays.asList(resultSourceIds));
		} catch ( AuthorizationException e ) {
			// ignore, and just  map to empty set
			result = Collections.emptySet();
		}
		return result;
	}

	/**
	 * Enforce Edge ID and source ID policy restrictions when requesting a
	 * reportable interval.
	 * 
	 * If the active policy has source ID restrictions, then if no
	 * {@code sourceId} is provided fill in the first available value from the
	 * policy. Otherwise, if {@code sourceId} is provided, check that value is
	 * allowed by the policy.
	 * 
	 * @param pjp
	 *        The join point.
	 * @param EdgeId
	 *        The Edge ID.
	 * @param sourceId
	 *        The source ID, or {@code null}.
	 * @return The reportable interval.
	 * @throws Throwable
	 *         If any error occurs.
	 */
	@Around("EdgeReportableInterval(EdgeId, sourceId)")
	public Object reportableIntervalAccessCheck(ProceedingJoinPoint pjp, Long EdgeId, String sourceId)
			throws Throwable {
		// verify Edge ID
		requireEdgeReadAccess(EdgeId);

		// now verify source ID
		SecurityPolicy policy = getActiveSecurityPolicy();
		if ( policy == null ) {
			return pjp.proceed();
		}

		Set<String> allowedSourceIds = policy.getSourceIds();
		if ( allowedSourceIds != null && !allowedSourceIds.isEmpty() ) {
			Authentication authentication = SecurityUtils.getCurrentAuthentication();
			Object principal = (authentication != null ? authentication.getPrincipal() : null);
			if ( sourceId == null ) {
				// force the first allowed source ID
				sourceId = allowedSourceIds.iterator().next();
				log.info("Access RESTRICTED to source {} for {}", sourceId, principal);
				Object[] args = pjp.getArgs();
				args[1] = sourceId;
				return pjp.proceed(args);
			} else if ( !allowedSourceIds.contains(sourceId) ) {
				log.warn("Access DENIED to source {} for {}", sourceId, principal);
				throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, sourceId);
			}
		}

		return pjp.proceed();
	}

	/**
	 * Allow the current user (or current Edge) access to Edge data.
	 * 
	 * @param EdgeId
	 *        the ID of the Edge to verify
	 */
	@Before("EdgeMostRecentWeatherConditions(EdgeId)")
	public void userEdgeAccessCheck(Long EdgeId) {
		if ( EdgeId == null ) {
			return;
		}
		requireEdgeReadAccess(EdgeId);
	}

	/**
	 * Enforce security policies on a {@link Filter}.
	 * 
	 * @param filter
	 *        The filter to verify.
	 * @return A possibly modified filter based on security policies.
	 * @throws AuthorizationException
	 *         if any authorization error occurs
	 */
	public <T extends Filter> T userEdgeAccessCheck(T filter) {
		Long[] EdgeIds = null;
		boolean EdgeIdRequired = true;
		if ( filter instanceof EdgeDatumFilter ) {
			EdgeDatumFilter cmd = (EdgeDatumFilter) filter;
			EdgeIdRequired = isEdgeIdRequired(cmd);
			if ( EdgeIdRequired ) {
				EdgeIds = cmd.getEdgeIds();
			}
		} else {
			EdgeIdRequired = false;
			Map<String, ?> f = filter.getFilter();
			if ( f.containsKey(FILTER_KEY_Edge_IDS) ) {
				EdgeIds = getLongArrayParameter(f, FILTER_KEY_Edge_IDS);
			} else if ( f.containsKey(FILTER_KEY_Edge_ID) ) {
				EdgeIds = getLongArrayParameter(f, FILTER_KEY_Edge_ID);
			}
		}
		if ( !EdgeIdRequired ) {
			return filter;
		}
		if ( EdgeIds == null || EdgeIds.length < 1 ) {
			log.warn("Access DENIED; no Edge ID provided");
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, null);
		}
		for ( Long EdgeId : EdgeIds ) {
			userEdgeAccessCheck(EdgeId);
		}

		return policyEnforcerCheck(filter);
	}

	/**
	 * Check if a Edge ID is required of a filter instance. This will return
	 * <em>true</em> unless the {@link #getEdgeIdNotRequiredSet()} set contains
	 * the value returned by {@link DatumFilter#getType()}.
	 * 
	 * @param filter
	 *        the filter
	 * @return <em>true</em> if a Edge ID is required for the given filter
	 */
	private boolean isEdgeIdRequired(DatumFilter filter) {
		final String type = (filter == null || filter.getType() == null ? null
				: filter.getType().toLowerCase());
		return (EdgeIdNotRequiredSet == null || !EdgeIdNotRequiredSet.contains(type));
	}

	private Long[] getLongArrayParameter(final Map<String, ?> map, final String key) {
		Long[] result = null;
		if ( map.containsKey(key) ) {
			Object o = map.get(key);
			if ( o instanceof Long[] ) {
				result = (Long[]) o;
			} else if ( o instanceof Long ) {
				result = new Long[] { (Long) o };
			}
		}
		return result;
	}

	public Set<String> getEdgeIdNotRequiredSet() {
		return EdgeIdNotRequiredSet;
	}

	public void setEdgeIdNotRequiredSet(Set<String> EdgeIdNotRequiredSet) {
		this.EdgeIdNotRequiredSet = EdgeIdNotRequiredSet;
	}

}
