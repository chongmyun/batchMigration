/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.marc.struct.service.util;


/**
* 마크 테그번호 정렬을 위한 데이터 객체
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
public class MarcSortTargetData
{
	private Integer tagNo;
	private String  sbCode;
	private String  fieldData;
	private String  punkMarK;

   /**
	* 생성자
	*/
	public MarcSortTargetData()
	{
		this.tagNo = null;
		this.sbCode = null;
		this.fieldData = null;
	}

   /**
	* @return fieldData
	*/
	public String getFieldData()
	{
		return fieldData;
	}

   /**
	* @return sbCode
	*/
	public String getSbCode()
	{
		return sbCode;
	}

   /**
	* @return tagNo
	*/
	public Integer getTagNo()
	{
		return tagNo;
	}

	/**
	 * @param fieldData
	 */
	public void setFieldData(String fieldData)
	{
		this.fieldData = fieldData;
	}

	/**
	 * @param sbCode
	 */
	public void setSbCode(String sbCode)
	{
		this.sbCode = sbCode;
	}

	/**
	 * @param tagNo
	 */
	public void setTagNo(Integer tagNo)
	{
		this.tagNo = tagNo;
	}

	/**
	 * @return the punkMarK
	 */
	public String getPunkMarK() {
		return punkMarK;
	}

	/**
	 * @param punkMarK the punkMarK to set
	 */
	public void setPunkMarK(String punkMarK) {
		this.punkMarK = punkMarK;
	}


}
