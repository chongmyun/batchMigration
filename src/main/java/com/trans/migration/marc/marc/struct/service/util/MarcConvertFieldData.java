/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */
package  com.trans.migration.marc.marc.struct.service.util;

import com.trans.migration.marc.marc.struct.service.config.PuncMarkInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * MARC 데이터 245, 260, 300 TAG 데이터의 식별기호 구두점 추가기능을 제공한다.<br>
 * [구두점 추가기능 적용대상 정보]<br>
 * 245  a b(:) x(=) n(,) p(,) d(/) e(;)<br>
 * 260  a b(:) c(,)<br>
 * 300  a b(:) c(;) e(%)<br>
 *
 * @author Written By 이태일 【taeil.lee@eco.co.kr】
 */
@Component("MarcConvertFieldData")
public class MarcConvertFieldData {
//	private Logger logger = LoggerFactory.getLogger(this.getClass());

	protected String IDENTIFIER	= "";
	
	
	@Resource(name = "PuncMarkInfo")
	protected PuncMarkInfo puncMarkInfo;

   /**
	* MARC 데이터의 245Tag 식별기호 앞에 해당 구두점을 추가한다.
	* 식별기호 'a' 또는 첫번째 나오는 서브필드에는 구두점을 추가 하지 않는다.
	* @param sbCode 식별기호
	* @param marcFieldData 필드데이터
	* @return 구두점 추가 완료데이터
	*/
	public String convertData245(char sbCode , boolean puncMarkFlag , String marcFieldData )
	{
		StringBuffer marcFieldBuffer = new StringBuffer();

		//---------------------------------------------------------------------------------------
		//해당 식별기호에 따른 구두점을 추가한다. [ 구두점 + 식별기호코드 + 식별기호 + 서브필드 데이터 ]
		//---------------------------------------------------------------------------------------
		switch(sbCode)
		{
			case 'a':
			{
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(removeLastClassWord(marcFieldData));
				return marcFieldBuffer.toString();
			}
			case 'x':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append("=");
				}

				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(removeLastClassWord(marcFieldData));

				return marcFieldBuffer.toString();
			}
			case 'b':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append(":");
				}
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(removeLastClassWord(marcFieldData));

				return marcFieldBuffer.toString();
			}
			case 'n':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append(".");
				}
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(removeLastClassWord(marcFieldData));

				return marcFieldBuffer.toString();
			}
			case 'p':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append(",");
				}

				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(removeLastClassWord(marcFieldData));

				return marcFieldBuffer.toString();
			}
			case 'd':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append("/");
				}
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(removeLastClassWord(marcFieldData));

				return marcFieldBuffer.toString();
			}
			case 'e':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append(";");
				}
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(marcFieldData);
				return marcFieldBuffer.toString();
			}
			default:
			{
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(marcFieldData);
				return marcFieldBuffer.toString();
			}
		}
	}

	/**
	 * 서브필드데이터를 생성한다.
	 * @param sbCode
	 * @param puncMarkFlag
	 * @param puncmark
	 * @param marcFieldData
	 * @return 245 서브필드 데이터
	 */
	public String convertData245(char sbCode,boolean puncMarkFlag, String puncmark, String marcFieldData )
	{
		StringBuffer marcFieldBuffer = new StringBuffer();

		if(puncmark == null || puncmark.equals("") == true || puncmark.length()>1){
			return convertData245(sbCode , puncMarkFlag , marcFieldData );
		}

		if(puncMarkFlag == false){
			marcFieldBuffer.append(puncmark);
		}
		marcFieldBuffer.append(IDENTIFIER);
		marcFieldBuffer.append(sbCode);
		marcFieldBuffer.append(marcFieldData);
		return marcFieldBuffer.toString();
	}

   /**
	* MARC 데이터의 260Tag 식별기호 앞에 해당 구두점을 추가한다.
	* 식별기호 'a' 또는 첫번째 나오는 서브필드에는 구두점을 추가 하지 않는다.
	* @param sbCode 식별기호
	* @param puncMarkFlag 구두점 여부
	* @param marcFieldData 필드데이터
	* @return 구두점 추가 완료데이터
	*/
	public String convertData260(char sbCode ,boolean puncMarkFlag , String marcFieldData )
	{
		StringBuffer marcFieldBuffer = new StringBuffer();

		//---------------------------------------------------------------------------------------
		//해당 식별기호에 따른 구두점을 추가한다. [ 구두점 + 식별기호코드 + 식별기호 + 서브필드 데이터 ]
		//---------------------------------------------------------------------------------------
		switch(sbCode)
		{
			case 'a':
			{
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(marcFieldData);

				return marcFieldBuffer.toString();
			}
			case 'b':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append(":");
				}
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(marcFieldData);

				return marcFieldBuffer.toString();
			}
			case 'c':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append(",");
				}
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(marcFieldData);

				return marcFieldBuffer.toString();
			}
			default:
			{
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);
				marcFieldBuffer.append(marcFieldData);

				return marcFieldBuffer.toString();
			}
		}
	}

   /**
	* MARC 데이터의 300Tag 식별기호 앞에 해당 구두점을 추가한다.
	* 식별기호 'a' 또는 첫번째 나오는 서브필드에는 구두점을 추가 하지 않는다.
	* @param sbCode 식별기호
	* @param puncMarkFlag 구두점 여부
	* @param marcFieldData 필드데이터
	* @return 구두점 추가 완료데이터
	*/
	public String convertData300(char sbCode , boolean puncMarkFlag , String marcFieldData )
	{
		StringBuffer marcFieldBuffer = new StringBuffer();

		//---------------------------------------------------------------------------------------
		//해당 식별기호에 따른 구두점을 추가한다. [ 구두점 + 식별기호코드 + 식별기호 + 서브필드 데이터 ]
		//---------------------------------------------------------------------------------------
		switch(sbCode)
		{
			case 'a':
			{
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);

				puncMarkFlag = isPuncMark(marcFieldData) == true ? true : false;

				marcFieldBuffer.append(marcFieldData);

				return marcFieldBuffer.toString();
			}
			case 'b':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append(":");
				}
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);

				puncMarkFlag = isPuncMark(marcFieldData) == true ? true : false;

				marcFieldBuffer.append(marcFieldData);

				return marcFieldBuffer.toString();
			}
			case 'c':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append(";");
				}
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);

				puncMarkFlag = isPuncMark(marcFieldData) == true ? true : false;

				marcFieldBuffer.append(marcFieldData);

				return marcFieldBuffer.toString();
			}
			case 'e':
			{
				if(puncMarkFlag == false)
				{
					marcFieldBuffer.append("+");
				}
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);

				puncMarkFlag = isPuncMark(marcFieldData) == true ? true : false;

				marcFieldBuffer.append(marcFieldData);

				return marcFieldBuffer.toString();
			}
			default:
			{
				marcFieldBuffer.append(IDENTIFIER);
				marcFieldBuffer.append(sbCode);

				puncMarkFlag = isPuncMark(marcFieldData) == true ? true : false;

				marcFieldBuffer.append(marcFieldData);

				return marcFieldBuffer.toString();
			}
		}
	}

   /**
    *
	* @param  tagNo 태그번호
	* @param  sbCode 식별기호
	* @param  marcFieldData 구두점 추가 및 정렬대상 필드 데이터
	* @return 구두점 추가 및 정렬완료된 필드데이터
	*/
	public String convertMarcFieldData(Integer tagNo, char sbCode, String marcFieldData)
	{
		String return_data = "";
		if(tagNo == 245)
		{
			return_data =convertData245(sbCode , false , marcFieldData);
		}
		else if(tagNo == 260)
		{
			return_data =convertData260(sbCode , false , marcFieldData);
		}
		else if(tagNo == 300)
		{
			return_data =convertData300(sbCode , false , marcFieldData);
		}
		return return_data;
	}

	/**
	 * 입력한 서브필드 데이터에 입력한 구두점으로 서브필드를 추가한다.
	 * 구두점을 입력하지 않았거나 유효하지 않은 구두점이라면 기본구두점 추가함수를 호출해준다.
	 * @param tagNo
	 * @param sbCode
	 * @param puncmark
	 * @param marcFieldData
	 * @return 서브필드
	 */
	public String convertMarcFieldData(Integer tagNo, char sbCode ,String puncmark, String marcFieldData){

		if(tagNo == 245)
		{
			return convertData245(sbCode , false , puncmark , marcFieldData);
		}
		else if(tagNo == 260)
		{
			return convertData260(sbCode , false , marcFieldData);
		}
		else if(tagNo == 300)
		{
			return convertData300(sbCode , false , marcFieldData);
		}

		StringBuffer marcFieldBuffer = new StringBuffer();
		marcFieldBuffer.append(IDENTIFIER);
		marcFieldBuffer.append(sbCode);
		marcFieldBuffer.append(marcFieldData);
		return marcFieldBuffer.toString();

	}

   /**
	*
	* @param  tagno 필드 데이터의 태그번호
	* @param  subFieldCode 식별기호 코드
	* @param  oldSubData 식별기호 필드 원본데이터
	* @param  newSubData 식별기호 필드 수정데이터
	* @param  fieldData 식별기호 필드 데이터 연결문자열
	* @return 수정완료된 필드 데이터 문자열
	*/
	public String convertUpdate(int tagno,
			                    char subFieldCode,
			                    String oldSubData,
			                    String newSubData,
			                    String fieldData)
	{
		StringBuffer fieldDataBuffer = new StringBuffer();

		//-----------------------------------------------------------------------------------
		//식별기호 시작코드 Token
		//-----------------------------------------------------------------------------------
		String[] fieldDataList = fieldData.split(IDENTIFIER);

		//-----------------------------------------------------------------------------------
		//태그내의 SubFieldDta 와 식별기호를 추출한다.
		//-----------------------------------------------------------------------------------
		for(int i = 1; i < fieldDataList.length; i++)
		{
			String sbCode = fieldDataList[i].substring(0, 1);
			String data   = fieldDataList[i].substring(1);

			if(data.equals(oldSubData) == true)
			{
				if(isPuncMark(newSubData) == false && isPuncMark(data) == true)
				{
					String oldPuncMark = data.substring(data.length()-1, data.length());

					data = newSubData + oldPuncMark;
				}
				else
					data = newSubData;
			}
			else if(removeLastClassWord(data).equals(oldSubData) == true)
			{
				if(isPuncMark(newSubData) == false && isPuncMark(data) == true)
				{
					String oldPuncMark = data.substring(data.length()-1, data.length());

					data = newSubData + oldPuncMark;
				}
				else
					data = newSubData;
			}
			fieldDataBuffer.append(IDENTIFIER);
			fieldDataBuffer.append(String.valueOf(sbCode.charAt(0)));
			fieldDataBuffer.append(data);
		}
		String resultData = fieldDataBuffer.toString();

		resultData = removeFirstPuncMark(resultData);
		resultData = removeLastClassWord(resultData);

		return resultData;
	}

   /**
	* 구두점 제거대상 식별기호인 경우 구두점 제거후 반환 아닌경우 원본 데이터를 반환한다.
	* @param  targetStr 구두점 제거대상 데이터
	* @return 구두점 제거완료 데이터
	*/
	public String removeLastClassWord(String targetStr)
	{
		if(targetStr != null && targetStr.equals("") == false && targetStr.equals(" ") == false)
		{
			targetStr = targetStr.stripLeading();
			if ( isPuncMark(targetStr.charAt(targetStr.length()-1)) == true ) {
				return targetStr.substring(0, targetStr.length()-1);
			}
		}
		return targetStr;
	}

	/**
	 *
	 * @param targetStr
	 * @return
	 */
	private String removeFirstPuncMark(String targetStr)
	{
		if(targetStr != null && targetStr.equals("") == false && targetStr.equals(" ") == false)
		{
			targetStr = targetStr.stripLeading();

			if ( isPuncMark(targetStr.charAt(0)) == true ) {
				return targetStr.substring(1);
			}
		}
		return targetStr;
	}

   /**
	* 구두점이 존재하는지 확인한다.
	* @param  targetData 구두점 존재여부 대상 데이터
	* @return true , false
	*/
	private boolean isPuncMark(String targetData)
	{
		if(targetData != null && targetData.equals("") == false && targetData.equals(" ") == false)
		{
			targetData = targetData.stripLeading();

			if(isPuncMark( targetData.charAt(targetData.length()-1) ) == true ) {
				return true;
			}
		}
		return false;
	}

   /**
	* 구두점이 존재하는지 확인한다.
	* @param  convertChar 구두점 존재여부 대상 데이터
	* @return true , false
	*/
	private boolean isPuncMark(char convertChar)
	{
		if( convertChar == ';' || convertChar == ':' || convertChar == '/' || convertChar == '-' ||convertChar == '=' ||
			convertChar == ',' || convertChar == '+' || convertChar == '%' || convertChar == '.' || convertChar == ',' ) {
			return true;
		}

		return false;
	}

   /**
	* MARC 동기화 수행시 입력 및 수정된 필드 데이터를 식별기호 정의순서 대로 정렬하여 반환한다.
	* @param  tagnoData 정렬대상 태그번호
	* @param  fieldData 정렬대상 필드데이터
	* @param  crudFlag   입력, 수정, 삭제 구분
	* @return 정렬완료 필드데이터
	*/
	public String getMarcFieldSortData(Integer tagnoData, String fieldData, char crudFlag)
	{

		List<MarcSortTargetData> fieldDataList = new ArrayList<>();

		if(fieldData == null)
		{
			return fieldData;
		}
		else if(tagnoData != 245 && tagnoData != 260 && tagnoData != 300)
		{
			return fieldData;
		}
		else
		{
			String[] input_fieldDataList = fieldData.split(IDENTIFIER);
			List<String > fieldDataArrayList = new ArrayList<String>();
			for(int i = 0; i < input_fieldDataList.length; i++)
			{
				if(input_fieldDataList[i] == null || input_fieldDataList[i].equals("") == true || input_fieldDataList[i].length() == 0)
					continue;

				fieldDataArrayList.add(input_fieldDataList[i]);
			}


			for(int i = 0; i < fieldDataArrayList.size(); i++)
			{
				MarcSortTargetData sortTargetData = new MarcSortTargetData();

				String sbCode = fieldDataArrayList.get(i).substring(0, 1);
				String data   = fieldDataArrayList.get(i).substring(1);

				if(i == fieldDataArrayList.size()-1)
				{
					data = removeLastClassWord(data); //테그의 마지막 식별기호의 마지막 데이터는 구두점을 삭제한다.
				}

				if(data.length() >= 2)
				{
					String checkChar  = data.substring(data.length()-2);

					String firstChar  = checkChar.charAt(0) + "";
					String secondChar = checkChar.charAt(1) + "";

					if(isPuncMark(firstChar) == true && isPuncMark(secondChar) == true)
					{
						data = data.substring(0, data.length()-1);
					}
				}
				sortTargetData.setTagNo(tagnoData);
				sortTargetData.setSbCode(sbCode);
				sortTargetData.setFieldData(data);

				if(i == 0){
					sortTargetData.setPunkMarK("");
				}else{

					MarcSortTargetData prev_Data = fieldDataList.get(i-1);


					if(isPuncMark(prev_Data.getFieldData().charAt(prev_Data.getFieldData().length()-1)) ){
						sortTargetData.setPunkMarK(String.valueOf(prev_Data.getFieldData().charAt(prev_Data.getFieldData().length()-1)));

						prev_Data.setFieldData(prev_Data.getFieldData().substring(0, prev_Data.getFieldData().length()-1));

					}else{
						sortTargetData.setPunkMarK("");
					}
				}

				fieldDataList.add(sortTargetData);
			}

			StringBuffer resultBuf = new StringBuffer();

			String data = "";
			for(MarcSortTargetData subFieldData :  fieldDataList){


				if(      tagnoData.intValue() == 245
					&& (
						   ( subFieldData.getSbCode().charAt(0)=='e' && subFieldData.getPunkMarK().equals(",") == true  )
						|| ( subFieldData.getSbCode().charAt(0)=='p' && subFieldData.getPunkMarK().equals(".") == true  )
					) ){

					data = convertMarcFieldData( tagnoData, subFieldData.getSbCode().charAt(0), subFieldData.getPunkMarK() ,subFieldData.getFieldData() );

					//resultBuf.append(  convertMarcFieldData( tagnoData, subFieldData.getSbCode().charAt(0), subFieldData.getPunkMarK() ,subFieldData.getFieldData() ) );

				}else{

					data = convertMarcFieldData( tagnoData, subFieldData.getSbCode().charAt(0), subFieldData.getFieldData() );

					//resultBuf.append( convertMarcFieldData( tagnoData, subFieldData.getSbCode().charAt(0), subFieldData.getFieldData() ) );
				}

				//logger.debug(String.valueOf(tagnoData) +"\t\t"+ subFieldData.getSbCode()+ "\t\t" +subFieldData.getPunkMarK()+ "\t\t"+data );
				resultBuf.append(data);
				data = "";
			}

			return resultBuf.toString();



			//return executeSortFieldData(fieldDataList , tagnoData);
		}
	}

   /**
    *
	* @param  tagnoData 정렬대상 태그번호
	* @return 정렬완료 필드데이터
	*/
