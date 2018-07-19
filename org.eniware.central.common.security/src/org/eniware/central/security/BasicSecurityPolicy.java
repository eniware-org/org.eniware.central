/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eniware.central.domain.Aggregation;
import org.eniware.central.domain.LocationPrecision;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Basic implementation of {@link SecurityPolicy}.
 
 * @version 1.1
 */
@JsonDeserialize(builder = org.eniware.central.security.BasicSecurityPolicy.Builder.class)
@JsonSerialize(using = SecurityPolicySerializer.class)
public class BasicSecurityPolicy implements SecurityPolicy, Serializable {

	private static final long serialVersionUID = -7842690205136252446L;

	/**
	 * A builder for {@link BasicSecurityPolicy} instances.
	 * 
	 * Configure properties on instances of this class, then call
	 * {@link #build()} to get a {@link BasicSecurityPolicy} instance.
	 */
	public static class Builder {

		private static final Map<Aggregation, Set<Aggregation>> MAX_AGGREGATION_CACHE = new HashMap<Aggregation, Set<Aggregation>>(
				16);
		private static final Map<LocationPrecision, Set<LocationPrecision>> MAX_LOCATION_PRECISION_CACHE = new HashMap<LocationPrecision, Set<LocationPrecision>>(
				16);

		private Set<Long> EdgeIds;
		private Set<String> sourceIds;
		private Set<Aggregation> aggregations;
		private Set<LocationPrecision> locationPrecisions;
		private Aggregation minAggregation;
		private LocationPrecision minLocationPrecision;
		private Set<String> EdgeMetadataPaths;
		private Set<String> userMetadataPaths;

		public Builder withPolicy(SecurityPolicy policy) {
			if ( policy != null ) {
				return this.withAggregations(policy.getAggregations())
						.withMinAggregation(policy.getMinAggregation())
						.withLocationPrecisions(policy.getLocationPrecisions())
						.withMinLocationPrecision(policy.getMinLocationPrecision())
						.withEdgeIds(policy.getEdgeIds()).withSourceIds(policy.getSourceIds())
						.withEdgeMetadataPaths(policy.getEdgeMetadataPaths())
						.withUserMetadataPaths(policy.getUserMetadataPaths());
			}
			return this;
		}

		public Builder withMergedPolicy(SecurityPolicy policy) {
			if ( policy != null ) {
				Builder b = this.withMergedAggregations(policy.getAggregations())
						.withMergedLocationPrecisions(policy.getLocationPrecisions())
						.withMergedEdgeIds(policy.getEdgeIds())
						.withMergedSourceIds(policy.getSourceIds())
						.withMergedEdgeMetadataPaths(policy.getEdgeMetadataPaths())
						.withMergedUserMetadataPaths(policy.getUserMetadataPaths());
				if ( policy.getMinAggregation() != null ) {
					b = b.withMinAggregation(policy.getMinAggregation());
				}
				if ( policy.getMinLocationPrecision() != null ) {
					b = b.withMinLocationPrecision(policy.getMinLocationPrecision());
				}
				return b;
			}
			return this;
		}

		public Builder withEdgeIds(Set<Long> EdgeIds) {
			this.EdgeIds = (EdgeIds == null || EdgeIds.isEmpty() ? null
					: Collections.unmodifiableSet(EdgeIds));
			return this;
		}

		public Builder withEdgeMetadataPaths(Set<String> EdgeMetadataPaths) {
			this.EdgeMetadataPaths = (EdgeMetadataPaths == null || EdgeMetadataPaths.isEmpty() ? null
					: Collections.unmodifiableSet(EdgeMetadataPaths));
			return this;
		}

		public Builder withUserMetadataPaths(Set<String> userMetadataPaths) {
			this.userMetadataPaths = (userMetadataPaths == null || userMetadataPaths.isEmpty() ? null
					: Collections.unmodifiableSet(userMetadataPaths));
			return this;
		}

