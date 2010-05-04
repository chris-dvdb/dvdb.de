package de.dvdb.web.mediabase.banner;

import java.io.Serializable;

public class BannerDefinition implements Serializable {

	private static final long serialVersionUID = -8036695962127279263L;
	private int width;
	private int height;
	private String creator;
	private String uri;

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
