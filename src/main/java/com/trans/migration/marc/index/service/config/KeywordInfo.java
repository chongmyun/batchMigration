/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.index.service.config;

import com.trans.migration.marc.BaseXml;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.core.io.Resource;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * keyword.xml 맵핑 객체  Keyword테그 맵핑
 * @author Written By 이태일 【taeil.lee@eco.co.kr】
 */
public @XStreamAlias("Keyword") class KeywordInfo extends BaseXml {

	/**
	 *
	 */
	private static final long serialVersionUID = 8807889855342041343L;

	@XStreamImplicit(itemFieldName="Field")
	private ArrayList<KeywordField> keywordInfoList = new ArrayList<KeywordField>();

	/**
	 * 생성자
	 * @param referenceResource
	 * @throws IOException
	 */
	public KeywordInfo(Resource referenceResource)throws IOException
	{
		Object o = super.fromXml(this ,referenceResource.getFile().getAbsolutePath() );

		keywordInfoList = ((KeywordInfo)o).getKeywordInfoList();

	}
   /**
    * Keyword 정보객체를 추가한다.
    * @param  object 추가대상 Keyword 정보객체
    ***************************************************************************/
	public void add(KeywordField object) {

		keywordInfoList.add(object);
	}


   /**
    * Keyword 정보객체 리스트를 반환한다.
    * @return Keyword 정보객체 리스트
    */
	public ArrayList<KeywordField> getKeywordInfoList() {
		return keywordInfoList;
	}


   /**
    * INDEX 에 해당하는 Keyword 정보객체를 반환한다.
    * @param  index 위치
    * @return KeywordField 객체
    */
	public KeywordField get(int index) {
		return keywordInfoList.get(index);
	}


   /**
    * @return keyword객체수
    */
	public int getSize() {
		return keywordInfoList.size();
	}



}
