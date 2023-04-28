/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */
package com.trans.migration.marc.index.service;

import com.trans.migration.marc.marc.struct.MarcStru;
import com.trans.migration.marc.marc.struct.service.MarcStructService;
/** 
 * 마크 데이터를 이용하여 색인 데이터를 생성하는대 도움이 되는 로직
 * @author Written By 이태일 【taeil.lee@eco.co.kr】
 */
public interface MarcIndexUtil {

	/**
	 * Display용 필드를 가공처리하며 TagNo Token 및 타입변환하여 데이터 추출 후 데이터 연결후 반환
	 * @param tagNoList TagNo 및 식별기호의 리스트 데이터
	 * @param marcMgr 마크 데이터 처리 객체 
	 * @param marcStru 마크 데이터 구조체 
	 * @return 성공: 색인필드 가공완료 데이터  실패: NULL
	 * @
	 */
	public String getDisplayFieldIndexDataProcess(String tagNoList , MarcStructService marcMgr, MarcStru marcStru) ;
	
	/**
	 * 구두점 제거대상 식별기호인 경우 구두점 제거후 반환 아닌경우 원본 데이터를 반환한다.
	 * @param targetStr 구두점 제거대상 데이터
	 * @return 구두점 제거완료 데이터  또는 원본데이터 
	 */
	public String removeLastClassWord(String targetStr);
	
	/**
	 * 검색용,정렬용 필드를 분류하여 TagNo 에 해당하는 데이터 추출 후 해당 분류에 맞는 가공처리를 한다.
	 * 데이터 가공처리 완료 후 List에 저장하고 데이터 Type 변환 및 중복조사를 완료하여 데이터 반환한다.
	 * @param tagNoList TagNo및 식별기호 데이터리스트
	 * @param multiDataProcessFlag 검색용,정렬용 분류 플래그 [true : 검색용,false:정렬용]
	 * @param marcMgr 마크 데이터 처리 객체 
	 * @param marcStru 마크 데이터 구조체 
	 * @return 성공: 색인필드 가공완료 데이터  실패: NULL
	 * @
	 */
	public String getTagDataIndexProcess(String tagNoList, boolean multiDataProcessFlag ,MarcStructService   marcMgr, MarcStru marcStru) ;
	
	/**
	 * TagNo 데이터를 입력받고 MARC 에서 해당 데이터를 추출하여 검색용,정렬용 필드데이터를 가공 후 반환
	 * @param tagNoSbcode TagNo및 식별기호 데이터
	 * @param multiDataProcessFlag 검색용,정렬용 분류 플래그 [true : 검색용,false:정렬용]
	 * @param marcMgr 마크 데이터 처리 객체 
	 * @param marcStru 마크 데이터 구조체 
	 * @return 성공: 색인필드 가공완료 데이터  실패: NULL
	 * @
	 */
	public String getMarcDataProcess(String tagNoSbcode, boolean multiDataProcessFlag ,MarcStructService   marcMgr, MarcStru marcStru) ;
	
	/**
	 * ISBN, ISSN 색인필드를 생성 시 MARC 데이터 추출 후 특수문자 제거만을 수행한 후 데이터를 반환한다.
	 * @param tagNoSbcode tagNoSbcode TagNo및 식별기호 데이터
	 * @param setIsbnFlag ISBN,ISSN 여부
	 * @param marcMgr 마크 데이터 처리 객체 
	 * @param marcStru 마크 데이터 구조체 
	 * @return 성공: 색인필드 가공완료 데이터  실패: NULL
	 */
	public String getDeleteCharIndexDataProcess(String tagNoSbcode, char setIsbnFlag , MarcStructService   marcMgr, MarcStru marcStru);
	
	/**
	 * 검색용 필드를 가공처리하며, 대문자 변환.특수문자 제거.한자한글변환.괄호 데이터삭제 및 포함 데이터 추출 
	 * @param indexDataList MARC 에서 추출한 색인생성 대상 데이터
	 * @return 성공: 색인필드 가공완료 데이터  실패: NULL
	 */
	public String getSearchFieldIndexDataProcess(String[] indexDataList) ;
	
	/**
	 * 정렬용 필드를 가공처리하며, 대문자 변환.특수문자 제거.한자한글변환.괄호 삭제 데이터 추출 
	 * @param indexDataList MARC 에서 추출한 색인생성 대상 데이터
	 * @return 성공: 색인필드 가공완료 데이터  실패: NULL
	 */
	public String getOrderByFieldIndexDataProcess(String[] indexDataList) ;
	
	/**
	 * 색인 필드 생성에 필요한 데이터 가공처리에서 괄호로 시작하는 데이터를 삭제하여 반환한다.
	 * @param data 처리대상 문자열
	 * @return 성공: 괄호 안의 데이터를 삭제한 데이터   실패: NULL
	 */
	public String getDeleteBrace(String data) ;
	
	/**
	 * 색인 필드 생성에 필요한 데이터 가공처리에서 괄호를 포함한 데이터를 반환한다.
	 * @param data 처리대상 문자열
	 * @return 성공: 괄호 안의 데이터 포함한 데이터   실패: NULL
	 */
	public String getDeleteBraceData(String data) ;
	
	/**
	 * 문자열 배열 형태의 데이터를 공백단위의 문자열 형태로 변환한다.
	 * @param array 변환대상 배열 데이터 
	 * @return 공백단위 문자열 데이터
	 */
	public String fromArrayToBlankString(String[] array) ;
	
	/**
	 * 추출된 기본 색인 데이터를 대문자 변환, 한자변환, 특수문자 제거를 적용한다.
	 * @param indexAppendBrace 첫괄호 포함한 색인 데이터
	 * @param indexDeleteBrace 첫괄호 삭제한 색인 데이터
	 * @param indexOfFirstBrace 첫괄호 색인 데이터
	 * @param indexBlankSplit 스페이스 단위 색인 데이터
	 * @return 색인 결과 데이터
	 */
	public String makeResultIndexData(String indexAppendBrace, String indexDeleteBrace,String indexOfFirstBrace,String indexBlankSplit) ;
	
	/**
	 * 배열내에 중복된 데이터를 제거한 뒤 문자열을 반환한다. 
	 * @param targetStrs 체크 대상 문자열 배열
	 * @return 중복이 제거된 문자열 
	 */
	public String overlapDataRemove(String[]targetStrs)  ;
	
}
