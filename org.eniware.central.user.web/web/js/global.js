/* Global EniwareNetwork App Support */

var EniwareReg = {
	showAlertBefore: function(el, clazz, msg) {
	    $('<div class="alert'+(clazz.length > 0 ? ' ' +clazz : '')
	    		+'"><button type="button" class="close" data-dismiss="alert">×</button>'
	    		+msg +'</div>').insertBefore(el);
	},
	
	eniwareUserURL : function(relativeURL) {
		return $('meta[name=eniwareUserRootURL]').attr('content') + relativeURL;
	},

	csrfData : (function() {
		var csrf = $("meta[name='csrf']").attr("content"),
			header = $("meta[name='csrf_header']").attr("content");
		return {token:csrf,headerName:header};
	}()),
	};

/**
 * Get the CSRF token value or set the token as a request header on an XHR object.
 * 
 * @param {XMLHttpRequest} [xhr] The XHR object to set the CSR request header on.
 * @return The CSRF value.
 */
EniwareReg.csrf = function(xhr) {
	 if ( xhr && typeof xhr.setRequestHeader === 'function' ) {
		 xhr.setRequestHeader(EniwareReg.csrfData.headerName, EniwareReg.csrfData.token);
	 }
	 return EniwareReg.csrfData.token;
};

$(document).ready(function() {
	$('body').on('hidden', '.modal.dynamic', function () {
		$(this).removeData('modal');
	});
	
	$('a.logout').on('click', function(event) {
		event.preventDefault();
		$('#logout-form').get(0).submit();
	});
});
