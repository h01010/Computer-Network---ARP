package chat_file;

import java.util.ArrayList;
import java.util.Arrays;

public class EthernetLayer implements BaseLayer {

	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	public EthernetLayer(String pName) {
		// super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
		ResetHeader();
	}

	public void ResetHeader() {
		m_sHeader = new _ETHERNET_HEADER();
	}

	private class _ETHERNET_ADDR {
		private byte[] addr = new byte[6];

		public _ETHERNET_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
			this.addr[4] = (byte) 0x00;
			this.addr[5] = (byte) 0x00;
		}
	}

	private class _ETHERNET_HEADER {
		_ETHERNET_ADDR enet_dstaddr;
		_ETHERNET_ADDR enet_srcaddr;
		byte[] enet_type;
		byte[] enet_data;

		public _ETHERNET_HEADER() {
			this.enet_dstaddr = new _ETHERNET_ADDR();
			this.enet_srcaddr = new _ETHERNET_ADDR();
			this.enet_type = new byte[2];
			this.enet_data = null;
		}
	}

	_ETHERNET_HEADER m_sHeader = new _ETHERNET_HEADER();


	public byte[] ObjToByte(_ETHERNET_HEADER Header, byte[] input, int length) {// data�뿉 �뿤�뜑 遺숈뿬二쇨린
		byte[] buf = new byte[length + 14];
		for (int i = 0; i < 6; i++) {
			buf[i] = Header.enet_dstaddr.addr[i];
			buf[i + 6] = Header.enet_srcaddr.addr[i];
		}
		buf[12] = Header.enet_type[0];
		buf[13] = Header.enet_type[1];
		for (int i = 0; i < length; i++)
			buf[14 + i] = input[i];

		return buf;
	}

	public boolean Send(byte[] input, int length) {
		System.out.println("Ethernet Send");
		if(input.length>1500) {				//MTU 珥덇낵�떆 �쟾�넚 遺덇�! MTU : 1500bytes
			return false;
		}
		for(int i = 0; i<6; i++) {
			if(m_sHeader.enet_dstaddr.addr[i]!=-1) {
				System.out.println("Ethernet Send Data");
				//not Broadcast, it's data
				m_sHeader.enet_type[0] = 8;			//0x08
				m_sHeader.enet_type[1] = 0;			//0x00
				byte[] bytes = ObjToByte(m_sHeader, input, length);
				return this.GetUnderLayer().Send(bytes, length + 14);
			}
		}
		//else : it's request or reply
		System.out.println("Ethernet Send Broadcast");
		m_sHeader.enet_type[0] = 8;					//0x08
		m_sHeader.enet_type[1] = 6;					//0x00
		byte[] bytes = ObjToByte(m_sHeader, input, length);
		return this.GetUnderLayer().Send(bytes, length + 14);
	}

	public boolean Receive(byte[] input) {
		System.out.println("Ethernet Receive");
		byte[] data;
		boolean MyPacket, Mine, Broadcast;
		MyPacket = IsItMyPacket(input);

		if (MyPacket) { // 蹂몄씤�씠 �넚�떊�븳 �뙣�궥�씤 寃쎌슦
			return false;
		} else {
			Broadcast = IsItBroadcast(input);
			if (!Broadcast) { // Broadcast �븘�땲硫댁꽌
				Mine = IsItMine(input);
				if (!Mine) { // 紐⑹쟻吏�媛� �옄�떊�씠 �븘�땶 寃쎌슦
					return false;
				}
			}
		}

		// Broadcast �샊�� �옄�떊�씠 紐⑹쟻吏��씤 寃쎌슦
		if (input[12] == 8 && input[13] == 0) {	//it's data -> IP layer
			data = this.RemoveEthernetHeader(input, input.length);
			System.out.println("Ethernet to IP");
			this.GetUpperLayer(0).Receive(data);
		} else if (input[12] == 8 && input[13] == 6) {	//it's reply or request -> ARP layer
			data = this.RemoveEthernetHeader(input, input.length);
			System.out.println("Ethernet to ARP");
			this.GetUpperLayer(1).Receive(data);
		}
		return true;
	}

	private boolean IsItMyPacket(byte[] input) {
		byte[] temp = new byte[6];
		System.arraycopy(input, 6, temp, 0, 6);
			if(!Arrays.equals(m_sHeader.enet_srcaddr.addr, temp)) {
				return false;
			}
		return true;
	}

	private boolean IsItBroadcast(byte[] input) {
		for(int i = 0; i<6; i++) {
			if(input[i] !=-1) {
				return false;
			}
		}
		return true;
	}

	private boolean IsItMine(byte[] input) {
		byte[] temp = new byte[6];
		System.arraycopy(input, 0, temp, 0, 6);
			if(!Arrays.equals(m_sHeader.enet_dstaddr.addr, temp)) {
				return false;
			}
		return true;
	}

	public byte[] RemoveEthernetHeader(byte[] input, int length) {
		byte[] cpyInput = new byte[length - 14];
		System.arraycopy(input, 14, cpyInput, 0, length - 14);
		input = cpyInput;
		return input;
	}

	public void SetEnetSrcAddress(byte[] srcAddress) {
		// TODO Auto-generated method stub
		m_sHeader.enet_srcaddr.addr = srcAddress;
	}

	public void SetEnetDstAddress(byte[] dstAddress) {
		// TODO Auto-generated method stub
		m_sHeader.enet_dstaddr.addr = dstAddress;
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
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
	}
}
