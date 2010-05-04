package de.dvdb.domain.model.mediabase;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public enum AccessLevelEnum {
	MYSELF(1), FRIEND(2), USER(3), PASSWORD(4), EVERYBODY(5), DUMMY(6);

	@SuppressWarnings("unused")
	private final Integer accessLevel;

	private AccessLevelEnum(Integer accessLevel) {
		this.accessLevel = accessLevel;
	}

	private static final Map INSTANCES = new HashMap();

	static {
		INSTANCES.put(1, MYSELF);
		INSTANCES.put(2, FRIEND);
		INSTANCES.put(3, USER);
		INSTANCES.put(4, PASSWORD);
		INSTANCES.put(5, EVERYBODY);
		INSTANCES.put(6, DUMMY);
	}

	// ********************** Common Methods ********************** //

}
