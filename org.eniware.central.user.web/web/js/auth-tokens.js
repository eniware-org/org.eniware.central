$(document).ready(function() {
	'use strict';

	var EdgeSourceMap = {};
	var activePolicy = {EdgeIds:[], sourceIds:[]};
	var tokenCreated = false;
	
	function handleAuthTokenCreated(json, status, xhr, form) {
		if ( json.success === true && json.data ) {
			tokenCreated = true;
			form.find('.result-token').text(json.data.id);
			form.find('.result-secret').text(json.data.authSecret);
		}
		form.find('.before').hide();
		form.find('.after').show();
	}
	
	function reloadIfTokenCreated() {
		if ( tokenCreated ) {
			document.location.reload(true);
		}
	}

	$('#create-user-auth-token').ajaxForm({
		dataType: 'json',
		success: function(json, status, xhr, form) {
			handleAuthTokenCreated(json, status, xhr, form);
		},
		error: function(xhr, status, statusText) {
			EniwareReg.showAlertBefore('#create-user-auth-token .modal-body > *:first-child', 'alert-warning', statusText);
		}
	}).on('hidden.bs.modal', reloadIfTokenCreated);
	
	$('.action-user-token').find('button').click(function(event) {
		//.user-token-change-status or .user-token-delete
		event.preventDefault();
		var button = $(this);
		var tokenId = button[0].form.elements['id'].value;
		var csrf = button[0].form.elements['_csrf'].value;
		if ( button.hasClass('user-token-delete') ) {
			var form = $('#delete-user-auth-token');
			form[0].elements['id'].value = tokenId;
			form.find('.container-token').text(tokenId);
			form.modal('show');
		} else if ( button.hasClass('user-token-change-status') ) {
			var newStatus = (button.data('status') === 'Active' ? 'Disabled' : 'Active');
			$.post(button.data('action'), {id:tokenId, status:newStatus, '_csrf':csrf}, function(data) {
				document.location.reload(true);
			}, 'json');
		}
	});
	
	$('#delete-user-auth-token').ajaxForm({
		dataType: 'json',
		success: function(json, status, xhr, form) {
			form.modal('hide');
			document.location.reload(true);
		},
		error: function(xhr, status, statusText) {
			EniwareReg.showAlertBefore('#delete-user-auth-token .modal-body > *:first-child', 'alert-error', statusText);
		}
	});

	function resetToggleButtons(root) {
		root.find('.toggle.btn')
			.removeClass('btn-primary btn-success btn-info btn-warning btn-danger')
			.addClass('btn-default');
	}
	
	function togglePolicyEdgeId(container, EdgeId, add) {
		var i = activePolicy.EdgeIds.indexOf(EdgeId);
		if ( add && i < 0 ) {
			activePolicy.EdgeIds.push(EdgeId);
			updatePolicySourceIdHint(container, activePolicy.EdgeIds);
		} else if ( !add && i >= 0 ) {
			activePolicy.EdgeIds.splice(i, 1);
			updatePolicySourceIdHint(container, activePolicy.EdgeIds);
		}
	}
	
	function updatePolicySourceIdHint(container, EdgeIds) {
		var urls = [],
			EdgeKey,
			sources,
			sourceSet = [],
			i;
		
		function populateSourceSet(array) {
			array.forEach(function(source) {
				if ( sourceSet.indexOf(source) < 0 ) {
					sourceSet.push(source);
				}
			});
		}
		
		function showSourceSet() {
			var html = $('<div>');
			sourceSet.forEach(function(source, i) {
				var label = $("<button type='button' class='toggle btn btn-xs btn-info'>\n").data('source-id', source).text(source);
				html.append(label);
			});
			container.html(html.children());
		}
		
		for ( i = 0; i < EdgeIds.length; i += 1 ) {
			EdgeKey = ''+EdgeIds[i];
			sources = EdgeSourceMap[EdgeKey];
			if ( sources === undefined ) {
				EdgeSourceMap[EdgeKey] = [];
				urls.push({EdgeKey:EdgeKey, url:EniwareReg.eniwareUserURL('/sec/Edge-data/'+EdgeKey+'/sources')});
			} else {
				populateSourceSet(sources);
			}
		}
		if ( urls.length ) {
			$.when.apply($,urls.map(function(url) {
				return $.getJSON(url.url);
			})).done(function() {
				var j, json, EdgeKey, max = urls.length
				for ( j = 0; j < max; j += 1 ) {
					json = arguments[j];
					if ( max > 1 ) {
						json = json[0];
					}
					EdgeKey = urls[j].EdgeKey;
					if ( json && json.success && Array.isArray(json.data) ) {
						EdgeSourceMap[EdgeKey] = json.data;
						populateSourceSet(json.data);
					}
				}
				showSourceSet();
			});
		} else {
			showSourceSet();
		}
	}
	
	$('#create-data-auth-token').ajaxForm({
		dataType: 'json',
		beforeSerialize: function(form, options) {
			var containerText = form.find('textarea[name=sourceIds]').val(),
				containerSourceIds = (containerText ? containerText.split(/\s*,\s*/) : []),
				containerEdgeMetadataText = form.find('textarea[name=EdgeMetadataPaths]').val(),
				containerEdgeMetadataPaths = (containerEdgeMetadataText ? containerEdgeMetadataText.split(/\s*,\s*/) : []),
				containerUserMetadataText = form.find('textarea[name=userMetadataPaths]').val(),
				containerUserMetadataPaths = (containerUserMetadataText ? containerUserMetadataText.split(/\s*,\s*/) : []);
			activePolicy.sourceIds = containerSourceIds;
			activePolicy.EdgeMetadataPaths = containerEdgeMetadataPaths;
			activePolicy.userMetadataPaths = containerUserMetadataPaths;
		},
		beforeSubmit: function(array, form, options) {
			var sourceIdsIdx = array.findIndex(function(obj) {
					return obj.name === 'sourceIds';
				}),
				EdgeMetadataPathsIdx,
				userMetadataPathsIdx;

			if ( sourceIdsIdx >= 0 ) {
				array.splice(sourceIdsIdx, 1);
			}
			EdgeMetadataPathsIdx = array.findIndex(function(obj) {
				return obj.name === 'EdgeMetadataPaths';
			});
			if ( EdgeMetadataPathsIdx >= 0 ) {
				array.splice(EdgeMetadataPathsIdx, 1);
			}
			userMetadataPathsIdx = array.findIndex(function(obj) {
				return obj.name === 'userMetadataPaths';
			});
			if ( userMetadataPathsIdx >= 0 ) {
				array.splice(userMetadataPathsIdx, 1);
			}

			activePolicy.sourceIds.forEach(function(sourceId) {
				array.push({
					name : 'sourceId',
					value : sourceId,
					type : 'text'
				});
			});
			activePolicy.EdgeIds.forEach(function(EdgeId) {
				array.push({
					name : 'EdgeId',
					value : EdgeId,
					type : 'text'
				});
			});
			activePolicy.EdgeMetadataPaths.forEach(function(path) {
				array.push({
					name : 'EdgeMetadataPath',
					value : path,
					type : 'text'
				});
			});
			activePolicy.userMetadataPaths.forEach(function(path) {
				array.push({
					name : 'userMetadataPath',
					value : path,
					type : 'text'
				});
			});
		},
		success: function(json, status, xhr, form) {
			handleAuthTokenCreated(json, status, xhr, form);
		},
		error: function(xhr, status, statusText) {
			EniwareReg.showAlertBefore('#create-data-auth-token .modal-body > *:first-child', 'alert-error', statusText);
		}
	}).on('hidden.bs.modal', function() {
		var form = $(this);
		updatePolicySourceIdsDisplay(form.find('textarea[name=sourceIds]'), []);
		resetToggleButtons(form);
		$('#create-data-auth-token .nav-pills > *:first').tab('show');
		$('#create-data-auth-token-policy-sourceids-hint').empty();
		reloadIfTokenCreated();
	});
	
	$('#create-data-auth-token-policy-Edgeids').on('click', function(event) {
		var target = $(event.target),
			on = true,
			EdgeId = target.data('Edge-id');
		event.preventDefault();
		if ( target.hasClass('toggle') ) {
			if ( target.hasClass('btn-default') ) {
				on = false;
			}
			target.toggleClass('btn-default', on)
				.toggleClass('btn-primary', !on);
			togglePolicyEdgeId($('#create-data-auth-token-policy-sourceids-hint'), EdgeId, !on);
		}
	});
	
	function togglePolicySourceId(container, sourceId, add) {
		var containerText = container.val(),
			containerSourceIds = (containerText ? containerText.split(/\s*,\s*/) : []),
			i = containerSourceIds.indexOf(sourceId);
		if ( add && i < 0 ) {
			containerSourceIds.push(sourceId);
			updatePolicySourceIdsDisplay(container, containerSourceIds);
		} else if ( !add && i >= 0 ) {
			containerSourceIds.splice(i, 1);
			updatePolicySourceIdsDisplay(container, containerSourceIds);
		}
	}
	
	function updatePolicySourceIdsDisplay(container, sourceIds) {
		container.val(sourceIds.join(', '));
	}
	
	$('#create-data-auth-token-policy-sourceids-hint').on('click', function(event) {
		var target = $(event.target),
			sourceId = target.data('source-id');
		event.preventDefault();
		if ( target.hasClass('toggle') ) {
			togglePolicySourceId($('#create-data-auth-token-policy-sourceids'), sourceId, true);
		}
	});
	
	$('.action-data-token').find('button').click(function(event) {
		//.user-token-change-status or .user-token-delete
		event.preventDefault();
		var button = $(this);
		var tokenId = button[0].form.elements['id'].value;
		var csrf = button[0].form.elements['_csrf'].value;
		if ( button.hasClass('data-token-delete') ) {
			var form = $('#delete-data-auth-token');
			form[0].elements['id'].value = tokenId;
			form.find('.container-token').text(tokenId);
			form.modal('show');
		} else if ( button.hasClass('data-token-change-status') ) {
			var newStatus = (button.data('status') === 'Active' ? 'Disabled' : 'Active');
			$.post(button.data('action'), {id:tokenId, status:newStatus, '_csrf':csrf}, function(data) {
				document.location.reload(true);
			}, 'json');
		}
	});
	
	$('#delete-data-auth-token').ajaxForm({
		dataType: 'json',
		success: function(json, status, xhr, form) {
			form.modal('hide');
			document.location.reload(true);
		},
		error: function(xhr, status, statusText) {
			EniwareReg.showAlertBefore('#delete-data-auth-token .modal-body > *:first-child', 'alert-error', statusText);
		}
	});
});
