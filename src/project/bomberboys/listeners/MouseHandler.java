package project.bomberboys.listeners;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import project.bomberboys.MainBoom;
import project.bomberboys.sockets.Client;
import project.bomberboys.sockets.Server;
import project.bomberboys.window.Button;

public class MouseHandler extends MouseAdapter{
	
	private MainBoom main;
	private String type;

	public MouseHandler(String type, MainBoom main) {
		this.type = type;
		this.main = main;
	}
	
	public void mouseClicked(MouseEvent e) {
		if(type.equals("menu")) {
			Button b = (Button) e.getComponent();
			if(b.isEnabled()) {
				JFrame menuWindow = main.getMenuWindow();
				Container c = menuWindow.getContentPane();
				CardLayout cl = (CardLayout) c.getLayout();
				switch(b.getText()) {
				case "Start as server":
					cl.show(c, "serverPanel");
					break;
				case "Start as client":
					cl.show(c, "clientPanel");
					break;
				case "Settings":
					cl.show(c, "settingsPanel");
					break;
				case "Back":
					cl.show(c, "mainOptionPanel");
					break;
				//Server panel case
				case "Start!":
					new Server(main.getPlayerCountTF(), main.getRoundCountTF(), main.getPortTF());
					cl.show(c, "serverWaitPanel");
					break;
				
				//Client panel case
				case "Connect":
					new Client(main.getIPTF(), main.getPortTF());
					cl.show(c, "clientWaitPanel");
					break;
				}
			}
		} else if(type.equals("game")) {
			
		}
	}

}