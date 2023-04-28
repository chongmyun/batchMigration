/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.marc.struct.service;

import com.trans.migration.marc.marc.struct.MarcFieldElement;
import com.trans.migration.marc.marc.struct.MarcStru;
import com.trans.migration.marc.marc.struct.MarcSubFieldElement;

import java.util.List;
import java.util.Map;

/**
* 마크 매니져의 인터페이스이다.
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
public interface MarcStructService
{
	/**
	 * 0x1d : 레코드종단기호 - 양쪽방향 화살표
	 */
	public String RECORD_END="";
	//
	/**
	 * 0x1e : 필드종단기호 - 삼각형
	 */
	public String FIELD_END= "";
	/**
	 * 0x1f : 식별기호 - 역삼각형
	 */
	String IDENTIFIER			= "";

	/**
	 * 언어셋 변경
	 * @param charsetName
	 */
	public void setCharsetName(String charsetName);
	/**
	 * 스트림 마크를 입력받아 마크 매니져의 마크데이터를 구조체에 저장하여 초기화한다.<br>
	 * return key <br>
	 * status <br>
	 * statusDescription <br>
	 * @param marc 스트림 마크
	 * @param marcStru 마크 구조체
	 * @return 마크 구조화 성공 또는 실패
	 * @
	 */
	public Map<String,String> readMarcStruFromStreamMarc(String marc , MarcStru marcStru);

	/**
	 * 마크 매니져에 구조체로 저장되어 있는 마크를 스트림 마크로 변환하여 가져온다.<br>
	 * return key <br>
	 * status <br>
	 * statusDescription <br>
	 * streamMarc <br>
	 * @param marcStru 마크구조체
	 * @return 스트림 마크를 가져온다, 실패시 null을 반환한다.
	 * @
	 */
	public Map<String,String> writeMarcStruToStreamMarc(MarcStru marcStru);

	/**
	 * 에디트 마크를 입력받아 마크 매니져의 마크데이터를 구조체에 저장하여 초기화한다.<br>
	 * return key <br>
	 * status <br>
	 * statusDescription <br>
	 * @param editMarc 에디트 마크
	 * @param marcStru 마크구조체
	 * @return 마크 구조화 성공 또는 실패
	 * @
	 */
	public Map<String,String> readMarcStruFromEditMarc(String editMarc , MarcStru marcStru);

	/**
	 * 마크 매니져에 구조체로 저장되어 있는 마크를 에디트 마크로 변환하여 가져온다.<br>
	 * return key <br>
	 * status <br>
	 * statusDescription <br>
	 * editMarc <br>
	 * @param marcStru 마크구조체
	 * @return 에디트마크
	 * @
	 */
	public Map<String,String> writeMarcStruToEditMarc(MarcStru marcStru);

	/**
	 * 스트림 마크를 입력하여 에디트 마크를 구한다. <br>
	 * return key <br>
	 * status <br>
	 * statusDescription <br>
	 * editMarc <br>
	 * @param streamMarc 스트림 마크
	 * @param marcStru 마크구조체
	 * @return 에디트마크
	 * @
	 */
	public Map<String,String> getEditMarcFromStreamMarc(String streamMarc , MarcStru marcStru);

	/**
	 * 에디트 마크를 입력하여 스트림 마크를 구한다.<br>
	 * return key <br>
	 * status <br>
	 * statusDescription <br>
	 * streamMarc <br>
	 * @param editMarc 에디트 마크
	 * @param marcStru 마크구조체
	 * @return 스트림 마크
	 * @
	 */
	public Map<String,String> getStreamMarcFromEditMarc(String editMarc , MarcStru marcStru);


	/**
	 * 마크에서 지정한 TAG를 찾아 가장 먼저 발견되는 1개의 TAG의 필드데이터를 반환한다.
	 * @param  tagno TAG정보
	 * @param marcStru 마크구조체
	 * @return 1.성공: 필드데이터 , 2.실패: null, TAG가 존재하지 않을 경우
	 * @
	 */
	public String getSingleFieldData(int tagno , MarcStru marcStru);

	/**
	 * (최초에 나오는) 테그번호에 해당하는 마크 필드 객체를 구한다.
	 * @param tagno
	 * @param marcStru
	 * @
	 * @return 마크 필드 객체
	 * @
	 */
	public MarcFieldElement getSingleFieldElement(int tagno , MarcStru marcStru) ;

	/**
	 * (최초에 나오는) 테그번호에 해당하는 마크 필드 객체를 구한다.
	 * @param tagno
	 * @param fIndicator 제일지시기호
	 * @param marcStru
	 * @
	 * @return 마크 필드 객체
	 * @
	 */
	public MarcFieldElement getSingleFieldElement(int tagno ,char fIndicator, MarcStru marcStru) ;

	/**
	 * 테그번호 및 지시기호에 해당하는 마크 필드 객체를 구한다.
	 * @param tagno
	 * @param fIndicator
	 * @param sIndicator
	 * @param marcStru
	 * @return
	 * @
	 */
	public MarcFieldElement getSingleFieldElement(int tagno ,char fIndicator,char sIndicator , MarcStru marcStru) ;

	/**
	 * 특정 테그번호 , 식별기호에 해당하는 데이터를 조회한다.
	 * @param tagno TAG정보
	 * @param subFieldCode 식별기호
	 * @param marcStru
	 * @return 필드 데이터
	 * @
	 */
	public String getSubFieldData(int tagno, char subFieldCode,MarcStru marcStru) ;

	/**
	 * 마크에서 지정한 TAG를 찾아 발견되는 모든 TAG의 필드데이터를 반환한다.
	 * @param  tagno TAG정보
	 * @param marcStru 마크구조체
	 * @return 1.성공: 필드데이터 , 2.실패: null, 발견되는 TAG가 없을 경우
	 * @
	 */
	public String[] getMultiFieldData(int tagno, MarcStru marcStru) ;

	/**
	 * 테그번호에 해당하는 마크 필드 객체 리스트를 구한다.
	 * @param tagno
	 * @param marcStru
	 * @
	 * @return 마크 필드 객체 리스트
	 * @
	 */
	public List<MarcFieldElement> getMultiFieldElement(int tagno , MarcStru marcStru) ;
	public List<MarcFieldElement> getMultiFieldElement(int tagno,char fIndicator  , MarcStru marcStru) ;
	public List<MarcFieldElement> getMultiFieldElement(int tagno ,char fIndicator, char sIndicator , MarcStru marcStru) ;

	/**
	 * 마크에서 지정한 TAG의 필드데이터 내에서 지정한 식별기호를 찾아 발견되는 모든 서브 필드데이터를 가져온다.
	 * 구두점을 삭제한다.
	 * @param  tagno        TAG정보
	 * @param  subFieldCode 식별기호
	 * @param marcStru 마크구조체
	 * @return 1.성공: 서브 필드데이터  , 2.실패: null, 발견되는 TAG가 없을 경우 or 발견되는 식별기호가 없을 경우
	 * @
	 */
	public String[] getMultiSubFieldData(int tagno, char subFieldCode,MarcStru marcStru);
	public String[] getMultiSubFieldDataForRegNo(int tagno, char subFieldCode,MarcStru marcStru);
	/**
	 *
	 * @param tagno
	 * @param subFieldCode
	 * @param marcStru
	 * @return
	 * @
	 */
	public List<MarcSubFieldElement> getMultiSubFieldElement(int tagno, char subFieldCode , MarcStru marcStru) ;

	/**
	 * 필드데이터에서 지정한 식별기호를 찾아 가장 먼저 발견되는 1개의 서브 필드데이터를 가져온다.
	 * 구두점을 제거한다.
	 * @param  subFieldCode 식별기호
	 * @param  fieldElement   필드데이터
	 * @return 1.성공: 서브 필드데이터 , 2.실패: null, 발견되는 식별기호가 없을 경우
	 * @
	 */
	public String getSingleSubFieldDataFromFieldElement(char subFieldCode, MarcFieldElement fieldElement);

	/**
	 * 마크에 새로운 TAG를 생성한 뒤 필드데이터를 입력한다.
	 * @param  tagno     TAG정보
	 * @param  fieldData 필드데이터
	 * @param marcStru 마크구조체
	 * @return 1.성공: 1 , 2.실패: (없음)
	 * @
	 */
	public int insertFieldData(int tagno, String fieldData,MarcStru marcStru);


	/**
	 * 마크에서 지정한 수만큼 해당하는 TAG와 필드데이터를 삭제한다.
	 * @param  tagno       TAG정보
	 * @param  removeCount 삭제할 필드데이터의 수(0:전체)
	 * @param marcStru 마크구조체
	 * @return 1.성공: 삭제된 TAG의 수 , 2.실패: 0, 해당하는 TAG가 존재하지 않아서 삭제하지 않은 경우
	 * @
	 */
	public int removeFieldData(int tagno, int removeCount, MarcStru marcStru);

	/**
	 * 필드데이터를 삭제한다.
	 * @param tagno
	 * @param fieldData
	 * @param marcStru
	 * @return
	 * @
	 */
	public int removeFieldData(int tagno, String fieldData, MarcStru marcStru);

	/**
	 * 특정 테그번호를 삭제한다.
	 * @param tagno
	 * @param marcStru
	 * @return 0
	 * @
	 */
	public int removeTagNo(int tagno, MarcStru marcStru);

	/**
	 * 마크에서 지정한 TAG의 필드데이터를 검사하여 변경대상 필드데이터일 경우 모두 찾아서 변경한다.
	 * @param  tagno        TAG정보
	 * @param  oldFieldData 기존 필드데이터
	 * @param  newFieldData 수정 필드데이터
	 * @param marcStru 마크구조체
	 * @return 1.성공: 지정한 TAG의 수정된 필드데이터 수 ,2.실패: 0, 지정한 TAG가 존재하지 않은 경우 or 수정대상 필드데이터가 일치하는 경우가 없는 경우
	 * @
	 */
	public int updateFieldData(int tagno, String oldFieldData, String newFieldData,MarcStru marcStru);

	/**
	 * 마크에서 해당하는 TAG 1개를 찾아 해당 식별기호를 생성하여 서브 필드데이터를 추가한다.
	 * 해당하는 TAG가 존재하지 않을 경우 TAG를 생성하여 추가한다.
	 * @param  tagno        TAG정보
	 * @param  subFieldCode 식별기호
	 * @param  subFieldData 서브 필드데이터
	 * @param marcStru 마크구조체
	 * @return 1.성공: 1 , 2.실패: (없음)
	 * @
	 */
	public int insertSubFieldData(int tagno, char subFieldCode, String subFieldData, MarcStru marcStru);

	/**
	 * 마크에 서브필드데이터를 입력한다. 구두점을 입력했다면 입력한 구두점으로 구두점을 셋팅해준다.
	 * @param tagno 테그번호
	 * @param subFieldCode 식별기호
	 * @param subFieldData 서브 필드데이터
	 * @param puncmark 추가할 구두점
	 * @param marcStru 마크구조체
	 * @return
	 * @
	 */
	//public int insertSubFieldData(int tagno, char subFieldCode, String subFieldData , String puncmark ,MarcStru marcStru);

	/**
	 * 마크에서 지정한 TAG를 모두 찾아 해당 TAG의 필드데이터에서 지정한 식별기호의 서브 필드데이터를 삭제한다.
	 * @param  tagno        TAG정보
	 * @param  subFieldCode 식별기호
	 * @param marcStru 마크구조체
	 * @return 1.성공: 삭제된 서브 필드데이터 수 , 2.실패: 0, 해당하는 TAG가 존재하지 않는 경우, 해당하는 TAG에 지정한 식별기호 데이터가 없는 경우
	 * @
	 */
	public int removeSubFieldData(int tagno, char subFieldCode , MarcStru marcStru);

	/**
	 * 마크에서 지정한 TAG를 모두 찾아 해당 TAG의 필드데이터에서 지정한 식별기호의 데이터를 비교하여 해당하는 서브 필드데이터를 삭제한다.
	 * @param  tagno        TAG정보
	 * @param  subFieldCode 식별기호
	 * @param  subFieldData 서브 필드데이터
	 * @param marcStru 마크구조체
	 * @return 1.성공: 삭제된 서브 필드데이터 수 , 2.실패: 0, 해당하는 TAG가 존재하지 않는 경우, 해당하는 TAG에 지정한 식별기호 데이터가 없는 경우
	 * @
	 */
	public int removeSubFieldData(int tagno, char subFieldCode, String subFieldData , MarcStru marcStru);

	/**
	 * 마크에서 지정한 TAG를 모두 찾아 해당 TAG의 필드데이터에서 지정한 식별기호의 데이터를 비교하여 해당하는 서브 필드데이터를 수정한다.
	 * @param  tagno           TAG정보
	 * @param  subFieldCode    식별기호
	 * @param  oldSubFieldData 기존 서브 필드데이터 <-- 기존에 구두점이 다 붙어 있는 데이터...? 우리가 데이터 꺼낼때 구두점을 빼고 꺼내니 아닌가 ?
	 * @param  newSubFieldData 수정 서브 필드데이터
	 * @param marcStru 마크구조체
	 * @return 1.성공: 수정된 서브 필드데이터 수  , 2.실패: 0, 해당하는 TAG가 존재하지 않는 경우, 해당하는 TAG에 지정한 식별기호 데이터가 없는 경우
	 * @
	 */
	public int updateSubFieldData(int tagno, char subFieldCode, String oldSubFieldData, String newSubFieldData , MarcStru marcStru);

	/**
	 * Alias를 이용하여 008 테그의 정보를 가져온다.
	 * Alias의 정보는 marcalias.xml에 정의되어 있다.
	 * @param  alias 마크필드 Alias
	 * @param marcStru 마크구조체
	 * @return 008의 ALIAS에 해당하는 데이터
	 * @
	 */
	public String getSubFieldDataUseAlias(String alias , MarcStru marcStru);


	/**
	 * 리더 정보를 저장한다.
	 * @param reader 리더
	 * @param marcStru 마크구조체
	 * @
	 */
	public void setReader(String reader , MarcStru marcStru);

	/**
	 * 리더정보를 반환 한다.
	 * @param marcStru 마크구조체
	 * @
	 * @return 리더
	 */
	public String getReader(MarcStru marcStru);

	/**
	 * @return 기본 24 리더 생성
	 */
	public String getDefaultReader();

	/**
	 * 마크에서 지정한 TAG를 찾아 가장 먼저 발견되는 1개의 TAG의 필드데이터를 반환한다. [사용]
	 * @param tagno
	 * @param marcStru
	 * @
	 * @return MarcFieldElement
	 */
	public MarcFieldElement getSingleMarcFieldElement(int tagno , MarcStru marcStru);

	/**
	 * 마크에서 지정한 Tag를 찾아 MarcFieldElement List를 리턴한다. [사용]
	 * @param tagno
	 * @param marcStru
	 * @
	 * @return MarcFieldElement List
	 */
	public List<MarcFieldElement> getMultipleMarcFieldElement(int tagno , MarcStru marcStru);

	/**
	 * 마크에서 지정한 Tag들을 찾아 MarcFieldElement List를 리턴한다. [색인 모듈에서 사용중]
	 * @param tagnoList
	 * @param marcStru
	 * @
	 * @return MarcFieldElement List
	 */
	public List<MarcFieldElement> getMultipleMarcFieldElement(List<Integer> tagnoList, MarcStru marcStru);

	/**
	 * ISBN테그 020중 제일지시기호가 1인 테그정보 삭제
	 * @param marcStru
	 * @
	 * @return 삭제건수
	 */
	public int removeSetISBN(MarcStru marcStru);



	//고정장 관련 조회 시작 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 마크구조체에서 낱권ISBN를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 (제일지시기호 ' ')020$a의 값
	 * @
	 */
	public String getEa_isbn (MarcStru marcStru);
	/**
	 * 마크구조체에서 낱권ISBN를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 (제일지시기호 ' ')020$g의 값
	 * @
	 */
	public String getEa_isbn_add_code (MarcStru marcStru);
	/**
	 * 마크구조체에서 셋트ISBN을 추출해 준다.
	 * @param marcStru
	 * @return 제일 처음 나오는 (제일지시기호 '1')020$a의 값
	 * @
	 */
	public String getSet_isbn(MarcStru marcStru);
	/**
	 * 마크 구조체에서 셋트ISBN ADD CODE를 추출해 준다.
	 * @param marcStru
	 * @return 제일 처음 나오는 (제일지시기호 '1')020$g의 값
	 * @
	 */
	public String getSet_isbn_add_code(MarcStru marcStru);
	/**
	 * 마크구조체에서 서명을 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 245$a의 값
	 * @
	 */
	public String getTitle         (MarcStru marcStru);

	/**
	 * 마크구조체에서 서명관련 정보를 추출
	 * 이전에는원서명으로 썻었지만 마크에서 사라졌음 변수명은 그대로 유지중이라 함수이름이 이럼..
	 * @param marcStru
	 * @return 제일 처음 나오는 245$b의 값
	 * @
	 */
	public String getOrigin_title (MarcStru marcStru);
	/**
	 * 마크구조체에서 대등표제를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 245$x의 값
	 * @
	 */
	public String getParrel_title        (MarcStru marcStru);
	/**
	 * 마크구조체에서 편권차를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 245$n의 값
	 * @
	 */
	public String getVol           (MarcStru marcStru);
	/**
	 * 마크구조체에서 편제를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 245$p의 값
	 * @
	 */
	public String getVol_title     (MarcStru marcStru);
	/**
	 * 마크구조체에서 저자을 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 245$d의 값
	 * @
	 */
	public String getAuthor        (MarcStru marcStru);
	/**
	 * 마크구조체에서 판표시 를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 250$a의 값
	 * @
	 */
	public String getEdit     (MarcStru marcStru);
	/**
	 * 마크구조체에서 발행지를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 260$a의 값
	 * @
	 */
	public String getPublish_place      (MarcStru marcStru);
	/**
	 * 마크구조체에서 발행자를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 260$b의 값
	 * @
	 */
	public String getPublisher     (MarcStru marcStru);
	/**
	 * 마크구조체에서 발행년를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 260$c의 값
	 * @
	 */
	public String getPublish_year  (MarcStru marcStru);
	/**
	 * 마크구조체에서 페이지를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 300$a의 값
	 * @
	 */
	public String getPage          (MarcStru marcStru);
	/**
	 * 마크구조체에서 물리적특성을 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 300$b의 값
	 * @
	 */
	public String getPhysical_property (MarcStru marcStru);
	/**
	 * 마크구조체에서 책크기를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 300$c의 값
	 * @
	 */
	public String getBook_size(MarcStru marcStru);
	/**
	 * 마크구조체에서 딸림자료를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 300$e의 값
	 * @
	 */
	public String getAccomp_mat(MarcStru marcStru);
	/**
	 * 마크구조체에서 총서명 를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 440$a의 값
	 * @
	 */
	public String getSeries_title  (MarcStru marcStru);
	/**
	 * 마크구조체에서 총서편차 를 추출한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 440$v의 값
	 * @
	 */
	public String getSeries_vol    (MarcStru marcStru);
	/**
	 * 마크 구조체에서 가격성격을 추출해준다.
	 * @param marcStru
	 * @return 제일 처음 나오는 950$a 가격성격
	 * @
	 */
	public String getPrice_character(MarcStru marcStru);

	/**
	 * 마크 구조체에서 가격을 추출해준다.
	 * @param marcStru
	 * @return 제일 처음 나오는 950$b의 화폐구분을 제거한데이터
	 * @
	 */
	public String getPrice         (MarcStru marcStru);
	/**
	 * 마크 구조체에서 가격을 추출해준다.
	 * @param marcStru
	 * @return 제일 처음 나오는 950$b의 가격정보를 제거하고 구한 화폐구분값
	 * @
	 */
	public String getCurrency_code(MarcStru marcStru);

	/**
	 * 마크 구조체에서 가격잡정보를 추출해준다.
	 * @param marcStru
	 * @return 제일 처음 나오는 950$c 가격잡정보
	 * @
	 */
	public String getPrice_other_info(MarcStru marcStru);


	/**
	 * 마크 구조체에서 049$l의 처음 별치기호를 구한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 950$c 가격잡정보
	 * @
	 */
	public String getSeparate_shelf_code(MarcStru marcStru);
	/**
	 * 마크 구조체에서 090$a의 분류번호를 구한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 950$c 가격잡정보
	 * @
	 */
	public String getClass_no           (MarcStru marcStru);
	/**
	 * 마크 구조체에서 090$b의 도서기호를 구한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 950$c 가격잡정보
	 * @
	 */
	public String getBook_code          (MarcStru marcStru);
	/**
	 * 마크 구조체에서 049$v의 처음 권·연차기호를 구한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 950$c 가격잡정보
	 * @
	 */
	public String getVol_code           (MarcStru marcStru);
	/**
	 * 마크 구조체에서 049$c의 처음 복본기호를 구한다.
	 * @param marcStru
	 * @return 제일 처음 나오는 950$c 가격잡정보
	 * @
	 */
	public String getCopy_code          (MarcStru marcStru);

	/**
	 * 제어번호를 구한다.
	 * @param marcStru
	 * @return 제어번호
	 * @
	 */
	public String getControl_no          (MarcStru marcStru);
	
	public String getKeyword_first       (MarcStru marcStru);

	//고정장 관련 조회 종료 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//고정장 관련 입력 시작 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * ISBN테그 020중 제일지시기호가 0인 테그정보 추가
	 * @param ea_isbn
	 * @param ea_isbn_add_code
	 * @param marcStru
	 * @
	 */
	public void setEaISBN(String ea_isbn,String ea_isbn_add_code,MarcStru marcStru);
	/**
	 * ISBN테그 020중 제일지시기호가 1인 테그정보 추가
	 * @param set_isbn
	 * @param set_isbn_add_code
	 * @param marcStru
	 * @
	 */
	public void setSetISBN(String set_isbn,String set_isbn_add_code,MarcStru marcStru);
	/**
	 * 서명정보를 마크에 추가 또는 수정한다. 245$a
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setTitle         (String data, MarcStru marcStru);
	/**
	 * 표제관련사항을 추가 또는 수정한다. 245$b
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setOrigin_title (String data, MarcStru marcStru);
	/**
	 * 대등서명을 마크에 추가 또는 수정한다. 245$x
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setParrel_title        (String data, MarcStru marcStru);
	/**
	 * 편권차 마크에 추가 또는 수정한다. 245$n
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setVol           (String data, MarcStru marcStru);
	/**
	 * 편제 마크에 추가 또는 수정한다. 245$p
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setVol_title     (String data, MarcStru marcStru);
	/**
	 * 저자정보를 마크에 추가 또는 수정한다. 245$d ,e
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setAuthor        (String data, MarcStru marcStru);
	/**
	 * 판표시 마크에 추가 또는 수정한다. 250$a
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setEdit     (String data, MarcStru marcStru);
	/**
	 * 발행지 마크에 추가 또는 수정한다. 260$a
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setPublish_place      (String data, MarcStru marcStru);
	/**
	 * 발행자 마크에 추가 또는 수정한다. 260$b
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setPublisher     (String data, MarcStru marcStru);
	/**
	 * 발행년 마크에 추가 또는 수정한다. 260$c
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setPublish_year  (String data, MarcStru marcStru);
	/**
	 * 페이지 마크에 추가 또는 수정한다.  300$a
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setPage          (String data, MarcStru marcStru);
	/**
	 * 물리적특성 마크에 추가 또는 수정한다. 300$b
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setPhysical_property (String data, MarcStru marcStru);
	/**
	 * 크기 마크에 추가 또는 수정한다. 300$c
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setBook_size(String data, MarcStru marcStru);
	/**
	 * 딸림자료 마크에 추가 또는 수정한다. 300$e
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setAccomp_mat(String data, MarcStru marcStru);
	/**
	 * 총서편제 마크에 추가 또는 수정한다. 440$a
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setSeries_title  (String data, MarcStru marcStru);
	/**
	 * 총서편차 마크에 추가 또는 수정한다. 440$v
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setSeries_vol    (String data, MarcStru marcStru);
	/**
	 * 가격성격 마크에 추가 또는 수정한다. 950$a
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setPrice_character(String data, MarcStru marcStru);
	/**
	 * 가격 마크에 추가 또는 수정한다. 950$b
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setPrice         (String data, MarcStru marcStru);
	/**
	 * 가격잡정보  마크에 추가 또는 수정한다.950$c
	 * @param data
	 * @param marcStru
	 * @
	 */
	public void setPrice_other_info(String data, MarcStru marcStru);
	//고정장 관련 입력 종료 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String getMarcFieldSortData(Integer tagnoData, String fieldData, char crudFlag);


}
