package chat_file;

import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ARPTable extends JFrame {

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
	JButton cancelBtn;
	JButton endBtn;

	JLabel lbIpAddress;
	JLabel lbMacAddress;

	JTextField IpAddress;
	JTextField MacAddress;

	JTextArea arpTable;
	JTextArea proxyTable;

	public ARPTable() {
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

		this.arpTable = new JTextArea();
		this.arpTable.setEditable(false);
		this.arpTable.setBounds(10, 20, 375, 280);
		ARPCache.add(this.arpTable);

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

			}
		});
		this.ARPItemDelBtn.setBounds(25, 310, 150, 30);
		ARPCache.add(this.ARPItemDelBtn);

		// ARP Table 초기화 버튼
		this.ARPAllDelBtn = new JButton("All Delete");
		this.ARPAllDelBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.ARPAllDelBtn.setBounds(220, 310, 150, 30);
		ARPCache.add(this.ARPAllDelBtn);

		// ip전송 버튼
		this.ipSendBtn = new JButton("Send");
		this.ipSendBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.ipSendBtn.setBounds(295, 360, 90, 30);
		ARPCache.add(this.ipSendBtn);

		// Proxy ARP Entry Panel과 칸에 해당하는 컴포넌트
		ProxyARPEntry = new JPanel();
		ProxyARPEntry.setLayout(null);
		ProxyARPEntry.setBorder(BorderFactory.createTitledBorder("Proxy ARP Entry"));
		ProxyARPEntry.setBounds(410, 10, 395, 320);

		this.proxyTable = new JTextArea();
		this.proxyTable.setEditable(false);
		this.proxyTable.setBounds(10, 20, 375, 250);
		ProxyARPEntry.add(this.proxyTable);

		// proxy arp 추가 버튼
		this.proxyAddBtn = new JButton("Add");
		this.proxyAddBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.proxyAddBtn.setBounds(30, 280, 140, 30);
		ProxyARPEntry.add(this.proxyAddBtn);

		// proxy arp 삭제 버튼
		this.proxyDelBtn = new JButton("Delete");
		this.proxyDelBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.proxyDelBtn.setBounds(225, 280, 140, 30);
		ProxyARPEntry.add(this.proxyDelBtn);

		// Gratuitous ARP Panel과 칸에 해당하는 컴포넌트
		GratuitousARP = new JPanel();
		GratuitousARP.setLayout(null);
		GratuitousARP.setBorder(BorderFactory.createTitledBorder("Gratuitous ARP"));
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

			}
		});
		this.macSendBtn.setBounds(305, 30, 80, 30);
		GratuitousARP.add(this.macSendBtn);

		// proxy arp 추가 버튼
		this.endBtn = new JButton("종료");
		this.endBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.endBtn.setBounds(260, 420, 140, 40);
		pane.add(this.endBtn);
		
		// proxy arp 추가 버튼
		this.cancelBtn = new JButton("취소");
		this.cancelBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.cancelBtn.setBounds(415, 420, 140, 40);
		pane.add(this.cancelBtn);

		pane.add(ARPCache);
		pane.add(ProxyARPEntry);
		pane.add(GratuitousARP);

		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}
