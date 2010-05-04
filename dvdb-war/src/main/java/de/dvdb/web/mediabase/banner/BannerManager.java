package de.dvdb.web.mediabase.banner;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.Log;

@Name("bannerManager")
@Scope(ScopeType.APPLICATION)
@Startup
public class BannerManager {

	@Logger
	Log log;

	private List<BannerDefinition> banners = new ArrayList<BannerDefinition>();

	@Create
	public void init() {
		log.info("Initializing banner manager");

		BannerDefinition b1 = new BannerDefinition();
		b1.setUri("/banner/banner1.png");
		b1.setCreator("DuraAce");
		b1.setWidth(468);
		b1.setHeight(60);

		banners.add(b1);

	}

	public BannerDefinition getBanner(Long id) {
		return banners.get(0);
	}
}
