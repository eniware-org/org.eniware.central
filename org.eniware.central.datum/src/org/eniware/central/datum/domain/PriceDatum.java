/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * Domain object for a unit of data collected from a power price monitor.
 * 
 * <p>
 * Note a {@code PriceDatum} is not directly related to a {@code EniwareEdge}, and
 * the {@code nodeId} value may actually be <em>null</em>. This class implements
 * both {@link NodeDatum} and {@link LocationDatum} for ease of use, although
 * strictly speaking it is only a {@link LocationDatum}.
 * </p>
 *
 * @version 1.0
 */
public class PriceDatum extends BaseNodeDatum implements LocationDatum {

	private static final long serialVersionUID = 4601794526965944988L;

	private Long locationId;
	private Float price = null; // TODO: should be BigDecimal

	/**
	 * Default constructor.
	 */
	public PriceDatum() {
		super();
	}

	@Override
	public String toString() {
		return "PriceDatum{nodeId=" + getNodeId() + ",locationId=" + getLocationId() + ",price="
				+ this.price + '}';
	}

	/**
	 * @return the locationId
	 */
	@Override
	public Long getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId
	 *        the locationId to set
	 */
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	/**
	 * @return the price
	 */
	public Float getPrice() {
		return price;
	}

	/**
	 * @param price
	 *        the price to set
	 */
	public void setPrice(Float price) {
		this.price = price;
	}

}
