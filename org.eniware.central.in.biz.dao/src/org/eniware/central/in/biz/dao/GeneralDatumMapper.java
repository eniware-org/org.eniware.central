/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.biz.dao;

import java.util.HashMap;
import java.util.Map;

import org.eniware.central.datum.dao.GeneralLocationDatumDao;
import org.eniware.central.datum.domain.ConsumptionDatum;
import org.eniware.central.datum.domain.Datum;
import org.eniware.central.datum.domain.DatumMappingInfo;
import org.eniware.central.datum.domain.DayDatum;
import org.eniware.central.datum.domain.GeneralLocationDatum;
import org.eniware.central.datum.domain.GeneralNodeDatum;
import org.eniware.central.datum.domain.HardwareControlDatum;
import org.eniware.central.datum.domain.LocationDatum;
import org.eniware.central.datum.domain.NodeDatum;
import org.eniware.central.datum.domain.PowerDatum;
import org.eniware.central.datum.domain.PriceDatum;
import org.eniware.central.datum.domain.WeatherDatum;
import org.eniware.domain.GeneralLocationDatumSamples;
import org.eniware.domain.GeneralEdgeDatumSamples;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map {@link Datum} instances into {@link GeneralNodeDatum} instances, to help
 * migrate away from {@link Datum}.
 *
 * @version 1.1
 */
public class GeneralDatumMapper {

	private final DateTimeFormatter localTimeFormatter;
	private GeneralLocationDatumDao generalLocationDatumDao;
	private final Map<String, DatumMappingInfo> datumMappingInfoCache;

	private final Logger log = LoggerFactory.getLogger(getClass());

