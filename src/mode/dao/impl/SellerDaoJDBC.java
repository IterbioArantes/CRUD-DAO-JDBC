package mode.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement("INSERT INTO seller "
									  + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
									  + "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, obj.getName());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			ps.setDouble(4, obj.getBaseSalary());
			ps.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = ps.executeUpdate();
			
			if(rowsAffected > 0) {
				rs = ps.getGeneratedKeys();
				
				if(rs.next()) {
					obj.setId(rs.getInt(1));
				}
			}else {
				throw new DbException("No rows affected!");
			}
			
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}	
	}

	@Override
	public void update(Seller obj) {
		
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement("UPDATE seller "
					  + "SET Name = ?,Email = ?,BirthDate = ?,BaseSalary = ?,DepartmentId = ? "
	                  + "WHERE Id = ? ");
			
			ps.setString(1, obj.getName());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			ps.setDouble(4, obj.getBaseSalary());
			ps.setInt(5, obj.getDepartment().getId());
			ps.setInt(6, obj.getId());
			
			int rowAffected = ps.executeUpdate();
			if(rowAffected == 0) {
				throw new DbException("User id not fount to update.");
			}
			
			
		}catch (SQLException e){
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void deleteById(Integer id) {
		
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement("DELETE FROM seller "
									 + "WHERE Id = ?");
			ps.setInt(1, id);
			
			int rowAffected = ps.executeUpdate();
			
			if(rowAffected == 0) {
				throw new DbException("User id not found to delete.");
			}
			
			
		}catch (SQLException e){
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(ps);
		}
		
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
