/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */
package com.trans.migration.marc.marc.struct.service.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.trans.migration.marc.BaseXml;

import java.util.List;

/**
* Taglist 테그 구조화 객체
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
public @XStreamAlias("Taglist") class PuncMarkTagInfo extends BaseXml
{
	/**
	 *
	 */
	private static final long serialVersionUID = -6051122555437153255L;
	//XML field Element Alias 상수선언
	@XStreamOmitField private final String NO    = "no";
	@XStreamOmitField private final String APPLY = "apply";

	@XStreamAsAttribute@XStreamAlias(NO)    private String no;
	@XStreamAsAttribute@XStreamAlias(APPLY) private String apply;

	@XStreamImplicit(itemFieldName="Sbcode")
	private List<String> sbCodeList;


   /**
	* 생성자
	*/
	public PuncMarkTagInfo()
	{
		this.no    = "";
		this.apply = "";
	}


   /**
	* @return apply
	*/
	public String getApply()
	{
		return apply;
	}


   /**
	* @return no
	*/
	public String getNo()
	{
		return no;
	}

	/**
	 * @param apply
	 */
	public void setApply(String apply)
	{
		this.apply = apply;
	}

	/**
	 * @param no
	 */
	public void setNo(String no)
	{
		this.no = no;
	}


   /**
	* @param  index 위치
	* @return 식별기호
	*/
	public String getSbcode(int index)
	{
		if(sbCodeList != null && sbCodeList.size() > index)
		{
			return this.sbCodeList.get(index);
		}
		return null;
	}

   /**
	* @return 식별기호 리스트
	*/
	public List<String> getSbCodeList()
	{
		return sbCodeList;
	}
}
