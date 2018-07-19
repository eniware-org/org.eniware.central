/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.aop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eniware.central.datum.biz.DatumMetadataBiz;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilter;
import org.eniware.central.datum.domain.EdgeSourcePK;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityPolicy;
import org.eniware.central.security.SecurityPolicyEnforcer;
import org.eniware.central.security.SecurityUtils;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.support.AuthorizationSupport;
import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;

/**
 * Security AOP support for {@link DatumMetadataBiz}.
 *
 * @version 1.2
 */
@Aspect
public class DatumMetadataSecurityAspect extends AuthorizationSupport {

	/**
	 * The default value for the {@link #setLocaitonMetadataAdminRoles(Set)}
	 * property, contains the single role {@code ROLE_LOC_META_ADMIN}.
	 */
	public static final Set<String> DEFAULT_LOCATION_METADATA_ADMIN_ROLES = Collections
			.singleton("ROLE_LOC_META_ADMIN");

	private Set<String> locaitonMetadataAdminRoles = DEFAULT_LOCATION_METADATA_ADMIN_ROLES;

	/**
	 * Constructor.
	 * 
	 * @param userEdgeDao
	 *        the UserEdgeDao to use
	 */
	public DatumMetadataSecurityAspect(UserEdgeDao userEdgeDao) {
		super(userEdgeDao);
		AntPathMatcher antMatch = new AntPathMatcher();
		antMatch.setCachePatterns(false);
		antMatch.setCaseSensitive(true);
		setPathMatcher(antMatch);
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.datum.biz.DatumMetadata*.addGeneralEdge*(..)) && args(EdgeId,..)")
	public void addMetadata(Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.datum.biz.DatumMetadata*.storeGeneralEdge*(..)) && args(EdgeId,..)")
	public void storeMetadata(Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.datum.biz.DatumMetadata*.removeGeneralEdge*(..)) && args(EdgeId,..)")
	public void removeMetadata(Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.datum.biz.DatumMetadata*.findGeneralEdge*(..)) && args(filter,..)")
	public void findMetadata(GeneralEdgeDatumMetadataFilter filter) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.datum.biz.DatumMetadata*.getGeneralEdgeDatumMetadataFilteredSources(..)) && args(EdgeIds,..)")
	public void getMetadataFilteredSources(Long[] EdgeIds) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.datum.biz.DatumMetadata*.addGeneralLocation*(..)) && args(locationId,..)")
	public void addLocationMetadata(Long locationId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.datum.biz.DatumMetadata*.storeGeneralLocation*(..)) && args(locationId,..)")
	public void storeLocationMetadata(Long locationId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.datum.biz.DatumMetadata*.removeGeneralLocation*(..)) && args(locationId,..)")
	public void removeLocationMetadata(Long locationId) {
	}

	/**
	 * Check access to modifying datum metadata.
	 * 
	 * @param EdgeId
	 *        the ID of the Edge to verify
	 */
	@Before("addMetadata(EdgeId) || storeMetadata(EdgeId) || removeMetadata(EdgeId)")
	public void updateMetadataCheck(Long EdgeId) {
		requireEdgeWriteAccess(EdgeId);
	}

	/**
	 * Check access to reading datum metadata.
	 * 
	 * @param EdgeId
	 *        the ID of the Edge to verify
	 */
	@Before("findMetadata(filter)")
	public void readMetadataCheck(GeneralEdgeDatumMetadataFilter filter) {
		requireEdgeReadAccess(filter == null ? null : filter.getEdgeId());
	}

	/**
	 * Enforce Edge ID and source ID policy restrictions when requesting the
	 * available sources for Edge datum metadata.
	 * 
	 * First the Edge ID is verified. Then, for all returned source ID values,
	 * if the active policy has no source ID restrictions return all values,
	 * otherwise remove any value not included in the policy.
	 * 
	 * @param pjp
	 *        The join point.
	 * @param EdgeIds
	 *        The Edge IDs.
	 * @return The set of EdgeSourcePK results.
	 * @throws Throwable
	 * @since 1.2
	 */
	@Around("getMetadataFilteredSources(EdgeIds)")
	public Object filteredMetadataSourcesAccessCheck(ProceedingJoinPoint pjp, Long[] EdgeIds)
			throws Throwable {
		// verify Edge IDs
		if ( EdgeIds != null ) {
			for ( Long EdgeId : EdgeIds ) {
				requireEdgeReadAccess(EdgeId);
			}
		}

		// verify source IDs in result
		@SuppressWarnings("unchecked")
		Set<EdgeSourcePK> result = (Set<EdgeSourcePK>) pjp.proceed();
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
			List<String> inputSourceIds = new ArrayList<String>(result.size());
			for ( EdgeSourcePK pk : result ) {
				inputSourceIds.add(pk.getSourceId());
			}
			String[] resultSourceIds = enforcer
					.verifySourceIds(inputSourceIds.toArray(new String[inputSourceIds.size()]));
			Set<String> allowedSourceIdSet = new HashSet<String>(Arrays.asList(resultSourceIds));
			Set<EdgeSourcePK> restricted = new LinkedHashSet<EdgeSourcePK>(resultSourceIds.length);
			for ( EdgeSourcePK oneResult : result ) {
				if ( allowedSourceIdSet.contains(oneResult.getSourceId()) ) {
					restricted.add(oneResult);
				}
			}
			result = restricted;
		} catch ( AuthorizationException e ) {
			// ignore, and just  map to empty set
			result = Collections.emptySet();
		}
		return result;
	}

	@Before("addLocationMetadata(locationId) || storeLocationMetadata(locationId) || removeLocationMetadata(locationId)")
	public void updateLocationMetadataCheck(Long locationId) {
		SecurityUtils.requireAnyRole(locaitonMetadataAdminRoles);
	}

	/**
	 * Set the set of roles required to administer location metadata. If more
	 * than one role is provided, any one role must match for the authorization
	 * to succeed.
	 * 
	 * @param locaitonMetadataAdminRoles
	 *        the set of roles
	 * @since 1.2
	 */
	public void setLocaitonMetadataAdminRoles(Set<String> locaitonMetadataAdminRoles) {
		if ( locaitonMetadataAdminRoles == null || locaitonMetadataAdminRoles.size() < 1 ) {
			throw new IllegalArgumentException(
					"The roleLocationMetadataAdmin argument must not be null or empty.");
		}
		Set<String> capitalized;
		if ( locaitonMetadataAdminRoles.size() == 1 ) {
			capitalized = Collections
					.singleton(locaitonMetadataAdminRoles.iterator().next().toUpperCase());
		} else {
			capitalized = new HashSet<String>(locaitonMetadataAdminRoles.size());
			for ( String role : locaitonMetadataAdminRoles ) {
				capitalized.add(role.toUpperCase());
			}
		}
		this.locaitonMetadataAdminRoles = capitalized;
	}

}
