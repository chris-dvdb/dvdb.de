package de.dvdb.web;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.MediabaseItemCollectible;

@Name("specialDVDBQueries")
@Scope(ScopeType.CONVERSATION)
public class SpecialDVDBQueries implements Serializable {

	private static final long serialVersionUID = 1755776412011374856L;

	@In("dvdb")
	EntityManager dvdb;

	@Logger
	Log log;

	private int startRowWishes = 0;

	private int startRowCollectibles = 0;

	public void moreCollectibles() {

		this.startRowCollectibles = this.startRowCollectibles + 20;
	}

	public void moreWishes() {
		log.info("adadadada");
		this.startRowWishes = this.startRowWishes + 20;
	}

	public List<MediabaseItemCollectible> getLatestCollectibles() {

		List<BigInteger> ids = (List<BigInteger>) dvdb
				.createNativeQuery(
						"select id from dvdb2_mediabase_item where dateofcreation > adddate(NOW(), interval -5 DAY) and itemtype = 1 order by dateofcreation desc limit :start,:length")
				.setParameter("start", startRowCollectibles).setParameter(
						"length", 20).getResultList();

		if (ids.size() == 0)
			return new ArrayList<MediabaseItemCollectible>();

		List<Long> longs = new ArrayList<Long>();
		for (BigInteger id : ids) {
			longs.add(id.longValue());
		}

		List<MediabaseItemCollectible> colls = dvdb
				.createQuery(
						"select mic from MediabaseItemCollectible mic inner join fetch mic.item item inner join fetch mic.mediabase mediabase inner join mediabase.user user where mic.id in (:ids) and item.itemType != 3 and item.indiziert = false order by mic.entityMetadata.dateOfCreation desc")
				.setMaxResults(6).setParameter("ids", longs).getResultList();

		return colls;
	}

	public List<MediabaseItemCollectible> getLatestWishes() {

		List<BigInteger> ids = (List<BigInteger>) dvdb
				.createNativeQuery(
						"select id from dvdb2_mediabase_item where dateofcreation > adddate(NOW(), interval -5 DAY) and itemtype = 2 order by dateofcreation desc limit :start,:length")
				.setParameter("start", startRowWishes).setParameter("length",
						20).getResultList();

		if (ids.size() == 0)
			return new ArrayList<MediabaseItemCollectible>();

		List<Long> longs = new ArrayList<Long>();
		for (BigInteger id : ids) {
			longs.add(id.longValue());
		}

		List<MediabaseItemCollectible> colls = dvdb
				.createQuery(
						"select mic from MediabaseItemWish mic inner join fetch mic.item item inner join fetch mic.mediabase mediabase inner join mediabase.user user where mic.id in (:ids) and item.itemType != 3 and item.indiziert = false order by mic.entityMetadata.dateOfCreation desc")
				.setMaxResults(6).setParameter("ids", longs).getResultList();

		return colls;
	}
}
