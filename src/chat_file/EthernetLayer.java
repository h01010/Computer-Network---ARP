package chat_file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class EthernetLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	private byte[] mac_addr;

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

	public EthernetLayer(String pName) {
		// super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;

		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface mac = NetworkInterface.getByInetAddress(ip);
			this.mac_addr = mac.getHardwareAddress();

		} catch (Exception e) {
			System.out.println("EthernetLayer Error : Can't read MAC address.\n" + e.getMessage());
		}
	}

	public byte[] ObjToByte(_ETHERNET_HEADER Header, byte[] input, int length) {// data에 헤더 붙여주기
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
		boolean isItRequest = true;
		for(int i=0; i<6; i++) {
			if( 0xff != m_sHeader.enet_dstaddr.addr[i]) {
				isItRequest = false;
				break;
			}
		}
		if(isItRequest) {	//Broadcast인 경우. 해당 패킷은 이후 ARPLayer로 올린다 
			m_sHeader.enet_type[0] = 0x08;
			m_sHeader.enet_type[1] = 0x00;
		}else{				//목적지 MAC주소가 있는 경우 해당 패킷은 이후 IPLayer로 올린다
			m_sHeader.enet_type[0] = 0x08;
			m_sHeader.enet_type[1] = 0x30;
		}
		byte[] bytes = ObjToByte(m_sHeader, input, length);
		this.GetUnderLayer().Send(bytes, length + 14);
		
		return false;
	}
	
	public boolean Receive(byte[] input) {
		byte[] data;
		boolean MyPacket, Mine, Broadcast;
		MyPacket = IsItMyPacket(input);

		if (MyPacket == true) { // 본인이 송신한 패킷인 경우
			return false;
		} else {
			Broadcast = IsItBroadcast(input);
			if (Broadcast == false) { // Broadcast 아니면서
				Mine = IsItMine(input);
				if (Mine == false) { // 목적지가 자신이 아닌 경우
					return false;
				}
			}
		}

		// Broadcast 혹은 자신이 목적지인 경우
		// 0x0800 = IPLayer; //0x0830 = ARPLayer;
		if (input[12] == 0x08 && input[13] == 0x00) {
			data = this.RemoveEthernetHeader(input, input.length);
			this.GetUpperLayer(0).Receive(data);
		} else if (input[12] == 0x08 && input[13] == 0x06) {
			data = this.RemoveEthernetHeader(input, input.length);
			this.GetUpperLayer(1).Receive(data);
		}
		return true;
	}

	private boolean IsItMyPacket(byte[] input) {
		// AND연산 결과를 저장할 임시 배열
		byte[] AND_result = new byte[6];

		for (int i = 6; i < 12; i++) {
			AND_result[i - 6] = (byte) (this.mac_addr[i - 6] & input[i]);
		}
		if (java.util.Arrays.equals(this.mac_addr, AND_result)) {
			return true;
		}
		return false;
	}

	private boolean IsItBroadcast(byte[] input) {
		// AND연산 결과를 저장할 임시 배열
		byte[] AND_result = new byte[6];

		for (int i = 0; i < 6; i++) {
			AND_result[i] = (byte) (this.mac_addr[i] & input[i]);
		}
		if (java.util.Arrays.equals(this.mac_addr, AND_result)) {
			return true;
		}
		return false;
	}

	private boolean IsItMine(byte[] input) {
		// AND연산 결과를 저장할 임시 배열
		byte[] AND_result = new byte[6];

		for (int i = 0; i < 6; i++) {
			AND_result[i] = (byte) (this.mac_addr[i] & input[i]);
		}
		if (java.util.Arrays.equals(this.mac_addr, AND_result)) {
			return true;
		}
		return false;
	}

	public byte[] RemoveEthernetHeader(byte[] input, int length) {
		byte[] cpyInput = new byte[length - 14];
		System.arraycopy(input, 14, cpyInput, 0, length - 14);
		input = cpyInput;
		return input;
	}
	
	public void SetEnetSrcAddress(byte[] srcAddress) {
		m_sHeader.enet_srcaddr.addr = srcAddress;
	}

	public void SetEnetDstAddress(byte[] dstAddress) {
		m_sHeader.enet_dstaddr.addr = dstAddress;
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		if (this.p_UnderLayer == null)
			return null;
		return this.p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		if (nindex < 0 || nindex > this.nUpperLayerCount || this.nUpperLayerCount < 0)
			return null;
		return this.p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
	}
}
