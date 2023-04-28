/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.index.service.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
* stopword.xml 맵핑 객체  word테그 맵핑
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
public @XStreamAlias("word")class StopWordData
{

	@XStreamAlias("data")
	private String data;

	@XStreamAlias("type")
	private String type;

   /**
	* 생성자
	*/
	public StopWordData()
	{
		this.data = null;
		this.type = null;
	}


   /**
	* @return 불용어 데이터 반환
	*/
	public String getData()
	{
		return data;
	}


   /**
	* @param  data 불용어 데이터
	*/
	public void setData(String data)
	{
		this.data = data;
	}


   /**
	* @return [불용어 데이터 타입
	*/
	public String getType()
	{
		return type;
	}


   /**
	* @param type 불용어 데이터 타입
	*/
	public void setType(String type)
	{
		this.type = type;
	}
}
