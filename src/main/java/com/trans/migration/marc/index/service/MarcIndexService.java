/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.index.service;

import com.trans.migration.marc.marc.struct.MarcStru;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * MARC를 이용하여 색인 데이터를 생성해주는 Interface
 * @author Written By 이태일 【taeil.lee@eco.co.kr】
 */
public interface MarcIndexService
{
	/**
	 * 입력된 마크구조체를 이용하여 색인데이터(전체필드)를 생성한다.
	 * @param marcStru 마크구조체
	 * @return 색인데이터
	 * @throws UnsupportedEncodingException
	 */
	public Map<String,Object> makeIndexData(MarcStru marcStru) throws UnsupportedEncodingException;

	/**
	 * 검색용 서명 생성 함수
	 * @param marcStru 마크구조체
	 * @return 검색용 서명
	 * @throws UnsupportedEncodingException
	 */
	public String makeIDX_TITLE(MarcStru marcStru) throws UnsupportedEncodingException          ;

	/**
	 * ALPAS 복본조사용 서명 생성 함수
	 * @param marcStru
	 * @return 복본조사용 서명
	 * @throws UnsupportedEncodingException
	 */
	public String makeIDX_DUP_TITLE(MarcStru marcStru) throws UnsupportedEncodingException;

	/**
	 * 검색용 키워드 생성함수
	 * @param marcStru 마크구조체
	 * @return 검색용 키워드
	 */
	public String makeIDX_KEYWORD(MarcStru marcStru)        ;
	/**
	 * 검색용 저자 생성 함수
	 * @param marcStru 마크구조체
	 * @return 검색용 저자
	 * @throws UnsupportedEncodingException
	 */
	public String makeIDX_AUTHOR(MarcStru marcStru) throws UnsupportedEncodingException        ;
	/**
	 * 검색용 발행자 생성 함수
	 * @param marcStru 마크구조체
	 * @return 검색용 발행자
	 */
	public String makeIDX_PUBLISHER(MarcStru marcStru)      ;
	/**
	 * 검색용 주제명 생성 함수
	 * @param marcStru 마크구조체
	 * @return 검색용 주제명
	 */
	public String makeIDX_SUBJECT(MarcStru marcStru)        ;
	/**
	 * 정렬용 서명 생성함수
	 * @param marcStru 마크구조체
	 * @return 정렬용 서명
	 */
	public String makeIDX_ITITLE(MarcStru marcStru)         ;
	/**
	 * 정렬용 발행자 생성함수
	 * @param marcStru 마크구조체
	 * @return 정렬용 발행자
	 */
	public String makeIDX_IPUBLISHER(MarcStru marcStru)     ;
	/**
	 * 정렬용 발행년 생성함수
	 * @param marcStru 마크구조체
	 * @return 정렬용 발행년
	 */
	public String makeIDX_IPUB_YEAR(MarcStru marcStru)      ;

	/**
	 * 검색용 전체 키워드
	 * @param marcStru
	 * @return 검색용 전체 키워드
	 * @throws UnsupportedEncodingException
	 */
	public String makeIDX_ALL_ITEM   (MarcStru marcStru) throws UnsupportedEncodingException     ;
	/**
	 * 편권차 (책정보의 VOL_INDEX)  245$n
	 * @param marcStru
	 * @return 편권차
	 */
	public String makeIDX_VOL        (MarcStru marcStru)      ;

	/**
	 * ICS번호 (091 $a) 이런거 없음
	 * @param marcStru
	 * @return IDX_ICS
	 */
	public String makeIDX_ICS        (MarcStru marcStru)      ;
	/**
	 * 규격번호(기타분류기호,085 $a 지시기호5)
	 * @param marcStru
	 * @return IDX_STANDARDNO
	 */
	public String makeIDX_STANDARDNO (MarcStru marcStru)      ;
	/**
	 * 별치기호 생성함수
	 * @param marcStru 마크구조체
	 * @return 별치기호
	 */
	public String makeIDX_SE_SHELF_CODE(MarcStru marcStru)  ;
	/**
	 * 출력용 서명 생성함수
	 * @param marcStru 마크구조체
	 * @return 출력용 서명
	 */
	public String makeTITLE_INFO(MarcStru marcStru)         ;
	/**
	 * 출력용 저자 생성함수
	 * @param marcStru 마크구조체
	 * @return 출력용 저자
	 */
	public String makeAUTHOR_INFO(MarcStru marcStru)        ;
	/**
	 * 출력용 발행자 생성함수
	 * @param marcStru 마크구조체
	 * @return 출력용 발행자
	 */
	public String makePUB_INFO(MarcStru marcStru)           ;
	/**
	 * 출력용 발행년 생성함수
	 * @param marcStru 마크구조체
	 * @return 출력용 발행년
	 */
	public String makePUB_YEAR_INFO(MarcStru marcStru)      ;
	/**
	 * 판사항
	 * @param marcStru 마크구조체
	 * @return 판사항
	 */
    public String makeEDIT_INFO      (MarcStru marcStru)      ;
	/**
	 * ICS번호
	 * @param marcStru 마크구조체
	 * @return ICS번호
	 */
    public String makeICS_INFO       (MarcStru marcStru)      ;
	/**
	 * 규격번호정보
	 * @param marcStru 마크구조체
	 * @return 규격번호정보
	 */
    public String makeSTANDARDNO_INFO(MarcStru marcStru)      ;
	/**
	 * 본표제(245 $A $X $B)
	 * @param marcStru 마크구조체
	 * @return 본표제
	 */
    public String makeTITLE          (MarcStru marcStru)      ;
	/**
	 * 저작자
	 * @param marcStru 마크구조체
	 * @return 본표제
	 */
    public String makeAUTHOR         (MarcStru marcStru)      ;
	/**
	 * 발행자
	 * @param marcStru 마크구조체
	 * @return 본표제
	 */
    public String makePUBLISHER      (MarcStru marcStru)      ;
    /**
     * 발행년
     * @param marcStru
     * @return 발행년
     */
    public String makePUB_YEAR       (MarcStru marcStru)      ;
	/**
	 * ISBN 생성함수
	 * @param marcStru 마크구조체
	 * @return ISBN
	 */
	public String makeST_CODE(MarcStru marcStru)            ;
	/**
	 * ISSN 생성함수
	 * @param marcStru 마크구조체
	 * @return ISSN
	 */
	public String makeST_ISSN(MarcStru marcStru)            ;
	/**
	 * STRN(표준기술보고서번호)
	 * @param marcStru 마크구조체
	 * @return STRN(표준기술보고서번호)
	 */
    public String makeST_STRN        (MarcStru marcStru)      ;
	/**
	 * RNSTRN(보고서번호)
	 * @param marcStru 마크구조체
	 * @return RNSTRN(보고서번호)
	 */
    public String makeST_RNSTRN      (MarcStru marcStru)      ;
	/**
	 * 국가서지번호
	 * @param marcStru 마크구조체
	 * @return 국가서지번호
	 */
    public String makeST_CBN         (MarcStru marcStru)      ;
	/**
	 * 저작권등록번호
	 * @param marcStru 마크구조체
	 * @return 저작권등록번호
	 */
    public String makeST_CAN         (MarcStru marcStru)      ;
	/**
	 * 별치기호
	 * @param marcStru 마크구조체
	 * @return 별치기호
	 */
    public String makeSE_SHELF_CODE  (MarcStru marcStru)      ;