//	private String executeSortFieldData(List<MarcSortTargetData> fieldDataList, Integer tagnoData)
//	{
//		List<MarcSortTargetData> resultDataList= new ArrayList<MarcSortTargetData>();
//		List<PuncMarkTagInfo> puncMarkTagList = puncMarkInfo.getTaglist();
//
//		for( int i = 0; i < puncMarkTagList.size(); i++ )
//		{
//			PuncMarkTagInfo puncMarkTag = puncMarkTagList.get(i);
//
//			if(puncMarkTag.getApply().equals("false") == true)
//				break;
//			else
//			{
//				if(tagnoData.toString().equals(puncMarkTag.getNo()) == true)
//				{
//					for(int j = 0; j < puncMarkTag.getSbCodeList().size(); j++)
//					{
//						String sbCode = puncMarkTag.getSbCodeList().get(j);
//
//						for(int k = 0; k < fieldDataList.size(); k++)
//						{
//							String sbCodeEqualData = fieldDataList.get(k).getSbCode();
//
//							if(sbCodeEqualData.equals(sbCode) == true)
//							{
//								resultDataList.add(fieldDataList.get(k));
//							}
//						}
//					}
//				}
//				else
//					continue;
//			}
//		}
//
//		return makeResultFieldData(resultDataList , currentPunkMarcList , tagnoData);
//	}

   /**
	*
	* @param  tagnoData 정렬대상 태그번호
	* @return 정렬완료 필드데이터
	*/
//	private String makeResultFieldData(List<MarcSortTargetData> resultDataList ,List<String> punkMarcList , Integer tagnoData)
//	{
//
//		StringBuffer resultBuf = new StringBuffer();
//
//
//		String fieldData    = "";
//		String subFieldData = "";
//
//		fieldData =  "";
//
//		for(int i = 0; i < resultDataList.size(); i++)
//		{
//			fieldData = resultDataList.get(i).getFieldData();
//
//			if(tagnoData == 245 && resultDataList.get(i).getSbCode().charAt(0)=='e' && punkMarcList.get(i).equals(",") == true){
//				subFieldData = convertMarcFieldData(tagnoData, resultDataList.get(i).getSbCode().charAt(0), punkMarcList.get(i) ,fieldData);
//			}else{
//				subFieldData = convertMarcFieldData(tagnoData, resultDataList.get(i).getSbCode().charAt(0), fieldData);
//			}
//
//			resultBuf.append(subFieldData);
//			fieldData    = "";
//			subFieldData = "";
//		}
//
//		fieldData = resultBuf.toString();
//
//		fieldData = removeFirstPuncMark(fieldData);
//		fieldData = removeLastClassWord(fieldData);
//
//		return fieldData;
//	}


}
