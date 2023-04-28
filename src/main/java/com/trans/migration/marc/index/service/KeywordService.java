/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.index.service;

import com.trans.migration.marc.marc.struct.MarcStru;

/**
 * 마크에서 특정 TAG의 필드데이터를 추출하여 키워드를 생성하는 클래스이다. keyword.xml에 정의되어진
 * 특정 TAG의 필드데이터를 사용한다.
 * @author Written By 이태일 【taeil.lee@eco.co.kr】
 */
public interface KeywordService {
	/**
	 * 0x1e : 필드종단기호 - 삼각형
	 */
	public String FIELD_END= "";
	/**
	 * MARC에서 653태그 키워드를(653$a) 추출한다.
	 * @param marcStru 마크구조체
	 * @return 성공: [0]추출된 키워드 데이터, [1]키워드 추출된 해당 태그 에디트마크(String[]) , 실패: null, 키워드 추출규칙 로딩이 실패한 경우, 마크에 문제가 있는 경우
	 */
	public String getKeywordFor653(MarcStru marcStru) ;

	/**
	 * 마크에 입력할 키워드 데이터(653$a)를 생성한다.
	 * @param marcStru 마크 구조체
	 * @return 키워드 데이터
	 */
	public String getKeywordForIndex(MarcStru marcStru)	;

	/**
	 * 마크에 키워드 데이터를 추가한다.
	 * @param marcStru 마크 구조체
	 * @param keyWord653Data 키워드 데이터(키워드별 공백으로 구분)
	 * @return 성공: 키워가 추가된 스트림마크(String) 실패: null
	 */
	public int insertMarc653Keyword(MarcStru marcStru, String keyWord653Data);

}
