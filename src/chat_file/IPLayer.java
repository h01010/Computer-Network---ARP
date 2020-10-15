package chat_file;

/**
 * @author parkmin(R99bbit@github)
 * @interface  BaseLayer(./BaseLayer.java)
 */

import java.util.*;

public class IPLayer implements BaseLayer {
	public int m_nUpperLayerCount = 0;
	public String m_pLayerName = null;
	public BaseLayer mp_UnderLayer = null;
	public ArrayList<BaseLayer> mp_aUpperLayer = new ArrayList<BaseLayer>();
	
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