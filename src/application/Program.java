package application;
import java.text.ParseException;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.dao.SellerDao;
import model.entities.Department;

public class Program {

	public static void main(String[] args) throws ParseException {
		
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		DepartmentDao departmentDao = DaoFactory.createDepartmenDao();
		
		
		Department aa = new Department(7, "outsystemsNew");	
		Department bb = departmentDao.findById(7);
		
		departmentDao.findAll().forEach(System.out::println);
		
	}
}