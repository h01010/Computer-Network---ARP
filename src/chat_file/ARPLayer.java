package chat_file;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
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
		
		// 占쏙옙틸占쏙옙티占쏙옙 占쌔댐옙占싹댐옙 占싸분듸옙占쏙옙 占쏙옙占쏙옙占쌘뤄옙 占쏙옙占쏙옙占쌍억옙占쏙옙.
		public _ARP_HEADER() {
			this.HardwareType[1] = (byte)0x01;	// 0x0001 --> Ethernet 占쏙옙占� 占쏙옙 占쌓삼옙 1占쏙옙 占쏙옙占쏙옙
			this.ProtocolType[0] = (byte)0x08;	// 0x0800 (IPv4) 0x0806 (ARP)
			this.HardwareLength[0] = (byte)0x06;	// Ethernet占쏙옙占쏙옙占쏙옙 0x06占쏙옙占쏙옙 占쏙옙占쏙옙占싹뤄옙 占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏢ㅎㅿ옙
			this.ProtocolLength[0] = (byte)0x04;	// IPv4占쏙옙 占쏙옙占� 0x04占쏙옙 占쏙옙占쏙옙
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
	 
	_ARP_HEADER arpheader = new _ARP_HEADER();	//占쏙옙占쏙옙? 占쏙옙占쏙옙? 占쏙옙占쏙옙 --> 占쏙옙튼 Receive 占쌉쇽옙占쏙옙占쏙옙 占쌨댐옙 input占쏙옙 占쏙옙占쏙옙 占쏙옙 占십울옙
	
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
		
		//return ip, mac address in the Arp_cache
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
	
	public static class _ARP_TABLE {
		ArrayList<_ARP_CACHE> ARPTable;
		int numberOfCache;
		public _ARP_TABLE() {
			ARPTable = new ArrayList<_ARP_CACHE>();
			numberOfCache = 0;
		}
	}
	
	public class _PROXYARP_CACHE {
		byte[] IPAddress = new byte[4];
		byte[] MACAddress = new byte[6];
		
		public _PROXYARP_CACHE(byte[] ipaddr, byte[] macaddr) {
			this.setIPAddress(ipaddr);
			this.setMACAddress(macaddr);
		}
		
		public _PROXYARP_CACHE setIPAddress(byte[] ipaddr) {
			this.IPAddress = ipaddr;
			return this;
		}
		
		public _PROXYARP_CACHE setMACAddress(byte[] macaddr) {
			this.MACAddress = macaddr;
			return this;
		}
		
		//return ip, mac address in the proxyArp_cache
		public byte[] return_IPAddress() {
			return this.IPAddress;
		}
		
		public byte[] return_MACAddress() {
			return this.MACAddress;
		}
		
	}
	
	public static class _PROXYARP_TABLE {
		ArrayList<_PROXYARP_CACHE> PROXYARPTable;
		int numberOfCache;
		
		public _PROXYARP_TABLE() {
			PROXYARPTable = new ArrayList<_PROXYARP_CACHE>();
			numberOfCache = 0;
		}
		
		public _PROXYARP_CACHE getProxy(byte[] ip) {
			Iterator<_PROXYARP_CACHE> iterator = PROXYARPTable.iterator();
			while(iterator.hasNext()) {
				_PROXYARP_CACHE cache = iterator.next();
				if(cache.return_IPAddress() == ip) {
					return cache;
				}
			}
			return null;
		}
	}
	
	//ARPTableDlg에서의 접근을 위해 static으로 선언
	static _ARP_TABLE arpTable = new _ARP_TABLE();	
	static _PROXYARP_TABLE proxyarpTable = new _PROXYARP_TABLE();
	
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
		System.out.println("ARPLayer Send");
		byte[] dstAddress = KnowDstMac(arpheader.DstIP);
		byte[] dstAddressInProxyTable = KnowDstMacProxy(arpheader.SrcIP);
		
		if (dstAddress != null) {
			System.out.println("dstAddress Know");
			this.GetUnderLayer().SetEnetDstAddress(dstAddress);	//Ethernet Layer SetEnetDstAddress Method
			//arpheader.setOpcode(new byte[] {0x00, 0x05});		//1,2占쏙옙 占쏙옙占쏙옙 占십깍옙 占쏙옙占쏙옙
			arpheader.setDstMac(dstAddress);
			
			this.GetUnderLayer().Send(input, length);
		} else if (dstAddressInProxyTable != null) {
			System.out.println("Proxy dstAddress Know");
			//arpheader.setOpcode(new byte[] {0x00, 0x05});
			arpheader.setDstMac(dstAddressInProxyTable);
			this.GetUnderLayer().Send(input, length);
		} else {
			System.out.println("dstAddress don't Know");
			arpheader.setOpcode(new byte[] {0x00, 0x01});
			
			_ARP_CACHE cache = new _ARP_CACHE(arpheader.DstIP, arpheader.DstMac, false);
			if(!isInTable(cache)) {
	            arpTable.ARPTable.add(cache);
	         }
			byte[] buf = ObjToByte(arpheader, input, length);
			this.GetUnderLayer().Send(buf, buf.length);
		}
		return true;
	}
	
	private boolean isInTable(_ARP_CACHE cache) {
	      Iterator<_ARP_CACHE> iterator = arpTable.ARPTable.iterator();
	      while (iterator.hasNext()) {
	         _ARP_CACHE c1 = iterator.next();
	         if (Arrays.equals(c1.return_IPAddress(), cache.return_IPAddress()) && Arrays.equals(c1.return_MACAddress(), cache.return_MACAddress()) && c1.return_Status() == cache.return_Status()) {
	            return true;
	         }
	      }
	      return false;
	   }

	private byte[] KnowDstMac(byte[] dstIP) {									//DstMAC이 arpTable에 있는지 확인하는 함수
		Iterator<_ARP_CACHE> iterator = arpTable.ARPTable.iterator();
		while (iterator.hasNext()) {
			_ARP_CACHE cache = iterator.next();
			if (Arrays.equals(cache.return_IPAddress(), dstIP)) {
				BigInteger bigInt = new BigInteger(cache.return_MACAddress());
				if (bigInt.intValue() != -1) {									// -1 == 0xffffffff
					return cache.return_MACAddress();
				}
				return null;
			}
		}
		return null;
	}
	
	private byte[] KnowDstMacProxy(byte[] dstIP) {									//DstMAC이 proxyTable에 있는지 확인하는 함수
		Iterator<_PROXYARP_CACHE> iterator = proxyarpTable.PROXYARPTable.iterator();
		while (iterator.hasNext()) {
			_PROXYARP_CACHE cache = iterator.next();
			if (Arrays.equals(cache.return_IPAddress(), dstIP)) {
					return cache.return_MACAddress();
			}
			return null;
		}
		return null;
	}

	public boolean Receive(byte[] input) {
		System.out.println("ARPLayer Receive");
		byte[] opcode = new byte[2];
		System.arraycopy(input, 6, opcode, 0, 2);
		if(opcode[1] == 1) {	//request
			/* 占쏙옙청占싹는곤옙占쏙옙 占쌨억옙占쏙옙占싹깍옙 占쏙옙占쏙옙 占쏙옙占쏙옙 호占쏙옙트 占쏙옙占썲에占쏙옙占쏙옙 Sender Mac, Sender IP占쏙옙 占시깍옙占쏙옙 占쏙옙 占쏙옙 占쏙옙占쏙옙. */
			System.out.println("Request");
			byte[] senderMAC = new byte[6];
			byte[] senderIP = new byte[4];
			byte[] targetIP = new byte[4];	//swap占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙
			
			System.arraycopy(input, 8, senderMAC, 0, 6);
			System.arraycopy(input, 14, senderIP, 0, 4);
			System.arraycopy(input, 24, targetIP, 0, 4);
			
			
			if(Arrays.equals(senderIP, targetIP)) {		//GARP占쏙옙 占쏙옙占쏙옙 IP占쏙옙 占쌨댐옙 IP占쏙옙 占쏙옙占쏙옙占실뤄옙, if占쏙옙占쏙옙占쏙옙 GARP占쏙옙占쏙옙 占쏙옙占쏙옙占싹곤옙, drop占싼댐옙.
				SetDstTrue(senderIP, senderMAC);
				return true;
			}
			_ARP_CACHE cache = new _ARP_CACHE(senderIP, senderMAC, true);
			if(!isInTable(cache)) {
	            arpTable.ARPTable.add(cache);
	         }
			if(Arrays.equals(targetIP, arpheader.SrcIP)) {	//request 占쌨쇽옙占쏙옙占쏙옙 占쌨억옙占쏙옙 占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙 占쌨쇽옙占쏙옙占싱몌옙 占쏙옙청占쏙옙 호占쏙옙트占쏙옙占쏙옙 占쏙옙占쏙옙 mac 占쌍소몌옙 占싯뤄옙占쌍억옙占쏙옙占�
				System.out.println("reply start!");
				opcode = new byte[] {0x00, 0x02};
				System.arraycopy(opcode, 0, input, 6, 2);
				byte[] swapData = swap(input, senderMAC, senderIP, arpheader.SrcMac, targetIP);
				GetUnderLayer().Send(swapData, swapData.length);
			}
			
			_PROXYARP_CACHE proxycache = proxyarpTable.getProxy(targetIP);
			if (proxycache != null) {
				byte[] swapData = swap(input, senderMAC, senderIP, proxycache.MACAddress, proxycache.IPAddress);
				GetUnderLayer().Send(swapData, swapData.length);
			}
				
		} else {	//opcode[0] == 0x02	reply
			/* 占쏙옙占쏙옙占싹댐옙 占쌨쇽옙占쏙옙占쏙옙 占쌨억옙占쏙옙占싹깍옙 占쏙옙占쏙옙 占쏙옙占쏙옙 호占쏙옙트 占쏙옙占썲에占쏙옙占쏙옙  Sender Mac, Sender IP占쏙옙 占시깍옙占쏙옙 占쏙옙 占쏙옙 占쏙옙占쏙옙.
			 * 占쌍놂옙? 占쌓곤옙 占시깍옙占쌔쇽옙 占쏙옙占쏙옙占신울옙占쏙옙占싹깍옙
			 * */
			System.out.println("Reply");
			byte[] senderMAC = new byte[6];
			byte[] senderIP = new byte[4];
			
			System.arraycopy(input, 8, senderMAC, 0, 6);
			System.arraycopy(input, 14, senderIP, 0, 4);
			
			SetDstTrue(senderIP, senderMAC);
			
			byte[] dstAddress = new byte[6];
			System.arraycopy(input, 8, dstAddress, 0, 6);
			for(int i = 0; i < 6; i++) {
				System.out.println(dstAddress[i]);
			}
			this.GetUnderLayer().SetEnetDstAddress(dstAddress);
			byte[] cpyInput = new byte[input.length - 28];
			System.arraycopy(input, 28, cpyInput, 0, input.length - 28);
			input = cpyInput;
			this.GetUnderLayer().Send(input, input.length);
		}
		return true;
	}
	
	private void SetDstTrue(byte[] senderIP, byte[] senderMAC) {
		Iterator<_ARP_CACHE> iterator = arpTable.ARPTable.iterator();
		while (iterator.hasNext()) {
			_ARP_CACHE cache = iterator.next();
			if (Arrays.equals(cache.return_IPAddress(), senderIP)) {
				cache.setMACAddress(senderMAC);
				cache.setStatus(true);
				return;
			}
		}
	}

	public byte[] swap(byte[] input, byte[] srcmac, byte[] srcip, byte[] tarmac, byte[] tarip) {
		input[7] = 0x02; //opcode 占쏙옙占쏙옙 (opcode reply占쏙옙 占쏙옙占쏙옙)
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
