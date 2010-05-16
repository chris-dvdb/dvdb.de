package de.dvdb.web.utils;

import java.util.Random;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.RememberMe;

@Name("org.jboss.seam.security.rememberMe")
@Scope(ScopeType.SESSION)
@Install(precedence = Install.APPLICATION, classDependencies = "javax.faces.context.FacesContext")
@BypassInterceptors
public class MyRememberMe extends RememberMe {

	private static final long serialVersionUID = -8487707367993877630L;

	protected String generateTokenValue() {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		sb.append(random.nextLong());
		return sb.toString();
	}
}