/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.marc.struct;


/**
* 마크 데이터 중 한개의 TAG에 대한 필드데이터 정보를 저장하는 데이터 클래스이다.
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
final public class MarcSubFieldElement
{
	private String sbCode;			    // 식별기호
	private String	subFieldData;		// 필드 데이터
	private String punkmark; 			// 구두점 [ 현재위치의 앞 서브 필드에 입력되어있는 구두점 ]

	/**
	 * 생성자
	 */
	public MarcSubFieldElement(){
	}
	/**
	 * 생성자
	 * @param sbCode
	 * @param subFieldData
	 * @param punkmark
	 */
	public MarcSubFieldElement(String sbCode,String subFieldData, String punkmark){
		this.sbCode = sbCode;
		this.subFieldData = subFieldData;
		this.punkmark = punkmark;
	}
	/** @return 식별기호 */
	public String getSbCode() {
		return sbCode;
	}
	/** @param sbCode 식별기호*/
	public void setSbCode(String sbCode) {
		this.sbCode = sbCode;
	}
	/** @return 필드 데이터 */
	public String getSubFieldData() {
		return subFieldData;
	}
	/** @param subFieldData 필드데이터 */
	public void setSubFieldData(String subFieldData) {
		this.subFieldData = subFieldData;
	}
	/** @return 구두점 */
	public String getPunkmark() {
		return punkmark;
	}
	/** @param punkmark 구두점 */
	public void setPunkmark(String punkmark) {
		this.punkmark = punkmark;
	}


}
