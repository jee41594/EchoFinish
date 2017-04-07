/*
 db���� �ʿ�! ���α׷� �������ڸ��� ä�����־���ؼ�
 rs�� �������Ƿ� �츮�� ����帣�� ip ��Ƴ��� collectionFramework �ʿ�
 while�� �������� ��� ip���� ��Ƴ��ƾ� �ϴµ�
 �迭�� ��ü�� �� �ִ� ���� dto
 �ٱ��� �ָӴϴ� ArrayList
 ----------------------
 �����ϸ� ������ Ÿ��Ʋ�� ip�ߵ���
 */

package echo.client;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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

public class ClientMain extends JFrame implements ItemListener, ActionListener{
	
	JPanel p_north;
	Choice choice;
	JTextField t_port, t_input;
	JButton bt_connect;
	JTextArea area;
	JScrollPane scroll;
	int port = 7777;
	DBManager manager;
	//���� Ŭ�������� ���̺��� �����ϰ� ����.
	ArrayList<Chat> list = new ArrayList<Chat>();
	Socket socket; //��ȭ�� �����̹Ƿ� stream�� �̾Ƴ�����
	String ip;
	ClientThread ct;

	
	public ClientMain() {
		p_north = new JPanel();
		choice = new Choice();
		t_port = new JTextField(Integer.toString(port),10);
		t_input = new JTextField();
		bt_connect = new JButton("����");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		manager = DBManager.getInstance();
		
		p_north.add(choice);
		p_north.add(t_port);
		p_north.add(bt_connect);
		
		add(p_north, BorderLayout.NORTH);
		
		add(scroll);
		add(t_input, BorderLayout.SOUTH);
		
		loadIP(); //������ �߸� ȣ���ؾ��ϹǷ� ���⿡��!
		
		//choice.add("����");
		
		//choice ����
		for(int i=0; i<list.size(); i++){
			choice.add(list.get(i).getName());
		}
		
		//������ ����
		choice.addItemListener(this);
		bt_connect.addActionListener(this);
		t_input.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_ENTER) {
					
					String msg = t_input.getText();
					
					ct.send(msg); //������
					t_input.setText(""); 	//�Է��� �۾� �����
					
					//listen(); //������ �̹� �ϰ������Ƿ� ������ �ȴ�.
				}				
			}
		});
		
		setVisible(true);
		setBounds(300,100,300,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//db��������
	public void loadIP() {
		//connection�޾ƿ�
		Connection con = manager.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs= null;
		
		String sql = "select * from chat order by chat_id asc";
		
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			//Ŀ���� �����ο�ɷ� ���׷��̵� �� �ʿ䰡 ����.
			
			//rs�� ��� �����͸� dto�� �ű�� ����!
			while(rs.next()) {
				Chat dto = new Chat();
				
				dto.setChat_id(rs.getInt("chat_id"));
				dto.setName(rs.getString("name"));
				dto.setIp(rs.getString("ip"));
				
				list.add(dto);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}			
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}			
			}
			//���⼭ close�������� manager���� �ؾ��Ѵ�. �ݳ�����!
			manager.disConnect(con);
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		Choice ch = (Choice)e.getSource();
		int index = ch.getSelectedIndex();
		
		//int indexx= choice.getSelectedIndex();
		Chat chat = list.get(index);
		this.setTitle(chat.getIp());
		ip=chat.getIp();
		
	}
	
	//������ ������ �õ��Ѵ�. �츮�� �̸� ip�� port �غ��ؾ��Ѵ�. ������! = socket
	//���� ������ ������ �߻�!
	public void connect() {
		try {
			port = Integer.parseInt(t_port.getText());
			socket =new Socket(ip, port);
			
			/*
			 �ǽð����� ������ �޼����� û���ϱ� ���� �����带 �����Ͽ�
			 ��ȭ ������ �� �ðܹ�����
			 ���� ������ & ���� �����ڴ� ����!  
			 �������� socket�̶� area���� �Ѱ�����!
			*/
			ct = new ClientThread(socket, area);
			ct.start();
			
			ct.send(" �ȳ�~~?");
			
			//2�� ���ÿ� �������ؼ� try/catch
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		connect();
	}
	
	public static void main(String[] args) {
		new ClientMain();
	}
}
