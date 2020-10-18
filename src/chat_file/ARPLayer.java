package chat_file;

import java.math.BigInteger;
import java.util.*;

public class ARPLayer implements BaseLayer{
	
	//Set variable
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	// constructor
	public ARPLayer(String name) {
		pLayerName = name;
	}
	
	private static class _ARP_HEADER {
		byte[] HardwareType = new byte[2];
		byte[] ProtocolType = new byte[2];
		byte[] HardwareLength = new byte[1];
		byte[] ProtocolLength = new byte[1];
		byte[] Opcode = new byte[2];
		byte[] SrcMac = new byte[6];
		byte[] SrcIP = new byte[4];
		byte[] DstMac = new byte[6];
		byte[] DstIP = new byte[4];
		
		// ��ƿ��Ƽ�� �ش��ϴ� �κе��� �����ڷ� �����־���.
		public _ARP_HEADER() {
			this.HardwareType[1] = (byte)0x01;	// 0x0001 --> Ethernet ��� �� �׻� 1�� ����
			this.ProtocolType[0] = (byte)0x08;	// 0x0800 (IPv4) 0x0806 (ARP)
			this.HardwareLength[0] = (byte)0x06;	// Ethernet������ 0x06���� �����Ϸ� �� ������ ���󤾤�
			this.ProtocolLength[0] = (byte)0x04;	// IPv4�� ��� 0x04�� ����
			this.DstMac[0] = (byte)0xff;
			this.DstMac[1] = (byte)0xff;
			this.DstMac[2] = (byte)0xff;
			this.DstMac[3] = (byte)0xff;
			this.DstMac[4] = (byte)0xff;
			this.DstMac[5] = (byte)0xff;
		}
		
		//Setter
		public void setOpcode(byte[] opcode) {
            Opcode = opcode;
        }
		
		public void setSrcMac(byte[] srcmac) {
			SrcMac = srcmac;
		}
		
		public void setSrcIP(byte[] srcip) {
			SrcIP = srcip;
		}
		
		public void setDstMac(byte[] dstmac) {
			DstMac = dstmac;
		}
		
		public void setDstIP(byte[] dstip) {
			DstIP = dstip;
		}
	}
	 
	_ARP_HEADER arpheader = new _ARP_HEADER();	//����? ����? ���� --> ��ư Receive �Լ����� �޴� input�� ���� �� �ʿ�
	
	public void setSrcMac(byte[] srcmac) {
		arpheader.setSrcMac(srcmac);
	}
	
	public void setSrcIP(byte[] srcip) {
		arpheader.setSrcIP(srcip);
	}
	
	public void setDstMac(byte[] dstmac) {
		arpheader.setDstMac(dstmac);
	}
	
	public void setDstIP(byte[] dstip) {
		arpheader.setDstIP(dstip);
	}
	
	public class _ARP_CACHE {
		// String Interface;
		byte[] IPAddress = new byte[4];
		byte[] MACAddress = new byte[6];
		boolean Status;
		
		public _ARP_CACHE(byte[] ipaddr, byte[] macaddr, boolean status) {
			this.setIPAddress(ipaddr);
			this.setMACAddress(macaddr);
			this.setStatus(status);
		}
		
		public _ARP_CACHE setIPAddress(byte[] ipaddr) {
			this.IPAddress = ipaddr;
			return this;
		}
		
		public _ARP_CACHE setMACAddress(byte[] macaddr) {
			this.MACAddress = macaddr;
			return this;
		}
		
		public _ARP_CACHE setStatus(boolean status) {
			this.Status = status;
			return this;
		}
		
		//�񱳸� �ϱ� ���� ���� �������ִ� �Լ���
		public byte[] return_IPAddress() {
			return this.IPAddress;
		}
		
		public byte[] return_MACAddress() {
			return this.MACAddress;
		}
		
		public boolean return_Status() {
			return this.Status;
		}
	}
	
//	public class _ARP_TABLE {
//		ArrayList<_ARP_CACHE> ARPTable;
//		int numberOfCache;
//		public _ARP_TABLE() {
//			ARPTable = new ArrayList<_ARP_CACHE>();
//			numberOfCache = 0;
//		}
//	}
	
	ArrayList<_ARP_CACHE> arpTable = new ArrayList<_ARP_CACHE>();
	
	public byte[] ObjToByte(_ARP_HEADER Header, byte[] input, int length) {
		byte[] buf = new byte[length + 28];	//2, 2, 1, 1, 2, 6, 4, 6, 4 => 28
		System.arraycopy(Header.HardwareType, 0, buf, 0, 2);
		System.arraycopy(Header.ProtocolType, 0, buf, 2, 2);
		System.arraycopy(Header.HardwareLength, 0, buf, 4, 1);
		System.arraycopy(Header.ProtocolLength, 0, buf, 5, 1);
		System.arraycopy(Header.Opcode, 0, buf, 6, 2);
		System.arraycopy(Header.SrcMac, 0, buf, 8, 6);
		System.arraycopy(Header.SrcIP, 0, buf, 14, 4);
		System.arraycopy(Header.DstMac, 0, buf, 18, 6);
		System.arraycopy(Header.DstIP, 0, buf, 24, 4);
		System.arraycopy(input, 0, buf, 28, length);
		return buf;
	}
	
