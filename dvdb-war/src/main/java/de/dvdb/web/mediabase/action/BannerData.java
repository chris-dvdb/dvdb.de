package de.dvdb.web.mediabase.action;

import java.io.Serializable;

import org.jboss.seam.annotations.Name;

@Name("bannerData")
public class BannerData implements Serializable {

	private static final long serialVersionUID = 1459434470822060294L;

	Integer Width = 100;

	Integer Height = 50;

	byte[] data = new byte[10000];

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
