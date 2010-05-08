package de.dvdb.web.social.geo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.application.DvdbException;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.mediabase.MediabaseService;
import de.dvdb.domain.model.social.Address;
import de.dvdb.web.Actor;

@Name("googleGeoService")
@Scope(ScopeType.CONVERSATION)
public class GoogleGeoService implements Serializable {

	static final long serialVersionUID = 784517547344928199L;

	@In(required = false)
	String addressString;

	@DataModel
	List addressList = new ArrayList();

	@DataModelSelection
	@Out(required = false)
	Address address;

	@In
	EntityManager dvdb;

	@In
	FacesMessages facesMessages;

	@Logger
	Log log;

	@In
	Actor actor;

	@In(create = true)
	MediabaseService mediabaseService;

	@In
	ApplicationSettings applicationSettings;

	public void echo() {
		log.info("echo");
	}

	public List<Object> getMoviebasesInRegion(Double o1, Double o2, Double o3,
			Double o4) {

		List<Object> result = dvdb
				.createNativeQuery(
						"select GROUP_CONCAT(username ORDER BY username ASC SEPARATOR ',') as users,breite,laenge from dvdb2_mediabase,dvdb2_user where user_id = dvdb2_user.id and breite is not null and geoactivated = true and breite >= :minbreite and breite <= :maxbreite and laenge >= :minlaenge and laenge <= :maxlaenge group by concat(breite, laenge)")
				.setParameter("minbreite", o3).setParameter("maxbreite", o1)
				.setParameter("minlaenge", o4).setParameter("maxlaenge", o2)
				.getResultList();

		List<Object> customList = new ArrayList<Object>();

		Object[] o;
		for (Object line : result) {

			o = new Object[3];
			Object[] la = (Object[]) line;
			o[0] = la[1];
			o[1] = la[2];
			o[2] = la[0];
			customList.add(o);
		}

		return customList;

	}

	@SuppressWarnings("unchecked")
	public List<Mediabase> getMediabasesNearby(Mediabase mediabase) {

		if (!mediabase.getGeoActivated())
			return null;

		if (mediabase.getAddress() == null
				|| mediabase.getAddress().getLaenge() == null)
			return null;

		Double minBreite = mediabase.getAddress().getBreite() - 1d;
		Double maxBreite = mediabase.getAddress().getBreite() + 1d;
		Double minLaenge = mediabase.getAddress().getLaenge() - 1d;
		Double maxLaenge = mediabase.getAddress().getLaenge() + 1d;

		return dvdb
				.createQuery(
						"from Mediabase mb where mb.address.breite >= :minbreite and mb.address.breite <= :maxbreite and mb.address.laenge >= :minlaenge and mb.address.laenge <= :maxlaenge and mb.geoActivated = true")
				.setParameter("minbreite", minBreite).setParameter("maxbreite",
						maxBreite).setParameter("minlaenge", minLaenge)
				.setParameter("maxlaenge", maxLaenge).getResultList();

	}

	@SuppressWarnings("unchecked")
	@Begin(join = true)
	public void lookup() {
		log.info("Looking up " + addressString);

		String urlencoded = addressString;
		try {
			urlencoded = URLEncoder.encode(addressString, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		}

		String url = String.format(
				"http://maps.google.com/maps/geo?q=%s&key=%s&output=json",
				urlencoded, applicationSettings.getGoogleMapsKey());
		String result = retrieveHttpDocument(url);

		JSONObject json = JSONObject.fromObject(result);
		DynaBean dBean = (DynaBean) JSONObject.toBean(json);

		if (getStatus(dBean) != 200) {
			facesMessages.addFromResourceBundle(Severity.INFO,
					"account.location.addressNotFound");
			return;
		}

		ArrayList<DynaBean> placemarks = (ArrayList<DynaBean>) dBean
				.get("Placemark");

		List addresses = new ArrayList();
		for (DynaBean placemark : placemarks) {
			Address adr = buildAddress(placemark);
			addresses.add(adr);
		}
		this.addressList = addresses;
	}

	@End
	public void select() {
		log.error("Selecting address " + address);
		Mediabase mb = (Mediabase) dvdb.find(Mediabase.class, actor.getUser()
				.getMediabase().getId());
		mb.setAddress(address);
		dvdb.flush();
		actor.getUser().setMediabase(mb);
	}

	public void delete() {
		log.error("Selecting address " + address);
		Mediabase mb = (Mediabase) dvdb.find(Mediabase.class, actor.getUser()
				.getMediabase().getId());
		mb.setAddress(null);
		dvdb.flush();
		actor.getUser().setMediabase(mb);
	}

	private Integer getStatus(DynaBean dBean) {
		DynaBean db = (DynaBean) dBean.get("Status");
		return (Integer) db.get("code");
	}

	@SuppressWarnings("unchecked")
	private Address buildAddress(DynaBean placemark) {

		String country = null;
		String city = null;
		String plz = null;
		String street = null;
		Double laenge = null;
		Double breite = null;

		try {
			country = (String) ((DynaBean) ((DynaBean) placemark
					.get("AddressDetails")).get("Country"))
					.get("CountryNameCode");

			DynaBean p = (DynaBean) placemark.get("Point");

			ArrayList<Double> coords = (ArrayList<Double>) p.get("coordinates");
			// log.info(p.getClass().getName());
			laenge = coords.get(0);
			breite = coords.get(1);

			DynaBean db = ((DynaBean) placemark.get("AddressDetails"));
			db = (DynaBean) db.get("Country");
			db = (DynaBean) db.get("AdministrativeArea");
			db = (DynaBean) db.get("SubAdministrativeArea");
			db = (DynaBean) db.get("Locality");
			city = (String) db.get("LocalityName");

			street = (String) ((DynaBean) db.get("Thoroughfare"))
					.get("ThoroughfareName");
			plz = (String) ((DynaBean) db.get("PostalCode"))
					.get("PostalCodeNumber");

		} catch (Exception e) {
			log.warn(e);
		}

		Address address = new Address();
		address.setCountry(country);
		address.setCity(city);
		address.setPlz(plz);
		address.setStreet(street);
		address.setLaenge(laenge);
		address.setBreite(breite);

		return address;
	}

	protected String retrieveHttpDocument(String url) throws DvdbException {
		HttpClient client = new HttpClient();
		// establish a connection within 5 seconds
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);

		String responseBody = null;
		HttpMethod method = null;
		try {
			method = new GetMethod(url);
			client.executeMethod(method);
			responseBody = method.getResponseBodyAsString();
		} catch (Exception e) {
			throw new DvdbException(e);
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return responseBody;
	}

	public String getGoogleMapsKey() {
		return applicationSettings.getGoogleMapsKey();
	}

}
