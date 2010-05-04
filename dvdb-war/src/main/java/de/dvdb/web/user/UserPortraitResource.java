package de.dvdb.web.user;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import de.dvdb.domain.model.user.User;
import de.dvdb.domain.model.user.UserRepository;
import de.dvdb.infrastructure.persistence.UserRepositoryJPA;

@Scope(APPLICATION)
@Name("userPortraitResource")
@BypassInterceptors
public class UserPortraitResource extends AbstractResource {

	// Resources URIs end with /<userId>/<l|s>
	public static Pattern RESOURCE_PATTERN = Pattern
			.compile("^/(.+)/([ls]{1})$");

	public static final String REGISTER_SEAM_RESOURCE = "/userPortrait";

	private Log log = Logging.getLog(UserPortraitResource.class);

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

		String pathInfo = request.getPathInfo().substring(
				getResourcePath().length());

		String username = null;
		String imageSize = null;
		Matcher matcher = RESOURCE_PATTERN.matcher(pathInfo);
		if (matcher.find()) {
			username = matcher.group(1);
			imageSize = matcher.group(2);
			log.debug("request for username " + username + ", image size: "
					+ imageSize);
		}

		if (username == null || imageSize == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid request path, use: /userPortrait/[0-9]+/(l|s)");
			return;
		}

		UserRepository userService = (UserRepository) Component
				.getInstance(UserRepositoryJPA.class);
		User user = userService.getUser(username);
		if (user == null || user.getMimeType() == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"User id not found or no portrait for user");
			return;
		}

		response.addHeader("Cache-Control", "max-age=6000"); // 100 minutes
		// freshness in
		// browser cache

		if (user.getImageDataSmall() == null)
			userService.scaleDown(user);

		byte[] image = null;
		if (imageSize.equalsIgnoreCase("s")) {
			image = user.getImageDataSmall();
		} else {
			image = user.getImageData();
		}

		response.setContentType(user.getMimeType());
		response.setContentLength(image.length);
		response.getOutputStream().write(image);
		response.getOutputStream().flush();

	}

}