	public GeneralDatumMapper() {
		super();
		localTimeFormatter = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':')
				.appendMinuteOfHour(2).toFormatter();
		datumMappingInfoCache = new HashMap<String, DatumMappingInfo>(16);
	}

	/**
	 * Map a {@link Datum} object into a {@link GeneralNodeDatum} object.
	 * 
	 * @param datum
	 *        The datum to map.
	 * @return The mapped datum, or <em>null</em> if not supported.
	 */
	public GeneralNodeDatum mapDatum(Datum datum) {
		GeneralNodeDatum g = null;
		if ( datum instanceof ConsumptionDatum ) {
			g = mapConsumptionDatum((ConsumptionDatum) datum);
		} else if ( datum instanceof HardwareControlDatum ) {
			g = mapHardwareControlDatum((HardwareControlDatum) datum);
		} else if ( datum instanceof PowerDatum ) {
			g = mapPowerDatum((PowerDatum) datum);
		}
		log.trace("Mapped Datum {} to general form {}", datum, g);
		return g;
	}

	private GeneralNodeDatum mapBaseGeneralNodeDatumProperties(NodeDatum datum) {
		GeneralNodeDatum g = new GeneralNodeDatum();
		g.setCreated(datum.getCreated() != null ? datum.getCreated() : new DateTime());
		g.setNodeId(datum.getNodeId());
		return g;
	}

	private GeneralNodeDatum mapConsumptionDatum(ConsumptionDatum datum) {
		assert datum != null;
		assert datum.getSourceId() != null;

		GeneralEdgeDatumSamples samples = new GeneralEdgeDatumSamples();
		if ( datum.getWatts() != null ) {
			samples.putInstantaneousSampleValue("watts", datum.getWatts());
		}
		if ( datum.getWattHourReading() != null ) {
			samples.putAccumulatingSampleValue("wattHours", datum.getWattHourReading());
		}

		GeneralNodeDatum g = mapBaseGeneralNodeDatumProperties(datum);
		g.setSourceId(datum.getSourceId());
		if ( samples.getSampleData() != null && samples.getSampleData().size() > 0 ) {
			g.setSamples(samples);
		}
		return g;
	}

	private GeneralNodeDatum mapHardwareControlDatum(HardwareControlDatum datum) {
		assert datum != null;

		// The source_id value could be in form source;prop where 'prop' is a property name.
		// If this is the case, we store only 'source' as the source_id, and name the JSON key the value of 'prop'.
		// If 'prop' is not defined, we name the JSON key 'val'.
		String[] source = (datum.getSourceId() != null ? datum.getSourceId() : "HardwareControl").split(
				";", 2);
		String sourceId = source[0];
		String propKey = "val";
		if ( source.length > 1 ) {
			propKey = source[1];
		}

		GeneralEdgeDatumSamples samples = new GeneralEdgeDatumSamples();
		if ( datum.getIntegerValue() != null ) {
			samples.putStatusSampleValue(propKey, datum.getIntegerValue());
		} else if ( datum.getFloatValue() != null ) {
			samples.putStatusSampleValue(propKey, datum.getFloatValue());
		}

		GeneralNodeDatum g = mapBaseGeneralNodeDatumProperties(datum);
		g.setSourceId(sourceId);
		if ( samples.getSampleData() != null && samples.getSampleData().size() > 0 ) {
			g.setSamples(samples);
		}
		return g;
	}

	private GeneralNodeDatum mapPowerDatum(PowerDatum datum) {
		assert datum != null;
		assert datum.getSourceId() != null;

		GeneralEdgeDatumSamples samples = new GeneralEdgeDatumSamples();
		if ( datum.getWatts() != null ) {
			samples.putInstantaneousSampleValue("watts", datum.getWatts());
		}
		if ( datum.getWattHourReading() != null ) {
			samples.putAccumulatingSampleValue("wattHours", datum.getWattHourReading());
		}

		GeneralNodeDatum g = mapBaseGeneralNodeDatumProperties(datum);
		g.setSourceId(datum.getSourceId());
		if ( samples.getSampleData() != null && samples.getSampleData().size() > 0 ) {
			g.setSamples(samples);
		}
		return g;
	}

	/**
	 * Map a {@link LocationDatum} object into a {@link GeneralLocationDatum}
	 * object.
	 * 
	 * @param datum
	 *        The datum to map.
	 * @return The mapped datum, or <em>null</em> if not supported.
	 */
	public GeneralLocationDatum mapLocationDatum(LocationDatum datum) {
		GeneralLocationDatum g = null;
		if ( datum instanceof DayDatum ) {
			g = mapDayDatum((DayDatum) datum);
		} else if ( datum instanceof PriceDatum ) {
			g = mapPriceDatum((PriceDatum) datum);
		} else if ( datum instanceof WeatherDatum ) {
			g = mapWeatherDatum((WeatherDatum) datum);
		}
		log.trace("Mapped LocationDatum {} to general form {}", datum, g);
		return g;
	}

	private GeneralLocationDatum mapBaseGeneralLocationDatumProperties(LocationDatum datum) {
		GeneralLocationDatum g = new GeneralLocationDatum();
		g.setCreated(datum.getCreated() != null ? datum.getCreated() : new DateTime());
		g.setLocationId(datum.getLocationId());
		return g;
	}

	private GeneralLocationDatum mapDayDatum(DayDatum datum) {
		assert datum != null;
		assert datum.getDay() != null;

		// get our mapping info to map source ID and location ID
		DatumMappingInfo info = getLocationDatumMappingInfo(datum);
		if ( info == null ) {
			return null;
		}

		GeneralLocationDatumSamples samples = new GeneralLocationDatumSamples();
		if ( datum.getSunrise() != null ) {
			samples.putStatusSampleValue("sunrise", localTimeFormatter.print(datum.getSunrise()));
		}
		if ( datum.getSunset() != null ) {
			samples.putStatusSampleValue("sunset", localTimeFormatter.print(datum.getSunset()));
		}

		GeneralLocationDatum g = mapBaseGeneralLocationDatumProperties(datum);

		// creation date must be converted from local time to UTC
		DateTime day = datum.getDay().toDateTimeAtStartOfDay(DateTimeZone.forID(info.getTimeZoneId()));
		g.setCreated(day);

		g.setLocationId(info.getId());
		g.setSourceId(info.getSourceId());
		if ( samples.getSampleData() != null && samples.getSampleData().size() > 0 ) {
			g.setSamples(samples);
		}
		return g;
	}

	private GeneralLocationDatum mapWeatherDatum(WeatherDatum datum) {
		assert datum != null;
		assert datum.getInfoDate() != null;
		assert datum.getTemperatureCelsius() != null;

		// get our mapping info to map source ID and location ID
		DatumMappingInfo info = getLocationDatumMappingInfo(datum);
		if ( info == null ) {
			return null;
		}

		GeneralLocationDatumSamples samples = new GeneralLocationDatumSamples();
		if ( datum.getTemperatureCelsius() != null ) {
			samples.putInstantaneousSampleValue("temp", datum.getTemperatureCelsius());
		}
		if ( datum.getHumidity() != null ) {
			samples.putInstantaneousSampleValue("humidity", datum.getHumidity());
		}
		if ( datum.getUvIndex() != null ) {
			samples.putInstantaneousSampleValue("uvIndex", datum.getUvIndex());
		}
		if ( datum.getDewPoint() != null ) {
			samples.putInstantaneousSampleValue("dew", datum.getDewPoint());
		}
		if ( datum.getBarometricPressure() != null ) {
			samples.putInstantaneousSampleValue("atm",
					(int) (datum.getBarometricPressure().floatValue() * 100));
		}
		if ( datum.getVisibility() != null && datum.getVisibility().floatValue() < 1000f ) {
			samples.putInstantaneousSampleValue("visibility",
					(int) (datum.getVisibility().floatValue() * 1000));
		}
		if ( datum.getSkyConditions() != null && datum.getSkyConditions().length() > 0 ) {
			samples.putStatusSampleValue("sky", datum.getSkyConditions());
		}

		GeneralLocationDatum g = mapBaseGeneralLocationDatumProperties(datum);
		g.setCreated(datum.getInfoDate());
		g.setLocationId(info.getId());
		g.setSourceId(info.getSourceId());
		if ( samples.getSampleData() != null && samples.getSampleData().size() > 0 ) {
			g.setSamples(samples);
		}
		return g;
	}

	private GeneralLocationDatum mapPriceDatum(PriceDatum datum) {
		assert datum != null;
		assert datum.getPrice() != null;

		// get our mapping info to map source ID and location ID
		DatumMappingInfo info = getLocationDatumMappingInfo(datum);
		if ( info == null ) {
			return null;
		}

		GeneralLocationDatumSamples samples = new GeneralLocationDatumSamples();
		if ( datum.getPrice() != null ) {
			samples.putInstantaneousSampleValue("price", datum.getPrice());
		}

		GeneralLocationDatum g = mapBaseGeneralLocationDatumProperties(datum);
		g.setCreated(datum.getCreated());
		g.setLocationId(info.getId());
		g.setSourceId(info.getSourceId());
		if ( samples.getSampleData() != null && samples.getSampleData().size() > 0 ) {
			g.setSamples(samples);
		}
		return g;
	}

	private DatumMappingInfo getLocationDatumMappingInfo(LocationDatum datum) {
		final String key = datum.getClass().getSimpleName() + datum.getLocationId();
		DatumMappingInfo info = datumMappingInfoCache.get(key);
		if ( info != null ) {
			return info;
		}
		info = generalLocationDatumDao.getMappingInfo(datum);
		if ( info != null ) {
			// note we're not concerned with thread safety here, assuming we might do DAO lookup more than once but that's OK
			datumMappingInfoCache.put(key, info);
		}
		return info;
	}

	public void setGeneralLocationDatumDao(GeneralLocationDatumDao generalLocationDatumDao) {
		this.generalLocationDatumDao = generalLocationDatumDao;
	}

}
