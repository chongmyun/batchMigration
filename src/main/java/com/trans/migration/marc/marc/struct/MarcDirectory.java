/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.marc.struct;

import java.util.ArrayList;

/** 
* 마크의 디렉토리 정보를 저장하고 있는 클래스이다. 여러 TAG의 디렉토리 정보를 ArrayList로 관리하고 있다.
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
final class MarcDirectory
{
	private ArrayList<MarcDirectoryElement> element = new ArrayList<MarcDirectoryElement>();
	
	/**
	 * @return 디렉토리 ElementArrayList를 반환한다.
	 */
	public ArrayList<MarcDirectoryElement> getElementList(){
		return this.element;
	}
	
	/**
	 * 디렉토리 리스트에 새로운 디렉토리 정보를 추가하는 함수이다.
	 * @param element 디렉토리 정보객체
	 */
	public void addElement(MarcDirectoryElement element)
	{
		this.element.add(element);
	}
	
	/**
	 * 디렉토리 리스트에 새로운 디렉토리 정보를 추가하는 함수이다.
	 * @param tagno TAG정보
	 * @param fieldLength 필드데이터 길이
	 * @param fieldStartPos 필드데이터 시작지점
	 */
	public void addElement(int tagno, String fieldLength, String fieldStartPos)
	{
		MarcDirectoryElement element = new MarcDirectoryElement();
		
		// set element info
		element.setTagno(tagno);
		element.setFieldLength(fieldLength);
		element.setFieldStartPos(fieldStartPos);
		
		this.element.add(element);
	}
	
	/**
	 * 디렉토리 리스트에 지정한 자리(index)에 새로운 디렉토리 정보를 추가하는 함수이다. 
	 * @param index 추가할자리
	 * @param tagno TAG정보
	 * @param fieldLength 필드데이터 길이
	 * @param fieldStartPos 필드데이터 시작지점
	 */
	public void addElement(int index, int tagno, String fieldLength, String fieldStartPos)
	{
		MarcDirectoryElement element = new MarcDirectoryElement();
		
		// set element info
		element.setTagno(tagno);
		element.setFieldLength(fieldLength);
		element.setFieldStartPos(fieldStartPos);

		this.element.add(index, element);
	}
	
	/**
	 * 디렉토리 리스트에 지정한 자리(index)에 디렉토리 정보를 수정하는 함수이다. 
	 * @param index 수정할자리
	 * @param tagno TAG정보
	 * @param fieldLength 필드데이터 길이
	 * @param fieldStartPos 필드데이터 시작지점
	 */
	public void setElement(int index, int tagno, String fieldLength, String fieldStartPos)
	{
		MarcDirectoryElement element = new MarcDirectoryElement();
		
		// set element info
		element.setTagno(tagno);
		element.setFieldLength(fieldLength);
		element.setFieldStartPos(fieldStartPos);
		
		this.element.set(index, element);
	}
	
	/**
	 * 
	 * @return 디렉토리 정보 수
	 */
	public int getElementCount()
	{
		return this.element.size();
	}
	
	/**
	 * 지정한 자리의 디렉토리 정보객체(MarcDirectoryElement)를 가져온다.
	 * @param index 가져올 디렉토리 정보객체의 index
	 * @return 지정한 자리의 디렉토리 정보객체
	 */
	public MarcDirectoryElement getElement(int index)
	{
		return this.element.get(index);
	}

	/** 
	 * 지정한 자리의 디렉토리 정보객체(MarcDirectoryElement)를 삭제한다.
	 * @param  index	삭제할 디렉토리 정보객체의 index
	 */
	public void remove(int index)
	{
		this.element.remove(index);
	}
}
