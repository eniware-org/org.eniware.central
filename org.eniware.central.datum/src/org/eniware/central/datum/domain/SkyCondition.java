/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * A standardized enumeration of weather sky conditions.
 *
 * @version 1.1
 */
public enum SkyCondition {

	/** Clear night. */
	ClearNight(-1),

	/** Clear day. */
	Clear(1),

	/** Few clouds night. */
	FewCloudsNight(-2),

	/** Few couds day. */
	FewClouds(2),

	/** Fog. */
	Fog(3),

	/** Overcast. */
	Overcast(4),

	/** Severe alert. */
	SevereAlert(5),

	/** Showers scattered. */
	ShowersScattered(6),

	/** Showers. */
	Showers(7),

	/** Rain. */
	Rain(8),

	/** Snow. */
	Snow(9),

	/** Storm. */
	Storm(10),

	/** Thunder. */
	Thunder(11),

	/** Windy */
	Windy(12),

	/** Hail. */
	Hail(13),

	/** Haze. */
	Haze(14),

	/** Rain and snow. */
	RainAndSnow(15);

	final private int code;

	private SkyCondition(int code) {
		this.code = code;
	}

	/**
	 * Get a night-time equivalent value for a given SkyCondition.
	 * 
	 * <p>
	 * Some conditions have day and night counterparts. If this object is a day
	 * condition and has a night equivalent, the night equivalent will be
	 * returned. Otherwise {@code this} will be returned.
	 * </p>
	 * 
	 * @return the night time equivalent SkyCondition
	 */
	public SkyCondition getNightEquivalent() {
		switch (this) {
			case Clear:
				return ClearNight;

			case FewClouds:
				return FewCloudsNight;

			default:
				return this;
		}
	}

	/**
	 * Get a day-time equivalent value for a given SkyCondition.
	 * 
	 * <p>
	 * Some conditions have day and night counterparts. If this object is a
	 * night condition and has a day equivalent, the day equivalent will be
	 * returned. Otherwise {@code this} will be returned.
	 * </p>
	 * 
	 * @return the day time equivalent SkyCondition
	 */
	public SkyCondition getDayEquivalent() {
		switch (this) {
			case ClearNight:
				return Clear;

			case FewCloudsNight:
				return FewClouds;

			default:
				return this;
		}
	}

	/**
	 * Map a string to a SkyCondition using a mapping of regular expressions.
	 * 
	 * @param condition
	 *        the string to map
	 * @param mapping
	 *        the mapping of expressions to SkyCondition objects
	 * @return the first matching result, or <em>null</em> if no match is found
	 */
	public static SkyCondition mapStringValue(String condition, Map<Pattern, SkyCondition> mapping) {
		if ( condition == null || condition.length() < 1 ) {
			return null;
		}
		for ( Map.Entry<Pattern, SkyCondition> me : mapping.entrySet() ) {
			if ( me.getKey().matcher(condition).find() ) {
				return me.getValue();
			}
		}
		return null;
	}

	public int getCode() {
		return code;
	}

}
