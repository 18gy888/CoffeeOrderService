package servlet.shoppingcart;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class getUserInfo
 */
@WebServlet("/api/shoppingcart/addShoppingCart")
public class AddShoppingCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddShoppingCart() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			Statement stmt = conn.createStatement();
			ServletInputStream is;
			try {
				is = request.getInputStream();
				int nRead = 1;
				int nTotalRead = 0;
				byte[] bytes = new byte[10240];
				while (nRead > 0) {
					nRead = is.read(bytes, nTotalRead, bytes.length - nTotalRead);
					if (nRead > 0)
						nTotalRead = nTotalRead + nRead;
				}
				String str = new String(bytes, 0, nTotalRead, "utf-8");
				JSONObject jsonObj = JSONObject.fromObject(str);
				String userId = (String) session.getAttribute("userId");
				double price = jsonObj.getDouble("price");
				String mealId = jsonObj.getString("mealId");
				String sql = "select * from user_meal where mealId= ? and userId= ?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, mealId);
				ps.setString(2, userId);
				
				ResultSet rs = ps.executeQuery();
				JSONObject jsonobj = new JSONObject();
				if(rs.next()){
					String sql_next= "UPDATE user_meal SET quality=quality+1 WHERE mealId=? and userId=?";
					PreparedStatement ps_next = conn.prepareStatement(sql_next);
					ps_next.setString(1, mealId);
					ps_next.setString(2, userId);
					ps_next.executeUpdate();
					JSONObject responseJson = new JSONObject();
					responseJson.put("success", true);
					responseJson.put("msg","修改成功");
					out.println(responseJson);
					jsonobj.put("占位符","123");
				}
				if(jsonobj.isEmpty()) {
					String sql_empty = "insert into user_meal(mealId,userId,price,quality) values (?,?,?,?)";
					PreparedStatement ps_empty = conn.prepareStatement(sql_empty);
					
					ps_empty.setString(1, mealId);
					ps_empty.setString(2, userId);
					ps_empty.setDouble(3, price);
					ps_empty.setInt(4,1);
					
					/* ִ��SQL���  */
					ps_empty.executeUpdate();
					JSONObject responseJson = new JSONObject();
					responseJson.put("success", true);
					responseJson.put("msg","新增成功");
					out.println(responseJson);
					
				}
				rs.close();
				stmt.close();
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
