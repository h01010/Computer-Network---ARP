package chat_file;

import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import chat_file.ARPLayer.*;

public class ARPTableDlg extends JFrame {

	Container contentPane;

	JPanel ARPCache;
	JPanel ProxyARPEntry;
	JPanel GratuitousARP;

	JButton ARPItemDelBtn;
	JButton ARPAllDelBtn;
	JButton ipSendBtn;
	JButton proxyAddBtn;
	JButton proxyDelBtn;
	JButton macSendBtn;
	JButton refleshBtn;
	JButton endBtn;

	JLabel lbIpAddress;
	JLabel lbMacAddress;

	JTextField IpAddress;
	JTextField MacAddress;

	List arpTable;
	List proxyTable;

	private class ProxyPopup extends JFrame {
		Container contentPane;

		JButton okBtn;
		JButton cancelBtn;

		JLabel lbDevice;
		JLabel lbIpAddress;
		JLabel lbMacAddress;

		JTextField ipAddress;
		JTextField macAddress;
		JTextField device;

		public ProxyPopup() {
			setTitle("Proxy ARP Entry 추가");
			setBounds(680, 300, 350, 220);

			contentPane = this.getContentPane();
			JPanel pane = new JPanel();

			pane.setLayout(null);
			contentPane.add(pane);

			this.lbDevice = new JLabel("Device");
			this.lbDevice.setBounds(20, 20, 100, 20);
			pane.add(this.lbDevice);

			this.device = new JTextField();
			this.device.setBounds(130, 20, 180, 20);
			pane.add(this.device);

			this.lbIpAddress = new JLabel("IP Address");
			this.lbIpAddress.setBounds(20, 50, 100, 20);
			pane.add(this.lbIpAddress);

			this.ipAddress = new JTextField();
			this.ipAddress.setBounds(130, 50, 180, 20);
			pane.add(this.ipAddress);

			this.lbMacAddress = new JLabel("Mac Address");
			this.lbMacAddress.setBounds(20, 80, 100, 20);
			pane.add(this.lbMacAddress);

			this.macAddress = new JTextField();
			this.macAddress.setBounds(130, 80, 180, 20);
			pane.add(this.macAddress);

			this.okBtn = new JButton("Ok");
			this.okBtn.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					addProxy(device.getText(), ipAddress.getText(),
							macAddress.getText());
					readProxy();
					dispose();
				}
			});

			this.okBtn.setBounds(50, 120, 100, 30);
			pane.add(okBtn);

			this.cancelBtn = new JButton("Cancel");
			this.cancelBtn.setBounds(160, 120, 100, 30);
			this.cancelBtn.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
			pane.add(cancelBtn);

			setVisible(true);
		}
	}

	public ARPTableDlg() {
		setTitle("ARP Table");
		setBounds(850, 250, 830, 520);

		contentPane = this.getContentPane();
		JPanel pane = new JPanel();

		pane.setLayout(null);
		contentPane.add(pane);

		// ARP Cache Panel과 칸에 해당하는 컴포넌트
		ARPCache = new JPanel();
		ARPCache.setLayout(null);
		ARPCache.setBorder(BorderFactory.createTitledBorder("ARP Cache"));
		ARPCache.setBounds(10, 10, 395, 400);

		this.arpTable = new List();
		this.arpTable.setBounds(10, 20, 375, 280);
		ARPCache.add(this.arpTable);
		readARP();

		this.lbIpAddress = new JLabel("IP주소");
		this.lbIpAddress.setBounds(10, 360, 50, 30);
		ARPCache.add(this.lbIpAddress);

		this.IpAddress = new JTextField();
		this.IpAddress.setBounds(60, 360, 230, 30);
		ARPCache.add(this.IpAddress);

		// ARP 삭제 버튼
		this.ARPItemDelBtn = new JButton("Item Delete");
		this.ARPItemDelBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int temp = arpTable.getSelectedIndex();
				if (temp >= 0) {
					boolean result = deleteARP(temp);
					if (result) {
						arpTable.remove(temp);
					}
				}
			}
		});
		this.ARPItemDelBtn.setBounds(25, 310, 150, 30);
		ARPCache.add(this.ARPItemDelBtn);

		// ARP Table 초기화 버튼
		this.ARPAllDelBtn = new JButton("All Delete");
		this.ARPAllDelBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				boolean result = deleteAllARP();
				if (result) {
					arpTable.removeAll();
				}
			}
		});
		this.ARPAllDelBtn.setBounds(220, 310, 150, 30);
		ARPCache.add(this.ARPAllDelBtn);

		// ip전송 버튼
		this.ipSendBtn = new JButton("Send");
		this.ipSendBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				boolean result = sendIpARP(IpAddress.getText());
				if (result) {
					IpAddress.setText("");
				}
			}
		});
		this.ipSendBtn.setBounds(295, 360, 90, 30);
		ARPCache.add(this.ipSendBtn);

		// Proxy ARP Entry Panel과 칸에 해당하는 컴포넌트
		ProxyARPEntry = new JPanel();
		ProxyARPEntry.setLayout(null);
		ProxyARPEntry.setBorder(BorderFactory
				.createTitledBorder("Proxy ARP Entry"));
		ProxyARPEntry.setBounds(410, 10, 395, 320);

		this.proxyTable = new List();
		this.proxyTable.setBounds(10, 20, 375, 250);
		ProxyARPEntry.add(this.proxyTable);

		// proxy arp 추가 버튼
		this.proxyAddBtn = new JButton("Add");
		this.proxyAddBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				new ProxyPopup();
			}
		});
		this.proxyAddBtn.setBounds(30, 280, 140, 30);
		ProxyARPEntry.add(this.proxyAddBtn);

		// proxy arp 삭제 버튼
		this.proxyDelBtn = new JButton("Delete");
		this.proxyDelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int temp = proxyTable.getSelectedIndex();
				if (temp >= 0) {
					boolean result = deleteProxy(temp);
					if (result) {
						proxyTable.remove(temp);
					}
				}
			}
		});
		this.proxyDelBtn.setBounds(225, 280, 140, 30);
		ProxyARPEntry.add(this.proxyDelBtn);

		// Gratuitous ARP Panel과 칸에 해당하는 컴포넌트
		GratuitousARP = new JPanel();
		GratuitousARP.setLayout(null);
		GratuitousARP.setBorder(BorderFactory
				.createTitledBorder("Gratuitous ARP"));
		GratuitousARP.setBounds(410, 330, 395, 80);

		this.lbMacAddress = new JLabel("H/W 주소");
		this.lbMacAddress.setBounds(15, 30, 60, 30);
		GratuitousARP.add(this.lbMacAddress);

		this.MacAddress = new JTextField();
		this.MacAddress.setBounds(75, 30, 225, 30);
		GratuitousARP.add(this.MacAddress);

		// mac Send 버튼
		this.macSendBtn = new JButton("Send");
		this.macSendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sendMacGARP(MacAddress.getText());
				MacAddress.setText("");
			}
		});
		this.macSendBtn.setBounds(305, 30, 80, 30);
		GratuitousARP.add(this.macSendBtn);

		// 종료 버튼
		this.endBtn = new JButton("종료");
		this.endBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		this.endBtn.setBounds(260, 420, 140, 40);
		pane.add(this.endBtn);

		// 새로고침 버튼
		this.refleshBtn = new JButton("새로고침");
		this.refleshBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				proxyTable.removeAll();
				arpTable.removeAll();
				readARP();
				readProxy();
			}
		});
		this.refleshBtn.setBounds(415, 420, 140, 40);
		pane.add(this.refleshBtn);

		pane.add(ARPCache);
		pane.add(ProxyARPEntry);
		pane.add(GratuitousARP);

		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	// ARP Table 관련 함수
	public boolean readARP() { // ARP Table 읽어오기
		System.out.println("-----readARP-----");
		Iterator<_ARP_CACHE> arp_iterator = ARPLayer.arpTable.ARPTable.iterator();
		while (arp_iterator.hasNext()) {
			_ARP_CACHE cache = arp_iterator.next();
			byte[] ip_add_byte = cache.return_IPAddress();
			byte[] mac_add_byte = cache.return_MACAddress();
			String ip_add = "", mac_add = "";

			// print ip address
			for (int i = 0; i < 4; i++) {
				if (i == 3)	//마지막 byte인 경우엔 .을 붙이지 않음
					ip_add += ip_add_byte[i] & 0xFF;
				else
					ip_add += ((ip_add_byte[i] & 0xFF) + ".");

			}

			// print mac address
			for (int i = 0; i < 6; i++) {
				if (i == 0
						&& Integer.toHexString(mac_add_byte[i] & 0xFF).toUpperCase().equals("FF")) {	//reply를 받기 전이라 mac주소를 모르는 경우
					mac_add = "??????";
					break;
				}
				if (i == 5)	//마지막 byte인 경우엔 :를 붙이지 않음
					mac_add += Integer.toHexString(mac_add_byte[i] & 0xFF).toUpperCase();
				else
					mac_add += (Integer.toHexString(mac_add_byte[i] & 0xFF).toUpperCase() + ":");
			}
			String status = cache.return_Status() == false ? "incomplete":"complete";	//status를 확인하여 출력
			this.arpTable.add(ip_add + "     " + mac_add + "     " + status);		//arpTable의 정보를 출력
		}
		return false;
	}

	public boolean deleteARP(int index) { // ARP Table에서 index에 해당하는 원소 하나 삭제
		ARPLayer.arpTable.ARPTable.remove(index);
		this.arpTable.remove(index);
		readARP();
		return false;
	}

	public boolean deleteAllARP() { // ARP Table 초기화
		ARPLayer.arpTable.ARPTable.removeAll(ARPLayer.arpTable.ARPTable);
		this.arpTable.removeAll();
		readARP();
		return false;
	}

	public boolean sendIpARP(String ipAddress) { // Ip Address 전송
		//arp test용, 현재 arp기능은 채팅 상에서 구현
		return false;
	}

	// ProxyARP Table 관련 함수
	public boolean readProxy() { // Proxy Table 읽어오기
		// 읽어온 데이터는 this.proxyTable.add()를 통해 하나씩 삽입해주세요!
		return false;
	}

	public boolean addProxy(String device, String iPAddress, String macAddress) { // Proxy
																					// ARP
																					// Table
																					// 내
																					// 새
																					// 원소
																					// 추가
		return false;
	}

	public boolean deleteProxy(int index) { // Proxy ARP Table 내 index 원소 삭제
		return false;
	}

	// GARP Table 관련 함수
	public boolean sendMacGARP(String macAddress) {
		/*
		 * //예상 GARP SEND 코드 byte[] mac = MACStringToByte(MacAddress.getText());
		 * arpLayer.setSrcMac(mac); ethernetLayer.setSrcAddr(mac);
		 * 
		 * Send send = new Send(); send.run();
		 * 
		 * }
		 * 
		 * private byte[] MACStringToByte(String Mac) { byte[] result = new
		 * byte[6]; StringTokenizer tokens = new StringTokenizer(Mac, "-");
		 * for(int i = 0; tokens.hasMoreElements(); i++) { String temp =
		 * tokens.nextToken(); try { result[i] = Byte.parseByte(temp, 16); }
		 * catch(NumberFormatException e) { int error = (Integer.parseInt(temp,
		 * 16)) - 256; result[i] = (byte) (error); } } return result;
		 */
		return false;
	}

}