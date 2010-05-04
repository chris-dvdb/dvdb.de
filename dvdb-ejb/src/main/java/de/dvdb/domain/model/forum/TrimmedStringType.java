package de.dvdb.domain.model.forum;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class TrimmedStringType implements UserType {

	private static final int[] SQL_TYPES = { Types.CHAR };

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	public Class<String> returnedClass() {
		return String.class;
	}

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object obj) throws HibernateException {
		if (obj == null) {
			return null;
		}
		return new String((String) obj);
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return (x == y) || (x != null && y != null && (x.equals(y)));
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object obj)
			throws HibernateException, SQLException {
		String val = rs.getString(names[0]);
		return val != null ? val.trim() : null;
	}

	public void nullSafeSet(PreparedStatement pstmt, Object obj, int i)
			throws HibernateException, SQLException {
		pstmt.setString(i, (String) obj);
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}
}
