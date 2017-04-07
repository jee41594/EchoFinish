package practice;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import db.DBManager;
import echo.client.Chat;

public class ClientManager extends JFrame{

	JPanel p_north;
	Choice choice;
	JTextField t_port, t_input;
	JButton bt_connect;
	JTextArea area;
	JScrollPane scroll;
	int port = 7777;
	DBManager manager;
	//보통 클래스명은 테이블명과 동일하게 간다.
	ArrayList<Chat> list = new ArrayList<Chat>();
	
	public ClientManager() {
		
		p_north = new JPanel();
		choice = new Choice();
		t_port = new JTextField(Integer.toString(port),10);
		t_input = new JTextField();
		bt_connect = new JButton("연결");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		manager = DBManager.getInstance();
		
		p_north.add(choice);
		p_north.add(t_port);
		p_north.add(bt_connect);
		
		add(p_north, BorderLayout.NORTH);
		
		add(scroll);
		add(t_input, BorderLayout.SOUTH);
		
		loadIP(); //윈도우 뜨면 호출해야하므로 여기에서!
		//choice 생성
		for(int i=0; i<list.size(); i++) {
			choice.add(list.get(i).getName());
		}

		
		setVisible(true);
		setBounds(300,100,300,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	public void loadIP(){
		Connection con = manager.getConnection();
		PreparedStatement pstmt= null;
		ResultSet rs=  null;
		
		String sql = "select * from chat order by chat_id asc";
		
		try {
			pstmt = con.prepareStatement(sql);
			rs= pstmt.executeQuery();
			
			while(rs.next()){
				Chat dto = new Chat();
				
				dto.setChat_id(rs.getInt("chat_id"));
				
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	public static void main(String[] args) {
		new ClientManager();
	}

}
