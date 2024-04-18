import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Department dp = new Department(1, "Computing");
		
		try {
			Seller seller = new Seller(7,"Itérbio","iterbio7@gmail.com",sdf.parse("12/09/1995"),5800.00, dp);
			System.out.println(seller);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}