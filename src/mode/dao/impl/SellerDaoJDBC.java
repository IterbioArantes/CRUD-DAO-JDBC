package mode.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{
	
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		
	}

	@Override
	public void update(Seller obj) {
		
	}

	@Override
	public void deleteById(Integer id) {

		
	}

	@Override
	public Seller findById(Integer id) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = this.conn.prepareStatement("SELECT seller.*,department.Name as DpName "
											+"from seller INNER JOIN department "
											+"ON department.Id = seller.DepartmentId "
											+"WHERE seller.Id = ?");
			
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				
				Department dp = instantiateDepartment(rs);
				Seller seller = instantiateSeller(rs,dp);
				
				return seller;
			}
			return null;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}
	
	private Department instantiateDepartment(ResultSet rs ) throws SQLException {
		
		Department dp = new Department(rs.getInt("DepartmentId"), rs.getString("DpName"));
		
		return dp;
	}
	
	private Seller instantiateSeller(ResultSet rs ,  Department dp) throws SQLException {
		
		Seller seller = new Seller(rs.getInt("Id"),rs.getString("Name"),rs.getString("Email"),rs.getDate("BirthDate"),rs.getDouble("BaseSalary"), dp);
		
		return seller;
	}

	@Override
	public List<Seller> findaAll() {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			ps = conn.prepareStatement("SELECT seller.*, department.Name as DpName "
									  + "FROM seller INNER JOIN department "
									  + "ON seller.DepartmentId = department.Id "
									  + "ORDER BY Name");
			
			rs = ps.executeQuery();
			List<Seller> listSeller = new ArrayList<Seller>();
			Map<Integer,Department> auxInstDept = new HashMap<Integer, Department>();
			
			while(rs.next()) {
				
				Department dp = auxInstDept.get(rs.getInt("DepartmentId"));
				if(dp == null) {
					auxInstDept.put(rs.getInt("DepartmentId"), instantiateDepartment(rs));
				}
				
				Seller seller = instantiateSeller(rs,dp);
				listSeller.add(seller);
			}
			
			return listSeller;
			
		}catch (SQLException e){
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department dp) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement("SELECT seller.*, department.Name as DpName "
					      			  + "FROM seller INNER JOIN department "		
					                  + "ON department.Id = seller.DepartmentId "
					                  + "WHERE DepartmentId =  ? "
					                  + "ORDER BY Name");
			ps.setInt(1, dp.getId());
			rs = ps.executeQuery();
			
			List<Seller> listSeller = new ArrayList<Seller>();
			
			Map<Integer, Department> auxInstDept = new HashMap<Integer, Department>(); 

			while(rs.next()) {
				
				Department dep = auxInstDept.get(rs.getInt("DepartmentId"));
				
				if(dep == null) {
					dep = instantiateDepartment(rs);
					auxInstDept.put(rs.getInt("DepartmentId"), dep);
				}
				
				listSeller.add(instantiateSeller(rs, dep));
			}
			
			return listSeller;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

}
