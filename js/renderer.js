(function(global, document) {
  var templateCache = {};
  
  var getFunction = function(path) {
    return path.reduce(function(acc, item) { return acc[item] }, global)
  }
  
  var parseUrl = function(url) {
    var parser = document.createElement('a');
    parser.href = url;
    return parser;
  }
  
  var findSelfOrChildren = function($node, selector) {
    return $node.find(selector).add($node.filter(selector));
  }
  
  var loadTemplate = function(url, cb) {
    if (!!templateCache[url]) {
      cb();
    } else {
      $.getScript(url, function() {
        templateCache[url] = true;
        cb();
      });
    }
  }
  
  var soyInclude = function($el) {
    findSelfOrChildren($el, '*[data-soy-include]').each(function(idx, node) {
      var $node = $(node);
      var url = parseUrl($node.attr('data-soy-include'));
      loadTemplate(url.pathname, function() {
        var $result = $(getFunction(url.hash.substring(1).split('.')).apply());
        $node.replaceWith($result);
        render($result);
      });
    });
  }
  
  var soyLayout = function($el) {
    findSelfOrChildren($el, '[data-soy-layout]').each(function(idx, node) {
      var $node = $(node);
      var url = parseUrl($node.attr('data-soy-layout'));
      $node.removeAttr('data-soy-layout');
      loadTemplate(url.pathname, function() {
        var $result = $(getFunction(url.hash.substring(1).split('.')).apply());
        $node.replaceWith($result);
        findSelfOrChildren($result, '[data-soy-insert]').each(function(idx, insert) {
          var $insert = $(insert);
          $insert.append($node);
          $insert.removeAttr('data-soy-insert');
        });
        render($result);
      });
    });
  }
  
  var render = function(dom) {
    var $dom = $(dom);
    soyInclude($dom);
    soyLayout($dom);
  }
  
  render(document);
})(window, document);
