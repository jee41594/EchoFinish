/*
 1. 접속버튼 누르면 서버 가동
 
 ----------
 버튼이 눌려있고 "가동"누르면 textArea , Field안묶인가
 메인쓰레드 문제! accept에서 무한대기에 빠지다. 
 GUI 기반일 경우에는 무조건 Thread 필요!
 
 */

package echo.server;

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
	ServerSocket server; //단순히 접속 감지용 소켓 - 기다리는자이므로 ip 필요x
	
	Thread thread; //서버 가동용 쓰레드!! 왜?? 메인쓰레드는 운영을 해야 하므로!
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ServerMain() {
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port), 15); //int는 오류나니까
		bt_start = new JButton("가동");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		
		p_north.add(t_port);
		p_north.add(bt_start);
		
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		//왜setBounds? 윈도우창이 여러개 생겨져있으면 헷갈리니까 나란히 뛰우도록!
		setBounds(600,100,300,400); //x,y좌표 결정할 수 있도록
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);	
	}
	
	/*예외의 종류 - 
	 Checked Exception : 예외처리 강요
	 RuntimeException : 예외처리 안강요
	*/
	
	//서버 생성 및 가동
	public void startServer() {
		//버튼을 비활성화 한번 누르고 계속 못누르도록!
		bt_start.setEnabled(false);

		//checek exception! 오류가 발생하지 않음에도컴파일 시키지 않는거
		//2가지가 있다.
		
		try {
			//String이 int로 바껴야함
			port = Integer.parseInt(t_port.getText());
			//서버 올리기
			server = new ServerSocket(port); //서버 생성
			area.append("서버 준비됨..\n");
			
			/*
			서버 가동
			실행부 라 불리는 메인쓰레드는 절대 무한대기상태에 빠뜨리면 안된다.
			왜? 실행부는 유저들의 이벤트를 감지하거나 프로그램을 운영해야 하므로
			무한루프나 대기에 빠지면 본연의 역할을 할 수 없게 된다.
			스마트폰 개발에서는 이 코드자체가 허용x..(컴파일 타임부터 에러발생)
			*/
			//서버는 듣고 말해야하므로 socket필요!
			Socket socket = server.accept(); 
			area.append("서버 가동..\n");
			
			/*클라이언트가 접속한 이유는? 대화를 하기 위해!
			 접속이 되는 순간 스트림들을 얻어놓자 */
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//클라이언트의 메세지 받고 보내기 한번만 해주면x 반복해줘야하한다.
			String data;
			
			while(true){
				data = buffr.readLine(); //받기
				area.append("클라이언트의 말:" +data+"\n");
				buffw.write(data+"\n"); //보내기
				buffw.flush(); //버퍼 비우기
			}	
			
		} catch (NumberFormatException e) {
			//이런걸 runTimeException이라고 한다. 강요하지 않는 예외
			JOptionPane.showMessageDialog(this, "port는 숫자로 넣어라!!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		//이 시점에 쓰레드를 뉴해야한다. 내부익명으로
		thread = new Thread(){
			public void run() {
				startServer();
			}
		};
		thread.start();
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
}