	public boolean Send(byte[] input, int length) {
		byte[] dstAddress = KnowDstMac(arpheader.DstIP);
		if (dstAddress != null) {
			this.GetUnderLayer().SetEnetDstAddress(dstAddress);	//Ethernet Layer SetEnetDstAddress Method
			arpheader.setOpcode(new byte[] {0x00, 0x05});		//1,2�� ���� �ʱ� ����
			arpheader.setDstMac(dstAddress);
		} else {
			arpheader.setOpcode(new byte[] {0x00, 0x01});
			
			_ARP_CACHE cache = new _ARP_CACHE(arpheader.DstIP, arpheader.DstMac, false);
			arpTable.add(cache);
		}
		
		//ARP CACHE Ȱ�� �κ�
		
		byte[] buf = ObjToByte(arpheader, input, length);
		this.GetUnderLayer().Send(buf, buf.length);
		return true;
	}

	private byte[] KnowDstMac(byte[] dstIP) {									//DstMAC �ּҸ� �˰��ִ��� ����� �Լ�
		Iterator<_ARP_CACHE> iterator = arpTable.iterator();
		while (iterator.hasNext()) {
			_ARP_CACHE cache = iterator.next();
			if (cache.return_IPAddress() == dstIP) {
				BigInteger bigInt = new BigInteger(cache.return_MACAddress());
				if (bigInt.intValue() != -1) {									// -1 == 0xffffffff
					return cache.return_MACAddress();
				}
				return null;
			}
		}
		return null;
	}

	public boolean Receive(byte[] input) {
		byte[] opcode = new byte[2];
		System.arraycopy(input, 6, opcode, 0, 2);
		
		if(opcode[0] == 0x01) {	//request
			/* ��û�ϴ°��� �޾����ϱ� ���� ���� ȣ��Ʈ ���忡���� Sender Mac, Sender IP�� �ñ��� �� �� ����. */
			byte[] senderMAC = new byte[6];
			byte[] senderIP = new byte[4];
			byte[] targetIP = new byte[4];	//swap�� ���� ����
			
			System.arraycopy(input, 8, senderMAC, 0, 6);
			System.arraycopy(input, 14, senderIP, 0, 4);
			System.arraycopy(input, 24, targetIP, 0, 4);
			
			
			_ARP_CACHE cache = new _ARP_CACHE(senderIP, senderMAC, true);
			arpTable.add(cache);
			
			
			if(targetIP == arpheader.SrcIP) {	//request �޼����� �޾��� �� ������ �� �޼����̸� ��û�� ȣ��Ʈ���� ���� mac �ּҸ� �˷��־����
				byte[] swapData = swap(input, senderMAC, senderIP, arpheader.SrcMac, targetIP);
				GetUnderLayer().Send(swapData, swapData.length);
			}
		} else {	//opcode[0] == 0x02	reply
			/* �����ϴ� �޼����� �޾����ϱ� ���� ���� ȣ��Ʈ ���忡����  Sender Mac, Sender IP�� �ñ��� �� �� ����.
			 * �ֳ�? �װ� �ñ��ؼ� �����ſ����ϱ�
			 * */
			byte[] senderMAC = new byte[6];
			byte[] senderIP = new byte[4];
			
			System.arraycopy(input, 8, senderMAC, 0, 6);
			System.arraycopy(input, 14, senderIP, 0, 4);
			
			SetDstTrue(senderIP, senderMAC);
			
		}
		return true;
	}
	
	private void SetDstTrue(byte[] senderIP, byte[] senderMAC) {
		Iterator<_ARP_CACHE> iterator = arpTable.iterator();
		while (iterator.hasNext()) {
			_ARP_CACHE cache = iterator.next();
			if (cache.return_IPAddress() == senderIP) {
				cache.setMACAddress(senderMAC);
				cache.setStatus(true);
				return;
			}
		}
	}

	public byte[] swap(byte[] input, byte[] srcmac, byte[] srcip, byte[] tarmac, byte[] tarip) {
		input[7] = 0x02; //opcode ���� (opcode reply�� ����)
		System.arraycopy(srcmac, 0, input, 18, 6);
		System.arraycopy(srcip, 0, input, 24, 4);
		System.arraycopy(tarmac, 0, input, 8, 6);
		System.arraycopy(tarip, 0, input, 14, 4);
		return input;
	}
	
	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		if(p_UnderLayer == null) {
			return null;
		}
		return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if(nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0) {
			return null;
		}
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if(pUnderLayer == null) {
			return;
		}
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if(pUpperLayer == null) {
	        return;
	    }
	    this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		// TODO Auto-generated method stub
		this.SetUpperLayer(pUULayer);
        pUULayer.SetUnderLayer(this);
	}

}
