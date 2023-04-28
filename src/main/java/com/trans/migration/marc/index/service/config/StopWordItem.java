/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.index.service.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.trans.migration.marc.BaseXml;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


/**
* stopword.xml 맵핑 객체  stopword 테그 맵핑
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
public  @XStreamAlias("stopword") class StopWordItem extends BaseXml
{
	@XStreamOmitField
	private final static long serialVersionUID = 1004L; //직렬화 UID 부여

	@XStreamAsAttribute@XStreamAlias("apply")
	private String apply;

	@XStreamImplicit(itemFieldName="word")         //First SubElement
	private List<StopWordData> stopWordData;


	/**
	 * 생성자
	 * @param referenceResource
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private StopWordItem(Resource referenceResource) throws FileNotFoundException, IOException
	{

		Object o = super.fromXml(this ,referenceResource.getFile().getAbsolutePath() );

		stopWordData = ((StopWordItem)o).getStopWordData();
	}

   /**
	* index에 포함되는 StopWordData 객체리스트를 반환한다.
	* @return StopWordData 객체리스트
	*/
	public List<StopWordData> getStopWordData()
	{
		return stopWordData;
	}


   /**
	* @param wordData 불용어 데이터 저장객체
	*/
	public void addStopWordData(StopWordData wordData)
	{
		stopWordData.add(wordData);
	}


   /**
	* @return KorMakeIndexInfo 객체 개수
	*/
	public int size()
	{
		return stopWordData == null ? 0 : stopWordData.size();
	}


   /**
	* 입력받은 데이터의 불용어 여부를 판단하여 반환한다.
	* @param  targetStopWord 불용어인지 확인할 데이터
	* @return 불용어: true  일반(불용어가 아닌 단어): false
	*/
	@SuppressWarnings("unlikely-arg-type")
	public boolean isStopWordData(String targetStopWord)
	{
		return stopWordData.contains(targetStopWord);
	}


   /**
	* @return 불용어 기능의 사용여부
	*/
	public String getApply()
	{
		return apply;
	}


   /**
	* @param  apply 불용어 기능의 사용여부
	*/
	public void setApply(String apply)
	{
		this.apply = apply;
	}
}
