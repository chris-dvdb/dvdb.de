package de.dvdb.web.mediabase.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

@Name("bannerAction")
public class BannerAction implements Serializable {

	private static final long serialVersionUID = 1459434470822060294L;

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	private int fontSize;

	private String text = "Hallo";

	public void paint(Graphics2D g2, Object obj) {
		
		log.info("Calling paint");

		int testLenght = text.length();
		fontSize = testLenght < 8 ? 40 : 40 - (testLenght - 8);
		if (fontSize < 12)
			fontSize = 12;
		Font font = new Font("Serif", Font.HANGING_BASELINE, fontSize);
		g2.setFont(font);

		int x = 10;
		int y = fontSize * 5 / 2;
		g2.translate(x, y);
		Color color = Color.black;

		g2.setPaint(new Color(color.getRed(), color.getGreen(),
				color.getBlue(), 30));
		AffineTransform origTransform = g2.getTransform();
		g2.drawString(text, 0, 0);

		g2.setTransform(origTransform);
		g2.setPaint(color);
		g2.drawString(text, 0, 0);

	}
}
