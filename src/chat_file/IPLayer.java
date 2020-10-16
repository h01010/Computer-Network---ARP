package chat_file;

/**
 * @author parkmin(R99bbit@github)
 * @interface  BaseLayer(./BaseLayer.java)
 */

import java.util.*;

public class IPLayer implements BaseLayer {
    int HeaderSize = 20;
    
	public int m_nUpperLayerCount = 0;
	public String m_pLayerName = null;
	public BaseLayer mp_UnderLayer = null;
	public ArrayList<BaseLayer> mp_aUpperLayer = new ArrayList<BaseLayer>();
	
    IPLayerHeader m_iHeader = new IPLayerHeader();
    EthernetLayer m_EthernetLayer;
    
    /**
     * IP Layer Packet Header
     * @class IPLayerHeader
     */
    private class IPLayerHeader {
        byte ip_version_and_len; // ip protocol version number and IHL(Internet Header Length)
        byte ip_service_type; // TOS(type of service)
        byte[] ip_total_len; // TL(Total Length)
        byte[] ip_id; // Identification(like sequence number)
        byte[] ip_flag; // fragmentation
        byte ip_ttl; // life cycle
        byte ip_protocol; // upper layer data
        byte[] ip_checksum; // checksum
        byte[] src; // source
        byte[] dest; // destination

        public void setSrc(byte[] src) {
            this.src = src;
        }

        public void setDest(byte[] dest) {
            this.dest = dest;
        }

        public IPLayerHeader() {
            ip_total_len = new byte[2];
            ip_id = new byte[2];
            ip_flag = new byte[2];
            ip_checksum = new byte[2];
            src = new byte[4];
            dest = new byte[4];
        }
    }

    public void setSrc(byte[] src) {
        this.m_iHeader.setSrc(src);
    }

    public void setDest(byte[] dest) {
        this.m_iHeader.setDest(dest);
    }

    public void setEthernetLayer(EthernetLayer m_EthernetLayer) {
        this.m_EthernetLayer = m_EthernetLayer;
    }


    public IPLayer(String pName){
        this.m_pLayerName = pName;
        ResetHeader();
    }

    public void ResetHeader() {
        this.m_iHeader.ip_version_and_len = (0x04 << 4);
        this.m_iHeader.ip_version_and_len += (byte) HeaderSize;
    }
    
    /**
     * This method will set frame total length
     * @param ip_total_len
     */
    void update_ip_total_len(int ip_total_len){
        m_iHeader.ip_total_len[0] = (byte) (ip_total_len >> 8);
        m_iHeader.ip_total_len[1] = (byte) (ip_total_len);
    }
    
	/**
	 * This method will switch object to byte
	 * @return {byte} swapped byte
	 */
    public byte[] ObjectToByte(byte[] input, int length){
        byte[] buffer = new byte[length + HeaderSize];
        int beforeHeaderSize = 0;

        update_ip_total_len(length + HeaderSize);
        
        /* copy header */
        buffer[beforeHeaderSize++] = this.m_iHeader.ip_version_and_len;
        buffer[beforeHeaderSize++] = this.m_iHeader.ip_service_type;

        System.arraycopy(this.m_iHeader.ip_total_len, 0, buffer, beforeHeaderSize, this.m_iHeader.ip_total_len.length);
        beforeHeaderSize += this.m_iHeader.ip_total_len.length;

        System.arraycopy(this.m_iHeader.ip_id, 0, buffer, beforeHeaderSize, this.m_iHeader.ip_id.length);
        beforeHeaderSize += this.m_iHeader.ip_id.length;

        System.arraycopy(this.m_iHeader.ip_flag, 0, buffer, beforeHeaderSize, this.m_iHeader.ip_flag.length);
        beforeHeaderSize += this.m_iHeader.ip_flag.length;

        buffer[beforeHeaderSize++] = this.m_iHeader.ip_ttl;
        buffer[beforeHeaderSize++] = this.m_iHeader.ip_protocol;

        System.arraycopy(this.m_iHeader.ip_checksum, 0, buffer, beforeHeaderSize, this.m_iHeader.ip_checksum.length);
        beforeHeaderSize += this.m_iHeader.ip_checksum.length;

        System.arraycopy(this.m_iHeader.src, 0, buffer, beforeHeaderSize, this.m_iHeader.src.length);
        beforeHeaderSize += this.m_iHeader.src.length;


        System.arraycopy(this.m_iHeader.dest, 0, buffer, beforeHeaderSize, this.m_iHeader.dest.length);
        
        for (int i = 0; i < length; i++){
            buffer[i + HeaderSize] = input[i];
        }

        return buffer;
    }

	/**
	 * This method will remove packet header
	 * @return {byte} removed byte
	 */
    public byte[] removeHeader(byte[] input) {
        int inputLength = input.length;
        byte[] buf = new byte[inputLength - HeaderSize];

        for (int i = HeaderSize; i < inputLength; i++) {
            buf[i - HeaderSize] = input[i];
        }
        return buf;
    }

    
	/**
	 * This method will return current layer's name
	 * @return {String} layer name
	 */
	@Override
	public String GetLayerName() {
		return this.m_pLayerName;
	}
	
	/**
	 * This method returns the previous layer
	 * @return {BaseLayer} just previous layer(under 1 level)
	 */
	@Override
	public BaseLayer GetUnderLayer() {
		boolean isUnderLayerExist = (this.mp_UnderLayer != null); // cond about under layer exsitence
		if(isUnderLayerExist) { // if under layer founded
			return this.mp_UnderLayer; // return under layer(just 1 level)
		} else {
			return null; // if not founded -> then, return null object
		}
	}

	/**
	 * This method returns the previous layer
	 * @return {BaseLayer} upper layer(as much as the entered value at nindex)
	 */
	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		boolean isUpperLayerNotExist = (nindex < 0 || nindex > this.m_nUpperLayerCount || this.m_nUpperLayerCount < 0); // condition that about upper layer
		if(isUpperLayerNotExist) {
			return null;
		} else {
			return this.mp_aUpperLayer.get(nindex);
		}
	}
	
	/**
	 * This method will set what the under layer is
	 * @return {void}
	 */
	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		boolean isUnderLayerExist = (this.mp_UnderLayer != null);
		if(isUnderLayerExist) {
			this.mp_UnderLayer = pUnderLayer; // set by argument
		} else {
			return;
		}
	}
	
	/**
	 * This method will set what the upper layer is
	 * @return {void}
	 */
	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		boolean isUpperLayerExist = (this.mp_aUpperLayer != null);
		if(isUpperLayerExist) {
			this.m_nUpperLayerCount += 1;
			this.mp_aUpperLayer.add(this.m_nUpperLayerCount, pUpperLayer);
		} else {
			return;
		}
	}
	
	/**
	 * This method places the layer entered as an argument on itself
	 * @return {void}
	 */
	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
	}
    
}