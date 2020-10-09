package chat_file;

import java.util.ArrayList;

public class ChatAppLayer implements BaseLayer{
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

    byte[] fullMessage;
    int nowLength = 0;
    public static int sendPoint = 0;
    public static int receivePoint = 0;
	
	private class _CAPP_HEADER{
		byte[] capp_totlen;
		byte capp_type;
		byte capp_unused;
		byte[] capp_data;
		
		public _CAPP_HEADER(){
			this.capp_totlen = new byte[2];
			this.capp_type = 0x00;
			this.capp_unused = 0x00;
			this.capp_data = null;
		}
	}
	
	_CAPP_HEADER m_sHeader = new _CAPP_HEADER();
	
	public ChatAppLayer(String pName) {
		//super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
		ResetHeader();
	}
	
	public void ResetHeader(){
		for(int i=0; i<2; i++){
			m_sHeader.capp_totlen[i] = (byte) 0x00;
		}
		m_sHeader.capp_type = (byte) 0x00;	
		m_sHeader.capp_unused = (byte) 0x00;	
		m_sHeader.capp_data = null;	
	}
	
	public byte[] ObjToByte(_CAPP_HEADER Header, byte[] input, int length){
	    byte[] buffer = new byte[length + 4];
	    buffer[0] = Header.capp_totlen[0];
	    buffer[1] = Header.capp_totlen[1];
	    buffer[2] = Header.capp_type;
	    buffer[3] = Header.capp_unused;

	    for(int i = 0; i < length; i++) {
	        buffer[4 + i] = input[i];
	    }
		return buffer;
	}
	
    public boolean Send(byte[] input, int length) {
    	int[] dataLength;
    	byte[] newData;
        m_sHeader.capp_totlen = new byte[2];
        m_sHeader.capp_totlen[0] = (byte) length;
        m_sHeader.capp_totlen[1] = (byte) (length >> 8);
        if(length > 1456) {
            if(length % 1456 == 0) {
                dataLength = new int[length / 1456];
                for(int i = 0; i < dataLength.length; i++) {
                    dataLength[i] = 1456;
                }
            }
            else {
                dataLength = new int[(length / 1456) + 1];
                for(int i = 0; i < dataLength.length; i++) {
                    dataLength[i] = 1456;
                    if(i == dataLength.length) {
                        dataLength[i] = length % 1456;
                    }
                }
            }
            for(int i = 0; i < dataLength.length; i++) {
                if(i == 0) {
                    m_sHeader.capp_type = (byte) 0x01;
                }
                else if(i == dataLength.length - 1) {
                    m_sHeader.capp_type = (byte) 0x03;
                }
                else {
                    m_sHeader.capp_type = (byte) 0x02;
                }
                newData = new byte[dataLength[i]];
                for(int j = 0; j < dataLength[i]; j++) {
                    newData[j] = input[sendPoint];
                    sendPoint++;
                }
                if(sendPoint == length) {
                    sendPoint = 0;
                }
            }
            byte[] data = ObjToByte(m_sHeader, newData, newData.length);
            this.GetUnderLayer().Send(data, newData.length + 4);
        }
        else {
            byte[] data = ObjToByte(m_sHeader, input, length);
            this.GetUnderLayer().Send(data, length + 4);
        }
		return true;
	}

    public byte[] RemoveCappHeader(byte[] input, int length){
        byte[] removeHeader = new byte[length-4];
        for(int i = 0; i < length-4; i++) {
            removeHeader[i] = input[i+4];
        }
        return removeHeader;
    }
           
	public synchronized boolean Receive(byte[] input){
        byte[] data;
        int length = (input[1] & 0xff) << 8 | (input[0] & 0xff);
        if(input[2] == (byte)0x01) {
            fullMessage = new byte[length];
        }
        if(length < 1456) {
            data = RemoveCappHeader(input, input.length);
            this.GetUpperLayer(0).Receive(data);
            return true;
        }
        else {
            nowLength = nowLength + (input.length - 4);
            data = RemoveCappHeader(input, input.length);
            for(int i = 0; i < data.length; i++) {
                if(receivePoint >= length || i>= (input.length - 4)) {
                    break;
                }
                fullMessage[receivePoint] = data[i];
                receivePoint++;
            }
            if(nowLength == length) {
                nowLength = 0;
                receivePoint = 0;
                this.GetUpperLayer(0).Receive(fullMessage);
                return true;
            }
            return false;
        }
		return false;
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
	    if(p_UnderLayer == null) {
	        return null;
	    }
	    return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
	    if(nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0) {
	        return null;
	    }
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
        if(pUnderLayer == null) {
            return;
        }
        this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
	    if(pUpperLayer == null) {
	        return;
	    }
	    this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
        this.SetUpperLayer(pUULayer);
        pUULayer.SetUnderLayer(this);
	}

}
