package model.dao;

import db.DB;
import mode.dao.impl.DepartmentDaoJDBC;
import mode.dao.impl.SellerDaoJDBC;

public class DaoFactory {
	
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	public static DepartmentDao createDepartmenDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
	}
}
