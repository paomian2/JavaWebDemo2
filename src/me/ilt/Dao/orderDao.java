package me.ilt.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.ilt.Bean.PageBean;
import me.ilt.Bean.goodsBean;
import me.ilt.Bean.orderBean;
import me.ilt.Bean.orderItemBean;
import me.ilt.Util.Conn;
import me.ilt.Util.jsonUtil;
import net.sf.json.JSONArray;

public class orderDao {
	/**
	 * ���ݶ���ID��ѯ������Ϣ
	 * @param orderId
	 * @return
	 */
	public static orderBean orderIdSel(String orderId){
		String sql = "select * from t_order where id =?";
		Connection con = Conn.getCon();
		ResultSet rs = null;
		orderBean order = null;
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, orderId);
			rs = ps.executeQuery();
			rs.next();
			int userId = rs.getInt("userId");
			String id = rs.getString("id"); //����ID
			double total = rs.getDouble("total"); //�����ܶ�
			int addressId = rs.getInt("addressId"); //�ջ���ַID
			String remarks = rs.getString("remarks");  //�������
			String time = rs.getString("time");  //�µ�ʱ��
			int state = rs.getInt("state");  //��ǰ״̬
			order = new orderBean(id, userId, total, addressId, remarks, time, state);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return order;
	}
	/**
	 * �����û�ID��ѯ������Ϣ
	 * @param userId
	 * @param p ����ҳ
	 * @return
	 */
	public static PageBean userIdIsorder(int userId,int p,String type){
		PageBean pb = new PageBean();
		int count = 0; //��ѯ��������
		String sql = null;
		switch (Integer.parseInt(type)) {
		case 1:
			sql = "select count(*) count from t_order where userId ="+userId;
			break;
		case 2:
			sql = "select count(*) count from t_order where userId ="+userId+"  and state = 1";
			break;
		case 3:
			sql = "select count(*) count from t_order where userId ="+userId+"  and state = 6";
			break;
		case 4:
			sql = "select count(*) count from t_order where userId ="+userId+"  and state = 3";
			break;
		default:
			break;
		}
		count = count(sql); //��ȡ����
		System.out.println("��ȡ����ѯ������Ϊ��"+count);
		pb.setCount(count); //����������
		pb.setP(p); //���뵱ǰҳ��
		String sql2 = null;
//		switch (Integer.parseInt(type)) {
//		case 1:
//			sql2 = "select top "+pb.getPagesize()+" * from t_order where userId ="+userId
//			+" and id not in(select top "+(pb.getP()-1)*pb.getPagesize()+" id from t_order where userId ="+userId+" order by time desc) "
//			+" order by time desc";
//			break;
//		case 2:
//			sql2 = "select top "+pb.getPagesize()+" * from t_order where userId ="+userId
//			+" and state = 1"
//			+" and id not in(select top "+(pb.getP()-1)*pb.getPagesize()+" id from t_order where userId ="+userId+" and state = 1 order by time desc) "
//			+" order by time desc";
//			break;
//		case 3:
//			sql2 = "select top "+pb.getPagesize()+" * from t_order where userId ="+userId
//			+" and state = 6"
//			+" and id not in(select top "+(pb.getP()-1)*pb.getPagesize()+" id from t_order where userId ="+userId+" and state = 6 order by time desc) "
//			+" order by time desc";
//			break;
//		case 4:
//			sql2 = "select top "+pb.getPagesize()+" * from t_order where userId ="+userId
//			+" and state = 3"
//			+" and id not in(select top "+(pb.getP()-1)*pb.getPagesize()+" id from t_order where userId ="+userId+" and state = 3 order by time desc) "
//			+" order by time desc";
//			break;
//		default:
//			break;
//		}


		switch (Integer.parseInt(type)) {
			case 1:
				sql2 = "select  * from t_order where userId ="+userId
						+" order by time desc";
				break;
			case 2:
				sql2 = "select * from t_order where userId ="+userId
						+" and state = 1"
						+" order by time desc";
				break;
			case 3:
				sql2 = "select * from t_order where userId ="+userId
						+" and state = 6"
						+" order by time desc";
				break;
			case 4:
				sql2 = "select * from t_order where userId ="+userId
						+" and state = 3"
						+" order by time desc";
				break;
			default:
				break;
		}
		
		System.out.println("���͵�sql:"+sql2);
		List<orderBean> list = new ArrayList<orderBean>();
		Connection con = Conn.getCon();
		ResultSet rs = null;
		try {
			PreparedStatement ps = con.prepareStatement(sql2);
			rs = ps.executeQuery();
			while(rs.next()){
				String id = rs.getString("id"); //����ID
				double total = rs.getDouble("total"); //�����ܶ�
				int addressId = rs.getInt("addressId"); //�ջ���ַID
				String remarks = rs.getString("remarks");  //�������
				String time = rs.getString("time");  //�µ�ʱ��
				int state = rs.getInt("state");  //��ǰ״̬
				List<orderItemBean> itemList = orderItemDao.orderIdSelItem(id);
				orderBean order = new orderBean(id, userId, total, addressId, remarks, time, state);
				order.setItemList(itemList);
				list.add(order);
			}
			pb.setData(list);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return pb;
	}
	/**
	 * ��ѯȫ��
	 * @param p
	 * @param pageSize
	 * @return
	 */
	public static JSONArray selAll(int p,int pageSize){
		String sql = "select t_order.*,t_user.userName,t_user.trueName,t_address.province,t_address.city,t_address.area,t_address.address,t_address.phone,t_address.username from t_order,t_user,t_address where t_order.userId=t_user.id and t_address.id=t_order.addressId order by time desc";
				return sel(sql);
	}
	/**
	 * ��ѯsql ����json����
	 * @param sql
	 * @return
	 */
	public static JSONArray sel(String sql){
		System.out.println("sql��ѯ��䣺"+sql);
		Connection con = Conn.getCon();
		ResultSet rs = null;
		JSONArray jsonArray = null;
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			jsonArray = jsonUtil.formatRsToJsonArray(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonArray;
		
	}
	/**
	 * ��ѯ������
	 * @return
	 */
	public static int count(String sql){
		Connection con = Conn.getCon();
		int i = 0;
		ResultSet rs = null;
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			rs.next();
			if(rs!=null){
				i = rs.getInt("count");
			}else{
				i = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
		System.out.println("��ѯ�����û�����Ϊ��"+i);
		return i;
	}
	/**
	 * ��Ӷ���
	 * @param u
	 * @return
	 */
	public static int add(orderBean u){

		String sql = "insert into t_order values(?,?,?,?,?,?,?)";
		Connection con = Conn.getCon();
		PreparedStatement ps = null;
		int i = 0;
		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, u.getId());
			ps.setDouble(2, u.getUserId());
			ps.setDouble(3, u.getTotal());
			ps.setInt(4, u.getAddressId());
			ps.setString(5, u.getRemarks());
			ps.setString(6, u.getTime());
			ps.setInt(7, u.getState());
			
			i = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}
	/**
	 * �޸Ķ�����Ϣ
	 * @param
	 * @return
	 */
	public static int update(String orderId,String addressId,String PayType,String liuyan){
		
		int state = 0;
		if(PayType.equals("1")){
			//��������
			state = 6;
		}else{
			//����֧��
			state = 2;
		}
		String sql = "update t_order set addressId=?,remarks=?,state=? where id=?";
		Connection con = Conn.getCon();
		PreparedStatement ps = null;
		int i = 0;
		try {
			ps = con.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(addressId));
			ps.setString(2, liuyan);
			ps.setInt(3, state);
			ps.setString(4, orderId);
			i = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}
	/**
	 * ���ȡ������
	 * @param orderId
	 * @return
	 */
	public static int qxdd(String orderId,int state){
		
		String sql = "update t_order set state=? where id=?";
		Connection con = Conn.getCon();
		PreparedStatement ps = null;
		int i = 0;
		try {
			ps = con.prepareStatement(sql);
			ps.setInt(1, state);
			ps.setString(2, orderId);
			i = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}
	
	public static void main(String[] args) {
		
		//orderBean or = new orderBean("12345", 12, 6.6, 0, "", "2015-01-01", 1);
		//add(or);
		//List<orderBean> list =userIdIsorder(10006);
		
		//PageBean pb = userIdIsorder(10026, 1,"3");
		//System.out.println(pb);
		
		orderIdSel("201602201723459");
		
	}
}
