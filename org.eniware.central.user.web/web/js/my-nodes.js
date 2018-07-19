$(document).ready(function() {
	'use strict';
	
	var tzPicker;
	var dynamicSearchTimer;
	
	$('#my-Edges-table').on('click', 'a.view-cert', function(event) {
		event.preventDefault();
		
		var btn = $(this);
		var id = btn.parents('.Edge-row').data('Edge-id');
		var form = $('#view-cert-modal');
		var downLink = $('#modal-cert-download').get(0);
		var renewLink = $('#modal-cert-renew').get(0);

		form.attr('action', form.attr('action').replace(/\d+$/, id));
		downLink.pathname = downLink.pathname.replace(/\d+$/, id);
		renewLink.pathname = renewLink.pathname.replace(/\d+$/, id);

		form.modal('show');
	}).on('click', 'a.transfer-ownership', function(event) {
		event.preventDefault();
		
		var btn = $(this);
		var EdgeRow = btn.parents('.Edge-row').first();
		var EdgeId = EdgeRow.data('Edge-id');
		var EdgeName = EdgeRow.data('Edge-name');
		var userId = EdgeRow.data('user-id');
		var form = $('#transfer-ownership-modal');

		form.find("input[name='EdgeId']").val(EdgeId || '');
		form.find("input[name='userId']").val(userId || '');
		
		$('#transfer-ownership-Edge').text(EdgeId + (EdgeName ? ' - ' + EdgeName : ''));
		
		form.modal('show');
	}).on('click', 'a.archive', function(event) {
		event.preventDefault();
		
		var btn = $(this);
		var EdgeRow = btn.parents('.Edge-row').first();
		var EdgeId = EdgeRow.data('Edge-id');
		var EdgeName = EdgeRow.data('Edge-name');
		var form = $('#archive-Edge-modal');

		form.find("input[name='EdgeIds']").val(EdgeId || '');
		form.find(".Edge-name-label").text(EdgeId + (EdgeName ? ' - ' + EdgeName : ''));
		
		form.modal('show');
	}).on('click', 'button.view-situation', function(event) {
		// use call(this) to preserve button as 'this' object
		var EdgeRow = $(this).parents('.Edge-row').first();
		var EdgeId = EdgeRow.data('Edge-id');
		var EdgeName = EdgeRow.data('Edge-name');
		if ( EdgeName ) {
			EdgeName = EdgeId + ' - ' + EdgeName;
		}
		EniwareReg.viewAlertSituation.call(this, event, EdgeName);
	});
	
	$('#pending-transfer').on('click', 'button.cancel-ownership-transfer', function(event) {
		event.preventDefault();
		var btn = $(this);
		var url = btn.data('action'),
			userId = btn.data('user-id'),
			EdgeId = btn.data('Edge-id');
		$.post(url, { userId:userId, EdgeId:EdgeId, _csrf:EniwareReg.csrf() }, function(json) {
			document.location.reload(true);
		}).fail(function(data, statusText, xhr) {
			EniwareReg.showAlertBefore('#top', 'alert-warning', statusText);
		});
	});
	
	$('#pending-transfer-requests-table').on('click', 'button.decide-ownership-transfer', function(event) {
		event.preventDefault();
		var btn = $(this);
		var EdgeId = btn.data('Edge-id');
		var userId = btn.data('user-id');
		var requester = btn.data('requester');
		var form = $('#decide-transfer-ownership-modal');
		
		form.find("input[name='EdgeId']").val(EdgeId || '');
		form.find("input[name='userId']").val(userId || '');
		form.find("input[name='accept']").val('false');
		
		$('#transfer-ownership-request-Edge').text(EdgeId);
		$('#transfer-ownership-request-requester').text(requester);

		form.modal('show');
	});
	
	function updateCertDisplayDetails(json) {
		var dateFormat = 'dddd, D MMM YYYY, h:mm a',
			validUntil = moment(json.certificateValidUntilDate),
			renewAfter = moment(json.certificateRenewAfterDate),
			renewAfterMsg,
			timeLeft;
		
		$('#modal-cert-container').text(json.pemValue);
		$('#view-cert-serial-number').text(json.certificateSerialNumber);
		$('#view-cert-subject').text(json.certificateSubjectDN);
		$('#view-cert-issuer').text(json.certificateIssuerDN);
		$('#view-cert-valid-from').text(moment(json.certificateValidFromDate).format(dateFormat));
		$('#view-cert-valid-until').text(validUntil.format(dateFormat));
		
		if ( json.certificateRenewAfterDate ) {
			renewAfterMsg = renewAfter.format(dateFormat);
			if ( renewAfter.isAfter() ) {
				renewAfterMsg += ' (in ' +renewAfter.diff(moment(), 'days') + ' days)';
			} else if ( validUntil.isAfter() ) {
				timeLeft = validUntil.diff(moment(), 'days');
				if ( timeLeft > 0 ) {
					renewAfterMsg +=  ' (' + timeLeft + ' days left before expires)';
				} else {
					timeLeft = validUntil.diff(moment(), 'hours');
					renewAfterMsg +=  ' (' + timeLeft + ' hours left before expires)';
				}
			}
			$('#view-cert-renew-after').text(renewAfterMsg).parent().show();
		} else {
			$('#view-cert-renew-after').parent().hide();
		}
	}
	
	$('#view-cert-modal').ajaxForm({
		dataType: 'json',
		success: function(json, status, xhr, form) {
			var renewAfter = moment(json.certificateRenewAfterDate);
			
			updateCertDisplayDetails(json);
			
			if ( renewAfter.isAfter() === false ) {
				$('#modal-cert-renew').removeClass('hidden');
			}
			
			$('#view-cert-modal .cert').removeClass('hidden');
			$('#view-cert-modal .nocert').addClass('hidden');
		},
		error: function(xhr, status, statusText) {
			EniwareReg.showAlertBefore('#view-cert-modal .modal-body > *:first-child', 'alert-warning', statusText);
		}
	}).on('shown.bs.modal', function() {
		$('#view-cert-password').focus();
	}).on('hidden.bs.modal', function() {
		document.location.reload(true);
	});
	
	$('#modal-cert-renew').on('click', function(event) {
		event.preventDefault();
		var btn = $(event.target),
			url = btn.attr('href'),
			pass = $('#view-cert-password').val();
		$.ajax({
			type: 'POST',
			url: url,
			data: {password:pass},
			dataType: 'json',
			beforeSend: function(xhr) {
				EniwareReg.csrf(xhr);
            },
			success: function(json, status, xhr) {
				$('#view-cert-modal .renewed').removeClass('hidden');
				$('#modal-cert-renew').addClass('hidden');
				updateCertDisplayDetails(json);
			},
			error: function(xhr, status, statusText){
				EniwareReg.showAlertBefore('#view-cert-modal .modal-body > *:first-child', 'alert-warning', statusText);
			}
		});
	});
	
	$('#transfer-ownership-modal').ajaxForm({
		dataType: 'json',
		success: function(json, status, xhr, form) {
			$('#transfer-ownership-modal').modal('hide');
		},
		error: function(xhr, status, statusText) {
			EniwareReg.showAlertBefore('#transfer-ownership-modal .modal-body > *:first-child', 'alert-warning', statusText);
		}
	}).on('shown.bs.modal', function() {
		$('#transfer-ownership-recipient').focus();
	}).on('hidden.bs.modal', function() {
		document.location.reload(true);
	});
	
	$('#decide-transfer-ownership-modal').ajaxForm({
		dataType: 'json',
		success: function(json, status, xhr, form) {
			$('#decide-transfer-ownership-modal').modal('hide');
		},
		error: function(xhr, status, statusText) {
			EniwareReg.showAlertBefore('#decide-transfer-ownership-modal .modal-body > *:first-child', 'alert-warning', statusText);
		}
	}).on('hidden.bs.modal', function() {
		document.location.reload(true);
	}).find('button.submit').on('click', function(event) {
		var btn = $(this);
		var form = $('#decide-transfer-ownership-modal');
		form.find('input[name="accept"]').val(btn.data('accept') ? 'true' : 'false');
		form.submit();
	});
	
	$('#archive-Edge-modal').ajaxForm({
		dataType: 'json',
		success: function(json, status, xhr, form) {
			document.location.reload(true);
		},
		error: function(xhr, status, statusText) {
			EniwareReg.showAlertBefore('#archive-Edge-modal .modal-body > *:first-child', 'alert-warning', statusText);
		}
	});
	
	function setupEditUserEdgeLocationDisplay(loc) {
		var locDisplay = [], text = '';
		if ( loc.street ) {
			locDisplay.push(loc.street);
		}
		if ( loc.locality ) {
			locDisplay.push(loc.locality);
		}
		if ( loc.region ) {
			locDisplay.push(loc.region);
		}
		if ( loc.stateOrProvince ) {
			locDisplay.push(loc.stateOrProvince);
		}
		if ( loc.postalCode ) {
			locDisplay.push(loc.postalCode);
		}
		if ( loc.country ) {
			locDisplay.push(loc.country);
		}
		if ( locDisplay.length > 0 ) {
			text = locDisplay.join(', ');
		}
		if ( loc.latitude !== undefined && loc.longitude !== undefined ) {
			if ( text.length > 0 ) {
				text += ' (';
			}
			text += loc.latitude.toFixed(3) + ', ' + loc.longitude.toFixed(3);
			if ( loc.elevation ) {
				text += ' @ ' +loc.elevation + 'm';
			}
			text += ')';
		}
		$('#userEdge-location').text(text);
	}
	
	function setupEditUserEdgeFields(form, userEdge) {
		var Edge = userEdge.Edge;
		if ( !Edge ) {
			Edge = {}
		}
		var loc = userEdge.EdgeLocation;
		if ( !loc ) {
			loc = {};
		}
		var user = userEdge.user;
		if ( !user ) {
			user = {};
		}
		$('#userEdge-id').text(Edge.id);
		$('#userEdge-name').val(userEdge.name);
		$('#userEdge-description').val(userEdge.description);
		$('#userEdge-private').prop('checked', userEdge.requiresAuthorization);
		
		setupEditUserEdgeLocationDisplay(loc);
		setupEditUserLocationFields(loc);

		form.find("input[name='Edge.id']").val(Edge.id || '');
		form.find("input[name='user.id']").val(user.id || '')
		form.find("input[name='Edge.locationId']").val(Edge.locationId || '');
	}
	
	$('#Edges').on('click', 'button.edit-Edge', function(event) {
		var btn = $(this);
		var form = $(btn.data('target'));
		var url = form.attr('action').replace(/\/[^\/]+$/, '/Edge');
		var req = {userId : btn.data('user-id'), EdgeId : btn.data('Edge-id') };
		setupEditUserEdgeFields(form, {Edge : {id : req.EdgeId}, user : {id : req.userId}});
		$.getJSON(url, req, function(json) {
			setupEditUserEdgeFields(form, json.data);
		}).fail(function(data, statusText, xhr) {
			EniwareReg.showAlertBefore('#edit-Edge-modal .modal-body > *:first-child', 'alert-warning', statusText);
		});
		editEdgeShowPage(form, 1);
		form.modal('show');
	});
	
	$('#edit-Edge-modal').ajaxForm({
		dataType: 'json',
		success: function(json, status, xhr, form) {
			form.modal('hide');
			document.location.reload(true);
		},
		error: function(xhr, status, statusText) {
			EniwareReg.showAlertBefore('#edit-Edge-modal .modal-body > *:first-child', 'alert-warning', statusText);
		}
	}).data('page', 1).on('show', function() {
		dynamicSearchTimer = undefined;
		$('#edit-Edge-location-search-results').addClass('hidden');
	});
	
	function editEdgeShowPage(form, newPage) {
		var currPage = form.data('page');
		if ( currPage === newPage || newPage < 1 ) {
			return;
		}
		if ( newPage > currPage ) {
			while ( currPage < newPage ) {
				form.removeClass('page'+currPage);
				currPage += 1;
			}
		} else {
			while ( currPage > newPage ) {
				form.removeClass('page'+currPage);
				currPage -= 1;
			}
		}
		form.data('page', newPage);
		form.addClass('page'+newPage);
	}
	
	function selectTzPickerArea(tzcontainer) {
		var timeZoneId = $('#edit-Edge-location-tz').val();
		var country = $('#edit-Edge-location-country').val();
		if ( timeZoneId && country ) {
			tzcontainer.find("area[data-timezone='"+timeZoneId+"'][data-country="+country+']').trigger('click');
		}
	}
	
	$('#edit-Edge-modal button.change-location').on('click', function(event) {
		var form = $(this).parents('form').first();
		var pageContainer = form.find('.modal-body .hbox');
		var pickerUrl = form.attr('action').replace(/\/[^\/]+$/, '/tzpicker.html');
		var tzcontainer = form.find('.tz-picker-container');
		if ( tzcontainer.children().length == 0 ) {
			tzcontainer.load(pickerUrl, function() {
				var picker = tzcontainer.find('.timezone-image');
				picker.timezonePicker({
					target : '#edit-Edge-location-tz',
					countryTarget : '#edit-Edge-location-country',
				});
				selectTzPickerArea(tzcontainer);
			});
		} else {
			selectTzPickerArea(tzcontainer);
		}
		editEdgeShowPage(form, 2);
	});
	
	$('#edit-Edge-page-back').on('click', function(event) {
		var form = $(this).parents('form').first();
		var destPage = form.data('page') - 1;
		$('#edit-Edge-location-search-results').toggleClass('hidden', destPage !== 3);
		editEdgeShowPage(form, destPage);
	});
	
	$('#edit-Edge-select-tz').on('click', function(event) {
		var form = $(this).parents('form').first();
		$('#edit-Edge-select-location').attr('disabled', 'disabled');
		editEdgeShowPage(form, 3);
		if ( dynamicSearchTimer === undefined ) {
			searchForLocationDetails();
		}
	});
	
	$('#edit-Edge-location-search-results').on('click', 'tr', function(event) {
		var me = $(this);
		var loc = me.data('location');
		if ( me.hasClass('success') === false ) {
			me.parent().find('tr.success').removeClass('success');
			me.addClass('success');
		}
		setupEditUserLocationFields(loc);
	});
	
	function showLocationSearchResults(results) {
		var table = $('#edit-Edge-location-search-results');
		var templateRow = table.find('tr.template');
		var tbody = table.find('tbody');
		var form = $('#edit-Edge-modal');
		var i, len, tr, loc, prop, cell;
		tbody.empty();
		if ( results.length > 0 ) {
			for ( i = 0, len = results.length; i < len; i += 1 ) {
				tr = templateRow.clone(true);
				tr.removeClass('template');
				loc = results[i];
				tr.data('location', loc);
				for ( prop in loc ) {
					if ( loc.hasOwnProperty(prop) ) {
						cell = tr.find("[data-tprop='" +prop +"']");
						cell.text(loc[prop]);
					}
				}
				tbody.append(tr);
			}
			table.removeClass('hidden');
			$('#edit-Edge-location-search-no-match').addClass('hidden');
		} else {
			table.addClass('hidden');
			$('#edit-Edge-location-search-no-match').removeClass('hidden'); // no matches, allow saving
		}
		$('#edit-Edge-select-location').removeAttr('disabled');
	}
	
	function setupEditUserLocationFields(location) {
		if ( !location ) {
			location = {};
		}
		var form = $('#edit-Edge-modal');
		var elements = form.get(0).elements;
		var criteria = ['country', 'timeZoneId', 'region', 'stateOrProvince', 'locality', 'postalCode', 
		                'street', 'latitude', 'longitude', 'elevation'];
		var input;
		criteria.forEach(function(prop) {
			input = elements['Edge.location.'+prop];
			if ( input ) {
				$(input).val(location[prop]);
			}
		});
		input = elements['Edge.locationId'];
		if ( input ) {
			$(input).val(location.id);
		}
	}
	
	function handleLocationDetailsChange(event) {
		if ( dynamicSearchTimer ) {
			clearTimeout(dynamicSearchTimer);
		}
		dynamicSearchTimer = setTimeout(searchForLocationDetails, 300);
	}
	
	function searchForLocationDetails() {
		var form = $('#edit-Edge-modal');
		var elements = form.get(0).elements;
		var url = $('#edit-Edge-location-details').data('lookup-url');
		var criteria = ['timeZoneId', 'country', 'region', 'stateOrProvince', 'locality', 'postalCode'];
		var req = {}, input;
		criteria.forEach(function(prop) {
			input = elements['Edge.location.'+prop];
			if ( input ) {
				input = $(input);
				if ( input.val().length > 0 ) {
					req['location.'+prop] = input.val();
				}
			}
		});
		$.getJSON(url, req, function(json) {
			if ( json.success == true && json.data && Array.isArray(json.data.results) ) {
				showLocationSearchResults(json.data.results);
			}
		}).fail(function(data, statusText, xhr) {
			EniwareReg.showAlertBefore('#edit-Edge-modal .modal-body > *:first-child', 'alert-warning', statusText);
		});
	}
	
	$('#edit-Edge-location-details').on('keyup', 'input', handleLocationDetailsChange);
	
	$('#edit-Edge-select-location').on('click', function() {
		var form = $('#edit-Edge-modal');
		$('#edit-Edge-location-search-results').addClass('hidden');
		editEdgeShowPage(form, 4);
	});
	
	function numberOrUndefined(value) {
		var result;
		if ( typeof value === 'number' ) {
			result = value;
		} else if ( value === '' ) {
			// empty string to undefined;
		} else {
			result = Number(value);
			if ( isNaN(result) ) {
				result = undefined;
			}
		}
		return result;
	}
	
	$('#edit-Edge-select-location-private').on('click', function() {
		var form = $('#edit-Edge-modal');
		editEdgeShowPage(form, 1);
		setupEditUserEdgeLocationDisplay({
			name : $('#edit-Edge-location-name').val(),
			country : $('#edit-Edge-location-country').val(),
			stateOrProvince : $('#edit-Edge-location-state').val(),
			region : $('#edit-Edge-location-region').val(),
			locality : $('#edit-Edge-location-locality').val(),
			postalCode : $('#edit-Edge-location-postal-code').val(),
			street : $('#edit-Edge-location-street').val(),
			latitude : numberOrUndefined($('#edit-Edge-location-latitude').val()),
			longitude : numberOrUndefined($('#edit-Edge-location-longitude').val()),
			elevation : numberOrUndefined($('#edit-Edge-location-elevation').val()),
			timeZoneId : $('#edit-Edge-location-tz').val()
		});
	});
	
	$('#invite-modal').on('show.bs.modal', function() {
		var form = $(this);
		var url = form.attr('action').replace(/\/[^\/]+$/, '/tzpicker.html');
		var tzcontainer = $('#tz-picker-container');
		if ( tzcontainer.children().length == 0 ) {
			tzcontainer.load(url, function() {
				var picker = tzcontainer.find('.timezone-image');
				picker.timezonePicker({
					target : '#invite-tz',
					countryTarget : '#invite-country',
					changeHandler : function(tzName, countryName, offset) {
						$('#invite-tz-country').text(countryName);
					}
				});
				picker.timezonePicker('detectLocation');
			});
		}
	});
	
	(function() {
		var EdgeRows = $('.Edge-row');
		if ( EdgeRows.length > 0 ) {
			// show active alert situations
			$.getJSON(EniwareReg.eniwareUserURL('/sec/alerts/user/situations'), function(json) {
				var i, alert;
				if ( json && json.data && Array.isArray(json.data) ) {
					for ( i = 0; i < json.data.length; i++ ) {
						alert = json.data[i];
						if ( alert.EdgeId ) {
							EdgeRows.filter('[data-Edge-id='+alert.EdgeId+']').find('button.view-situation').each(function(idx, el) {
								$(el).data('alert-id', alert.id);
							}).removeClass('hidden');
						}
					}
				}
			});
		}
	}());
});
