package space_shooter;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JTextField;

public class StartupWindow{

	private JFrame frame;
	private JTextField txtIpAddress;
	private JButton btnPlayGame = new JButton("Play Game");
	GameServerTCP server;
	private int portID = 10001;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartupWindow window = new StartupWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StartupWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JCheckBox chckbxMultiplayer = new JCheckBox("Multiplayer");
		GridBagConstraints gbc_chckbxMultiplayer = new GridBagConstraints();
		gbc_chckbxMultiplayer.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxMultiplayer.gridx = 6;
		gbc_chckbxMultiplayer.gridy = 3;
		panel.add(chckbxMultiplayer, gbc_chckbxMultiplayer);
		
		JCheckBox chckbxSinglePlayer = new JCheckBox("Single Player");
		GridBagConstraints gbc_chckbxSinglePlayer = new GridBagConstraints();
		gbc_chckbxSinglePlayer.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxSinglePlayer.gridx = 8;
		gbc_chckbxSinglePlayer.gridy = 3;
		panel.add(chckbxSinglePlayer, gbc_chckbxSinglePlayer);
		
		txtIpAddress = new JTextField();
		txtIpAddress.setText("IP Address");
		GridBagConstraints gbc_txtIpAddress = new GridBagConstraints();
		gbc_txtIpAddress.insets = new Insets(0, 0, 5, 5);
		gbc_txtIpAddress.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtIpAddress.gridx = 4;
		gbc_txtIpAddress.gridy = 8;
		panel.add(txtIpAddress, gbc_txtIpAddress);
		txtIpAddress.setColumns(10);
		
		JButton btnRunServer = new JButton("Run Server");
		GridBagConstraints gbc_btnRunServer = new GridBagConstraints();
		gbc_btnRunServer.insets = new Insets(0, 0, 0, 5);
		gbc_btnRunServer.gridx = 4;
		gbc_btnRunServer.gridy = 10;
		panel.add(btnRunServer, gbc_btnRunServer);
		
		btnRunServer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					System.out.println("Server address:" + InetAddress.getLocalHost());
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					server = new GameServerTCP(portID);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		GridBagConstraints gbc_btnPlayGame = new GridBagConstraints();
		gbc_btnPlayGame.insets = new Insets(0, 0, 0, 5);
		gbc_btnPlayGame.gridx = 9;
		gbc_btnPlayGame.gridy = 10;
		panel.add(btnPlayGame, gbc_btnPlayGame);
		
		btnPlayGame.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Hello World");
				String address = txtIpAddress.getText();
				MyNetworkingClient game = new MyNetworkingClient(address, portID);
				game.start();
			}
		});
	}
}
