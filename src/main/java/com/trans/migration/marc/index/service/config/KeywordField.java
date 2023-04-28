/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */
package com.trans.migration.marc.index.service.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import com.trans.migration.marc.BaseXml;
/**
 * keyword.xml 맵핑객체 Field 테그 맵핑
 * @author Written By 이태일 【taeil.lee@eco.co.kr】
 */
public @XStreamAlias("Field") class KeywordField extends BaseXml {

	/**
	 *
	 */
	private static final long serialVersionUID = -5737159200967164375L;
	@XStreamAlias("Tagno") private String tagno     = "";
	@XStreamAlias("Indicator") private String indicator = "";
	@XStreamAlias("Index") private String isIndex   = "";

	/**
	 * @return tagno
	 */
	public String getTagno() {
		if( this.tagno != null ) return this.tagno;
		else                     return null;
	}

	/**
	 * @return indicator
	 */
	public String getIndicator() {
		if( this.indicator != null ) {
			return this.indicator.replaceAll("\\$", "");
		}else{
			return null;
		}

	}

	/**
	 * @return isIndex
	 */
	public String getIsIndex() {
		if( this.isIndex != null ) return this.isIndex;
		else                       return null;
	}

	/**
	 * @param tagno
	 */
	public void setTagno(String tagno) {
		this.tagno = tagno;
	}

	/**
	 * @param indicator
	 */
	public void setIndicator(String indicator) {
		this.indicator = indicator;
	}

	/**
	 * @param index
	 */
	public void setIndex(String index) {
		this.isIndex = index;
	}

	public String toString(){
		return tagno+indicator+isIndex;
	}
}
