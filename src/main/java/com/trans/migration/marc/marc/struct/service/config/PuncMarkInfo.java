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
import java.util.ArrayList;
import java.util.List;

/**
* Puncmark 테그 구조화 객체
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
@Component("PuncMarkInfo")
public @XStreamAlias("Puncmark") class PuncMarkInfo extends BaseXml {
//	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@XStreamOmitField
	private final static long serialVersionUID = 1004L;

	@XStreamImplicit(itemFieldName="Taglist")
	public List<PuncMarkTagInfo> tagList = new ArrayList<PuncMarkTagInfo>();

	/**
	 * 생성자
	 * @param referenceResource
	 * @throws IOException
	 */
	public PuncMarkInfo(@Value("classpath:reference/marc/puncmark.xml") Resource referenceResource)throws IOException{

		Object o = super.fromXml(this ,referenceResource.getFile().getAbsolutePath() );

		tagList = ((PuncMarkInfo)o).getTaglist();

	}

   /**
	* @return PuncMarkTagInfo 객체 리스트
	*/
	public List<PuncMarkTagInfo> getTaglist()
	{
		return tagList;
	}


   /**
	* @return PuncMarkTagInfo 객체 리스트 size
	*/
	public int size() {
		if(tagList != null)
			return tagList.size();
		return 0;
	}
}
