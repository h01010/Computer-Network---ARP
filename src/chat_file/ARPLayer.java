package chat_file;

import java.util.ArrayList;

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
		
		// 유틸리티에 해당하는 부분들을 생성자로 정해주었다.
		public _ARP_HEADER() {
			this.HardwareType[1] = (byte)0x01;	// 0x0001 --> Ethernet 통신 시 항상 1로 설정
			this.ProtocolType[0] = (byte)0x08;	// 0x0800 (IPv4) 0x0806 (ARP)
			this.HardwareLength[0] = (byte)0x06;	// Ethernet에서는 0x06으로 설정하래 왜 인지는 몰라ㅎㅎ
			this.ProtocolLength[0] = (byte)0x04;	// IPv4인 경우 0x04로 설정
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
	 
	_ARP_HEADER arpheader = new _ARP_HEADER();	//현재? 나의? 상태 --> 암튼 Receive 함수에서 받는 input과 비교할 때 필요
	
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
		String Interface;
		byte[] IPAddress = new byte[4];
		byte[] MACAddress = new byte[6];
		boolean Status;
		
		public _ARP_CACHE() {
			this.Interface = "hme0";
			this.Status = false;
		}
	}
	
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
		// 완성 아님!
		// Send는 보내기만 하므로 항상 0x0001로 고정해도 될 것 같다.
		arpheader.setOpcode(0x0001);
		
		
		byte[] buf = ObjToByte(arpheader, input, length);
		this.GetUnderLayer().Send(buf, buf.length);
		return true;
	}
	
	public boolean Receive(byte[] input) {
		// 완성 아님!
		byte[] opcode = new byte[2];
		System.arraycopy(input, 6, opcode, 0, 2);
		
		if(opcode[0] == 0x01) {	//request
			/* 요청하는것을 받았으니까 현재 받은 호스트 입장에서는 Sender Mac, Sender IP를 궁금해 할 것 같다. */
			byte[] senderMac = new byte[6];
			byte[] senderIP = new byte[4];
			byte[] targetIP = new byte[4];	//swap를 쓰기 위함
			
			System.arraycopy(input, 8, senderMac, 0, 6);
			System.arraycopy(input, 14, senderIP, 0, 4);
			System.arraycopy(input, 24, targetIP, 0, 4);
			
			if(targetIP == arpheader.SrcIP) {	//request 메세지를 받았을 때 나에게 온 메세지이면 요청한 호스트에서 나의 mac 주소를 알려주어야함
				byte[] swapData = swap(input, senderMac, senderIP, arpheader.SrcMac, targetIP);
				GetUnderLayer().Send(swapData, swapData.length);
			}
			
			
		} else {	//opcode[0] == 0x02	reply
			/* 응답하는 메세지를 받았으니까 현재 받은 호스트 입장에서는  Sender Mac, Sender IP를 궁금해 할 것 같다.
			 * 왜냐? 그게 궁금해서 보낸거였으니까
			 * */
			byte[] senderMac = new byte[6];
			byte[] senderIP = new byte[4];
			
			System.arraycopy(input, 8, senderMac, 0, 6);
			System.arraycopy(input, 14, senderIP, 0, 4);
			
			
		}
		
		
		return true;
	}
	
	public byte[] swap(byte[] input, byte[] srcmac, byte[] srcip, byte[] tarmac, byte[] tarip) {
		input[7] = 0x02; //opcode 수정 (opcode reply로 설정)
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
