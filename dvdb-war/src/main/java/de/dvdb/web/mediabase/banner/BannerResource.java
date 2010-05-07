package de.dvdb.web.mediabase.banner;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.web.AbstractResource;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.mediabase.MediabaseBanner;
import de.dvdb.domain.model.mediabase.MediabaseService;

@Scope(APPLICATION)
@Name("bannerResource")
@BypassInterceptors
public class BannerResource extends AbstractResource {

	// Resources URIs end with /<userId>/<l|s>
	public static Pattern RESOURCE_PATTERN = Pattern.compile("^/(.+)/$");

	public static final String REGISTER_SEAM_RESOURCE = "/banner";

	private Log log = Logging.getLog(BannerResource.class);

	@Override
	public String getResourcePath() {
		return REGISTER_SEAM_RESOURCE;
	}

	@Override
	public void getResource(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		// Wrap this, we need an ApplicationContext
		new ContextualHttpServletRequest(request) {
			@Override
			public void process() throws IOException {
				doWork(request, response);
			}
		}.run();

	}

	public void doWork(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		EntityManager dvdb = (EntityManager) Component.getInstance("dvdb");
		MediabaseService mediabaseService = (MediabaseService) Component
				.getInstance("mediabaseService");
		BannerManager bannerManager = (BannerManager) Component
				.getInstance("bannerManager");

		String pathInfo = request.getPathInfo().substring(
				getResourcePath().length());

		String username = null;
		Matcher matcher = RESOURCE_PATTERN.matcher(pathInfo);
		if (matcher.find()) {
			username = matcher.group(1);
			log.debug("request for username " + username);
		}

		if (username == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid request path, use: /banner/.+/");
			return;
		}

		Mediabase mediabase = mediabaseService.getMediabase(username);
		if (mediabase == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Mediabaes not found " + username);
			return;
		}

		List<MediabaseBanner> banners = dvdb.createQuery(
				"from MediabaseBanner mb where mb.mediabase = :mediabase")
				.setParameter("mediabase", mediabase).getResultList();

		MediabaseBanner mb = null;
		if (banners.size() == 0) {
			mb = new MediabaseBanner();
			mb.setBannerId(0l);
			mb.setDisplayLatestDVD(true);
			mb.setLine1("Moviebase");
		}

		response.addHeader("Cache-Control", "max-age=600"); // 10 minutes

		BannerDefinition bannerDefinition = bannerManager.getBanner(mb
				.getBannerId());

		InputStream is = getClass().getResourceAsStream(
				bannerDefinition.getUri());

		BufferedImage bufferedImage = ImageIO.read(is);

		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		List<Item> items = dvdb
				.createQuery(
						"select mic.item from MediabaseItemCollectible mic where mic.mediabase = :mediabase order by mic.entityMetadata.dateOfCreation Desc")
				.setParameter("mediabase", mediabase).setMaxResults(1)
				.getResultList();

		Font font = new Font("Sans Serif", Font.BOLD, 11);
		g.setColor(Color.white);
		g.setFont(font);

		g.translate(5, 25);
		g.drawString("Moviebase und Blog von "
				+ mediabase.getUser().getUsername(), 0, 0);

		g.translate(0, 13);
		g.drawString("http://moviebase.dvdb.de/"
				+ mediabase.getUser().getUsername(), 0, 0);

		if (items.size() > 0 && !items.get(0).getRequires18()) {
			g.translate(0, 13);
			g.drawString(
					"Aktuell " + mediabase.getNumberCollectibles()
							+ " DVD, Neuzugang "
							+ items.get(0).getTitleWithMediatype(), 0, 0);
		}

		else if (items.size() > 0 && items.get(0).getRequires18()) {
			g.translate(0, 13);
			g.drawString("Aktuell " + mediabase.getNumberCollectibles()
					+ " DVD, Neuzugang ************", 0, 0);
		}

		else {
			g.translate(0, 13);
			g.drawString(mediabase.getNumberCollectibles() + " DVDs ", 0, 0);
		}

		g.dispose();

		ImageIO.write(bufferedImage, "png", response.getOutputStream());
		response.setContentType("image/png");
		response.getOutputStream().flush();
		is.close();
	}
}