		public Builder withSourceIds(Set<String> sourceIds) {
			this.sourceIds = (sourceIds == null || sourceIds.isEmpty() ? null
					: Collections.unmodifiableSet(sourceIds));
			return this;
		}

		public Builder withAggregations(Set<Aggregation> aggregations) {
			this.aggregations = aggregations;
			return this;
		}

		public Builder withLocationPrecisions(Set<LocationPrecision> locationPrecisions) {
			this.locationPrecisions = locationPrecisions;
			return this;
		}

		public Builder withMergedEdgeIds(Set<Long> EdgeIds) {
			Set<Long> set = EdgeIds;
			if ( this.EdgeIds != null && !this.EdgeIds.isEmpty() ) {
				set = new LinkedHashSet<Long>(this.EdgeIds);
				if ( EdgeIds != null ) {
					set.addAll(EdgeIds);
				}
			}
			return withEdgeIds(set);
		}

		public Builder withMergedEdgeMetadataPaths(Set<String> EdgeMetadataPaths) {
			Set<String> set = EdgeMetadataPaths;
			if ( this.EdgeMetadataPaths != null && !this.EdgeMetadataPaths.isEmpty() ) {
				set = new LinkedHashSet<String>(this.EdgeMetadataPaths);
				if ( EdgeMetadataPaths != null ) {
					set.addAll(EdgeMetadataPaths);
				}
			}
			return withEdgeMetadataPaths(set);
		}

		public Builder withMergedUserMetadataPaths(Set<String> userMetadataPaths) {
			Set<String> set = userMetadataPaths;
			if ( this.userMetadataPaths != null && !this.userMetadataPaths.isEmpty() ) {
				set = new LinkedHashSet<String>(this.userMetadataPaths);
				if ( userMetadataPaths != null ) {
					set.addAll(userMetadataPaths);
				}
			}
			return withUserMetadataPaths(set);
		}

		public Builder withMergedSourceIds(Set<String> sourceIds) {
			Set<String> set = sourceIds;
			if ( this.sourceIds != null && !this.sourceIds.isEmpty() ) {
				set = new LinkedHashSet<String>(this.sourceIds);
				if ( sourceIds != null ) {
					set.addAll(sourceIds);
				}
			}
			return withSourceIds(set);
		}

		public Builder withMergedAggregations(Set<Aggregation> aggregations) {
			Set<Aggregation> set = aggregations;
			if ( this.aggregations != null && !this.aggregations.isEmpty() ) {
				if ( aggregations != null ) {
					set = new LinkedHashSet<Aggregation>(this.aggregations);
					set.addAll(aggregations);
				} else {
					set = this.aggregations;
				}
			}
			return withAggregations(set);
		}

		public Builder withMergedLocationPrecisions(Set<LocationPrecision> locationPrecisions) {
			Set<LocationPrecision> set = locationPrecisions;
			if ( this.locationPrecisions != null && !this.locationPrecisions.isEmpty() ) {
				if ( locationPrecisions != null ) {
					set = new LinkedHashSet<LocationPrecision>(this.locationPrecisions);
					set.addAll(locationPrecisions);
				} else {
					set = this.locationPrecisions;
				}
			}
			return withLocationPrecisions(set);
		}

		public Builder withMinAggregation(Aggregation minAggregation) {
			this.minAggregation = minAggregation;
			return this;
		}

		private Set<Aggregation> buildAggregations() {
			if ( minAggregation == null && aggregations != null && !aggregations.isEmpty() ) {
				return Collections.unmodifiableSet(aggregations);
			} else if ( minAggregation == null ) {
				return null;
			}
			Set<Aggregation> result = MAX_AGGREGATION_CACHE.get(minAggregation);
			if ( result != null ) {
				return result;
			}
			result = new HashSet<Aggregation>(16);
			for ( Aggregation agg : Aggregation.values() ) {
				if ( agg.compareLevel(minAggregation) > -1 ) {
					result.add(agg);
				}
			}
			result = Collections.unmodifiableSet(EnumSet.copyOf(result));
			MAX_AGGREGATION_CACHE.put(minAggregation, result);
			return result;
		}

