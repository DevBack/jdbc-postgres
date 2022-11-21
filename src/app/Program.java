package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

public class Program {

	public static void main(String[] args) throws SQLException {
		
		Connection conn = DB.getConnection();
	
		Statement st = conn.createStatement();
			
		ResultSet rs = st.executeQuery("SELECT * FROM tb_order "
				+ "INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id "
				+ "INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");
		
		Map<Long, Order> ord = new HashMap<>();
		Map<Long, Product> pdt = new HashMap<>();
		
		while (rs.next()) {
			
			Long orderId = rs.getLong("order_id");
			if(ord.get(orderId) == null) {
				Order order = instantiateOrder(rs);
				ord.put(orderId, order);
			}
			
			Long productId = rs.getLong("product_id");
			if(pdt.get(productId) == null) {
				Product product = instantiateProduct(rs);
				pdt.put(productId, product);
		
			}
			
			ord.get(orderId).getProducts().add(pdt.get(productId));
		}
		
		for(Long orderId : ord.keySet()) {
			System.out.println(ord.get(orderId));
			for(Product product : ord.get(orderId).getProducts()) {
				System.out.println(product);
			}
			System.out.println();
		}
	}
	
	private static Product instantiateProduct (ResultSet rs) throws SQLException {
		
		Product product = new Product();
		
		product.setId(rs.getLong("product_id"));
		product.setName(rs.getString("name"));
		product.setPrice(rs.getDouble("price"));
		product.setDescription(rs.getString("description"));
		product.setImageUri(rs.getString("image_uri"));
		
		return product;
	}
	
	private static Order instantiateOrder (ResultSet rs) throws SQLException {
		
		Order order = new Order();
		
		order.setId(rs.getLong("order_id"));
		order.setLatitude(rs.getDouble("latitude"));
		order.setLongitude(rs.getDouble("longitude"));
		order.setMoment(rs.getTimestamp("moment").toInstant());
		order.setStatus(OrderStatus.values()[rs.getInt("status")]);
		
		return order;
	}
}
