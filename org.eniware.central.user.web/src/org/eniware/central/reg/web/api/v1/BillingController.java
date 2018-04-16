/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package net.solarnetwork.central.reg.web.api.v1;

import static net.solarnetwork.web.domain.Response.response;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.support.BasicFilterResults;
import org.eniware.central.user.billing.biz.BillingBiz;
import org.eniware.central.user.billing.biz.BillingSystem;
import org.eniware.central.user.billing.domain.BillingSystemInfo;
import org.eniware.central.user.billing.domain.Invoice;
import org.eniware.central.user.billing.domain.InvoiceFilterCommand;
import org.eniware.central.user.billing.domain.InvoiceMatch;
import org.eniware.central.user.billing.domain.LocalizedInvoiceInfo;
import org.eniware.central.user.billing.support.LocalizedInvoice;
import org.eniware.central.user.billing.support.LocalizedInvoiceMatch;
import org.eniware.util.OptionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.solarnetwork.central.security.SecurityUser;
import net.solarnetwork.central.security.SecurityUtils;
import net.solarnetwork.central.web.support.WebServiceControllerSupport;
import net.solarnetwork.web.domain.Response;

/**
 * Web service API for billing management.
 *
 * @version 1.1
 */
@RestController("v1BillingController")
@RequestMapping(value = { "/sec/billing", "/v1/sec/user/billing" })
public class BillingController extends WebServiceControllerSupport {

	private final OptionalService<BillingBiz> billingBiz;

	/**
	 * Constructor.
	 * 
	 * @param billingBiz
	 *        the billing biz to use
	 */
	@Autowired
	public BillingController(@Qualifier("billingBiz") OptionalService<BillingBiz> billingBiz) {
		super();
		this.billingBiz = billingBiz;
	}

	/**
	 * Get billing system info for the current user.
	 * 
	 * @param locale
	 *        the Locale of the request
	 * @return the billing system info
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/systemInfo")
	public Response<BillingSystemInfo> billingSystemInfoForUser(Locale locale) {
		SecurityUser actor = SecurityUtils.getCurrentUser();
		BillingSystemInfo info = null;
		BillingBiz biz = billingBiz.service();
		if ( biz != null ) {
			BillingSystem system = biz.billingSystemForUser(actor.getUserId());
			info = (system != null ? system.getInfo(locale) : null);
		}
		return response(info);
	}

	/**
	 * Get a single invoice with full details.
	 * 
	 * @param invoiceId
	 *        the ID of the invoice to get
	 * @param userId
	 *        the optional user ID to get the invoice for; if not provided the
	 *        current actor's ID is used
	 * @param locale
	 *        the request locale
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/invoices/{invoiceId}", method = RequestMethod.GET)
	public Response<Invoice> getInvoice(@PathVariable("invoiceId") String invoiceId,
			@RequestParam(value = "userId", required = false) Long userId, Locale locale) {
		BillingBiz biz = billingBiz.service();
		Invoice result = null;
		if ( biz != null ) {
			if ( userId == null ) {
				SecurityUser actor = SecurityUtils.getCurrentUser();
				userId = actor.getUserId();
			}
			result = biz.getInvoice(userId, invoiceId, locale);
		}

		// localize the response
		if ( result != null && !(result instanceof LocalizedInvoiceInfo) ) {
			if ( locale == null ) {
				locale = Locale.getDefault();
			}
			result = new LocalizedInvoice(result, locale);
		}

		return response(result);
	}

	/**
	 * Render an invoice.
	 * 
	 * @param invoiceId
	 *        the invoice ID to render
	 * @param accept
	 *        an optional output type, defaults to {@literal text/html}
	 * @param userId
	 *        the optional user ID to get the invoice for; if not provided the
	 *        current actor's ID is used
	 * @param locale
	 *        the request locale
	 * @return the rendered invoice entity
	 */
	@ResponseBody
	@RequestMapping(value = "/invoices/{invoiceId}/render", method = RequestMethod.GET)
	public ResponseEntity<Resource> renderInvoice(@PathVariable("invoiceId") String invoiceId,
			@RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String accept,
			@RequestParam(value = "userId", required = false) Long userId, Locale locale) {
		BillingBiz biz = billingBiz.service();
		if ( biz != null ) {
			if ( userId == null ) {
				SecurityUser actor = SecurityUtils.getCurrentUser();
				userId = actor.getUserId();
			}
			List<MediaType> acceptTypes = MediaType.parseMediaTypes(accept);
			MediaType outputType = acceptTypes.isEmpty() ? MediaType.TEXT_HTML
					: acceptTypes.get(0).removeQualityValue();
			Resource result = biz.renderInvoice(userId, invoiceId, outputType, locale);
			if ( result != null ) {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(outputType);
				return new ResponseEntity<Resource>(result, headers, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/**
	 * Find matching invoices.
	 * 
	 * @param filter
	 *        the search criteria
	 * @return the search results
	 */
	@ResponseBody
	@RequestMapping(value = "/invoices/list", method = RequestMethod.GET)
	public Response<FilterResults<InvoiceMatch>> findFilteredInvoices(InvoiceFilterCommand filter,
			Locale locale) {
		BillingBiz biz = billingBiz.service();
		FilterResults<InvoiceMatch> results = null;
		if ( biz != null ) {
			if ( filter.getUserId() == null ) {
				SecurityUser actor = SecurityUtils.getCurrentUser();
				filter.setUserId(actor.getUserId());
			}
			results = biz.findFilteredInvoices(filter, filter.getSortDescriptors(), filter.getOffset(),
					filter.getMax());
		}

		// localize the response
		if ( results.getReturnedResultCount() != null && results.getReturnedResultCount() > 0 ) {
			if ( locale == null ) {
				locale = Locale.getDefault();
			}
			List<InvoiceMatch> localizedMatches = new ArrayList<InvoiceMatch>(
					results.getReturnedResultCount());
			for ( InvoiceMatch match : results ) {
				localizedMatches.add(LocalizedInvoiceMatch.of(match, locale));
			}
			results = new BasicFilterResults<InvoiceMatch>(localizedMatches, results.getTotalResults(),
					results.getStartingOffset(), results.getReturnedResultCount());
		}

		return response(results);
	}

}