		/**
		 * Treat the configured {@code locationPrecisions} set as a single
		 * minimum value or a list of exact values.
		 * 
		 * By default if {@code locationPrecisions} is configured with a single
		 * value it will be treated as a <em>minimum</em> value, and any
		 * {@link LocationPrecision} with a
		 * {@link LocationPrecision#getPrecision()} equal to or higher than that
		 * value's level will be included in the generated
		 * {@link BasicSecurityPolicy#getLocationPrecisions()} set. Set this to
		 * {@code false} to disable that behavior and treat
		 * {@code locationPrecisions} as the exact values to include in the
		 * generated {@link BasicSecurityPolicy#getLocationPrecisions()} set.
		 * 
		 * @param minLocationPrecision
		 *        {@code false} to treat configured location precision values
		 *        as-is, {@code true} to treat as a minimum threshold
		 * @return The builder.
		 */
		public Builder withMinLocationPrecision(LocationPrecision minLocationPrecision) {
			this.minLocationPrecision = minLocationPrecision;
			return this;
		}

		private Set<LocationPrecision> buildLocationPrecisions() {
			if ( minLocationPrecision == null && locationPrecisions != null
					&& !locationPrecisions.isEmpty() ) {
				return Collections.unmodifiableSet(locationPrecisions);
			} else if ( minLocationPrecision == null ) {
				return null;
			}
			Set<LocationPrecision> result = MAX_LOCATION_PRECISION_CACHE.get(minLocationPrecision);
			if ( result != null ) {
				return result;
			}
			result = new HashSet<LocationPrecision>(16);
			for ( LocationPrecision agg : LocationPrecision.values() ) {
				if ( agg.comparePrecision(minLocationPrecision) > -1 ) {
					result.add(agg);
				}
			}
			result = Collections.unmodifiableSet(EnumSet.copyOf(result));
			MAX_LOCATION_PRECISION_CACHE.put(minLocationPrecision, result);
			return result;
		}

		public BasicSecurityPolicy build() {
			return new BasicSecurityPolicy(EdgeIds, sourceIds, buildAggregations(), minAggregation,
					buildLocationPrecisions(), minLocationPrecision, EdgeMetadataPaths,
					userMetadataPaths);
		}

	}

	private final Set<Long> EdgeIds;
	private final Set<String> sourceIds;
	private final Set<Aggregation> aggregations;
	private final Set<LocationPrecision> locationPrecisions;
	private final Aggregation minAggregation;
	private final LocationPrecision minLocationPrecision;
	private final Set<String> EdgeMetadataPaths;
	private final Set<String> userMetadataPaths;

	/**
	 * Constructor.
	 * 
	 * @param EdgeIds
	 *        The Edge IDs to restrict to, or {@code null} for no restriction.
	 * @param sourceIds
	 *        The source ID to restrict to, or {@code null} for no restriction.
	 * @param aggregations
	 *        The aggregations to restrict to, or {@code null} for no
	 *        restriction.
	 * @param minAggregation
	 *        If specified, a minimum aggregation level that is allowed.
	 * @param locationPrecisions
	 *        The location precisions to restrict to, or {@code null} for no
	 *        restriction.
	 * @param minALocationPrecision
	 *        If specified, a minimum location precision that is allowed.
	 * @param EdgeMetadataPaths
	 *        The {@code EniwareEdgeMetadata} paths to restrict to, or
	 *        {@code null} for no restriction.
	 * @param userMetadataPaths
	 *        The {@code UserEdgeMetadata} paths to restrict to, or {@code null}
	 *        for no restriction.
	 */
	public BasicSecurityPolicy(Set<Long> EdgeIds, Set<String> sourceIds, Set<Aggregation> aggregations,
			Aggregation minAggregation, Set<LocationPrecision> locationPrecisions,
			LocationPrecision minLocationPrecision, Set<String> EdgeMetadataPaths,
			Set<String> userMetadataPaths) {
		super();
		this.EdgeIds = EdgeIds;
		this.sourceIds = sourceIds;
		this.aggregations = aggregations;
		this.minAggregation = minAggregation;
		this.locationPrecisions = locationPrecisions;
		this.minLocationPrecision = minLocationPrecision;
		this.EdgeMetadataPaths = EdgeMetadataPaths;
		this.userMetadataPaths = userMetadataPaths;
	}

