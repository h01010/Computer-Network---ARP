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

		// ARP Cache Panel�� ĭ�� �ش��ϴ� ������Ʈ
		ARPCache = new JPanel();
		ARPCache.setLayout(null);
		ARPCache.setBorder(BorderFactory.createTitledBorder("ARP Cache"));
		ARPCache.setBounds(10, 10, 395, 400);

		this.arpTable = new JTextArea();
		this.arpTable.setEditable(false);
		this.arpTable.setBounds(10, 20, 375, 280);
		ARPCache.add(this.arpTable);

		this.lbIpAddress = new JLabel("IP�ּ�");
		this.lbIpAddress.setBounds(10, 360, 50, 30);
		ARPCache.add(this.lbIpAddress);

		this.IpAddress = new JTextField();
		this.IpAddress.setBounds(60, 360, 230, 30);
		ARPCache.add(this.IpAddress);

		// ARP ���� ��ư
		this.ARPItemDelBtn = new JButton("Item Delete");
		this.ARPItemDelBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.ARPItemDelBtn.setBounds(25, 310, 150, 30);
		ARPCache.add(this.ARPItemDelBtn);

		// ARP Table �ʱ�ȭ ��ư
		this.ARPAllDelBtn = new JButton("All Delete");
		this.ARPAllDelBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.ARPAllDelBtn.setBounds(220, 310, 150, 30);
		ARPCache.add(this.ARPAllDelBtn);

		// ip���� ��ư
		this.ipSendBtn = new JButton("Send");
		this.ipSendBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.ipSendBtn.setBounds(295, 360, 90, 30);
		ARPCache.add(this.ipSendBtn);

		// Proxy ARP Entry Panel�� ĭ�� �ش��ϴ� ������Ʈ
		ProxyARPEntry = new JPanel();
		ProxyARPEntry.setLayout(null);
		ProxyARPEntry.setBorder(BorderFactory.createTitledBorder("Proxy ARP Entry"));
		ProxyARPEntry.setBounds(410, 10, 395, 320);

		this.proxyTable = new JTextArea();
		this.proxyTable.setEditable(false);
		this.proxyTable.setBounds(10, 20, 375, 250);
		ProxyARPEntry.add(this.proxyTable);

		// proxy arp �߰� ��ư
		this.proxyAddBtn = new JButton("Add");
		this.proxyAddBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.proxyAddBtn.setBounds(30, 280, 140, 30);
		ProxyARPEntry.add(this.proxyAddBtn);

		// proxy arp ���� ��ư
		this.proxyDelBtn = new JButton("Delete");
		this.proxyDelBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.proxyDelBtn.setBounds(225, 280, 140, 30);
		ProxyARPEntry.add(this.proxyDelBtn);

		// Gratuitous ARP Panel�� ĭ�� �ش��ϴ� ������Ʈ
		GratuitousARP = new JPanel();
		GratuitousARP.setLayout(null);
		GratuitousARP.setBorder(BorderFactory.createTitledBorder("Gratuitous ARP"));
		GratuitousARP.setBounds(410, 330, 395, 80);

		this.lbMacAddress = new JLabel("H/W �ּ�");
		this.lbMacAddress.setBounds(15, 30, 60, 30);
		GratuitousARP.add(this.lbMacAddress);

		this.MacAddress = new JTextField();
		this.MacAddress.setBounds(75, 30, 225, 30);
		GratuitousARP.add(this.MacAddress);

		// mac Send ��ư
		this.macSendBtn = new JButton("Send");
		this.macSendBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.macSendBtn.setBounds(305, 30, 80, 30);
		GratuitousARP.add(this.macSendBtn);

		// proxy arp �߰� ��ư
		this.endBtn = new JButton("����");
		this.endBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		this.endBtn.setBounds(260, 420, 140, 40);
		pane.add(this.endBtn);
		
		// proxy arp �߰� ��ư
		this.cancelBtn = new JButton("���");
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
