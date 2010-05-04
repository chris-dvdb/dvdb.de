package de.dvdb.domain.model.item.palace;

public interface PalaceConverter {

	public void cleanUpDeletedItems();

	public void importUpdatedDVDs(Integer batchSize);

	public void importSingleDVD(Long id);

}