	@Override
	public Set<Long> getEdgeIds() {
		return EdgeIds;
	}

	@Override
	public Set<String> getSourceIds() {
		return sourceIds;
	}

	@Override
	public Set<Aggregation> getAggregations() {
		return aggregations;
	}

	@Override
	public Set<LocationPrecision> getLocationPrecisions() {
		return locationPrecisions;
	}

	@Override
	public Aggregation getMinAggregation() {
		return minAggregation;
	}

	@Override
	public LocationPrecision getMinLocationPrecision() {
		return minLocationPrecision;
	}

	@Override
	public Set<String> getEdgeMetadataPaths() {
		return EdgeMetadataPaths;
	}

	@Override
	public Set<String> getUserMetadataPaths() {
		return userMetadataPaths;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aggregations == null) ? 0 : aggregations.hashCode());
		result = prime * result + ((locationPrecisions == null) ? 0 : locationPrecisions.hashCode());
		result = prime * result + ((minAggregation == null) ? 0 : minAggregation.hashCode());
		result = prime * result + ((minLocationPrecision == null) ? 0 : minLocationPrecision.hashCode());
		result = prime * result + ((EdgeIds == null) ? 0 : EdgeIds.hashCode());
		result = prime * result + ((sourceIds == null) ? 0 : sourceIds.hashCode());
		result = prime * result + ((EdgeMetadataPaths == null) ? 0 : EdgeMetadataPaths.hashCode());
		result = prime * result + ((userMetadataPaths == null) ? 0 : userMetadataPaths.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( !(obj instanceof BasicSecurityPolicy) ) {
			return false;
		}
		BasicSecurityPolicy other = (BasicSecurityPolicy) obj;
		if ( aggregations == null ) {
			if ( other.aggregations != null ) {
				return false;
			}
		} else if ( !aggregations.equals(other.aggregations) ) {
			return false;
		}
		if ( locationPrecisions == null ) {
			if ( other.locationPrecisions != null ) {
				return false;
			}
		} else if ( !locationPrecisions.equals(other.locationPrecisions) ) {
			return false;
		}
		if ( minAggregation != other.minAggregation ) {
			return false;
		}
		if ( minLocationPrecision != other.minLocationPrecision ) {
			return false;
		}
		if ( EdgeIds == null ) {
			if ( other.EdgeIds != null ) {
				return false;
			}
		} else if ( !EdgeIds.equals(other.EdgeIds) ) {
			return false;
		}
		if ( sourceIds == null ) {
			if ( other.sourceIds != null ) {
				return false;
			}
		} else if ( !sourceIds.equals(other.sourceIds) ) {
			return false;
		}
		if ( EdgeMetadataPaths == null ) {
			if ( other.EdgeMetadataPaths != null ) {
				return false;
			}
		} else if ( !EdgeMetadataPaths.equals(other.EdgeMetadataPaths) ) {
			return false;
		}
		if ( userMetadataPaths == null ) {
			if ( other.userMetadataPaths != null ) {
				return false;
			}
		} else if ( !userMetadataPaths.equals(other.userMetadataPaths) ) {
			return false;
		}
		return true;
	}

}
