package chat_file;

import java.util.ArrayList;


public class TCPLayer implements BaseLayer{
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	public TCPLayer(String pName) {
		pLayerName = pName;
	}
	
	//TCP HEADER 데이터
	private class _TCP_HEADER {
		byte[] tcp_sport;
		byte[] tcp_dport;
		byte[] tcp_seq;
		byte[] tcp_ack;
		byte[] tcp_offset;
		byte[] tcp_flag;
		byte[] tcp_window;
		byte[] tcp_cksum;
		byte[] tcp_urgptr;
		byte[] Padding;
		byte[] tcp_data;
		
		public _TCP_HEADER() {	//Padding 포함 총 24byte
			this.tcp_sport = new byte[2];
			this.tcp_dport = new byte[2];
			this.tcp_seq = new byte[4];
			this.tcp_ack = new byte[4];
			this.tcp_offset = new byte[1];
			this.tcp_flag = new byte[1];
			this.tcp_window = new byte[2];
			this.tcp_cksum = new byte[2];
			this.tcp_urgptr = new byte[2];
			this.Padding = new byte[4];	
			this.tcp_data = null;
		}
	}
	
	_TCP_HEADER m_sHeader = new _TCP_HEADER();
	
	public byte[] ObjToByte(_TCP_HEADER Header, byte[] input, int length) {// data에 헤더 붙여주기
		byte[] buf = new byte[length + 24];
		for (int i = 0; i < 4; i++) {
			buf[4+i] = Header.tcp_seq[i];
			buf[8+i] = Header.tcp_ack[i];
			buf[20+i] = Header.Padding[i];
		}
		for (int i = 0; i < 2; i++) {
			buf[i] = Header.tcp_sport[i];
			buf[2+i] = Header.tcp_dport[i];
			buf[14+i] = Header.tcp_window[i];
			buf[16+i] = Header.tcp_cksum[i];
			buf[18+i] = Header.tcp_urgptr[i];
		}
		buf[12] = Header.tcp_offset[0];
		buf[13] = Header.tcp_flag[0];
		for (int i = 0; i < length; i++)
			buf[24 + i] = input[i];

		return buf;
	}
	
	public byte[] RemoveTCPHeader(byte[] input, int length) {	//전체 헤더 삭제
		byte[] cpyInput = new byte[length - 24];
		System.arraycopy(input, 24, cpyInput, 0, length - 24);
		input = cpyInput;
		return input;
	}
	
	public boolean Send(byte[] input, int length) {	//ChatAppLayer에서 TCP Layer 호출 시의 함수
		m_sHeader.tcp_dport[0] = 0x08;
		m_sHeader.tcp_dport[1] = 0x20;
		byte[] bytes = ObjToByte(m_sHeader, input, length);
		return this.GetUnderLayer().Send(bytes, length + 24);
	}
	public boolean FileSend(byte[] input, int length) { 	//FileAppLayer에서 TCP Layer 호출 시의 함수
		m_sHeader.tcp_dport[0] = 0x08;
		m_sHeader.tcp_dport[1] = 0x30;
		byte[] bytes = ObjToByte(m_sHeader, input, length);
		return this.GetUnderLayer().Send(bytes, length + 24);
	}

	public boolean Receive(byte[] input) {
		byte[] data = this.RemoveTCPHeader(input, input.length);
		
		// tcp_dport = 0x0820 : ChatAppLayer; tcp_dport = 0x0830 : FileAppLayer; 
		if(input[2] == 0x08 && input[3] == 0x20) {
			this.GetUpperLayer(0).Receive(data);
		}else if(input[2] == 0x08 && input[3] == 0x30) {
			this.GetUpperLayer(1).Receive(data);
		}
		return false;
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
