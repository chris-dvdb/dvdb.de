package de.dvdb.infrastructure.persistence;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.dialect.MySQLInnoDBDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;

public class ExtendedMySQL5InnoDBDialect extends MySQLInnoDBDialect {

	public ExtendedMySQL5InnoDBDialect() {
		super();
		registerFunction("date_add_interval", new SQLFunctionTemplate(
				Hibernate.DATE, "date_add(?1, INTERVAL ?2 ?3)"));
		registerHibernateType(Types.LONGVARCHAR, Hibernate.TEXT.getName()); 
	}

}
