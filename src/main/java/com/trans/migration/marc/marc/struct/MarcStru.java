/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.marc.struct;

import java.util.ArrayList;
import java.util.List;

/**
* 마크구조체 객체
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
public final class MarcStru
{
	private String reader;
	private List<MarcFieldElement>	fieldList;

	/**
	 * 생성자
	 */
	public MarcStru()
	{
		this.reader		= "";
		this.fieldList	= new ArrayList<MarcFieldElement>();
	}



	public String getReader() {
		return reader;
	}



	public void setReader(String reader) {
		this.reader = reader;
	}



	public List<MarcFieldElement> getFieldList() {
		return fieldList;
	}



	public void setFieldList(List<MarcFieldElement> fieldList) {
		this.fieldList = fieldList;
	}



	/**
	 * 마크 구조체 객체 초기화
	 */
	public void init(){
		//리더 초기화
		this.reader = "";

		//필드리스트 초기화
		for(MarcFieldElement item : fieldList){
			item.init();
		}
		fieldList.clear();
	}


	/**
	 * 마크 필드를 추가한다.
	 * @param element 마크 필드 Element
	 */
	public void addField(MarcFieldElement element)
	{

		int tagno = element.getTagno();
		if(tagno == 20){
			fieldList.add(element);
			return ;
		}

		int index = findAddFieldIndex(tagno);
		if(index == -1){
			fieldList.add(element);
		}else{
			fieldList.add(index , element);
		}
	}

	/**
	 * 입력된 정보로 마크 필드 객체를 생성한 뒤 마크 필드를 추가한다.
	 * @param tagno 테그번호
	 * @param fIndicator 제1 지시기호
	 * @param sIndicator 제2 지시기호
	 * @param repetSymbol 반복여부
	 * @param fieldData 마크 필드 데이터
	 */
	public void addField(int tagno,  String fIndicator, String sIndicator, String repetSymbol, String fieldData)
	{
		MarcFieldElement element = new MarcFieldElement();
		element.setTagno(tagno);
		element.setFIndicator(fIndicator);
		element.setSIndicator(sIndicator);
		element.setRepetSymbol(repetSymbol);
		element.setFieldData(fieldData);

		if(tagno == 20){
			fieldList.add(element);
			return ;
		}

		int index = findAddFieldIndex(tagno);
		if(index == -1){
			fieldList.add(element);
		}else{
			fieldList.add(index , element);
		}

	}

	/**
	 * 입력된 정보로 마크 필드 객체를 생성한 뒤 지정한 위치에 마크 필드를 추가한다.
	 * @param index 마크 필드를 입력할 위치
	 * @param tagno 테그번호
	 * @param fIndicator 제1 지시기호
	 * @param sIndicator 제2 지시기호
	 * @param repetSymbol 반복여부
	 * @param fieldData 마크 필드 데이터
	 */
	public void addField(int index, int tagno,  String fIndicator, String sIndicator, String repetSymbol, String fieldData)
	{
		MarcFieldElement element = new MarcFieldElement();
		element.setTagno(tagno);
		element.setFIndicator(fIndicator);
		element.setSIndicator(sIndicator);
		element.setRepetSymbol(repetSymbol);
		element.setFieldData(fieldData);


		fieldList.add(index , element);
	}

	/**
	 * 마크 필드 객체 정보를 저장한다.
	 * @param index 마크 필드를 위치
	 * @param tagno 테그번호
	 * @param fIndicator 제1 지시기호
	 * @param sIndicator 제2 지시기호
	 * @param repetSymbol 반복여부
	 * @param fieldData 마크 필드 데이터
	 */
	public void setField(int index, int tagno, String fIndicator, String sIndicator, String repetSymbol, String fieldData)
	{
		MarcFieldElement element = new MarcFieldElement();
		element.setTagno(tagno);
		element.setFIndicator(fIndicator);
		element.setSIndicator(sIndicator);
		element.setRepetSymbol(repetSymbol);
		element.setFieldData(fieldData);

		fieldList.set(index, element);
	}

	/**
	 * 마크 필드 Array의 필드 객체 수를 구한다.
	 * @return 마크 필드 수
	 */
	public int getFieldCount()
	{
		return fieldList.size();
	}

	/**
	 * index에 위치한 마크 필드 객체를 반환한다.
	 * @param index 마크 필드 위치 (index)
	 * @return 마크필드 객체
	 */
	public MarcFieldElement getField(int index)
	{
		return fieldList.get(index);
	}

	/**
	 * 마크 필드를 삭제한다.
	 * @param index 삭제할 필드의 위치
	 */
	public void removeField(int index)
	{
		fieldList.remove(index);
	}

	/**
	 * 마크를 신규 입력할 때의 위치를 찾는 함수
	 * @param tagno 테그번호
	 * @return 입력해야할 필드의 index
	 */
	public int findAddFieldIndex(int tagno){
		for(int index = 0; index < fieldList.size(); index++ )
		{
			if( tagno < fieldList.get(index).getTagno() ){
				return index;
			}
		}
		return  -1;
	}

}
