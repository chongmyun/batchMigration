/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.marc.struct.service.config;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.trans.migration.marc.BaseXml;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
* MarcAlias 정보를 저장하는 객체
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
@Component("MarcAlias")
public @XStreamAlias("MarcAlias")class MarcAlias extends BaseXml {
//	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@XStreamOmitField
	protected final static long serialVersionUID = 1003L;

	@XStreamImplicit(itemFieldName="MarcAliasItem")
	protected List<MarcAliasItem> marcAliasItemList;

	public MarcAlias() {
	}

	/**
	 * 생성자
	 * @param referenceResource
	 * @throws IOException
	 */
	public MarcAlias(@Value("classpath:reference/marc/marcalias.xml") Resource referenceResource)throws IOException{
		Object o = super.fromXml(this ,referenceResource.getFile().getAbsolutePath() );

		marcAliasItemList = ((MarcAlias)o).getMarcAliasItemList();
	}

	/**
	 * @param object 추가대상 MarcAlias 정보객체
	 */
	public void add(MarcAliasItem object) {
		marcAliasItemList.add(object);
	}

	/**
	 * @return MarcAliasList
	 */
	public List<MarcAliasItem> getMarcAliasItemList() {
		return marcAliasItemList;
	}

	/**
	 * @param  index 위치
	 * @return MarcAliasItem 객체
	 */
	public MarcAliasItem get(int index) {
		return (MarcAliasItem)marcAliasItemList.get(index);
	}

	/**
	 * @return MarcAliasList size
	 */
	public int getSize() {
		return marcAliasItemList.size();
	}




}
