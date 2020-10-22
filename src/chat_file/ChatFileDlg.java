package chat_file;

import java.awt.Container;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat.Encoding;
import javax.swing.*;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;


public class ChatFileDlg extends JFrame implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	String path;

	private static LayerManager m_LayerMgr = new LayerManager();
	int selected_index;
	private JTextField ChattingWrite;
	private JTextField FileDir_path;

	Container contentPane;

	JTextArea ChattingArea;
	JTextArea srcMacAddress;	//modify
	JTextArea srcIpAddress;
	JTextArea dstIpAddress;

	JLabel lblSelectNic;
	JLabel macsrc;		//modify
	JLabel lblsrc;
	JLabel lbldst;

	JButton Setting_Button;
	JButton File_select_Button;
	JButton Chat_send_Button;
	JButton NIC_select_Button;
	JButton File_send_Button;
	JButton Table_Button;

	JComboBox comboBox;

	FileDialog fd;

	public static void main(String[] args) {
		/*
		 * Layer 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쌕뀐옙占쏙옙 占쏙옙 占쏙옙!!!
		 * 占쏙옙占쏙옙 占쏘떠占쏙옙 占쏙옙占쏙옙占쏙옙 占쌕뀐옙占� 占싼다몌옙 占쌕꾸곤옙 占쏙옙 占쏙옙占쌀곤옙!!!
		 */
		m_LayerMgr.AddLayer(new ChatFileDlg("GUI"));
		m_LayerMgr.AddLayer(new ChatAppLayer("CHAT"));
		//m_LayerMgr.AddLayer(new FileLayer("FILE"));
		m_LayerMgr.AddLayer(new TCPLayer("TCP"));
		m_LayerMgr.AddLayer(new IPLayer("IP"));
		m_LayerMgr.AddLayer(new ARPLayer("ARP"));
		m_LayerMgr.AddLayer(new EthernetLayer("ETH"));
		m_LayerMgr.AddLayer(new NILayer("NI"));

		m_LayerMgr.ConnectLayers("NI ( *ETH ( +IP ( *TCP ( *CHAT ( *GUI ) ) ) ) )");
		// m_LayerMgr.ConnectLayers("NI ( *ETH ( +IP ( *TCP ( *CHAT ( *GUI ) *FILE ( *GUI )");
		
		m_LayerMgr.GetLayer("IP").SetUnderLayer(m_LayerMgr.GetLayer("ARP"));
		
		m_LayerMgr.GetLayer("ETH").SetUpperUnderLayer(m_LayerMgr.GetLayer("ARP"));
	}

	public ChatFileDlg(String pName) {
		
		pLayerName = pName;
		setTitle("Chatting & File Transfer");

		setBounds(250, 250, 580, 520);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = this.getContentPane();
		JPanel pane = new JPanel();

		pane.setLayout(null);
		contentPane.add(pane);

		ChattingArea = new JTextArea();
		ChattingArea.setEditable(false);
		ChattingArea.setBounds(12, 13, 359, 326);
		pane.add(ChattingArea);// 채占쏙옙

		srcMacAddress = new JTextArea();
		srcMacAddress.setEditable(false);
		srcMacAddress.setBounds(383, 123, 170, 24);
		pane.add(srcMacAddress);// 占쏙옙占쏙옙占쏙옙 占쌍쇽옙(mac)
		
		srcIpAddress = new JTextArea();
		srcIpAddress.setEditable(false);
		srcIpAddress.setBounds(383, 195, 170, 24);
		pane.add(srcIpAddress);// 占쏙옙占쏙옙占쏙옙 占쌍쇽옙

		dstIpAddress = new JTextArea();
		dstIpAddress.setBounds(383, 254, 170, 24);
		pane.add(dstIpAddress);// 占쌨댐옙 占쏙옙占� 占쌍쇽옙

		ChattingWrite = new JTextField();
		ChattingWrite.setBounds(12, 349, 359, 20);// 249
		pane.add(ChattingWrite);
		ChattingWrite.setColumns(10);// 채占쏙옙 占쏙옙占쏙옙 占쏙옙

		FileDir_path = new JTextField();
		FileDir_path.setEditable(false);
		FileDir_path.setBounds(12, 380, 532, 20); // 280
		pane.add(FileDir_path);
		FileDir_path.setColumns(10);// file 占쏙옙占�

		lblSelectNic = new JLabel("NIC List");
		lblSelectNic.setBounds(383, 13, 170, 20);
		pane.add(lblSelectNic);// 占쏙옙占쏙옙
		
		macsrc = new JLabel("Your MAC Address");			//占쌩곤옙 : 占쏙옙占쏙옙占쏙옙 Mac		
		macsrc.setBounds(383, 98, 170, 20);						
		pane.add(macsrc);										

		lblsrc = new JLabel("Source IP Address");			//Mac->Ip
		lblsrc.setBounds(383, 170, 170, 20);
		pane.add(lblsrc);

		lbldst = new JLabel("Destination IP Address");		//Mac->Ip
		lbldst.setBounds(383, 229, 170, 20);	
		pane.add(lbldst);

		Setting_Button = new JButton("Setting");// setting
		Setting_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (Setting_Button.getText() == "Reset") {
					srcIpAddress.setText("");
					dstIpAddress.setText("");
					Setting_Button.setText("Setting");
					dstIpAddress.setEditable(true);
				} else {
					// 占쏙옙占쏙옙 占십울옙 : 占쌔댐옙 占싸븝옙 IP address占쏙옙 占쏙옙占싹울옙 占쌜억옙占쏙옙 占싹듸옙占쏙옙 占쏙옙占쏙옙占쌔억옙占쏙옙!
					byte[] ipSrcAddress = new byte[4];
					byte[] ipDstAddress = new byte[4];

					String ip_src = srcIpAddress.getText();
					String ip_dst = dstIpAddress.getText();
					String mac_src = srcMacAddress.getText();
					String mac_dst = "ff-ff-ff-ff-ff-ff";
					
					String[] byte_ip_src = ip_src.split("\\.");
					for (int i = 0; i < 4; i++) {
						ipSrcAddress[i] = (byte) Integer.parseInt(byte_ip_src[i]);
					}

					String[] byte_ip_dst = ip_dst.split("\\.");
					for (int i = 0; i < 4; i++) {
						ipDstAddress[i] = (byte) Integer.parseInt(byte_ip_dst[i]);
					}

					byte[] macSrcAddress = new byte[6];
					byte[] macDstAddress = new byte[6];
					
					String[] byte_mac_src = mac_src.split("-");
					for (int i = 0; i < 6; i++) {
						macSrcAddress[i] = (byte) Integer.parseInt(byte_mac_src[i], 16);
					}

					String[] byte_mac_dst = mac_dst.split("-");
					for (int i = 0; i < 6; i++) {
						macDstAddress[i] = (byte) Integer.parseInt(byte_mac_dst[i], 16);
					}
					
					
					((IPLayer) m_LayerMgr.GetLayer("IP")).setSrc(ipSrcAddress);
					((IPLayer) m_LayerMgr.GetLayer("IP")).setDest(ipDstAddress);
					((EthernetLayer) m_LayerMgr.GetLayer("ETH")).SetEnetSrcAddress(macSrcAddress);
					((EthernetLayer) m_LayerMgr.GetLayer("ETH")).SetEnetDstAddress(macDstAddress);

					((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(selected_index);

					Setting_Button.setText("Reset");
					dstIpAddress.setEditable(false);
				}

			}
		});
		Setting_Button.setBounds(418, 288, 87, 20);
		pane.add(Setting_Button);// setting

		Table_Button = new JButton("ARP Table");// ARP table
		Table_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new ARPTableDlg();
			}
		});
		Table_Button.setBounds(408, 318, 107, 20);
		pane.add(Table_Button);// ARP table

		File_select_Button = new JButton("File select");// 占쏙옙占쏙옙 占쏙옙占쏙옙
		File_select_Button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (Setting_Button.getText() == "Reset") {

					fd = new FileDialog(ChatFileDlg.this, "占쏙옙占싹쇽옙占쏙옙", FileDialog.LOAD);
					fd.setVisible(true);

					if (fd.getFile() != null) {
						path = fd.getDirectory() + fd.getFile();
						FileDir_path.setText("" + path);
					}
				} else {
					JOptionPane.showMessageDialog(null, "占쌍쇽옙 占쏙옙占쏙옙 占쏙옙占쏙옙", "WARNING_MESSAGE", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		File_select_Button.setBounds(75, 411, 161, 21);// 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙치 280
		pane.add(File_select_Button);

		Chat_send_Button = new JButton("Send");
		Chat_send_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Setting_Button.getText() == "Reset") {
					String input = ChattingWrite.getText();

					ChattingArea.append("[SEND] : " + input + "\n");

					byte[] type = new byte[2];
					type[0] = 0x08;
					type[1] = 0x20;
					//((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetType(type);

					byte[] bytes = input.getBytes();
					m_LayerMgr.GetLayer("CHAT").Send(bytes, bytes.length);
					// p_UnderLayer.Send(bytes, bytes.length);
				} else {
					JOptionPane.showMessageDialog(null, "占쌍쇽옙 占쏙옙占쏙옙 占쏙옙占쏙옙");
				}
			}
		});
		Chat_send_Button.setBounds(383, 349, 161, 21);
		pane.add(Chat_send_Button);

		NIC_select_Button = new JButton("Select");
		NIC_select_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selected = comboBox.getSelectedItem().toString();
				selected_index = comboBox.getSelectedIndex();
				srcMacAddress.setText("");
				try {
					byte[] MacAddress = ((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index)
							.getHardwareAddress();
					String hexNumber;
					for (int i = 0; i < 6; i++) {
						hexNumber = Integer.toHexString(0xff & MacAddress[i]);
						srcMacAddress.append(hexNumber.toUpperCase());
						if (i != 5)
							srcMacAddress.append("-");
					}
					srcIpAddress.append(InetAddress.getLocalHost().getHostAddress());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		NIC_select_Button.setBounds(418, 69, 87, 23);
		pane.add(NIC_select_Button);

		File_send_Button = new JButton("File Send");
		File_send_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Setting_Button.getText() == "Reset") {
					byte[] type = new byte[2];
					type[0] = 0x08;
					type[1] = 0x30;
//					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetType(type);

					String filepath = FileDir_path.getText();
					System.out.println(filepath);
					m_LayerMgr.GetLayer("File").Send(filepath);
					// p_UnderLayer.Send(filename);
				}

				else {
					JOptionPane.showMessageDialog(null, "占쌍쇽옙 占쏙옙占쏙옙 占쏙옙占쏙옙");
				}
			}
		});
		File_send_Button.setBounds(322, 411, 161, 23);
		pane.add(File_send_Button);

		comboBox = new JComboBox();

		comboBox.setBounds(380, 38, 170, 24);
		pane.add(comboBox);

		setVisible(true);

		SetCombobox();
	}

	private void SetCombobox() {
		List<PcapIf> m_pAdapterList = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();

		int r = Pcap.findAllDevs(m_pAdapterList, errbuf);
		if (r == Pcap.NOT_OK || m_pAdapterList.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}
		for (int i = 0; i < m_pAdapterList.size(); i++)
			this.comboBox.addItem(m_pAdapterList.get(i).getDescription());
	}
	
	
	public boolean Receive(byte[] input) {
		byte[] data = input;
		String Text = new String(data);
		ChattingArea.append("[RECV] : " + Text + "\n");
		return false;
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);

	}
}
