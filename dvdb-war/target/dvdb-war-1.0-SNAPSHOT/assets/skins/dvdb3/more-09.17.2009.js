YUI(YUIConfig)
		.use(
				"node",
				"event-mouseenter",
				function(a) {
					var g = a.get("#projects a.more"), f = null, d = null, c = [], b = false, j = 0, e = false;
					var i = function() {
						e = true;
						g.get("parentNode").addClass("selected");
						c = g.getXY();
						j = g.get("offsetHeight");
						c[1] = c[1] + j;
						f.setStyle("display", "block");
						f.setXY(c)
					};
					var h = function() {
						e = false;
						f.setStyle("display", "none");
						if (!b) {
							g.get("parentNode").removeClass("selected")
						}
					};
					if (g) {
						b = g.get("parentNode").hasClass("selected");
						d = a.all("#trough .yui-g.first .yui-u.first li.more");
						if (d) {
							f = a.Node
									.create('<div id="more_menu"><ul></ul></div>');
							a.get("body").appendChild(f);
							d.each(function() {
								f.query("ul").appendChild(this.cloneNode(true))
							});
							f.on("mouseleave", h);
							f.on("mouseenter", i);
							g.on("mouseenter", i);
							g.on("mouseleave", function() {
								if (e) {
									h()
								}
							})
						}
					}
				});