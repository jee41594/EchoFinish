/*
 1. ���ӹ�ư ������ ���� ����
 
 ----------
 ��ư�� �����ְ� "����"������ textArea , Field�ȹ��ΰ�
 ���ξ����� ����! accept���� ���Ѵ�⿡ ������. 
 GUI ����� ��쿡�� ������ Thread �ʿ�!
 
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
	ServerSocket server; //�ܼ��� ���� ������ ���� - ��ٸ������̹Ƿ� ip �ʿ�x
	
	Thread thread; //���� ������ ������!! ��?? ���ξ������ ��� �ؾ� �ϹǷ�!
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ServerMain() {
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port), 15); //int�� �������ϱ�
		bt_start = new JButton("����");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		
		p_north.add(t_port);
		p_north.add(bt_start);
		
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		//��setBounds? ������â�� ������ ������������ �򰥸��ϱ� ������ �ٿ쵵��!
		setBounds(600,100,300,400); //x,y��ǥ ������ �� �ֵ���
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);	
	}
	
	/*������ ���� - 
	 Checked Exception : ����ó�� ����
	 RuntimeException : ����ó�� �Ȱ���
	*/
	
	//���� ���� �� ����
	public void startServer() {
		//��ư�� ��Ȱ��ȭ �ѹ� ������ ��� ����������!
		bt_start.setEnabled(false);

		//checek exception! ������ �߻����� �������������� ��Ű�� �ʴ°�
		//2������ �ִ�.
		
		try {
			//String�� int�� �ٲ�����
			port = Integer.parseInt(t_port.getText());
			//���� �ø���
			server = new ServerSocket(port); //���� ����
			area.append("���� �غ��..\n");
			
			/*
			���� ����
			����� �� �Ҹ��� ���ξ������ ���� ���Ѵ����¿� ���߸��� �ȵȴ�.
			��? ����δ� �������� �̺�Ʈ�� �����ϰų� ���α׷��� ��ؾ� �ϹǷ�
			���ѷ����� ��⿡ ������ ������ ������ �� �� ���� �ȴ�.
			����Ʈ�� ���߿����� �� �ڵ���ü�� ���x..(������ Ÿ�Ӻ��� �����߻�)
			*/
			//������ ��� ���ؾ��ϹǷ� socket�ʿ�!
			Socket socket = server.accept(); 
			area.append("���� ����..\n");
			
			/*Ŭ���̾�Ʈ�� ������ ������? ��ȭ�� �ϱ� ����!
			 ������ �Ǵ� ���� ��Ʈ������ ������ */
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//Ŭ���̾�Ʈ�� �޼��� �ް� ������ �ѹ��� ���ָ�x �ݺ���������Ѵ�.
			String data;
			
			while(true){
				data = buffr.readLine(); //�ޱ�
				area.append("Ŭ���̾�Ʈ�� ��:" +data+"\n");
				buffw.write(data+"\n"); //������
				buffw.flush(); //���� ����
			}	
			
		} catch (NumberFormatException e) {
			//�̷��� runTimeException�̶�� �Ѵ�. �������� �ʴ� ����
			JOptionPane.showMessageDialog(this, "port�� ���ڷ� �־��!!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		//�� ������ �����带 ���ؾ��Ѵ�. �����͸�����
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
