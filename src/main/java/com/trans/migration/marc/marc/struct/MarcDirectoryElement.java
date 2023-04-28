/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.marc.struct;

/**
* 마크 데이터 중 한개의 TAG에 대한 디렉토리 정보를 저장하는 데이터 클래스이다.
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
final public class MarcDirectoryElement
{
	private Integer	tagno;				// 태그번호
	private String	fieldLength;		// 필드 데이터 길이
	private String	fieldStartPos;		// 필드 데이터 시작지점

	/**
	 * @return tagno
	 */
	public int getTagno() {
		return this.tagno.intValue();
	}
	/**
	 * @return fieldLength
	 */
	public String getFieldLength() {
		return this.fieldLength;
	}
	/**
	 * @return fieldStartPos
	 */
	public String getFieldStartPos() {
		return this.fieldStartPos;
	}

	/**
	 * @param tagno
	 */
	public void setTagno(int tagno) {
		this.tagno = tagno;
	}
	/**
	 * @param fieldLength
	 */
	public void setFieldLength(String fieldLength) {
		this.fieldLength = fieldLength;
	}
	/**
	 * @param fieldStartPos
	 */
	public void setFieldStartPos(String fieldStartPos) {
		this.fieldStartPos = fieldStartPos;
	}

	/**
	 * 맴버변수 초기화 함수
	 */
	public void init(){
		tagno = null;
		fieldLength = null;
		fieldStartPos = null;
	}

	public String toString(){
		return String.format("%03d", tagno)+fieldLength+fieldStartPos;
	}
}