	/**
	 * 분류번호 생성함수
	 * @param marcStru 마크구조체
	 * @return 분류번호
	 */
	public String makeCLASS_NO(MarcStru marcStru)           ;
	/**
	 * 도서기호 생성함수
	 * @param marcStru 마크구조체
	 * @return 도서기호
	 */
	public String makeBOOK_CODE(MarcStru marcStru)          ;
	/**
	 * 권책기호
	 * @param marcStru 마크구조체
	 * @return 권책기호
	 */
    public String makeVOL_CODE       (MarcStru marcStru)      ;
	/**
	 * 권책기호설명
	 * @param marcStru 마크구조체
	 * @return 권책기호설명
	 */
    public String makeVOL_CODE_DISP  (MarcStru marcStru)      ;
	/**
	 * 제어번호
	 * @param marcStru 마크구조체
	 * @return 제어번호
	 */
    public String makeCONTROL_NO     (MarcStru marcStru)      ;

    /**
     * KDCP
     * @param marcStru
     * @return KDCP
     */
	public String makeKDCP_CLASS(MarcStru marcStru) ;
    /**
     * KDC
     * @param marcStru
     * @return KDC
     */
	public String makeKDC_CLASS (MarcStru marcStru) ;
    /**
     * DDC
     * @param marcStru
     * @return DDC
     */
	public String makeDDC_CLASS (MarcStru marcStru) ;
    /**
     * CEC
     * @param marcStru
     * @return CEC
     */
	public String makeCEC_CLASS (MarcStru marcStru) ;
    /**
     * CWC
     * @param marcStru
     * @return CWC
     */
	public String makeCWC_CLASS (MarcStru marcStru) ;
    /**
     * COC
     * @param marcStru
     * @return COC
     */
	public String makeCOC_CLASS (MarcStru marcStru) ;
    /**
     * UDC
     * @param marcStru
     * @return UDC
     */
	public String makeUDC_CLASS (MarcStru marcStru) ;
    /**
     * NDC
     * @param marcStru
     * @return NDC
     */
	public String makeNDC_CLASS (MarcStru marcStru) ;
    /**
     * LC
     * @param marcStru
     * @return LC
     */
	public String makeLC_CLASS  (MarcStru marcStru) ;
    /**
     * ETC
     * @param marcStru
     * @return ETC
     */
	public String makeETC_CLASS  (MarcStru marcStru) ;

	/**
	 * 한국대학부호
	 * @param marcStru 마크구조체
	 * @return 한국대학부호
	 */
    public String makeUNI_CODE   (MarcStru marcStru)      ;
	/**
	 * 한국정부기관부호
	 * @param marcStru 마크구조체
	 * @return 한국정부기관부호
	 */
    public String makeGOV_CODE   (MarcStru marcStru)      ;
	/**
	 * 학위논문학과/전공 생성함수
	 * @param marcStru 마크구조체
	 * @return 학위논문학과/전공 생성함수
	 */
	public String makeDEG_CODE(MarcStru marcStru)           ;
	/**
	 * 판종
	 * @param marcStru 마크구조체
	 * @return 판종
	 */
    public String makeEDIT       (MarcStru marcStru)      ;
	/**
	 * 본문언어
	 * @param marcStru 마크구조체
	 * @return 본문언어
	 */
    public String makeTEXT_LANG  (MarcStru marcStru)      ;
	/**
	 * 요약문언어
	 * @param marcStru 마크구조체
	 * @return 요약문언어
	 */
    public String makeSUM_LANG   (MarcStru marcStru)      ;

	/**
	 * 발행국정보 조회
	 * @param marcStru 마크구조체
	 * @return 본문언어
	 */
    public String makePUBLISH_COUNTRY_CODE(MarcStru marcStru) ;
    
    /**
	 * 발행국정보 조회
	 * @param marcStru 마크구조체
	 * @return 본문언어
	 */
    public String makeEdit_no(MarcStru marcStru) ;
    
    


}
