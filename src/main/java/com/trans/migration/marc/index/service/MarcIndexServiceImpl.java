/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */
package com.trans.migration.marc.index.service;

import com.trans.migration.marc.StringCheckObject;
import com.trans.migration.marc.StringUtil;
import com.trans.migration.marc.marc.struct.MarcFieldElement;
import com.trans.migration.marc.marc.struct.MarcStru;
import com.trans.migration.marc.marc.struct.MarcSubFieldElement;
import com.trans.migration.marc.marc.struct.service.MarcStructService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;


/**
* 마크를 이용하여 색인 테이블에 입력할 데이터를 생성 한다.
* indexField.xml 설정파이을 참조한다.
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
@Service("MarcIndexService")
public class MarcIndexServiceImpl implements MarcIndexService {
	
	@Resource(name = "MarcStructService")
	protected MarcStructService marcStructService;

	@Resource(name = "StringUtil")
	protected StringUtil stringUtil;

	private static final List<String> rollWordList = new ArrayList<>(Arrays.asList("illustratedby","ilustratedby","...\'[등\']옮김","...\'[등\']공역","...\'[등\']그림","\'[같이\']옮김","\'[같이\']지음","...\'[등\']","\'[옮김_\']","\'[공\']편저","\'[공\']지음","글·그림","\'[공\']옮김","만든이","\'[외\']","\'[편\']","글사진","지은이","by","옮김","지음","엮음","그림","저자","공역","번역","외","글","역","저","강"));

	@Override
	public Map<String,Object> makeIndexData(MarcStru marcStru) throws UnsupportedEncodingException
	{
		Map<String,Object> resultObject = new HashMap<String,Object>();

		//resultObject.put("WORK_CODE"             , DB로 구할것 );
		//resultObject.put("REG_CODE"              , DB로 구할것 );
		//resultObject.put("MAT_CODE"              , DB로 구할것 );
		//resultObject.put("FORM_CODE"             , DB로 구할것 );
		//resultObject.put("MEDIA_CODE"            , DB로 구할것 );
		//resultObject.put("USE_OBJ_CODE"          , DB로 구할것 );
		//resultObject.put("USE_LIMIT_CODE"        , DB로 구할것 );
		//resultObject.put("MANAGE_CODE"           , DB로 구할것 );
		//resultObject.put("SUB_REG_CODE"          , DB로 구할것 );
		resultObject.put("IDX_TITLE"             , makeIDX_TITLE             (marcStru) ); //서명 색인
		resultObject.put("IDX_DUP_TITLE"         , makeIDX_DUP_TITLE         (marcStru) ); //복본조사용 서명 색인
		resultObject.put("IDX_KEYWORD"           , makeIDX_KEYWORD           (marcStru) );
		resultObject.put("IDX_AUTHOR"            , makeIDX_AUTHOR            (marcStru) ); //GetIndex_Author.txt
		resultObject.put("IDX_PUBLISHER"         , makeIDX_PUBLISHER         (marcStru) );
		resultObject.put("IDX_SUBJECT"           , makeIDX_SUBJECT           (marcStru) );
		resultObject.put("IDX_ITITLE"            , makeIDX_ITITLE            (marcStru) );
		resultObject.put("IDX_IPUBLISHER"        , makeIDX_IPUBLISHER        (marcStru) );
		resultObject.put("IDX_IPUB_YEAR"         , makeIDX_IPUB_YEAR         (marcStru) ); //GetIndex_IPUB_YEAR.txt
		resultObject.put("IDX_ALL_ITEM"          , makeIDX_ALL_ITEM          (marcStru) );
		// 마크 아닌듯..
		resultObject.put("IDX_VOL"               , makeIDX_VOL               (marcStru) );
		//resultObject.put("IDX_VOL_TITLE"         , "책테이블의 VOL_TITLE_INDEX [ ]공백을 넣고 문자열을 더한다." );
		resultObject.put("IDX_ICS"               , makeIDX_ICS               (marcStru) );
		resultObject.put("IDX_STANDARDNO"        , makeIDX_STANDARDNO        (marcStru) );
		//resultObject.put("IDX_PLACE"             , "DB 또는 책정보에서 구할것 ");
		//resultObject.put("IDX_HOLD_PLACE"        , "DB 또는 책정보에서 구할것 ");
		resultObject.put("IDX_SE_SHELF_CODE"     , makeIDX_SE_SHELF_CODE     (marcStru) );
		resultObject.put("TITLE_INFO"            , makeTITLE_INFO            (marcStru) );  //GetIndex_TITLE_INFO
		resultObject.put("AUTHOR_INFO"           , makeAUTHOR_INFO           (marcStru) );
		resultObject.put("PUB_INFO"              , makePUB_INFO              (marcStru) );
		resultObject.put("PUB_YEAR_INFO"         , makePUB_YEAR_INFO         (marcStru) );  //GetIndex_PUB_YEAR_INFO.txt
//		resultObject.put("PLACE_INFO"            , "DB 또는 책정보에서 구할것 ");
//		resultObject.put("MAIN_PLACE_INFO"       , "DB 또는 책정보에서 구할것 ");
		resultObject.put("EDIT_INFO"             , makeEDIT_INFO             (marcStru) );
		resultObject.put("ICS_INFO"              , makeICS_INFO              (marcStru) );
		resultObject.put("STANDARDNO_INFO"       , makeSTANDARDNO_INFO       (marcStru) );
		resultObject.put("TITLE"                 , makeTITLE                 (marcStru) );
		resultObject.put("AUTHOR"                , makeAUTHOR                (marcStru) );
		resultObject.put("PUBLISHER"             , makePUBLISHER             (marcStru) );
		resultObject.put("PUB_YEAR"              , makePUB_YEAR              (marcStru) ); //GetIndex_PublishYear.txt
		resultObject.put("ST_CODE"               , makeST_CODE               (marcStru) );
		resultObject.put("ST_ISSN"               , makeST_ISSN               (marcStru) );
		resultObject.put("ST_STRN"               , makeST_STRN               (marcStru) );
		resultObject.put("ST_RNSTRN"             , makeST_RNSTRN             (marcStru) );
		resultObject.put("ST_CBN"                , makeST_CBN                (marcStru) );
		resultObject.put("ST_CAN"                , makeST_CAN                (marcStru) );
		resultObject.put("SE_SHELF_CODE"         , makeSE_SHELF_CODE         (marcStru) );
		//resultObject.put("CLASS_NO_TYPE"         , "DB 또는 서지정보 구할것 " );
		resultObject.put("CLASS_NO"              , makeCLASS_NO              (marcStru) );
		resultObject.put("BOOK_CODE"             , makeBOOK_CODE             (marcStru) );
		resultObject.put("VOL_CODE"              , makeVOL_CODE              (marcStru) );
		resultObject.put("VOL_CODE_DISP"         , makeVOL_CODE_DISP         (marcStru) );
		resultObject.put("CONTROL_NO"            , makeCONTROL_NO            (marcStru) );
		resultObject.put("KDCP_CLASS"            , makeKDCP_CLASS(marcStru) );
		resultObject.put("KDC_CLASS"             , makeKDC_CLASS(marcStru) );
		resultObject.put("DDC_CLASS"             , makeDDC_CLASS(marcStru) );
		resultObject.put("CEC_CLASS"             , makeCEC_CLASS(marcStru) );
		resultObject.put("CWC_CLASS"             , makeCWC_CLASS(marcStru) );
		resultObject.put("COC_CLASS"             , makeCOC_CLASS(marcStru) );
		resultObject.put("UDC_CLASS"             , makeUDC_CLASS(marcStru) );
		resultObject.put("NDC_CLASS"             , makeNDC_CLASS(marcStru) );
		resultObject.put("LC_CLASS"              , makeLC_CLASS(marcStru) );
		resultObject.put("ETC_CLASS"             , makeETC_CLASS(marcStru)  );
		resultObject.put("UNI_CODE"              , makeUNI_CODE              (marcStru) );
		resultObject.put("GOV_CODE"              , makeGOV_CODE              (marcStru) );
		resultObject.put("DEG_CODE"              , makeDEG_CODE              (marcStru) );
		resultObject.put("EDIT"                  , makeEDIT                  (marcStru) );
		resultObject.put("TEXT_LANG"             , makeTEXT_LANG             (marcStru) );
		resultObject.put("SUM_LANG"              , makeSUM_LANG              (marcStru) );
//		resultObject.put("SPECIES_CLASS"         , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("HOLD_DATE"             , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("DUP_YN"                , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("WORKING_STATUS"        , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
		resultObject.put("PUBLISH_COUNTRY_CODE"  , makePUBLISH_COUNTRY_CODE  (marcStru) );
//		resultObject.put("DELETE_DATE"           , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("CONTENTS_YN"           , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("ABSTRACTS_YN"          , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("WONMUN_YN"             , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("MANAGE_NO"             , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("DUMMY"                 , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("USER_DEFINE_CODE1"     , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("USER_DEFINE_CODE2"     , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("USER_DEFINE_CODE3"     , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("FIRST_WORK"            , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("LAST_WORK"             , "서지또는 책등 따로 데이터를 구해야 함 MARC 아님 ");
//		resultObject.put("ST_CODEN"              , "용도를 모름 ");

		return resultObject;
	}

	@Override
	public String makeIDX_TITLE(MarcStru marcStru) throws UnsupportedEncodingException
	{

		List<String> duplicateCheckList = new ArrayList<String>();
		String strTemp1 = "";
		List<MarcFieldElement> index_tag_list = null;
		Set<Integer>keySet  = null;

		// 2018년 02월 박병구 이사님과의 회의 결과 기존 KOLAS 로직 무시 새로 규칙을 정함
		// 1. 지기시호 무시
		// 2. (데이터1) + 데이터2 = > 데이터2 , [특수기호 제거후]데이터1+데이터2
		// 245 부터 생성
		Map<Integer , String> IndexRuleInfo = new HashMap<Integer,String>();
		IndexRuleInfo.put(245, "a,b,p,x");
		keySet = IndexRuleInfo.keySet();
    	for(Integer tagNo : keySet){

    		String check_subFieldData = IndexRuleInfo.get(tagNo);
    		String[] check_subFieldList = check_subFieldData.split(",");

			index_tag_list = marcStructService.getMultipleMarcFieldElement(tagNo, marcStru);
			if(index_tag_list.size() > 0){
				for(MarcFieldElement tagField : index_tag_list){

					List<MarcSubFieldElement> subFieldList = tagField.getSubFieldList();

					for(MarcSubFieldElement subField : subFieldList){

						strTemp1 = "";
						for(String check_subField : check_subFieldList){

							if(subField.getSbCode().equals(check_subField)){
								strTemp1 = subField.getSubFieldData();
								strTemp1 = removePuncMarc (strTemp1);

								IDX_WORD(strTemp1 , duplicateCheckList);
							}
						}
					}
				}
				index_tag_list.clear();
			}


			if(tagNo.equals(245)  == true){
				List<MarcFieldElement> temp_index_tag_list = marcStructService.getMultipleMarcFieldElement(tagNo, marcStru);
				if(temp_index_tag_list.size() > 0){

					MarcSubFieldElement subfield_245_a = null;
					MarcSubFieldElement subfield_245_b = null;
//					MarcSubFieldElement subfield_245_n = null;
					List<MarcSubFieldElement> subFieldList = temp_index_tag_list.get(0).getSubFieldList();

					for(MarcSubFieldElement subField : subFieldList){
						if(subField.getSbCode().equals("a") == true ) subfield_245_a = subField;
						if(subField.getSbCode().equals("b") == true ) subfield_245_b = subField;
//						if(subField.getSbCode().equals("n") == true ) subfield_245_n = subField;
					}

					String t_strTemp1 = "";
					String t_strTemp2 = "";
//					String t_strTemp3 = "";
					if(subfield_245_a != null && subfield_245_b != null){
						t_strTemp1 = subfield_245_a.getSubFieldData();
						t_strTemp1 = removePuncMarc (t_strTemp1);

						t_strTemp2 = subfield_245_b.getSubFieldData();
						t_strTemp2 = removePuncMarc (t_strTemp2);

//						t_strTemp3 = subfield_245_n.getSubFieldData();
//						t_strTemp3 = removePuncMarc (t_strTemp3);

						t_strTemp1 = t_strTemp1+" "+t_strTemp2;
//						t_strTemp1 = t_strTemp1+" "+t_strTemp2+" "+t_strTemp3;

						IDX_WORD(t_strTemp1 , duplicateCheckList);
					}

				}
			}

	    }
		
		//1. 130(기본표목) 반복 없음
		//    $a[반복없음] ,$p[반복]
		//    식별기호들의 문자열은 더한다.
		//2. 240(통일표제) 반복 없음
		//    $a[반복없음] ,$p[반복]
		//    식별기호들의 문자열은 더한다.
		//3. 730(부출표목 - 통일표제) 반복 없음
		//    $a[반복없음] ,$p[반복]
		//    식별기호들의 문자열은 더한다.
		//4. 740(부출표목) 반복가능
		//   $a[반복없음] , $p[반복]
		//    식별기호들의 문자열은 더한다.
		//5. 930(로컬표목) 반복 가능
		//     $a[반복없음] , $p[반복가능]
		//    식별기호들의 문자열은 더한다.
		//6. 940(로컬표목) 반복 가능
		//     $a[반복없음] , $p[반복가능]
		//    식별기호들의 문자열은 더한다.
		//7. 630(통일표제) 반복가능
		//   $a[반복없음] , $p[반복] , $x[반복] , $y[반복] , $z[반복]
		//    식별기호들의 문자열은 더한다.
		//8. 650(일반표제) 반복가능
		//   $a[반복없음] , $x[반복] , $y[반복] , $z[반복]
		//    식별기호들의 문자열은 더한다.
		//9. 651(지명) 반복가능
		//   $a[반복없음] , $x[반복] , $y[반복] , $z[반복]
		//    식별기호들의 문자열은 더한다.

		//  IndexRuleInfo1 : 식별기호의 데이터는 모두 더하는 형식
		//  식별기호 a 는 공통이라 식별기호에 체크 하지 않고 소스로 무조건 조회
		Map<Integer , String> IndexRuleInfo1 = new HashMap<Integer,String>();
		IndexRuleInfo1.put(130, "p");
		IndexRuleInfo1.put(240, "p");
		IndexRuleInfo1.put(730, "p");
		IndexRuleInfo1.put(740, "p");
		IndexRuleInfo1.put(930, "p");
		IndexRuleInfo1.put(940, "p");
		IndexRuleInfo1.put(630, "p,x,y,z");
		IndexRuleInfo1.put(650, "x,y,z");
		IndexRuleInfo1.put(651, "x,y,z");


    	keySet = IndexRuleInfo1.keySet();
    	for(Integer tagNo : keySet){

    		String check_subFieldData = IndexRuleInfo1.get(tagNo);
    		String[] check_subFieldList = check_subFieldData.split(",");

			index_tag_list = marcStructService.getMultipleMarcFieldElement(tagNo, marcStru);
			if(index_tag_list.size() > 0){

				for(MarcFieldElement tagField : index_tag_list){
					strTemp1 = "";

					List<MarcSubFieldElement> subFieldList = tagField.getSubFieldList();

					for(MarcSubFieldElement subField : subFieldList){
						if(subField.getSbCode().equals("a")){
							strTemp1 = subField.getSubFieldData();
							strTemp1 = removePuncMarc (strTemp1);
							break;
						}
					}

					for(MarcSubFieldElement subField : subFieldList){

						for(String check_subField : check_subFieldList){
							if(subField.getSbCode().equals(check_subField) == true)
							{
								strTemp1 +=  subField.getSubFieldData();
								strTemp1 = removePuncMarc (strTemp1);
							}
						}
					}

					IDX_WORD(strTemp1 , duplicateCheckList);

				}
				index_tag_list.clear();

			}
    	}
    	keySet.clear();

		//10. 210(축약표제) 반복가능		$a[반복없음]
		//11. 220(등록표제) 반복가능		$a[반복없음]
		//12. 246(여러형태표제) 반복가능	$a[반복없음]
		//13. 247(변경전표제) 반복가능		$a[반복없음]
		//14. 245(본표제) 반복않됨
		//    $a[반복] ,$b[반복] ,$p[반복] ,$x[반복]
		//  각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.
		//따라서 테그 번호와 식별기호가 순서쌍으로 맞으면 해당 테그번호의 테그들을 읽어 색인어 규칙에 보내면 된다.
		//15. 440(총서표제) 반복가능
		//    $a[반복않됨] ,$p[반복] ,$x[반복]
		//  각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.
		//16. 490(총서표제) 반복가능
		//    $a[반복]
		//  각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.
		//16. 830(총서부출) 반복가능
		//    $a[반복없음], $p[반복]
		//    각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.
		//17. 949(로컬-총서표제) 반복가능
		//    $a[반복없음], $p[반복]
		//    각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.
		//18. 500(일반주기) 반복가능
		//    $a[반복없음],
		//    각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.

    	//  IndexRuleInfo2 : 식별기호별로 독립적으로 데이터를 처리하는 방식
		Map<Integer , String> IndexRuleInfo2 = new HashMap<Integer,String>();
		IndexRuleInfo2.put(210, "a");
		IndexRuleInfo2.put(222, "a");
		IndexRuleInfo2.put(246, "a");
		IndexRuleInfo2.put(247, "a");
//		IndexRuleInfo2.put(245, "a,b,p,x"); 21.11.18 총명 245가 안들어가는 경우가 있어서 위로 뺌
		IndexRuleInfo2.put(440, "a,x,p");
		IndexRuleInfo2.put(490, "a");
		IndexRuleInfo2.put(830, "a,p");
		IndexRuleInfo2.put(949, "a,p");
		//IndexRuleInfo2.put(500, "a"); 4000바이트 넘는경우가 존재하야 삭제

    	keySet = IndexRuleInfo2.keySet();
    	for(Integer tagNo : keySet){

    		String check_subFieldData = IndexRuleInfo2.get(tagNo);
    		String[] check_subFieldList = check_subFieldData.split(",");

			index_tag_list = marcStructService.getMultipleMarcFieldElement(tagNo, marcStru);
			if(index_tag_list.size() > 0){
				for(MarcFieldElement tagField : index_tag_list){

					List<MarcSubFieldElement> subFieldList = tagField.getSubFieldList();

					for(MarcSubFieldElement subField : subFieldList){

						strTemp1 = "";
						for(String check_subField : check_subFieldList){

							if(subField.getSbCode().equals(check_subField)){
								strTemp1 = subField.getSubFieldData();
								strTemp1 = removePuncMarc (strTemp1);

								IDX_WORD(strTemp1 , duplicateCheckList);
							}
						}
					}
				}
				index_tag_list.clear();
			}
	    }

		String returnValue = "";
		int count = 0;
		int index = 0 ;
		for(String item : duplicateCheckList){

			if(index == 0){
				returnValue = item;
				count = returnValue.getBytes("UTF-8").length;
			}else{
				if(count + (" "+item).getBytes("UTF-8").length < 4000  ){
					returnValue+=" "+item;
					count = count + (" "+item).getBytes("UTF-8").length;
				}else{
					break;
				}
			}
			index++;
		}


		return returnValue;
	}

	@Override
	public String makeIDX_DUP_TITLE(MarcStru marcStru) throws UnsupportedEncodingException
	{

		List<String> duplicateCheckList = new ArrayList<String>();
		String strTemp1 = "";
		String strTemp2 = "";
		List<MarcFieldElement> index_tag_list = null;
		Set<Integer>keySet  = null;

		// 2018년 02월 박병구 이사님과의 회의 결과 기존 KOLAS 로직 무시 새로 규칙을 정함
		// 1. 지기시호 무시
		// 2. (데이터1) + 데이터2 = > 데이터2 , [특수기호 제거후]데이터1+데이터2

		//1. 130(기본표목) 반복 없음
		//    $a[반복없음] ,$p[반복]
		//    식별기호들의 문자열은 더한다.
		//2. 240(통일표제) 반복 없음
		//    $a[반복없음] ,$p[반복]
		//    식별기호들의 문자열은 더한다.
		//3. 730(부출표목 - 통일표제) 반복 없음
		//    $a[반복없음] ,$p[반복]
		//    식별기호들의 문자열은 더한다.
		//4. 740(부출표목) 반복가능
		//   $a[반복없음] , $p[반복]
		//    식별기호들의 문자열은 더한다.
		//5. 930(로컬표목) 반복 가능
		//     $a[반복없음] , $p[반복가능]
		//    식별기호들의 문자열은 더한다.
		//6. 940(로컬표목) 반복 가능
		//     $a[반복없음] , $p[반복가능]
		//    식별기호들의 문자열은 더한다.
		//7. 630(통일표제) 반복가능
		//   $a[반복없음] , $p[반복] , $x[반복] , $y[반복] , $z[반복]
		//    식별기호들의 문자열은 더한다.
		//8. 650(일반표제) 반복가능
		//   $a[반복없음] , $x[반복] , $y[반복] , $z[반복]
		//    식별기호들의 문자열은 더한다.
		//9. 651(지명) 반복가능
		//   $a[반복없음] , $x[반복] , $y[반복] , $z[반복]
		//    식별기호들의 문자열은 더한다.

		//  IndexRuleInfo1 : 식별기호의 데이터는 모두 더하는 형식
		//  식별기호 a 는 공통이라 식별기호에 체크 하지 않고 소스로 무조건 조회
		Map<Integer , String> IndexRuleInfo1 = new HashMap<Integer,String>();
		IndexRuleInfo1.put(130, "p");
		IndexRuleInfo1.put(240, "p");
		IndexRuleInfo1.put(730, "p");
		IndexRuleInfo1.put(740, "a,p");
		IndexRuleInfo1.put(930, "p");
		IndexRuleInfo1.put(940, "p");
		IndexRuleInfo1.put(630, "p,x,y,z");
		IndexRuleInfo1.put(650, "x,y,z");
		IndexRuleInfo1.put(651, "x,y,z");


    	keySet = IndexRuleInfo1.keySet();
    	for(Integer tagNo : keySet){

    		String check_subFieldData = IndexRuleInfo1.get(tagNo);
    		String[] check_subFieldList = check_subFieldData.split(",");

			index_tag_list = marcStructService.getMultipleMarcFieldElement(tagNo, marcStru);
			if(index_tag_list.size() > 0){

				for(MarcFieldElement tagField : index_tag_list){
					strTemp1 = "";
					strTemp2 = "";

					List<MarcSubFieldElement> subFieldList = tagField.getSubFieldList();

					for(MarcSubFieldElement subField : subFieldList){
						if(subField.getSbCode().equals("a")){
							strTemp1 = subField.getSubFieldData();
							strTemp1 = removePuncMarc (strTemp1);
							break;
						}
					}

					for(MarcSubFieldElement subField : subFieldList){

						for(String check_subField : check_subFieldList){
							if(subField.getSbCode().equals(check_subField) == true)
							{
								strTemp1 +=  subField.getSubFieldData();
								strTemp1 = removePuncMarc (strTemp1);
							}
						}
					}

					//strTemp1 = $a+$p.+.. 다 합쳐진 한단어
					strTemp2 = stringUtil.RemoveOneParenthesisReturnString(strTemp1); //

					//시작단어가 ( 인가
					if(strTemp1.equals(strTemp2) == true ){
						Non_ContainParenthesisIndexWordForDup(strTemp1 ,duplicateCheckList);
					}else{
						ContainParenthesisIndexWordForDup(strTemp1,strTemp2, duplicateCheckList);
					}
				}
				index_tag_list.clear();

			}
    	}
    	keySet.clear();

		//10. 210(축약표제) 반복가능		$a[반복없음]
		//11. 220(등록표제) 반복가능		$a[반복없음]
		//12. 246(여러형태표제) 반복가능	$a[반복없음]
		//13. 247(변경전표제) 반복가능		$a[반복없음]
		//14. 245(본표제) 반복않됨
		//    $a[반복] ,$b[반복] ,$p[반복] ,$x[반복]
		//  각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.
		//따라서 테그 번호와 식별기호가 순서쌍으로 맞으면 해당 테그번호의 테그들을 읽어 색인어 규칙에 보내면 된다.
		//15. 440(총서표제) 반복가능
		//    $a[반복않됨] ,$p[반복] ,$x[반복]
		//  각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.
		//16. 490(총서표제) 반복가능
		//    $a[반복]
		//  각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.
		//16. 830(총서부출) 반복가능
		//    $a[반복없음], $p[반복]
		//    각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.
		//17. 949(로컬-총서표제) 반복가능
		//    $a[반복없음], $p[반복]
		//    각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.
		//18. 500(일반주기) 반복가능
		//    $a[반복없음],
		//    각각의 식별기호별로 규칙을 적용하고 뛰어쓰기 단위로 데이터를 저장한다.

    	//  IndexRuleInfo2 : 식별기호별로 독립적으로 데이터를 처리하는 방식
		Map<Integer , String> IndexRuleInfo2 = new HashMap<Integer,String>();
		IndexRuleInfo2.put(210, "a");
		IndexRuleInfo2.put(222, "a");
		IndexRuleInfo2.put(246, "a");
		IndexRuleInfo2.put(247, "a");
		IndexRuleInfo2.put(245, "a,b,p,x");
		IndexRuleInfo2.put(440, "a,x,p");
		IndexRuleInfo2.put(490, "a");
		IndexRuleInfo2.put(830, "a,p");
		IndexRuleInfo2.put(949, "a,p");
		// IndexRuleInfo2.put(500, "a"); 4000바이트 넘는경우가존재하여 삭제

    	keySet = IndexRuleInfo2.keySet();
    	for(Integer tagNo : keySet){

    		String check_subFieldData = IndexRuleInfo2.get(tagNo);
    		String[] check_subFieldList = check_subFieldData.split(",");

			index_tag_list = marcStructService.getMultipleMarcFieldElement(tagNo, marcStru);
			if(index_tag_list.size() > 0){
				for(MarcFieldElement tagField : index_tag_list){

					List<MarcSubFieldElement> subFieldList = tagField.getSubFieldList();

					for(MarcSubFieldElement subField : subFieldList){

						strTemp1 = ""; strTemp2 = "";

						for(String check_subField : check_subFieldList){

							if(subField.getSbCode().equals(check_subField)){
								strTemp1 = subField.getSubFieldData();
								strTemp1 = removePuncMarc (strTemp1);

								strTemp2 = stringUtil.RemoveOneParenthesisReturnString(strTemp1);

								//시작단어가 ( 인가
								if(strTemp1.equals(strTemp2) == true ){
									Non_ContainParenthesisIndexWordForDup(strTemp1 ,duplicateCheckList);
								}else{
									ContainParenthesisIndexWordForDup(strTemp1,strTemp2, duplicateCheckList);
								}
							}
						}
					}
				}
				index_tag_list.clear();
			}

			if(tagNo.equals(245)  == true){
				List<MarcFieldElement> temp_index_tag_list = marcStructService.getMultipleMarcFieldElement(tagNo, marcStru);
				if(temp_index_tag_list.size() > 0){

					MarcSubFieldElement subfield_245_a = null;
					MarcSubFieldElement subfield_245_b = null;
					List<MarcSubFieldElement> subFieldList = temp_index_tag_list.get(0).getSubFieldList();

					for(MarcSubFieldElement subField : subFieldList){
						if(subField.getSbCode().equals("a") == true ) subfield_245_a = subField;
						if(subField.getSbCode().equals("b") == true ) subfield_245_b = subField;
					}

					String t_strTemp1 = "";
					String t_strTemp2 = "";
					String t_strTemp3 = "";
					String t_strTemp4 = "";
					if(subfield_245_a != null && subfield_245_b != null){
						t_strTemp1 = subfield_245_a.getSubFieldData();
						t_strTemp1 = removePuncMarc (t_strTemp1);

						t_strTemp2 = stringUtil.RemoveOneParenthesisReturnString(t_strTemp1);

						t_strTemp3 = subfield_245_b.getSubFieldData();
						t_strTemp3 = removePuncMarc (t_strTemp3);

						t_strTemp4 = stringUtil.RemoveOneParenthesisReturnString(t_strTemp3);

						t_strTemp1 = t_strTemp1+" "+t_strTemp3;
						t_strTemp2 = t_strTemp2+" "+t_strTemp4;

						if(t_strTemp1.equals(t_strTemp2) == true ){
							Non_ContainParenthesisIndexWordForDup(t_strTemp1 ,duplicateCheckList);
						}else{
							ContainParenthesisIndexWordForDup(t_strTemp1,t_strTemp2, duplicateCheckList);
						}
					}

				}
			}


	    }

		String returnValue = "";
		int count = 0;
		int index = 0 ;
		for(String item : duplicateCheckList){

			if(index == 0){
				returnValue = item;
				count = returnValue.getBytes("UTF-8").length;
			}else{
				if(count + (" "+item).getBytes("UTF-8").length < 4000  ){
					returnValue+=" "+item;
					count = count + (" "+item).getBytes("UTF-8").length;
				}else{
					break;
				}
			}
			index++;
		}


		return returnValue;
	}

	@Override
	public String makeIDX_KEYWORD(MarcStru marcStru)
	{
		//============================================================================================
		//1. "검색 키워드" 추출
		//============================================================================================
		String[] keywords = marcStructService.getMultiSubFieldData(653, 'a', marcStru);

		if(keywords == null) return null;

		List<String> list = new ArrayList<String>();
		for(String keyword : keywords){
			if(keyword.trim().equals("") == true) continue;
			if(list.contains(keyword) == false) list.add(keyword);
		}

		StringBuffer sb = new StringBuffer();
		int i = 0 ;
		for(String keyword : list){
			if( i == 0 ){
				sb.append(keyword);
			}else{
				sb.append(" "+keyword);
			}
			i++;
		}
		return sb.toString();

	}

	@Override
	public String makeIDX_AUTHOR(MarcStru marcStru) throws UnsupportedEncodingException
	{
		List<String> duplicateCheckList = new ArrayList<String>();
		String strTemp1 = "";
		List<MarcFieldElement> index_tag_list = null;
		Set<Integer>keySet  = null;

		Map<Integer , String> IndexRuleInfo1 = new HashMap<Integer,String>();
		IndexRuleInfo1.put(100, "b,c");
		IndexRuleInfo1.put(110, "b,k,g");
		IndexRuleInfo1.put(600, "b,c,,x,y,z");
		IndexRuleInfo1.put(610, "b,k,x,y,z");
		IndexRuleInfo1.put(611, "x,y,z");
		IndexRuleInfo1.put(700, "b,c");
		IndexRuleInfo1.put(710, "b,k,g");
		IndexRuleInfo1.put(900, "b,c");
		IndexRuleInfo1.put(910, "b,k,g");

    	keySet = IndexRuleInfo1.keySet();
    	for(Integer tagNo : keySet){

    		String check_subFieldData = IndexRuleInfo1.get(tagNo);
    		String[] check_subFieldList = check_subFieldData.split(",");

			index_tag_list = marcStructService.getMultipleMarcFieldElement(tagNo, marcStru);
			if(index_tag_list.size() > 0){

				for(MarcFieldElement tagField : index_tag_list){
					strTemp1 = "";

					List<MarcSubFieldElement> subFieldList = tagField.getSubFieldList();

					for(MarcSubFieldElement subField : subFieldList){
						if(subField.getSbCode().equals("a")){
							strTemp1 = subField.getSubFieldData();
							strTemp1 = removePuncMarc (strTemp1);
							break;
						}
					}

					for(MarcSubFieldElement subField : subFieldList){

						for(String check_subField : check_subFieldList){
							if(subField.getSbCode().equals(check_subField) == true)
							{
								strTemp1 +=  subField.getSubFieldData();
								strTemp1 = removePuncMarc (strTemp1);
							}
						}
					}

					IDX_WORD(strTemp1 , duplicateCheckList);

				}
				index_tag_list.clear();

			}
    	}
    	keySet.clear();

		Map<Integer , String> IndexRuleInfo2 = new HashMap<Integer,String>();
		IndexRuleInfo2.put(111, "a");
		IndexRuleInfo2.put(711, "a");
		IndexRuleInfo2.put(911, "a");
		IndexRuleInfo2.put(245, "d,e");

		keySet = IndexRuleInfo2.keySet();
    	for(Integer tagNo : keySet){

    		String check_subFieldData = IndexRuleInfo2.get(tagNo);
    		String[] check_subFieldList = check_subFieldData.split(",");

			index_tag_list = marcStructService.getMultipleMarcFieldElement(tagNo, marcStru);
			if(index_tag_list.size() > 0){

				for(MarcFieldElement tagField : index_tag_list){
					strTemp1 = "";

					List<MarcSubFieldElement> subFieldList = tagField.getSubFieldList();

					for(MarcSubFieldElement subField : subFieldList){

						for(String check_subField : check_subFieldList){

							if(subField.getSbCode().equals(check_subField)){
								strTemp1 = subField.getSubFieldData();
								strTemp1 = removePuncMarc (strTemp1);

								if(tagNo.intValue() == 245 ){
									//역활어 제거 처리 필요
									//역활어 제거는 뛰어쓰기를 붙인후 제거
									for(String rollWord : rollWordList){
										strTemp1 = strTemp1.replaceAll(" "+rollWord , "");
										if(rollWord.length()>2){
											strTemp1 = strTemp1.replaceAll(rollWord , "");
										}
									}
								}
								IDX_WORD(strTemp1 , duplicateCheckList);

							}
						}
					}
				}
				index_tag_list.clear();
			}
    	}


		String returnValue = "";
		int count = 0;
		int index = 0 ;
		for(String item : duplicateCheckList){

			if(index == 0){
				returnValue = item;
				count = returnValue.getBytes("UTF-8").length;
			}else{
				if(count + (" "+item).getBytes("UTF-8").length < 4000  ){
					returnValue+=" "+item;
					count = count + (" "+item).getBytes("UTF-8").length;
				}else{
					break;
				}
			}
			index++;
		}
		return returnValue;
	}

	@Override
	public String makeIDX_PUBLISHER(MarcStru marcStru)
	{
		StringBuffer strIndexItem = new StringBuffer();
		String strTemp1 = "";
		MarcFieldElement tag260 = marcStructService.getSingleMarcFieldElement(260, marcStru);

		if(tag260 != null){
			List<MarcSubFieldElement> subFieldList = tag260.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("b") )
				{
					strTemp1 = subField.getSubFieldData();
					if(isPuncMark(strTemp1) == true ) strTemp1 = strTemp1.substring(0,strTemp1.length()-1);

					strTemp1 = strTemp1.toUpperCase();
					//2017-06-07 제목처럼 뛰어쓰기 단위로 일부러 이렇게 잡아놨음 : 이태일
					strTemp1 = stringUtil.MakeIndex(strTemp1);

					strTemp1 = strTemp1.trim();

					strIndexItem.append(" " + strTemp1);

				}
			}
		}
		//////////////////////////////////////////////////////////////////////
		// 550 발행처주기
		int indexColon = -1;
		MarcFieldElement tag550 = marcStructService.getSingleMarcFieldElement(550, marcStru);

		if(tag550 != null){
			List<MarcSubFieldElement> subFieldList = tag550.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("a") )
				{
					strTemp1 = subField.getSubFieldData();

					strTemp1 = strTemp1.toUpperCase();

					strTemp1 = strTemp1.trim();

					strIndexItem.append(" " + strTemp1);

					indexColon = strTemp1.indexOf(":");
					if ( indexColon > 0 )
					{
						strTemp1 = stringUtil.MakeIndex(strTemp1.substring(indexColon+1));
						strIndexItem.append(" " + strTemp1);
					}
					else
					{
						strTemp1 = stringUtil.MakeIndex(strTemp1);
						strIndexItem.append(" " + strTemp1);
					}
				}
			}
		}

		remove_duplication_word(strIndexItem);
		String returnValue = strIndexItem.toString();
		returnValue = stringUtil.getKeywordNoRemoveSpace(returnValue);

		if(returnValue.trim().equals("") == true) return null;

		return returnValue;

	}

	@Override
	public String makeIDX_SUBJECT(MarcStru marcStru)
	{
		StringCheckObject checkParenthesis = new StringCheckObject();
		StringBuffer idx_subject = new StringBuffer();
		{
			MarcFieldElement fieldElement = marcStructService.getSingleMarcFieldElement(600, marcStru);

			if(fieldElement != null){
				String strTemp1 = "";
				List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
				for(MarcSubFieldElement subField :subFieldList){

					if( subField.getSbCode().equals("a") == true
					 || subField.getSbCode().equals("b") == true
					 || subField.getSbCode().equals("x") == true
					 || subField.getSbCode().equals("y") == true
					 || subField.getSbCode().equals("z") == true ){
						if(subField.getSubFieldData() == null || subField.getSubFieldData().trim().equals("") == true) continue;
						strTemp1 += removePunctuationMark(subField.getSubFieldData());
						strTemp1 = strTemp1.toUpperCase();
						strTemp1 = strTemp1.replaceAll(" ", "");
						checkParenthesis.init();
						checkParenthesis = stringUtil.RemoveAllParenthesis(strTemp1);
						strTemp1 = checkParenthesis.getValue();
						strTemp1 = strTemp1.trim();
					}
				}

				if (strTemp1.equals("") == false){
					idx_subject.append(strTemp1);
				}
			}
		}
		{
			MarcFieldElement fieldElement = marcStructService.getSingleMarcFieldElement(610, marcStru);
			if(fieldElement != null){
				String strTemp1 = "";
				List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
				for(MarcSubFieldElement subField :subFieldList){

					if( subField.getSbCode().equals("a") == true
					 || subField.getSbCode().equals("b") == true
					 || subField.getSbCode().equals("k") == true
					 || subField.getSbCode().equals("x") == true
					 || subField.getSbCode().equals("y") == true
					 || subField.getSbCode().equals("z") == true ){
						if(subField.getSubFieldData() == null || subField.getSubFieldData().trim().equals("") == true) continue;
						strTemp1 += removePunctuationMark(subField.getSubFieldData());
						strTemp1 = strTemp1.toUpperCase();
						strTemp1 = strTemp1.replaceAll(" ", "");
						checkParenthesis.init();
						checkParenthesis = stringUtil.RemoveAllParenthesis(strTemp1);
						strTemp1 = checkParenthesis.getValue();
						strTemp1 = strTemp1.trim();
					}
				}

				if (strTemp1.equals("") == false){
					idx_subject.append(strTemp1);
				}
			}
		}
		{
			MarcFieldElement fieldElement = marcStructService.getSingleMarcFieldElement(611, marcStru);
			if(fieldElement != null){
				String strTemp1 = "";
				List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
				for(MarcSubFieldElement subField :subFieldList){

					if( subField.getSbCode().equals("a") == true
					 || subField.getSbCode().equals("x") == true
					 || subField.getSbCode().equals("k") == true
					 || subField.getSbCode().equals("y") == true
					 || subField.getSbCode().equals("z") == true ){
						if(subField.getSubFieldData() == null || subField.getSubFieldData().trim().equals("") == true) continue;
						strTemp1 += removePunctuationMark(subField.getSubFieldData());
						strTemp1 = strTemp1.toUpperCase();
						strTemp1 = strTemp1.replaceAll(" ", "");
						checkParenthesis.init();
						checkParenthesis = stringUtil.RemoveAllParenthesis(strTemp1);
						strTemp1 = checkParenthesis.getValue();
						strTemp1 = strTemp1.trim();
					}
				}

				if (strTemp1.equals("") == false){
					idx_subject.append(strTemp1);
				}
			}
		}
		{
			MarcFieldElement fieldElement = marcStructService.getSingleMarcFieldElement(630, marcStru);
			if(fieldElement != null){
				String strTemp1 = "";
				List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
				for(MarcSubFieldElement subField :subFieldList){

					if( subField.getSbCode().equals("a") == true
					 || subField.getSbCode().equals("p") == true
					 || subField.getSbCode().equals("x") == true
					 || subField.getSbCode().equals("y") == true
					 || subField.getSbCode().equals("z") == true ){
						if(subField.getSubFieldData() == null || subField.getSubFieldData().trim().equals("") == true) continue;
						strTemp1 += removePunctuationMark(subField.getSubFieldData());
						strTemp1 = strTemp1.toUpperCase();
						strTemp1 = strTemp1.replaceAll(" ", "");
						checkParenthesis.init();
						checkParenthesis = stringUtil.RemoveAllParenthesis(strTemp1);
						strTemp1 = checkParenthesis.getValue();
						strTemp1 = strTemp1.trim();
					}
				}

				if (strTemp1.equals("") == false){
					idx_subject.append(strTemp1);
				}
			}
		}
		{
			MarcFieldElement fieldElement = marcStructService.getSingleMarcFieldElement(650, marcStru);
			if(fieldElement != null){
				String strTemp1 = "";
				List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
				for(MarcSubFieldElement subField :subFieldList){

					if( subField.getSbCode().equals("a") == true
					 || subField.getSbCode().equals("x") == true
					 || subField.getSbCode().equals("y") == true
					 || subField.getSbCode().equals("z") == true ){
						if(subField.getSubFieldData() == null || subField.getSubFieldData().trim().equals("") == true) continue;
						strTemp1 += removePunctuationMark(subField.getSubFieldData());
						strTemp1 = strTemp1.toUpperCase();
						strTemp1 = strTemp1.replaceAll(" ", "");
						checkParenthesis.init();
						checkParenthesis = stringUtil.RemoveAllParenthesis(strTemp1);
						strTemp1 = checkParenthesis.getValue();
						strTemp1 = strTemp1.trim();
					}
				}

				if (strTemp1.equals("") == false){
					idx_subject.append(strTemp1);
				}
			}
		}
		{
			MarcFieldElement fieldElement = marcStructService.getSingleMarcFieldElement(651, marcStru);
			if(fieldElement != null){
				String strTemp1 = "";
				List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
				for(MarcSubFieldElement subField :subFieldList){

					if( subField.getSbCode().equals("a") == true
					 || subField.getSbCode().equals("x") == true
					 || subField.getSbCode().equals("y") == true
					 || subField.getSbCode().equals("z") == true ){
						if(subField.getSubFieldData() == null || subField.getSubFieldData().trim().equals("") == true) continue;
						strTemp1 += removePunctuationMark(subField.getSubFieldData());
						strTemp1 = strTemp1.toUpperCase();
						strTemp1 = strTemp1.replaceAll(" ", "");
						checkParenthesis.init();
						checkParenthesis = stringUtil.RemoveAllParenthesis(strTemp1);
						strTemp1 = checkParenthesis.getValue();
						strTemp1 = strTemp1.trim();
					}
				}

				if (strTemp1.equals("") == false){
					idx_subject.append(strTemp1);
				}
			}
		}

		return idx_subject.toString();


	}

	@Override
	public String makeIDX_ITITLE(MarcStru marcStru)
	{
		MarcFieldElement tag245 = marcStructService.getSingleMarcFieldElement(245, marcStru);
		String strTemp1 = "";
		//String strTemp2 = "";
		StringBuffer indexItem = new StringBuffer();  // = Kolas 의 strIndexItem 변수

		if(tag245 != null){
			List<MarcSubFieldElement> tag245subFieldList = tag245.getSubFieldList();
			for(MarcSubFieldElement item : tag245subFieldList){
				if(item.getSbCode().equals("a") ){
					strTemp1 = item.getSubFieldData();
					strTemp1 = stringUtil.MakeIndex(strTemp1);
					strTemp1 = strTemp1.replaceAll("\\[외\\]","");
					strTemp1 = strTemp1.replaceAll("\\[外\\]","");
					strTemp1 = strTemp1.replaceAll("\\[et al.\\]","");
				}

//				if(item.getSbCode().equals("n") ){
//					strTemp2 = item.getSubFieldData();
//					strTemp2 = stringUtil.MakeIndex(strTemp2);
//				}
			}
		}
		indexItem.append(strTemp1);
		String returnValue = indexItem.toString();
		returnValue = stringUtil.getKeywordNoRemoveSpace(returnValue);
//		indexItem.append(" "+strTemp2);
//		returnValue = indexItem.toString();
//
//		if(returnValue.trim().equals("") == true) return null;

		return returnValue;

	}

	@Override
	public String makeIDX_IPUBLISHER(MarcStru marcStru)
	{
		MarcFieldElement tag260 = marcStructService.getSingleMarcFieldElement(260, marcStru);
		String strTemp1 = "";
		StringBuffer indexItem = new StringBuffer();  // = Kolas 의 strIndexItem 변수

		if(tag260 != null){
			List<MarcSubFieldElement> tag260subFieldList = tag260.getSubFieldList();
			for(MarcSubFieldElement item : tag260subFieldList){
				if(item.getSbCode().equals("b") ){
					strTemp1 = stringUtil.MakeIndex(strTemp1);
				}
			}
		}
		indexItem.append(strTemp1);
		String returnValue = indexItem.toString();
		returnValue = stringUtil.getKeywordNoRemoveSpace(returnValue);

		if(returnValue.trim().equals("") == true) return null;

		return returnValue;

	}

	@Override
	public String makeIDX_IPUB_YEAR(MarcStru marcStru)
	{
		//GetIndex_IPUB_YEAR
		String strIndexItem = "";
		String sPubYear = "";	//발행년
		String sYear = "";		//숫자4자리 발행년
		MarcFieldElement tag260 = marcStructService.getSingleMarcFieldElement(260, marcStru);

		if(tag260 != null){
			sPubYear = "";
			List<MarcSubFieldElement> subFieldList = tag260.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("c") )
				{
					sPubYear = subField.getSubFieldData();

					for ( int i = 0 ; i < sPubYear.length() ; i++ ) {
						char sOneWord;
						sOneWord = sPubYear.charAt(i);
						if ( sOneWord == '0' || sOneWord == '1' ||sOneWord == '2' || sOneWord == '3' || sOneWord == '4'
						  || sOneWord == '5' || sOneWord == '6' || sOneWord == '7' || sOneWord == '8' || sOneWord == '9') {
							sYear = sYear + String.valueOf(sOneWord);
						}
					}

				}
			}
		}

		if ( sYear.length() == 4 ) {
			strIndexItem = sYear;
		}
		else {
			MarcFieldElement tag008 = marcStructService.getSingleMarcFieldElement(8, marcStru);
			if(tag008 != null){

				String filedData = tag008.getFieldData();

				if(filedData != null && filedData.length() > 10){
					strIndexItem = filedData.substring(7, 10);
				}

			}
		}

		strIndexItem = stringUtil.MakeIndex(strIndexItem);

		if(strIndexItem.trim().equals("") == true) strIndexItem = null;

		return strIndexItem;
		//===========================================================================================
		//1. 범위 검색용 발행년도(정렬)
		//===========================================================================================
		//return marcStructService.get008FiexdFieldDataUseAlias("MARC_CREATE_PUBLISH_YEAR" , marcStru);

	}

	@Override
	public String makeIDX_ALL_ITEM(MarcStru marcStru) throws UnsupportedEncodingException{

		String idx_title     = makeIDX_TITLE(marcStru);
		String idx_author    = makeIDX_AUTHOR(marcStru);
		String idx_publisher = makeIDX_PUBLISHER(marcStru);
		String idx_keyword   = makeIDX_KEYWORD(marcStru);

		String temp = "";
		if(idx_title != null){
			temp = idx_title;
		}
		if(idx_author != null){
			if(temp.equals("") == true){
				temp = idx_author;
			}else{
				temp+= " " + idx_author;
			}
		}
		if(idx_publisher != null){
			if(temp.equals("") == true){
				temp = idx_publisher;
			}else{
				temp+= " " + idx_publisher;
			}
		}
		if(idx_keyword != null){
			if(temp.equals("") == true){
				temp = idx_keyword;
			}else{
				temp+= " " + idx_keyword;
			}
		}
		String []tempArray = temp.split(" ");
		String temp2 = overlapDataRemove(tempArray);
		String idx_all_item = fromArrayToBlankString(temp2.split(" "));

		String[] return_value_list = idx_all_item.split(" ");
		String returnValue2 = "";
		int count = 0;
		int index = 0 ;
		for(String item : return_value_list){

			if(index == 0){
				returnValue2 = item;
				count = returnValue2.getBytes("UTF-8").length;
			}else{
				if(count + (" "+item).getBytes("UTF-8").length < 4000  ){
					returnValue2+=" "+item;
					count = count + (" "+item).getBytes("UTF-8").length;
				}else{
					break;
				}
			}
			index++;
		}
		return returnValue2;
	}

	@Override
	public String makeIDX_VOL(MarcStru marcStru) {

		String idx_vol = "";
		String [] tag090_c = marcStructService.getMultiSubFieldData(90, 'c', marcStru);
		if(tag090_c != null){
			for(String vol : tag090_c){
				if(idx_vol.equals("") == true)idx_vol = stringUtil.getKeyword(vol);
				else idx_vol = idx_vol + " " + stringUtil.getKeyword(vol);
			}
		}

		if(idx_vol.trim().equals("") == true) idx_vol = null;
		return idx_vol;
	}

	@Override
	public String makeIDX_ICS(MarcStru marcStru) {
		String fieldData_091 = marcStructService.getSingleFieldData(91, marcStru);
    	if(fieldData_091 != null){
    		fieldData_091 = fieldData_091.substring(4);
    		fieldData_091 = fieldData_091.replaceAll(String.valueOf(MarcStructService.IDENTIFIER)+"a", " ");
    		fieldData_091 = stringUtil.getKeyword(fieldData_091);

    		if(fieldData_091.trim().equals("") == true) fieldData_091 = null;
    		return fieldData_091;
    	}
		return null;
	}

	@Override
	public String makeIDX_STANDARDNO(MarcStru marcStru) {
		//return null;
		String fieldData_085 = marcStructService.getSingleFieldData(85, marcStru);
    	if(fieldData_085 != null){
    		fieldData_085 = fieldData_085.substring(4);
    		fieldData_085 = fieldData_085.replaceAll(String.valueOf(MarcStructService.IDENTIFIER)+"a", " ");

    		if(fieldData_085.indexOf(" ") == -1){
    			fieldData_085 = stringUtil.getKeyword(fieldData_085);
        		return fieldData_085;
    		}else{

    			String strSTANDARDNO_TOTAL = "";
    			String strSTANDARDNO_LASTITEM = "";
    			String strTmp = fieldData_085;
    			String strItemData = "";
    			String strLeftData = "";

    			//전체
    			strTmp = stringUtil.getKeyword(strTmp);
    			strSTANDARDNO_TOTAL = strTmp;

    			//마지막 데이터
    			strTmp = fieldData_085;
    			int lastIndex = strTmp.lastIndexOf(" ");

    			strSTANDARDNO_LASTITEM = strTmp.substring(lastIndex);

    			strSTANDARDNO_TOTAL = strSTANDARDNO_TOTAL + strSTANDARDNO_LASTITEM;

    			//돌면서 뭔가 처리
    			strTmp = fieldData_085;

    			int charIndex = strTmp.indexOf(" ");
    			while(charIndex > 0 ){

    				strLeftData = strTmp.substring(0,charIndex);
    				if ( strLeftData.equals(strSTANDARDNO_LASTITEM) == true ) break;

    				strItemData = strLeftData + strSTANDARDNO_LASTITEM ;

    				strItemData = stringUtil.getKeyword(strItemData);

    				strSTANDARDNO_TOTAL = strSTANDARDNO_TOTAL + " " + strItemData ;

    				if ( strTmp.equals("") == false)
    					strTmp = strTmp.substring( charIndex+1 );

    				charIndex = strTmp.indexOf(" ");

    			}

    			if(strSTANDARDNO_TOTAL.trim().equals("") == true) strSTANDARDNO_TOTAL = null;
    			return strSTANDARDNO_TOTAL;
    		}

    	}
    	return null;
	}

	@Override
	public String makeIDX_SE_SHELF_CODE(MarcStru marcStru)
	{
		return marcStructService.getSubFieldData(49, 'f', marcStru);
	}

	@Override
	public String makeTITLE_INFO(MarcStru marcStru)
	{
		String strIndexItem = "";
		MarcFieldElement tag_245 = marcStructService.getSingleMarcFieldElement(245, marcStru);

		if(tag_245 != null){

			List<MarcSubFieldElement> tag245subFieldList = tag_245.getSubFieldList();

			String subFieldData = "";
			String strPunkMark = "";
			for(MarcSubFieldElement subField : tag245subFieldList){
				if(   subField.getSbCode().equals("a") || subField.getSbCode().equals("b") || subField.getSbCode().equals("n")
			        || subField.getSbCode().equals("x") || subField.getSbCode().equals("p") ){

					subFieldData = subField.getSubFieldData();

					if(subField.getPunkmark() != null && subField.getPunkmark().equals("") == false ){

						strPunkMark = subField.getPunkmark();

						if (  strPunkMark.equals(".") == true || strPunkMark.equals(",") == true  )
						{
							strPunkMark = strPunkMark+" ";
						}else{
							strPunkMark = " "+strPunkMark+" ";
						}

						subFieldData = subFieldData + strPunkMark;
					}else{
						subFieldData = subFieldData +" ";
					}

					strIndexItem += subFieldData;
				}
			}


			strIndexItem = strIndexItem.trim();

			if(isPuncMark(strIndexItem) == true){
				strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);
			}

		}

		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem ;
	}

	@Override
	public String makeAUTHOR_INFO(MarcStru marcStru)
	{
		String strIndexItem = "";
		MarcFieldElement tag_245 = marcStructService.getSingleMarcFieldElement(245, marcStru);

		if(tag_245 != null){

			List<MarcSubFieldElement> tag245subFieldList = tag_245.getSubFieldList();

			String subFieldData = "";
			for(MarcSubFieldElement subField : tag245subFieldList){
				if( subField.getSbCode().equals("d") ){
					subFieldData = subField.getSubFieldData();
					if(strIndexItem.equals("") == true){
						strIndexItem = subFieldData;
					}else{
						// 20.04.13 이현주 -> 저자에 , 혹은 ; 같은 구두점이 붙으면  따로 콤마를 붙이지 않도록 수정.
						if(strIndexItem.substring(strIndexItem.length()-1, strIndexItem.length()).equals(",") || strIndexItem.substring(strIndexItem.length()-1, strIndexItem.length()).equals(";")) {
							strIndexItem += " " + subFieldData;
						} else {
							strIndexItem+=", "+ subFieldData;
						}
					}
				}
			}

			for(MarcSubFieldElement subField : tag245subFieldList){
				if( subField.getSbCode().equals("e") ){
					subFieldData = subField.getSubFieldData();
					if(strIndexItem.equals("") == true){
						strIndexItem = subFieldData;
					}else{

						if(strIndexItem.substring(strIndexItem.length()-1, strIndexItem.length()).equals(",") || strIndexItem.substring(strIndexItem.length()-1, strIndexItem.length()).equals(";")) {
							strIndexItem += " " + subFieldData;
						} else {
							strIndexItem+=", "+ subFieldData;
						}
					}
				}
			}

			strIndexItem = strIndexItem.trim();

			if(isPuncMark(strIndexItem) == true){
				strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);
			}

		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem ;
	}

	@Override
	public String makePUB_INFO(MarcStru marcStru)
	{
		String strIndexItem = "";
		MarcFieldElement tag260 = marcStructService.getSingleMarcFieldElement(260, marcStru);

		if(tag260 != null){
			List<MarcSubFieldElement> subFieldList = tag260.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("b") )
				{
					strIndexItem = subField.getSubFieldData();
					strIndexItem = strIndexItem.trim();
					// 20.04.14 이현주 -> 입력시 발행자,발행년도 입력하면 생기는 구둣점 제거.
					if(strIndexItem.substring(strIndexItem.length()-1, strIndexItem.length()).equals(",")) {
						strIndexItem = strIndexItem.substring(0, strIndexItem.length()-1);
					}

					break;
				}
			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;
	}

	@Override
	public String makePUB_YEAR_INFO(MarcStru marcStru)
	{
		String strIndexItem = "";
		MarcFieldElement tag260 = marcStructService.getSingleMarcFieldElement(260, marcStru);

		if(tag260 != null){
			List<MarcSubFieldElement> subFieldList = tag260.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("c") )
				{
					strIndexItem = subField.getSubFieldData();
					if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);
					break;
				}
			}
		}

		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;
	}

	@Override
	public String makeEDIT_INFO(MarcStru marcStru)
	{
		String strIndexItem = "";
		MarcFieldElement tag250 = marcStructService.getSingleMarcFieldElement(250, marcStru);

		if(tag250 != null){
			List<MarcSubFieldElement> subFieldList = tag250.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("a") )
				{
					strIndexItem = subField.getSubFieldData();
					if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);
					break;
				}
			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;
	}

	@Override
	public String makeICS_INFO(MarcStru marcStru)
	{
		String fieldData_091 = marcStructService.getSingleFieldData(91, marcStru);
    	if(fieldData_091 != null){
    		fieldData_091 = fieldData_091.substring(4);
    		fieldData_091 = fieldData_091.replaceAll(String.valueOf(MarcStructService.IDENTIFIER)+"a", " ");


    		if(fieldData_091.trim().equals("") == true) fieldData_091 = null;
    		return fieldData_091;
    	}
		return null;
	}

	@Override
	public String makeSTANDARDNO_INFO(MarcStru marcStru)
	{
    	String fieldData_085 = marcStructService.getSingleFieldData(85, marcStru);
    	if(fieldData_085 != null){
    		fieldData_085 = fieldData_085.substring(4);
    		fieldData_085 = fieldData_085.replaceAll(String.valueOf(MarcStructService.IDENTIFIER)+"a", " ");

    		if(fieldData_085.trim().equals("") == true) fieldData_085 = null;

    		return fieldData_085;

    	}
    	return null;
	}

	@Override
	public String makeTITLE(MarcStru marcStru)
	{
		String tag245_a = marcStructService.getSubFieldData(245, 'a', marcStru);
		String tag245_x = marcStructService.getSubFieldData(245, 'x', marcStru);
		String tag245_b = marcStructService.getSubFieldData(245, 'b', marcStru);

		if(tag245_a == null) tag245_a = "";
		if(tag245_x == null) tag245_x = "";
		if(tag245_b == null) tag245_b = "";

		String title = "";

		if(tag245_a.equals("") == false){
			title += tag245_a;
		}
		if(tag245_x.equals("") == false){
			title += " = "+tag245_x;
		}
		if(tag245_b.equals("") == false){
			title += " : "+tag245_b;
		}

		title = title.trim();
		if(isPuncMark(title) == true){
			title = title.substring(0,title.length()-1);
		}

		if(title.trim().equals("") == true) title = null;
		return title;

	}

	@Override
	public String makeAUTHOR(MarcStru marcStru)
	{
		String [] tag245_d = marcStructService.getMultiSubFieldData(245, 'd', marcStru);
		String [] tag245_e = marcStructService.getMultiSubFieldData(245, 'e', marcStru);
		String   author = "";

		if(tag245_d != null){
			for(String t_author : tag245_d){
				if(author.equals("") == true) author = t_author;
				else{
					author = author+" ; "+t_author;
				}
			}
		}
		if(tag245_e != null){
			for(String t_author : tag245_e){
				if(author.equals("") == true) author = t_author;
				else{
					author = author+" ; "+t_author;
				}
			}
		}
		author = author.trim();
		if(isPuncMark(author) == true){
			author = author.substring(0,author.length()-1);
		}
		if(author.trim().equals("") == true) author = null;
		return author;
	}

	@Override
	public String makePUBLISHER(MarcStru marcStru)
	{
		String publisher = "";
		String [] tag260_b = marcStructService.getMultiSubFieldData(260, 'b', marcStru);
		if(tag260_b != null){
			for(String t_publisher : tag260_b){
				if(publisher.equals("") == true)publisher = t_publisher;
				else publisher = publisher + ":" + t_publisher;
			}
		}
		if(publisher.trim().equals("") == true) publisher = null;
		return publisher;
	}

	@Override
	public String makePUB_YEAR(MarcStru marcStru)
	{
		String pub_year = "";
		String [] tag260_c = marcStructService.getMultiSubFieldData(260, 'c', marcStru);

		if(tag260_c != null){
			pub_year = tag260_c[0];
		}
		if(pub_year.trim().equals("") == true) pub_year = null;
		return pub_year;
	}

	@Override
	public String makeST_CODE(MarcStru marcStru)
	{
		String isbn = "";
		List<MarcFieldElement> list = marcStructService.getMultiFieldElement(20, ' ', ' ', marcStru);
		for(MarcFieldElement item : list) {
			List<MarcSubFieldElement> subFieldList = item.getSubFieldList();
			for(MarcSubFieldElement sub_item : subFieldList) {
				if(sub_item.getSbCode().equals("a") == true ) {
					isbn = sub_item.getSubFieldData();		
				}
				if(isbn.equals("") == false) break;
			}
			
			if(isbn.equals("") == false) break;
		}
		//String [] tag020_a = marcStructService.getMultiSubFieldData(20, 'a', marcStru);
//		if(tag020_a != null){
//			isbn = tag020_a[0];
//		}
		isbn = isbn.contains("(") == true ? stringUtil.getKeyword(isbn).substring(0, isbn.indexOf("(")) : stringUtil.getKeyword(isbn);
		if(isbn.trim().equals("") == true) isbn = null;
		return isbn;
	}

	@Override
	public String makeST_ISSN(MarcStru marcStru)
	{
		String issn = "";
		String [] tag022_a = marcStructService.getMultiSubFieldData(22, 'a', marcStru);

		if(tag022_a != null){
			issn = tag022_a[0];
		}
		issn = stringUtil.getKeyword(issn);
		if(issn.trim().equals("") == true) issn = null;
		return issn;
	}

	@Override
	public String makeST_STRN(MarcStru marcStru)
	{
		String strn = "";
		String [] tag027_a = marcStructService.getMultiSubFieldData(27, 'a', marcStru);

		if(tag027_a != null){
			strn = tag027_a[0];
		}
		strn = stringUtil.getKeyword(strn);
		if(strn.trim().equals("") == true) strn = null;
		return strn;
	}

	@Override
	public String makeST_RNSTRN(MarcStru marcStru)
	{
		String rnstrn = "";
		String [] tag088_a = marcStructService.getMultiSubFieldData(88, 'a', marcStru);

		if(tag088_a != null){
			rnstrn = tag088_a[0];
		}
		rnstrn = stringUtil.getKeyword(rnstrn);
		if(rnstrn.trim().equals("") == true) rnstrn = null;
		return rnstrn;
	}

	@Override
	public String makeST_CBN(MarcStru marcStru)
	{
		String cbn = "";
		String [] tag015_a = marcStructService.getMultiSubFieldData(15, 'a', marcStru);

		if(tag015_a != null){
			cbn = tag015_a[0];
		}
		cbn = stringUtil.getKeyword(cbn);
		if(cbn.trim().equals("") == true) cbn = null;
		return cbn;
	}

	@Override
	public String makeST_CAN(MarcStru marcStru)
	{
		String can = "";
		String [] tag017_a = marcStructService.getMultiSubFieldData(17, 'a', marcStru);

		if(tag017_a != null){
			can = tag017_a[0];
		}
		can = stringUtil.getKeyword(can);

		if(can.trim().equals("") == true) can = null;
		return can;
	}

	@Override
	public String makeSE_SHELF_CODE(MarcStru marcStru)
	{
		String strIndexItem = "";
		MarcFieldElement tag49 = marcStructService.getSingleMarcFieldElement(49, marcStru);

		if(tag49 != null){
			List<MarcSubFieldElement> subFieldList = tag49.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("f") )
				{
					strIndexItem = subField.getSubFieldData();
					break;
				}
			}
		}

		if(strIndexItem == null || strIndexItem.equals("") == true) return null;
		return strIndexItem;
	}

	@Override
	public String makeCLASS_NO(MarcStru marcStru)
	{
		return marcStructService.getSubFieldData(90, 'a', marcStru);
	}

	@Override
	public String makeBOOK_CODE(MarcStru marcStru)
	{
		return marcStructService.getSubFieldData(90, 'b', marcStru);
	}

	@Override
	public String makeVOL_CODE(MarcStru marcStru)
	{
		String vol_code =  marcStructService.getSubFieldData(90, 'c', marcStru);
		return vol_code;
	}

	@Override
	public String makeVOL_CODE_DISP(MarcStru marcStru)
	{
		return marcStructService.getSubFieldData(90, 'c', marcStru);
	}

	@Override
	public String makeCONTROL_NO(MarcStru marcStru)
	{
		String tag_001 = marcStructService.getSingleFieldData(1, marcStru);
		if(tag_001 == null) return null;
		if(tag_001 != null) tag_001 = stringUtil.getKeyword(tag_001);
		if(tag_001 == null) return null;
		if(tag_001.trim().equals("") == true) tag_001 = null;
		return tag_001;
	}

	public String makeKDCP_CLASS(MarcStru marcStru) {

		String strIndexItem = "";
		MarcFieldElement tag085 = marcStructService.getSingleMarcFieldElement(85, marcStru);

		if(tag085 != null){
			List<MarcSubFieldElement> subFieldList = tag085.getSubFieldList();

			String firstIndicator = tag085.getFIndicator();

			for(MarcSubFieldElement subField : subFieldList){

				if(firstIndicator.equals("0") == true ){
					if (subField.getSbCode().equals("a") )
					{
						strIndexItem = subField.getSubFieldData();
						if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);
						strIndexItem = stringUtil.getKeyword(strIndexItem);
						break;
					}
				}

			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;

	}
	public String makeKDC_CLASS (MarcStru marcStru) {

		String strIndexItem = "";

		String[] tag_056_a = marcStructService.getMultiSubFieldData(56, 'a', marcStru);
		if(tag_056_a != null){
			for(String t : tag_056_a){
				if(strIndexItem.equals("") == true ){
					strIndexItem = t;
				}else{
					strIndexItem+= " "+t;
				}
			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;

	}
	public String makeDDC_CLASS (MarcStru marcStru) {

		String strIndexItem = "";

		String[] tag_082_a = marcStructService.getMultiSubFieldData(82, 'a', marcStru);
		if(tag_082_a != null){
			for(String t : tag_082_a){
				if(strIndexItem.equals("") == true ){
					strIndexItem = t;
				}else{
					strIndexItem+= " "+t;
				}
			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;
	}
	public String makeCEC_CLASS (MarcStru marcStru) {
		String strIndexItem = "";
		MarcFieldElement tag085 = marcStructService.getSingleMarcFieldElement(85, marcStru);

		if(tag085 != null){
			List<MarcSubFieldElement> subFieldList = tag085.getSubFieldList();

			String firstIndicator = tag085.getFIndicator();

			for(MarcSubFieldElement subField : subFieldList){

				if(firstIndicator.equals("2") == true ){
					if (subField.getSbCode().equals("a") )
					{
						strIndexItem = subField.getSubFieldData();
						if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);

						break;
					}
				}

			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;

	}
	public String makeCWC_CLASS (MarcStru marcStru) {
		String strIndexItem = "";
		MarcFieldElement tag085 = marcStructService.getSingleMarcFieldElement(85, marcStru);

		if(tag085 != null){
			List<MarcSubFieldElement> subFieldList = tag085.getSubFieldList();

			String firstIndicator = tag085.getFIndicator();

			for(MarcSubFieldElement subField : subFieldList){

				if(firstIndicator.equals("3") == true ){
					if (subField.getSbCode().equals("a") )
					{
						strIndexItem = subField.getSubFieldData();
						if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);

						break;
					}
				}

			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;
	}

	public String makeCOC_CLASS (MarcStru marcStru) {
		String strIndexItem = "";
		MarcFieldElement tag085 = marcStructService.getSingleMarcFieldElement(85, marcStru);

		if(tag085 != null){
			List<MarcSubFieldElement> subFieldList = tag085.getSubFieldList();

			String firstIndicator = tag085.getFIndicator();

			for(MarcSubFieldElement subField : subFieldList){

				if(firstIndicator.equals("4") == true ){
					if (subField.getSbCode().equals("a"))
					{
						strIndexItem = subField.getSubFieldData();
						if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);

						break;
					}
				}

			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;

	}
	public String makeUDC_CLASS (MarcStru marcStru) {


		String strIndexItem = "";

		String[] tag_080_a = marcStructService.getMultiSubFieldData(80, 'a', marcStru);
		if(tag_080_a != null){
			strIndexItem = tag_080_a[0];

			strIndexItem = stringUtil.getKeyword(strIndexItem);
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;
	}

	public String makeNDC_CLASS (MarcStru marcStru) {
		String strIndexItem = "";
		MarcFieldElement tag085 = marcStructService.getSingleMarcFieldElement(85, marcStru);

		if(tag085 != null){
			List<MarcSubFieldElement> subFieldList = tag085.getSubFieldList();

			String firstIndicator = tag085.getFIndicator();

			for(MarcSubFieldElement subField : subFieldList){

				if(firstIndicator.equals("1") == true ){
					if (subField.getSbCode().equals("a"))
					{
						strIndexItem = subField.getSubFieldData();
						if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);

						break;
					}
				}

			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;

	}
	public String makeLC_CLASS  (MarcStru marcStru) {

		String strIndexItem = "";
		MarcFieldElement tag085 = marcStructService.getSingleMarcFieldElement(85, marcStru);

		if(tag085 != null){
			List<MarcSubFieldElement> subFieldList = tag085.getSubFieldList();

			String firstIndicator = tag085.getFIndicator();

			for(MarcSubFieldElement subField : subFieldList){

				if(firstIndicator.equals("7") == true ){
					if (subField.getSbCode().equals("a") )
					{
						strIndexItem = subField.getSubFieldData();
						if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);

						break;
					}
				}

			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;

	}

	public String makeETC_CLASS   (MarcStru marcStru) {

		String strIndexItem = "";
		MarcFieldElement tag085 = marcStructService.getSingleMarcFieldElement(85, marcStru);

		if(tag085 != null){
			List<MarcSubFieldElement> subFieldList = tag085.getSubFieldList();

			String firstIndicator = tag085.getFIndicator();

			for(MarcSubFieldElement subField : subFieldList){

				if(firstIndicator.equals(" ") == true ){
					if (subField.getSbCode().equals("a") )
					{
						strIndexItem = subField.getSubFieldData();
						if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);

						break;
					}
				}

			}
		}
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;

	}

	@Override
    public String makeUNI_CODE   (MarcStru marcStru)
    {
		String item = marcStructService.getSubFieldDataUseAlias("008@26-27" , marcStru);
		if(item == null) return null;
		if(item.trim().equals("") == true) item = null;
		return item;
	}

	@Override
    public String makeGOV_CODE   (MarcStru marcStru)
	{
		String item = marcStructService.getSubFieldDataUseAlias("008@38-39" , marcStru);
		if(item == null) return null;
		if(item.trim().equals("") == true) item = null;
		return item;
	}

	@Override
	public String makeDEG_CODE(MarcStru marcStru){
		String strIndexItem = "";
		String strTemp1 = "";
		String strTemp2 = "";
		//////////////////////////////////////////////////////////////////////
		// 041
		MarcFieldElement tag502 = marcStructService.getSingleMarcFieldElement(502, marcStru);

		if(tag502 != null){
			List<MarcSubFieldElement> subFieldList = tag502.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("c") )
				{
					strTemp1 = subField.getSubFieldData();
					if(isPuncMark(strTemp1) == true ) strTemp1 = strTemp1.substring(0,strTemp1.length()-1);

					strTemp1 = strTemp1.toUpperCase();
					strTemp1 = strTemp1.replaceAll("\\(", "");
					strTemp1 = strTemp1.replaceAll("\\)", "");
					strTemp1 = strTemp1.trim();
					if(strIndexItem.equals("") == true){
						strIndexItem = strTemp1;
					}else{
						strIndexItem += " " + strTemp1;
					}

					strTemp2 = strTemp1;
					strTemp2 = strTemp2.replaceAll(" ", "");

					strIndexItem += " " + strTemp2;

					int nFind = strTemp1.indexOf(" ");
					if (nFind > 0)
					{
						strIndexItem += " " + strTemp1.substring(0,nFind);
						strIndexItem += " " + strTemp1.substring(nFind + 1);
					}

				}
			}
		}

		if(strIndexItem.trim().equals("") == true) strIndexItem = null;

		return strIndexItem;

	}

	@Override
    public String makeEDIT       (MarcStru marcStru)      {
		String strIndexItem = "";
		MarcFieldElement tag250 = marcStructService.getSingleMarcFieldElement(250, marcStru);

		if(tag250 != null){
			List<MarcSubFieldElement> subFieldList = tag250.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("a") )
				{
					strIndexItem = subField.getSubFieldData();
					if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);
					strIndexItem = stringUtil.getKeyword(strIndexItem);
					break;
				}
			}
		}

		if(strIndexItem.trim().equals("") == true) strIndexItem = null;

		return strIndexItem;
	}

	@Override
    public String makeTEXT_LANG  (MarcStru marcStru)      {

		String strIndexItem = "";
		String strTemp1 = "";
		String strTemp2 = "";
		List<String> list = new ArrayList<String>();
		//////////////////////////////////////////////////////////////////////
		// 041
		MarcFieldElement tag041 = marcStructService.getSingleMarcFieldElement(41, marcStru);

		if(tag041 != null){
			List<MarcSubFieldElement> subFieldList = tag041.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("a") )
				{
					strTemp1 = subField.getSubFieldData();
					if(isPuncMark(strTemp1) == true ) strTemp1 = strTemp1.substring(0,strTemp1.length()-1);

					strTemp1 = strTemp1.toLowerCase();
					strTemp1 = strTemp1.replaceAll(" ", "");
					strTemp1 = strTemp1.trim();

					if(list.contains(strTemp1) == false ){
						list.add(strTemp1);
					}
				}
			}
		}

		// 008@35-37
		strTemp2 = marcStructService.getSubFieldDataUseAlias("008@35-37" , marcStru);
		if(strTemp2 != null){
			strTemp2 = strTemp2.toLowerCase();
			strTemp2 = strTemp2.trim();

			if(list.contains(strTemp2) == false ){
				list.add(strTemp2);
			}
		}

		if(list.size()==0) return null;

		for(String item : list ){
			if(strIndexItem.equals("") == true) strIndexItem = item;
			else strIndexItem+= " "+item;
		}

		return strIndexItem;
	}

	@Override
    public String makeSUM_LANG   (MarcStru marcStru)      {

		String strIndexItem = "";
		String strTemp1 = "";
		String strTemp2 = "";

		MarcFieldElement tag041 = marcStructService.getSingleMarcFieldElement(41, marcStru);

		if(tag041 != null){
			List<MarcSubFieldElement> subFieldList = tag041.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("b") )
				{
					strTemp1 = subField.getSubFieldData();
					if(isPuncMark(strTemp1) == true ) strTemp1 = strTemp1.substring(0,strTemp1.length()-1);

					strTemp1 = strTemp1.toUpperCase();
					strTemp1 = strTemp1.replaceAll(" ", "");
					strTemp1 = strTemp1.trim();


					int n = 0;
					int nCount = strTemp1.length();
					for (int i = 0; i < nCount; i++)
					{
						strTemp2 +=	String.valueOf(strTemp1.charAt(i));
						n++;

						if (n % 3 == 0 && i + 1 < nCount)
							strTemp2 += ",";
					}

				}

				if (strTemp2.equals("") == false)
					strIndexItem += " " + strTemp2;
			}
		}

		if(strIndexItem.trim().equals("") == true) strIndexItem = null;

		return strIndexItem;
	}

	@Override
	public String makePUBLISH_COUNTRY_CODE(MarcStru marcStru)      {
		String strIndexItem = "";

		strIndexItem = marcStructService.getSubFieldDataUseAlias("008@15-17" , marcStru);

		if(strIndexItem == null) return null;
		if(strIndexItem.trim().equals("") == true) strIndexItem = null;

		return strIndexItem;


	}
	@Override
	public String makeEdit_no(MarcStru marcStru)
	{
		String strIndexItem = "";
		MarcFieldElement tag260 = marcStructService.getSingleMarcFieldElement(260, marcStru);

		if(tag260 != null){
			List<MarcSubFieldElement> subFieldList = tag260.getSubFieldList();

			for(MarcSubFieldElement subField : subFieldList){

				if (subField.getSbCode().equals("f") )
				{
					strIndexItem = subField.getSubFieldData();
					if(isPuncMark(strIndexItem) == true ) strIndexItem = strIndexItem.substring(0,strIndexItem.length()-1);
					break;
				}
			}
		}

		if(strIndexItem.trim().equals("") == true) strIndexItem = null;
		return strIndexItem;
	}
	


	private String removePuncMarc(String item ){

		if(item==null || item.length() == 0 ) return null;

		if(isPuncMark(item) == true ) //구둣점 제거
			item = item.substring(0,item.length()-1);

		return item;
	}
	private boolean isPuncMark(String targetData)
	{
		if(targetData != null && targetData.equals("") == false && targetData.equals(" ") == false)
		{
			//-----------------------------------------------------------------------------------
			//SubFieldData 에 마지막 자리에 문자를 추출
			//-----------------------------------------------------------------------------------
			char convertChar = targetData.substring(targetData.length()-1, targetData.length()).charAt(0);

			//-----------------------------------------------------------------------------------
			//구두점에 해당하는 경우 삭제한다.
			//-----------------------------------------------------------------------------------
			if( convertChar == ';' || convertChar == ':' ||
				convertChar == '/' || convertChar == '-' ||
				convertChar == '=' || convertChar == ',' || convertChar == '.' ||
				convertChar == '+' || convertChar == '%' )
			{
				return true;
			}
		}
		return false;
	}

	private void remove_duplication_word(StringBuffer indexItem){
		//중복 제거
		List<String> indexWord = new ArrayList<String>();
		if(indexItem.toString().equals("") == false){
			String[] temp_array = indexItem.toString().split(" ");
			for(String t : temp_array){
				t = t.trim();
				if(t.equals("") == true ) continue;
				if(indexWord.contains(t) == false) indexWord.add(t);
			}

			indexItem.setLength(0);
			for(String t:indexWord) {

				if(indexItem.length() == 0){
					indexItem.append(t);
				}else{
					indexItem.append(" "+t);
				}
			}
		}
	}


	private  String removePunctuationMark(String subFieldData){
		if( subFieldData == null || subFieldData.length() < 0 )
			return null;

		if( subFieldData.length() == 0 )
			return subFieldData;

		// 서브 필드데이터의 맨마지막이 구두점이면 제거한다.
		char endChar = subFieldData.charAt(subFieldData.length()-1);

		if( endChar == ';' || endChar == ':' ||
			endChar == '/' || endChar == '-' ||
			endChar == '=' || endChar == ',' ||
			endChar == '+' || endChar == '%' )
		{
			subFieldData = subFieldData.substring(0, subFieldData.length()-1);
		}

		return subFieldData;
	}


	private String overlapDataRemove(String[]targetStrs)
	{
		boolean isFirst = true;
		String resultData = "";

		LinkedList <String>overlapRomoveList = new LinkedList<String>();

		if(targetStrs != null)
		{
			for(int idx = 0; idx < targetStrs.length; idx++ )
			{
				if(targetStrs[idx]!= null && (targetStrs[idx].trim().equals("") == false))
				{
					boolean check = overlapRomoveList.contains(targetStrs[idx]);

					if( check == false )
					{
						overlapRomoveList.add(targetStrs[idx]);

						if( isFirst == true )
						{
							resultData += targetStrs[idx].trim();
							isFirst = false;
						}
						else
						{
							resultData += " " + targetStrs[idx].trim();
						}
					}//if
				}//if
			}//for
		}
		else
		{
			return null;
		}
		return resultData;
	}

	private String fromArrayToBlankString(String[] array)
	{
		if(array == null)
			return "";
		else
		{
			StringBuffer blankBuffer = new StringBuffer();

			for(int i = 0; i < array.length; i++)
			{
				if(array[i] != null && array[i].equals("") == false)
				{
					blankBuffer.append(array[i]);
				}
				blankBuffer.append(" ");
			}
			return blankBuffer.toString().trim();
		}
	}

	/**
	 * 시작단어에 ( 로 시작하는 검색어의 색인어 잡는 방식
	 * @param keyword 검색어
	 * @param keyword2 검색어2 시작하는 ()안의 데이터를 제거한 검색어
	 * @param duplicateCheckList
	 */
	private void IDX_WORD(String keyword ,List<String> duplicateCheckList){
		// 뛰어쓰기 단위의 단어들을 색인으로 잡는것
		String strTemp3 = keyword;
		//괄호가 있던 없던 괄호를 뛰어쓰기처럼 여기고 , 이중뛰어쓰기는 뛰어쓰기로 변경
		strTemp3 = strTemp3.replaceAll("\\(", " ");
		strTemp3 = strTemp3.replaceAll("\\)", " ");
		strTemp3 = strTemp3.replaceAll("  ", " ");
		strTemp3 = strTemp3.trim();

		String[] strTemp3_array = strTemp3.split(" ");
		List<String> wordList = new ArrayList<String>();
		for(String word : strTemp3_array){
			if(word.equals("") == true) continue;
			wordList.add(word);
		}

		String t_word="";
		//전체단어 , 중간단어검색을 위한 키워드 입력 [마지막 단어는 제외 가각의 단어입력에서 들어감]
		for(int i=0; i < wordList.size()-1; i++ ){
			t_word = "";
			for(int j=i; j < wordList.size(); j++ ){
				t_word+= wordList.get(j);
			}

			t_word = stringUtil.getKeyword(t_word);
			if(duplicateCheckList.contains(t_word) == false)	duplicateCheckList.add(t_word);

		}

		//각각의 단어 입력
		for(int i=0; i <wordList.size(); i++ ){
			t_word = wordList.get(i);
			t_word = stringUtil.getKeyword(t_word);
			if(duplicateCheckList.contains(t_word) == false)	duplicateCheckList.add(t_word);
		}

//		keyword = stringUtil.getKeyword(keyword);  // 전체 단어 다 붙인것 색인으로 잡는것
//		keyword2 = stringUtil.getKeyword(keyword2);  // 괄호를 제거 하고 다 붙인것 색인으로 잡는것
//		if(duplicateCheckList.contains(keyword) == false)	duplicateCheckList.add(keyword);
//		if(duplicateCheckList.contains(keyword2) == false)	duplicateCheckList.add(keyword2);

	}

	/**
	 * 복본서명검색용 시작단어에 ( 로 시작하는 검색어의 색인어 잡는 방식
	 * @param keyword 검색어
	 * @param keyword2 검색어 시작하는 ()안의 데이터를 제거한 검색어
	 * @param duplicateCheckList
	 */
	private void ContainParenthesisIndexWordForDup(String keyword ,String keyword2 , List<String> duplicateCheckList){
		// 뛰어쓰기 단위의 단어들을 색인으로 잡는것
		keyword = stringUtil.getKeyword(keyword);  // 전체 단어 다 붙인것 색인으로 잡는것
		keyword2 = stringUtil.getKeyword(keyword2);  // 괄호를 제거 하고 다 붙인것 색인으로 잡는것
		if(duplicateCheckList.contains(keyword) == false)	duplicateCheckList.add(keyword);
		if(duplicateCheckList.contains(keyword2) == false)	duplicateCheckList.add(keyword2);

	}

	/**
	 * 복본서명검색용  시작단어에 ( 로 시작하지 않는 검색어의 색인어 잡는 방식
	 * @param keyword 검색어
	 * @param duplicateCheckList 검색어 리스트
	 */
	private void Non_ContainParenthesisIndexWordForDup(String keyword , List<String> duplicateCheckList){
		// 뛰어쓰기 단위의 단어들을 색인으로 잡는것
		keyword = stringUtil.getKeyword(keyword);
		if(duplicateCheckList.contains(keyword) == false)	duplicateCheckList.add(keyword);
	}
}
