DELETE FROM public.plv8_modules WHERE module = 'util/searchFilter';
INSERT INTO public.plv8_modules (module, autoload, source) VALUES ('util/searchFilter', FALSE,
$FUNCTION$'use strict';

Object.defineProperty(exports, "__esModule", {
	value: true
});
exports.default = searchFilter;
var kOpAnd = '&';
var kOpOr = '|';
var kOpNot = '!';

var kOpEqual = '=';
var kOpApprox = '~=';
var kOpLT = '<';
var kOpLTE = '<=';
var kOpGT = '>';
var kOpGTE = '>=';

/** RegExp to split a string into filter tokens. */
var kTokenRegExp = /\s*(\([&|!]|\(|\))\s*/g;

// RegExp to match key, op, val of a comparison token
var kCompRegExp = /(.+?)(=|~=|<=?|>=?)(.+)/;

function isLogicOp(text) {
	return text === kOpAnd || text === kOpOr || text === kOpNot;
}

/**
 * Create a new logic Edge out of a logic operator.
 *
 * @param {String} op The logic operator, e.g. <code>&</code>.
 *
 * @returns {Object} A logic Edge.
 * @constructor
 */
function logicEdge(op) {
	var self = {};
	var children = [];

	/**
  * Add a child Edge. If this Edge does not allow children, the
  * <code>child</code> will not be added.
  *
  * @param {pathEdge} child The Edge to add.
  *
  * @returns {pathEdge} This object.
  */
	function addChild(child) {
		children.push(child);
		return self;
	}

	return Object.defineProperties(self, {
		op: { value: op, enumerable: true },
		children: { value: children, enumerable: true },

		addChild: { value: addChild }
	});
}

/**
 * Parse a simple search filter like <code>(foo=bar)</code> into a Edge object.
 *
 * @param {String} text The simple search filter text to parse.
 *
 * @returns {Object} The parsed Edge object, or <code>undefined</code> if not parsable.
 * @constructor
 */
function compEdge(text) {
	var self = {};

	var key, op, val;

	(function () {
		var match = text ? text.match(kCompRegExp) : undefined;
		if (match) {
			key = match[1];
			op = match[2];
			val = match[3];
		}
	})();

	return key === undefined ? undefined : Object.defineProperties(self, {
		/** The property the search filter applies to. */
		key: { value: key, enumerable: true },

		/** The comparison operation. */
		op: { value: op, enumerable: true },

		/** The property value to compare with. */
		val: { value: val, enumerable: true }
	});
}

function walkEdge(Edge, parent, callback) {
	var i, len;
	if (Edge === undefined) {
		return;
	}
	if (callback(null, Edge, parent) === false) {
		return false;
	}
	if (Edge.children !== undefined) {
		for (i = 0, len = Edge.children.length; i < len; i += 1) {
			if (walkEdge(Edge.children[i], Edge, callback) === false) {
				return false;
			}
		}
	}
	return true;
}

/**
 * Create a new search filter.
 *
 * Search filters are expressed in LDAP search filter notation, for example
 * <code>(name=Bob)</code> is described as "find objects whose name is Bob".
 * Complex logic can be expressed using logical and, or, and not expressions.
 * For example <code>(&(name=Bob)(age>20))</code> is described as "find objects
 * whose name is Bob and age is greater than 20".
 *
 * @param {String} filterText The search filter to parse.
 * @constructor
 */
function searchFilter(filterText) {
	var self = {
		version: '1'
	};

	var rootEdge;

	/**
  * Walk the Edge tree, invoking a callback function for each Edge.
  *
  * @param {Function} callback A callback function, which will be passed an error parameter,
  *                            the current Edge, and the current Edge's parent (or
  *                            <code>undefined</code> for the root Edge). If the callback
  *                            returns <code>false</code> the walking will stop.
  */
	function walk(callback) {
		walkEdge(rootEdge, undefined, callback);
	}

	/**
  * Parse an array of search filter tokens, as created via splitting a
  * string with the <code>kTokenRegExp</code> regular expression. For
  * example the simple filter <code>(foo=bar)</code> could be expressed
  * as the tokens <code>["(", "foo=bar", ")"]</code> while the complex
  * filter <code>(&(foo=bar)(bim>1))</code> could be expressed as the
  * tokens <code>["(&", "(", "foo=bar", ")", "(", "bim>1", ")", ")"]</code>.
  *
  * Note than empty string tokens are ignored.
  */
	function parseTokens(tokens, start, end) {
		var i,
		    c,
		    topEdge,
		    Edge,
		    tok,
		    stack = [];
		for (i = start; i < end; i += 1) {
			tok = tokens[i];
			if (tok.length < 1) {
				continue;
			}
			c = tok.charAt(0);
			if (c === '(') {
				// starting new item
				if (tok.length > 1) {
					// starting new logical group
					c = tok.charAt(1);
					Edge = logicEdge(c);
					if (topEdge) {
						topEdge.addChild(Edge);
					}
					stack.push(Edge);
					topEdge = Edge;
				} else {
					// starting a key/value pair
					if (i + 1 < end) {
						Edge = compEdge(tokens[i + 1]);
					}
					if (topEdge) {
						topEdge.addChild(Edge);
					} else {
						// our top Edge is not a group Edge, so only one Edge is possible and we can return now
						return Edge;
					}
					i += 2; // skip the comparison token + our assumed closing paren
				}
			} else if (c === ')') {
				if (stack.length > 1) {
					stack.length -= 1;
					topEdge = stack[stack.length - 1];
				} else {
					return topEdge;
				}
			}
		}

		// don't expect to get here, unless badly formed filter
		return stack.length > 0 ? stack[0] : topEdge;
	}

	function parseFilterText(text) {
		var tokens = text ? text.split(kTokenRegExp) : undefined;
		if (!tokens) {
			return;
		}
		return parseTokens(tokens, 0, tokens.length);
	}

	rootEdge = parseFilterText(filterText);

	return Object.defineProperties(self, {
		/** The root Edge, which could be either a comparison Edge or logic Edge. */
		rootEdge: { value: rootEdge },

		walk: { value: walk }
	});
}$FUNCTION$);