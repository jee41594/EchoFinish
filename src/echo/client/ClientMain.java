/*
 db연동 필요! 프로그램 가동하자마자 채워져있어야해서
 rs는 곧죽으므로 우리반 사람드르이 ip 담아놓을 collectionFramework 필요
 while문 돌릴동안 얻는 ip들을 담아놓아야 하는데
 배열을 대체할 수 있는 것은 dto
 바깥쪽 주머니는 ArrayList
 ----------------------
 선택하면 윈도우 타이틀에 ip뜨도록
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
	//보통 클래스명은 테이블명과 동일하게 간다.
	ArrayList<Chat> list = new ArrayList<Chat>();
	Socket socket; //대화용 소켓이므로 stream도 뽑아내야함
	String ip;
	ClientThread ct;

	
	public ClientMain() {
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
		
		//choice.add("선택");
		
		//choice 생성
		for(int i=0; i<list.size(); i++){
			choice.add(list.get(i).getName());
		}
		
		//리스너 연결
		choice.addItemListener(this);
		bt_connect.addActionListener(this);
		t_input.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_ENTER) {
					
					String msg = t_input.getText();
					
					ct.send(msg); //보내기
					t_input.setText(""); 	//입력한 글씨 지우기
					
					//listen(); //동생이 이미 하고있으므로 지워도 된다.
				}				
			}
		});
		
		setVisible(true);
		setBounds(300,100,300,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//db가져오기
	public void loadIP() {
		//connection받아옴
		Connection con = manager.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs= null;
		
		String sql = "select * from chat order by chat_id asc";
		
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			//커서가 자유로운걸로 업그레이드 할 필요가 없다.
			
			//rs의 모든 데이터를 dto로 옮기는 과정!
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
			//여기서 close하지말고 manager에서 해야한다. 반납하자!
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
	
	//서버에 접속을 시도한다. 우리는 미리 ip와 port 준비해야한다. 종이컵! = socket
	//소켓 생성시 접속이 발생!
	public void connect() {
		try {
			port = Integer.parseInt(t_port.getText());
			socket =new Socket(ip, port);
			
			/*
			 실시간으로 서버에 메세지를 청취하기 위해 쓰레드를 생성하여
			 대화 업무를 다 맡겨버리자
			 따라서 종이컵 & 실의 보유자는 동생!  
			 동생에게 socket이랑 area같이 넘겨주자!
			*/
			ct = new ClientThread(socket, area);
			ct.start();
			
			ct.send(" 안녕~~?");
			
			//2개 동시에 블럭지정해서 try/catch
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
