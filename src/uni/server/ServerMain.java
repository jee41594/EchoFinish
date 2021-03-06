/*
 한명이 들어가면 또다른한명은 여기 접속할 수 없으므로
 쓰레드를 만들어 각각 독립적으로 들어갈 수 있도록 한다.
 */
package uni.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerMain extends JFrame implements ActionListener{
	JPanel p_north; 
	JTextField t_port;
	JButton bt_start;
	JTextArea area;
	JScrollPane scroll;
	int port=7777;
	ServerSocket server; //1.
	Thread thread; //2.서버 운영 쓰레드!
	
	public ServerMain() {
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port) ,10);
		bt_start = new JButton("가동");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		p_north.add(t_port);
		p_north.add(bt_start);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		setBounds(600,100,300,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	//서버를 가동한다.
	public void startServer() {
		bt_start.setEnabled(false);
		
		try {
			port = Integer.parseInt(t_port.getText()); //2
			server = new ServerSocket(port); //1
			area.append("서버생성\n");
			
			//3 쓰레드생성 개발자는 아무리 new해도 
			thread = new Thread(){
				public void run() {
					//독립수행할것을 run에 넣어라! 초인종누를때 누구세요?~하는 작업! 아까랑은 상황이 다름
					//나 혼자가 아닌 여러명을 감당해야함
					
					try {
						while(true){
							//ip알아맞추려면 종이컵필요
							Socket socket = server.accept();
							String ip = socket.getInetAddress().getHostAddress();
							area.append(ip+"접속자 발견\n");
						
							//buffr, buffw이 빠져나가면 server는 대화를하지 않는다.
							//아바타한테 종이컵을 줘야 한다. 접속만 집중!
							//String msg = buffr.readLine();
							//area.append(msg+"\n");		
							//접속자마다 아바타 생성해줘서 대화를 나눌 수 있도록 해준다.
							 Avatar av = new Avatar(socket, area);
							 av.start();				
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			thread.start();//동작시작
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		startServer();
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
}