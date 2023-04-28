/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */
package com.trans.migration.marc.marc.struct.service.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.trans.migration.marc.BaseXml;

/**
* MarcAliasItem 정보를 저장하는 객체
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
public @XStreamAlias("MarcAliasItem") class MarcAliasItem extends BaseXml {
	/**
	 *
	 */
	private static final long serialVersionUID = 1002L;

	@XStreamAlias("Name")      private String name  ;
	@XStreamAlias("Tagno")     private String tagno;
	@XStreamAlias("Indicator") private String indicator;

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return tagno
	 */
	public String getTagno() {
		return tagno;
	}
	/**
	 * @param tagno
	 */
	public void setTagno(String tagno) {
		this.tagno = tagno;
	}
	/**
	 * @return indicator
	 */
	public String getIndicator() {
		return indicator;
	}
	/**
	 * @param indicator
	 */
	public void setIndicator(String indicator) {
		this.indicator = indicator;
	}


}
