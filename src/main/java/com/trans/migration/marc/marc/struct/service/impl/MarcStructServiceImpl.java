/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */
package com.trans.migration.marc.marc.struct.service.impl;

import com.trans.migration.marc.Message;
import com.trans.migration.marc.marc.struct.MarcFieldElement;
import com.trans.migration.marc.marc.struct.MarcStru;
import com.trans.migration.marc.marc.struct.MarcSubFieldElement;
import com.trans.migration.marc.marc.struct.service.MarcStructService;
import com.trans.migration.marc.marc.struct.service.config.MarcAlias;
import com.trans.migration.marc.marc.struct.service.config.MarcAliasItem;
import com.trans.migration.marc.marc.struct.service.util.MarcConvertFieldData;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;
/**
* 마크 매니져 구현객체
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
@Service("MarcStructService")
public class MarcStructServiceImpl implements MarcStructService {
//	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "MarcAlias")
	protected MarcAlias marcAlias;
	
	@Resource(name = "MarcConvertFieldData")
	protected MarcConvertFieldData marcConvertFieldData;
	
	@Resource(name = "Message")
	protected Message message;

	protected @Value("UTF-8") String charsetName;
	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	

	@Override
	public Map<String,String> readMarcStruFromStreamMarc(String marc , MarcStru marcStru) {
		Map<String,String> result = new HashMap<String,String>();

		if( marc == null )
		{
			result.put("status", "ERROR");
			result.put("statusDescription", "입력된 마크가 [NULL]입니다.");
			return result;
		}

		// 2-2. 입력된 마크의 길이가 0일 경우
		//  - 디폴트 마크를 생성 , - 디폴트 마크: 리더정보만 가지고 있는 마크
		if( marc.length() == 0 )
		{
			marcStru.setReader(this.getDefaultReader());

			result.put("status", "SUCCESS");
			result.put("statusDescription", "마크리더만 존재하는 마크객체를 생성했습니다.");
			return result;
		}

		// 2-3. 마크의 종단키호 체크
		int pos = marc.indexOf(RECORD_END);
		if( pos == -1 || pos != (marc.length()-1) )
		{
			result.put("status", "ERROR");
			result.put("statusDescription", "마크에 레코드 종단기호가 존재하지 않습니다.");
			return result;
		}

		// -----------------------------------------------------------------------
		// 3. 마크 데이터 분석/ 파싱
		// -----------------------------------------------------------------------
		// 3-1. 리더 파싱
		try {
			marcStru.setReader(marc.substring(0, Integer.parseInt(message.getMessage("MARC.READER_SIZE"))));
		} catch(StringIndexOutOfBoundsException e) {
			result.put("status", "ERROR");
			result.put("statusDescription", "마크에 리더 파싱중 오류가 발생했습니다. ");
			return result;
		}


		// 3-3. 필드데이터 파싱
		pos = marc.indexOf(FIELD_END);
		if( pos == -1 )
		{
			result.put("status", "SUCCESS");
			result.put("statusDescription", "리더만 존재하는 마크객체를 위한 공간으로 생각됨");
			return result;
		}

		String directory = marc.substring(Integer.parseInt(message.getMessage("MARC.READER_SIZE")), pos);
		// 2. check Directory
		if( directory.length() % Integer.parseInt(message.getMessage("MARC.DIRECTORY_ELEMENT_SIZE")) != 0 )
		{
			result.put("status", "ERROR");
			result.put("statusDescription", "마크의 Directory 부분이 DIRECTORY_ELEMENT_SIZE["+Integer.parseInt(message.getMessage("MARC.DIRECTORY_ELEMENT_SIZE"))+"]로 나누어 떨어지지 않습니다.");
			return result;
		}

		// 3. set directory element
		pos = 0;
		String element = "";
		String fieldLength = "";
		String fieldStartPos = "";
		String tagno = "";
		List<Map<String,Object>> directoryList = new ArrayList<Map<String,Object>>();
		while( directory.length() > pos )
		{
			//사용할 변수 초기화
			element       = "";
			fieldLength   = "";
			fieldStartPos = "";
			tagno         = "";

			element = directory.substring(pos, Integer.parseInt(message.getMessage("MARC.DIRECTORY_ELEMENT_SIZE")) + pos);
			tagno   = element.substring(0, Integer.parseInt(message.getMessage("MARC.TAG_NO_SIZE")));

			fieldLength	 = element.substring(Integer.parseInt(message.getMessage("MARC.TAG_NO_SIZE")), Integer.parseInt(message.getMessage("MARC.TAG_NO_SIZE")) + Integer.parseInt(message.getMessage("MARC.FIELD_LENGTH_SIZE")));
			fieldStartPos= element.substring(Integer.parseInt(message.getMessage("MARC.TAG_NO_SIZE")) + Integer.parseInt(message.getMessage("MARC.FIELD_LENGTH_SIZE")));

			if( isNumber(tagno) != true )
			{
				//setLastErrorMsg("디렉토리 정보중 TAG번호가 숫자가 아닌 다른 문자로 되있습니다.");
				result.put("status", "ERROR");
				result.put("statusDescription", "["+tagno+"]디렉토리 정보중 TAG번호가 숫자가 아닌 다른 문자로 되있습니다.");
				return result;
			}


			Map<String,Object> item = new HashMap<String,Object>();
			item.put("TAG_NO", Integer.parseInt(tagno));
			item.put("FIELD_LENGTH", fieldLength);
			item.put("FIELD_START_POS", fieldStartPos);

			directoryList.add(item);

			//marcStru.addDirectory(Integer.parseInt(tagno), fieldLength, fieldStartPos);
			pos += Integer.parseInt(message.getMessage("MARC.DIRECTORY_ELEMENT_SIZE"));
		}

		// 1. Data split
		String[] fieldList = marc.split(FIELD_END);

		// 2. check Data
		int fieldCount		= fieldList.length - 2;			// (-2) : Reader/Directory, Record-End
		int directoryCount	= directoryList.size();

		if( fieldCount <= 0 ){
			result.put("status", "SUCCESS");
			result.put("statusDescription", "리더만 존재하는 마크객체를 위한 공간으로 생각됨");
			return result;
		}

		if( fieldCount != directoryCount )
		{
			result.put("status", "ERROR");
			result.put("statusDescription", "디렉토리 정보와 필드데이터의 수가 일치하지 않습니다.");
			return result;
		}

		// 3. parse field data
		int i_tagno;
		for( int i = 0; i < fieldCount; i++ )
		{

			i_tagno = Integer.parseInt(directoryList.get(i).get("TAG_NO").toString());

			// 5. set field element
			if( i_tagno < 10 || i_tagno == 908 )
			{
				String fieldData = fieldList[i+1];
				marcStru.addField(i_tagno, "", "", "", fieldData);
			}
			else
			{
				String	fIndicator = "";
				String	sIndicator = "";
				String	fieldData = "";
				int len = fieldList[i+1].length();

				if( len > 2 ) {
					fIndicator	= fieldList[i+1].substring(0,1);
					sIndicator	= fieldList[i+1].substring(1,2);
					fieldData	= fieldList[i+1].substring(2);
				}

				if(i_tagno==245){
					marcStru.addField(i_tagno, fIndicator, sIndicator, "", fieldData);
				}else{
					marcStru.addField(i_tagno, fIndicator, sIndicator, "", fieldData);
				}


			}
		}

		result.put("status", "SUCCESS");
		result.put("statusDescription", "입력된 마크를 마크구조체에 저장했습니다.");
		return result;
	}

	
	private Map<String,String> getMo_MARC_PUNCTUATION_MARK(List<Map<String, String>> Mo_MARC_PUNCTUATION_MARK_List ,String tag_no, String sbCode) {
		if(Mo_MARC_PUNCTUATION_MARK_List == null || Mo_MARC_PUNCTUATION_MARK_List.isEmpty() == true) return null;
		
		for(Map<String, String> item : Mo_MARC_PUNCTUATION_MARK_List) {
			if(item.get("tagNo").toString().equals(tag_no) && item.get("sbCode").toString().equals(sbCode) ) {
				return item;
			}
		}
		return null;
	}
	
	@Override
	public Map<String,String> writeMarcStruToStreamMarc(MarcStru marcStru) {

		
		Map<String,String> result = new HashMap<String,String>();
		
		StringBuffer marcBuffer = new StringBuffer();

		// 1. check marc structure

		int fieldCount = marcStru.getFieldCount();

		// 2. newest marc structure
		newestMarcStructure(marcStru);

		// 3. write Reader
		String reader = marcStru.getReader();
		if( reader == null || reader.length() != Integer.parseInt(message.getMessage("MARC.READER_SIZE")) )
		{
			result.put("status", "ERROR");			result.put("statusDescription", "리더의 길이에 문제가 있습니다.");
			return result;
		}

		marcBuffer.append(reader);

		// 4. write directory
		int		tagno;
		String  fieldLength   = "";
		String  fieldStartPos = "";

		int i_fieldLength = 0;
		int i_StartPos    = 0;


		for( int i = 0; i < fieldCount; i++ )
		{
			MarcFieldElement field = marcStru.getField(i);
			tagno		 		= field.getTagno();

			//i_fieldLength  = getByteSize(field.toString(), this.charsetName)+FIELD_END.length()+2; 기존것 삭제 ... 2022-10-15년 코라스와 비교하여 디렉토리길이가 다르게 계산되는것 수정 +2는 왜 했지 .. ? 
			i_fieldLength  = getByteSize(field.toString(), this.charsetName)+FIELD_END.length(); //
			fieldLength    = String.format("%04d", i_fieldLength);
			fieldStartPos  = String.format("%05d", i_StartPos);
			i_StartPos = i_StartPos + i_fieldLength;

			marcBuffer.append(String.format("%03d", tagno)).append(fieldLength).append(fieldStartPos);

			if( i == (marcStru.getFieldCount()-1) ) 
				marcBuffer.append(FIELD_END);
		}

		// 5. write Field
		for( int i = 0; i < fieldCount; i++ )
		{
			marcBuffer.append(marcStru.getField(i).getFieldData()).append(FIELD_END);
		}

		// 6. write record-end
		marcBuffer.append(RECORD_END);

		result.put("status", "SUCCESS");
		result.put("statusDescription", "스트림마크를 생성했습니다.");
		result.put("streamMarc", marcBuffer.toString());

		return result;
	}

	@Override
	public Map<String,String> readMarcStruFromEditMarc(String editMarc , MarcStru marcStru){

		Map<String,String> result = new HashMap<String,String>();

		//2. 입력값 확인
		if( editMarc == null )
		{
			result.put("status", "ERROR");
			result.put("statusDescription", "입력된 Edit Marc가 [NULL]입니다.");
			return result;
		}

		//3. 기본 리더 생성
		marcStru.setReader(getDefaultReader());

		// 2. EditMarc split
		String[] fieldDatas;
		if( editMarc.indexOf("\r\n") != -1 )
			fieldDatas = editMarc.split("\r\n");
		else if( editMarc.indexOf("\n") != -1 )
			fieldDatas = editMarc.split("\n");
		else
		{
			// 레코드 종단기호만 입력된 에디트 마크의 경우
			if( editMarc.equals(RECORD_END) == true )
				fieldDatas = null;
			else
			{
				fieldDatas = new String[1];
				fieldDatas[0] = editMarc;
			}
		}

		// 3. delete Record-End
		if( fieldDatas != null ) {
			int lastIndex = fieldDatas.length-1;
			fieldDatas[lastIndex] = fieldDatas[lastIndex].substring(0, fieldDatas[lastIndex].length()-1);

			// 4. insert field data to Marc Structure
			int    tagno     = -1;
			String fieldData = null;
			for( int i = 0; i < fieldDatas.length; i++ )
			{
				tagno = Integer.parseInt(fieldDatas[i].substring(0, 3));

				// 5. TAG Separately Processing
				if( tagno == 49 || tagno == 653 )
				{
					StringBuffer buffer = new StringBuffer();
					buffer.append(fieldDatas[i].substring(3, fieldDatas[i].length()));

					for( ; i < (fieldDatas.length-1); i++ )
					{
						String temp = fieldDatas[i+1].substring(0, 3);
						if( temp.equals("   ") != true )
							break;

						buffer.append(fieldDatas[i+1].substring(5, fieldDatas[i+1].length()));
					}

					fieldData = buffer.toString();
					fieldData = fieldData.substring(0, fieldData.length()-1);
				}
				else
				{
					fieldData = fieldDatas[i].substring(3, fieldDatas[i].length()-1);
				}

				this.insertFieldData(tagno, fieldData, marcStru);
			}
		}else{
			result.put("status", "ERROR");
			result.put("statusDescription", "입력된 필드 데이터가 존재하지 않습니다.");
			return result;
		}

		result.put("status", "SUCCESS");
		result.put("statusDescription", "입력된 마크를 마크구조체에 저장했습니다.");
		return result;

	}

	@Override
	public Map<String,String> writeMarcStruToEditMarc(MarcStru marcStru) {

		Map<String,String> result = new HashMap<String,String>();

		int index049Tag = -1;
		StringBuffer editMarcBuffer = new StringBuffer();

		for( int index = 0; index < marcStru.getFieldCount(); index++ )
		{
			// 4. get FieldData
			MarcFieldElement element = marcStru.getField(index);

			// 5. make EditMarc
			// 5-1. make TAG Number
			int tagno = element.getTagno();

			// 5-2. 049 TAG Separately Processing
			if( tagno == 49 )
			{
				index049Tag = index;
				continue;
			}

			editMarcBuffer.append(String.format("%03d", tagno));
			if (tagno == 505 ){
				editMarcBuffer.append(convert505FieldData(element.getFieldData()));
			}else{
				editMarcBuffer.append(element.getFieldData());
			}

			// 5-4. make Field-End & Record-End
			editMarcBuffer.append(FIELD_END);
			if( index049Tag != -1 || index != (marcStru.getFieldCount()-1) )
				editMarcBuffer.append("\n");
		}

		// 5-6. 049 TAG Separately Processing
		if( index049Tag != -1 )
		{
			MarcFieldElement element = marcStru.getField(index049Tag);

			// TAG Number
			int tagno = element.getTagno();
			editMarcBuffer.append(String.format("%03d", tagno));

			// Field Data
			String fieldData  = element.getFieldData();
			editMarcBuffer.append(convert049FieldData(fieldData));
			editMarcBuffer.append(FIELD_END);
		}



		editMarcBuffer.append(RECORD_END);

		result.put("status", "SUCCESS");
		result.put("statusDescription", "에디트마크를 생성했습니다.");
		result.put("editMarc", editMarcBuffer.toString());
		return result;
	}

	@Override
	public Map<String,String> getEditMarcFromStreamMarc(String streamMarc , MarcStru marcStru){
		readMarcStruFromStreamMarc(streamMarc,marcStru);
		return writeMarcStruToEditMarc(marcStru);
	}

	@Override
	public Map<String,String> getStreamMarcFromEditMarc(String editMarc, MarcStru marcStru){
		readMarcStruFromEditMarc(editMarc , marcStru);
		return writeMarcStruToStreamMarc(marcStru);
	}

	@Override
	public String getSingleFieldData(int tagno , MarcStru marcStru){
		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			if( tagno == marcStru.getField(i).getTagno().intValue() )
			{
				return marcStru.getField(i).toString();
			}
		}
		return null;
	}

	@Override
	public MarcFieldElement getSingleFieldElement(int tagno , MarcStru marcStru) {
		MarcFieldElement fieldElement = null;
		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			if( tagno == marcStru.getField(i).getTagno().intValue() )
			{
				fieldElement = marcStru.getField(i);
				break;
			}
		}
		return fieldElement;
	}

	public MarcFieldElement getSingleFieldElement(int tagno ,char fIndicator, MarcStru marcStru) {
		MarcFieldElement fieldElement = null;
		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			if( tagno == marcStru.getField(i).getTagno().intValue() && marcStru.getField(i).getFIndicator().equals(String.valueOf(fIndicator)) == true )
			{
				fieldElement = marcStru.getField(i);
				break;
			}
		}
		return fieldElement;
	}

	public MarcFieldElement getSingleFieldElement(int tagno ,char fIndicator, char sIndicator , MarcStru marcStru) {
		MarcFieldElement fieldElement = null;
		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			if( tagno == marcStru.getField(i).getTagno().intValue()
					&& marcStru.getField(i).getFIndicator().equals(String.valueOf(fIndicator)) == true
					&& marcStru.getField(i).getSIndicator().equals(String.valueOf(sIndicator)) == true)
			{
				fieldElement = marcStru.getField(i);
				break;
			}
		}
		return fieldElement;
	}

	@Override
	public String getSubFieldData(int tagno, char subFieldCode , MarcStru marcStru)
	{
		MarcFieldElement fieldElement = getSingleFieldElement(tagno , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if(String.valueOf(subFieldCode).equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}

	@Override
	public String[]getMultiFieldData(int tagno , MarcStru marcStru) {
		ArrayList<String> fieldBuffer = new ArrayList<String>();

		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			if( tagno == marcStru.getField(i).getTagno().intValue() )
			{
				fieldBuffer.add( marcStru.getField(i).toString() );
			}
		}

		// 2. make field list
		if( fieldBuffer.size() > 0 )
		{
			String[] fieldData = new String[fieldBuffer.size()];
			for( int i = 0; i < fieldBuffer.size(); i++ )
				fieldData[i] = fieldBuffer.get(i);
			return fieldData;
		}

		return null;
	}

	@Override
	public List<MarcFieldElement> getMultiFieldElement(int tagno , MarcStru marcStru) {

		List<MarcFieldElement> fieldElementList = new ArrayList<MarcFieldElement>();

		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			if( tagno == marcStru.getField(i).getTagno().intValue() )
			{
				fieldElementList.add(marcStru.getField(i));

			}
		}
		return fieldElementList;
	}

	@Override
	public List<MarcFieldElement> getMultiFieldElement(int tagno , char fIndicator , MarcStru marcStru) {

		List<MarcFieldElement> fieldElementList = new ArrayList<MarcFieldElement>();

		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			if( tagno == marcStru.getField(i).getTagno().intValue()
				&& marcStru.getField(i).getFIndicator().equals(String.valueOf(fIndicator)) == true)
			{
				fieldElementList.add(marcStru.getField(i));

			}
		}
		return fieldElementList;
	}

	@Override
	public List<MarcFieldElement> getMultiFieldElement(int tagno ,char fIndicator, char sIndicator , MarcStru marcStru) {

		List<MarcFieldElement> fieldElementList = new ArrayList<MarcFieldElement>();

		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			if( tagno == marcStru.getField(i).getTagno().intValue()
				&& marcStru.getField(i).getFIndicator().equals(String.valueOf(fIndicator)) == true
				&& marcStru.getField(i).getSIndicator().equals(String.valueOf(sIndicator)) == true)
			{
				fieldElementList.add(marcStru.getField(i));

			}
		}
		return fieldElementList;
	}

	@Override
	public String[] getMultiSubFieldData(int tagno, char subFieldCode , MarcStru marcStru)
	{
		ArrayList<String> subFieldBuffer = new ArrayList<String>();
		List<MarcFieldElement> fieldElementList = getMultiFieldElement(tagno , marcStru);
		if( fieldElementList.size() == 0 )
			return null;

		for( MarcFieldElement fieldElement : fieldElementList)
		{
			List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

			for(MarcSubFieldElement subFieldElement : subFieldList ){
				if(String.valueOf(subFieldCode).equals(subFieldElement.getSbCode() ) == true ){
					subFieldBuffer.add( removePunctuationMark(subFieldElement.getSubFieldData()) ) ;
				}
			}
		}

		// 6. make subfield list
		if( subFieldBuffer.size() > 0 )
		{
			String[] subFieldData = new String[subFieldBuffer.size()];
			for( int i = 0; i < subFieldBuffer.size(); i++ )
				subFieldData[i] = subFieldBuffer.get(i);
			return subFieldData;
		}

		return null;
	}

	public String[] getMultiSubFieldDataForRegNo(int tagno, char subFieldCode,MarcStru marcStru){
		ArrayList<String> subFieldBuffer = new ArrayList<String>();
		List<MarcFieldElement> fieldElementList = getMultiFieldElement(tagno , marcStru);
		if( fieldElementList.size() == 0 )
			return null;

		for( MarcFieldElement fieldElement : fieldElementList)
		{
			List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

			for(MarcSubFieldElement subFieldElement : subFieldList ){
				if(String.valueOf(subFieldCode).equals(subFieldElement.getSbCode() ) == true ){
					subFieldBuffer.add( subFieldElement.getSubFieldData()) ;
				}
			}
		}

		// 6. make subfield list
		if( subFieldBuffer.size() > 0 )
		{
			String[] subFieldData = new String[subFieldBuffer.size()];
			for( int i = 0; i < subFieldBuffer.size(); i++ )
				subFieldData[i] = subFieldBuffer.get(i);
			return subFieldData;
		}

		return null;
	}

	public List<MarcSubFieldElement> getMultiSubFieldElement(int tagno, char subFieldCode , MarcStru marcStru) {

		List<MarcSubFieldElement> subFieldBuffer = new ArrayList<MarcSubFieldElement>();
		List<MarcFieldElement> fieldElementList = getMultiFieldElement(tagno , marcStru);
		if( fieldElementList.size() == 0 )
			return null;

		for( MarcFieldElement fieldElement : fieldElementList)
		{
			List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

			for(MarcSubFieldElement subFieldElement : subFieldList ){

				if(String.valueOf(subFieldCode).equals(subFieldElement.getSbCode() ) == true ){
					subFieldBuffer.add( subFieldElement ) ;
				}

			}
		}

		return subFieldBuffer;
	}

	@Override
	public String getSingleSubFieldDataFromFieldElement(char subFieldCode, MarcFieldElement fieldElement){

		MarcSubFieldElement subFieldElement = fieldElement.getSubFieldElement(String.valueOf(subFieldCode));
		if(subFieldElement == null)
			return null;

		// 4. 구도점을 제거
		return removePunctuationMark(subFieldElement.getSubFieldData());
	}

	@Override
	public int insertFieldData(int tagno, String fieldData , MarcStru marcStru)	{
		int index;

		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( index = 0; index < fieldList.size(); index++ )
		{
			if( tagno < fieldList.get(index).getTagno() )
				break;
		}

		String fIndicator = "";
		String sIndicator = "";

		if( tagno >= 10 && tagno != 908 )
		{
			fIndicator	= fieldData.substring(0,1);
			sIndicator	= fieldData.substring(1,2);
			fieldData	= fieldData.substring(2);
		}

		// 3. insert data
		if( index >= fieldList.size() )
		{
			marcStru.addField(tagno, fIndicator, sIndicator, "", fieldData);
		}
		else
		{
			marcStru.addField(index, tagno, fIndicator, sIndicator, "", fieldData);
		}

		return 1;
	}

	@Override
	public int removeFieldData(int tagno, int removeCount, MarcStru marcStru){
		// 1. search remove index
		int count = 0;
		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( int index = fieldList.size()-1; index >= 0; index-- )
		{
			if( tagno == fieldList.get(index).getTagno() )
			{
				// 2. remove field data
				marcStru.removeField(index);
				count++;

				if( count >= removeCount && removeCount != 0 )
					break;
			}
		}

		return count;
	}

	public int removeFieldData(int tagno, String fieldData, MarcStru marcStru) {
		int count = 0;

		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( int index = fieldList.size()-1; index >= 0; index-- )
		{
			if(   tagno == fieldList.get(index).getTagno()  && fieldData.equals( fieldList.get(index).toString() ) == true )
			{
				marcStru.removeField(index);
				count++;
			}
		}

		return count;
	}

	@Override
	public int removeTagNo(int tagno, MarcStru marcStru) {
		// 1. search remove index
		int count = 0;
		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( int index = fieldList.size()-1; index >= 0; index-- )
		{
			if( tagno == fieldList.get(index).getTagno() )
			{
				// 2. remove field data
				marcStru.removeField(index);
			}
		}

		return count;
	}

	@Override
	public int updateFieldData(int tagno, String oldFieldData, String newFieldData , MarcStru marcStru){
		// 1. search update index
		int count = 0;

		String fIndicator = "";
		String sIndicator = "";
		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( int index = 0; index < fieldList.size(); index++ )
		{
			if( tagno == fieldList.get(index).getTagno() && oldFieldData.compareTo( fieldList.get(index).toString() ) == 0 )
			{


				if( tagno >= 10 && tagno != 908 )
				{
					fIndicator	= newFieldData.substring(0,1);
					sIndicator	= newFieldData.substring(1,2);
					newFieldData= newFieldData.substring(2);
				}

				// 4. update data
				marcStru.setField(index, tagno, fIndicator, sIndicator, "", newFieldData);

				count++;

			}
			fIndicator = "";
			sIndicator = "";
		}

		return count;
	}

	@Override
	public int insertSubFieldData(int tagno, char subFieldCode, String subFieldData , MarcStru marcStru) {
		// 1. search insert index
		MarcFieldElement fieldElement = getSingleFieldElement(tagno, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			// 구두점은 ? <-- 여기 이상하군...
			subFieldList.add(new MarcSubFieldElement(String.valueOf(subFieldCode) , subFieldData,""));
			String DefaultIndicator = getDefaultIndicator(tagno);
			marcStru.addField(new MarcFieldElement( tagno, DefaultIndicator.substring(0, 1) , DefaultIndicator.substring(1, 2) , "" , subFieldList));
			return 1;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode(String.valueOf(subFieldCode));
		if (index == -1){
			fieldElement.getSubFieldList().add( new MarcSubFieldElement(String.valueOf(subFieldCode) , subFieldData,""));
		}else{
			subFieldList.add(index+1, new MarcSubFieldElement(String.valueOf(subFieldCode) , subFieldData,""));
		}

		return 1;
	}

//	public int insertSubFieldData(int tagno, char subFieldCode, String subFieldData , String puncmark ,MarcStru marcStru){
//
//		MarcFieldElement fieldElement = getSingleFieldElement(tagno, marcStru);
//		if( fieldElement == null ){
//			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
//			subFieldList.add(new MarcSubFieldElement(String.valueOf(subFieldCode) , subFieldData,puncmark));
//			String DefaultIndicator = getDefaultIndicator(tagno);
//			marcStru.addField(new MarcFieldElement( tagno, DefaultIndicator.substring(0, 1) , DefaultIndicator.substring(1, 2) , "" , subFieldList));
//			return 1;
//		}
//
//		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
//		int index = fieldElement.findIndexOfSbCode(String.valueOf(subFieldCode));
//		if (index == -1){
//			List<String> orderSbCodeList = new ArrayList<String>(); // <-- 테그 번호별로 뭔가 만드는 함수가 필요한가 ?
////			orderSbCodeList.add("b");
////			orderSbCodeList.add("a");
//			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement(String.valueOf(subFieldCode), subFieldData,puncmark));
//
//			subFieldList.add(index+1, new MarcSubFieldElement(String.valueOf(subFieldCode) , subFieldData,puncmark));
//
//		}else{
//			subFieldList.add(index+1, new MarcSubFieldElement(String.valueOf(subFieldCode) , subFieldData,puncmark));
//		}
//		return 1;
//	}

	@Override
	public int removeSubFieldData(int tagno, char subFieldCode , MarcStru marcStru){
		// 1. search remove index
		int count = 0;
		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( int index = fieldList.size()-1; index >= 0; index-- )
		{
			if( tagno == fieldList.get(index).getTagno() )
			{
				MarcFieldElement field = marcStru.getField(index);
				List<MarcSubFieldElement> subFieldList = field.getSubFieldList();

				for(int i = 0 ; i < subFieldList.size(); i++){
					MarcSubFieldElement subFieldElement = subFieldList.get(i);

					if(String.valueOf(subFieldCode).equals(subFieldElement.getSbCode())  == true) {

						if(i == 0 ){
							// 최초의 서브필드 삭제 - 그냥 삭제 하면 됨 ..
							subFieldList.remove(i);
							i--; //삭제시 인덱스 조정
							count++;
							continue;
						}else if( i == subFieldList.size()-1){
							// 마지막 서브필드의 삭제
							MarcSubFieldElement prev_SubFieldElement = subFieldList.get(i-1);

							//앞의 식별기호 데이터에 있던 구두점은 삭제
							if( isPuncMark(prev_SubFieldElement.getSubFieldData().substring(prev_SubFieldElement.getSubFieldData().length()-1, prev_SubFieldElement.getSubFieldData().length())) ){
								prev_SubFieldElement.setSubFieldData(prev_SubFieldElement.getSubFieldData().substring(0, prev_SubFieldElement.getSubFieldData().length()-1));
							}
							subFieldList.remove(i);
							i--;
							count++;
							continue;
						}


						//중간에 존제하는 식별기호 삭제의 경우
						MarcSubFieldElement prev_SubFieldElement = subFieldList.get(i-1);
						MarcSubFieldElement next_SubFieldElement = subFieldList.get(i+1);

						//앞의 식별기호 데이터에 있던 구두점은 삭제
						if( isPuncMark(prev_SubFieldElement.getSubFieldData().substring(prev_SubFieldElement.getSubFieldData().length()-1, prev_SubFieldElement.getSubFieldData().length())) ){
							// 앞데이터의 구두점 정리
							prev_SubFieldElement.setSubFieldData(prev_SubFieldElement.getSubFieldData().substring(0, prev_SubFieldElement.getSubFieldData().length()-1));
							// 뒤에 붙을 자료의 구두점 입력
							prev_SubFieldElement.setSubFieldData(prev_SubFieldElement.getSubFieldData()+next_SubFieldElement.getPunkmark());
						}

						subFieldList.remove(i);
						i--;
						count++;
					}//end if
				}//end for

				//하위 서브필드가 존재하지 않는 필드 삭제
				if(subFieldList.size() == 0){
					marcStru.removeField(index);
				}
			}//end if
		}

		return count;
	}

	@Override
	public int removeSubFieldData(int tagno, char subFieldCode, String subFieldData , MarcStru marcStru){
		// 1. search remove index
		int count = 0;
		String old_subFieldData ="";
		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( int index = fieldList.size()-1; index >= 0; index-- )
		{
			if( tagno == fieldList.get(index).getTagno() )
			{
				MarcFieldElement field = marcStru.getField(index);
				List<MarcSubFieldElement> subFieldList = field.getSubFieldList();

				for(int i = 0 ; i < subFieldList.size(); i++){
					MarcSubFieldElement subFieldElement = subFieldList.get(i);
					old_subFieldData = subFieldElement.getSubFieldData();
					if( isPuncMark(old_subFieldData.substring(old_subFieldData.length()-1, old_subFieldData.length())) ){
						old_subFieldData = old_subFieldData.substring(0, old_subFieldData.length()-1);
					}

					if( String.valueOf(subFieldCode).equals(subFieldElement.getSbCode())  == true
					&& old_subFieldData.equals(subFieldData) ) {


						if(i == 0 ){
							// 최초의 서브필드 삭제 - 그냥 삭제 하면 됨 ..
							subFieldList.remove(i);
							i--; //삭제시 인덱스 조정
							count++;
							continue;
						}else if( i == subFieldList.size()-1){
							// 마지막 서브필드의 삭제
							MarcSubFieldElement prev_SubFieldElement = subFieldList.get(i-1);

							//앞의 식별기호 데이터에 있던 구두점은 삭제
							if( isPuncMark(prev_SubFieldElement.getSubFieldData().substring(prev_SubFieldElement.getSubFieldData().length()-1, prev_SubFieldElement.getSubFieldData().length())) ){
								prev_SubFieldElement.setSubFieldData(prev_SubFieldElement.getSubFieldData().substring(0, prev_SubFieldElement.getSubFieldData().length()-1));
							}
							subFieldList.remove(i);
							i--;
							count++;
							continue;
						}


						//중간에 존제하는 식별기호 삭제의 경우
						MarcSubFieldElement prev_SubFieldElement = subFieldList.get(i-1);
						MarcSubFieldElement next_SubFieldElement = subFieldList.get(i+1);

						//앞의 식별기호 데이터에 있던 구두점은 삭제
						if( isPuncMark(prev_SubFieldElement.getSubFieldData().substring(prev_SubFieldElement.getSubFieldData().length()-1, prev_SubFieldElement.getSubFieldData().length())) ){
							// 앞데이터의 구두점 정리
							prev_SubFieldElement.setSubFieldData(prev_SubFieldElement.getSubFieldData().substring(0, prev_SubFieldElement.getSubFieldData().length()-1));
							// 뒤에 붙을 자료의 구두점 입력
							prev_SubFieldElement.setSubFieldData(prev_SubFieldElement.getSubFieldData()+next_SubFieldElement.getPunkmark());
						}

						subFieldList.remove(i);
						i--;
						count++;
					}//end if

					old_subFieldData ="";
				}//end for

				//하위 서브필드가 존재하지 않는 필드 삭제
				if(subFieldList.size() == 0){
					marcStru.removeField(index);
				}
			}//end if
		}

		return count;
	}

	@Override
	public int updateSubFieldData(int tagno, char subFieldCode, String oldSubFieldData, String newSubFieldData , MarcStru marcStru)	{
		// 1. search update index
		int count = 0;
		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( int index = 0; index < fieldList.size(); index++ )
		{
			if( tagno == fieldList.get(index).getTagno() )
			{
				//2. get field data
				MarcFieldElement field = marcStru.getField(index);
				List<MarcSubFieldElement> subFieldList = field.getSubFieldList();

				for(int i = 0 ; i < subFieldList.size(); i++){
					MarcSubFieldElement subFieldElement = subFieldList.get(i);
					if(String.valueOf(subFieldCode).equals(subFieldElement.getSbCode()) == true && oldSubFieldData.equals(subFieldElement.getSubFieldData()) == true){

						if( isPuncMark(oldSubFieldData.substring(oldSubFieldData.length()-1, oldSubFieldData.length())) == true
						  &&isPuncMark(newSubFieldData.substring(newSubFieldData.length()-1, newSubFieldData.length())) == false  ){
							newSubFieldData += oldSubFieldData.substring(oldSubFieldData.length()-1, oldSubFieldData.length());
						}
						subFieldElement.setSubFieldData(newSubFieldData);
						//구두점은 기존 구두점이 유지가 됨

					}
				}
				count++;
			}
		}

		return count;
	}

	@Override
	public String getSubFieldDataUseAlias(String alias , MarcStru marcStru)	{
		// 1. MARC_ALIAS_INFO 초기화
		List<MarcAliasItem> marcAliasItemList = marcAlias.getMarcAliasItemList();
		if( marcAliasItemList == null )
			return null;

		// 2. ALIAS 정보확인
		String[] aliasInfo = null;
		for(MarcAliasItem item : marcAliasItemList ){
			if( item.getName().compareTo(alias) == 0 ){
				aliasInfo = new String[2];
				aliasInfo[0] = item.getTagno();
				aliasInfo[1] = item.getIndicator();
				break;
			}
		}

		// 3. 존재하지 않는 ALIAS 정보일 경우
		if( aliasInfo == null )
			return null;

		// 4. 필드데이터 가져오기
		int tagno = Integer.parseInt(aliasInfo[0]);

		if(tagno == 8 ){

			String fieldData = getSingleFieldData(tagno, marcStru);

			if(aliasInfo[1].equals("") == true){
				return fieldData;
			}

			if(fieldData == null) return null	;

			String []index = aliasInfo[1].split("-");

			if( Integer.parseInt(index[0]) > fieldData.length()-1 ){
				return null;
			}

			char[] fieldDataArray = fieldData.toCharArray();
			if(index.length == 1){
				return String.valueOf(fieldDataArray[Integer.parseInt(index[0])]) ;
			}else{
				if( Integer.parseInt(index[1]) > fieldData.length()-1 ){
					return null;
				}

				StringBuffer temp = new StringBuffer();
				for(int i = Integer.parseInt(index[0]) ; i <= Integer.parseInt(index[1]) ; i++ ){
					temp.append(String.valueOf(fieldDataArray[i]));
				}
				return temp.toString();
			}
		}

		return null;
	}

	@Override
	public void setReader(String reader , MarcStru marcStru){
		marcStru.setReader(reader);
	}

	@Override
	public String getReader(MarcStru marcStru){
		return marcStru.getReader();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 마크 내부에서 사용할 함수
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 *
	 * @param marcStru
	 */
	private void newestMarcStructure(MarcStru marcStru)	{
		// 1. set field start position & field length
		int fieldStartPos = 0;
		int fieldCount = marcStru.getFieldCount();

		String fieldLength = "";
		int fieldLen = -1;
		for( int i = 0; i < fieldCount; i++ )
		{
			MarcFieldElement     field     = marcStru.getField(i);
			fieldLen = getByteSize(field.getFieldData(), charsetName) + 1;
			if(fieldLen >= 10000)
			{
				fieldLength = "9999";
			}else{
				fieldLength = String.format("%04d", fieldLen);
			}

			fieldStartPos += Integer.parseInt(fieldLength);
			fieldLength = "";
			fieldLen = -1 ;
		}

		// 2. set reader
		int recordLength = 0;
		int fieldBasePos = 0;

		// get reader size
		recordLength += Integer.parseInt(message.getMessage("MARC.READER_SIZE"));
		// get directory size
		if( marcStru.getFieldCount() != 0 )
			recordLength += (marcStru.getFieldCount() * Integer.parseInt(message.getMessage("MARC.DIRECTORY_ELEMENT_SIZE"))) + FIELD_END.length();
		fieldBasePos = recordLength;
		// get field data size
		recordLength += (fieldStartPos+RECORD_END.length());

		// make new Reader
		String reader = marcStru.getReader();
		StringBuffer buffer = new StringBuffer();

		buffer.append(String.format("%05d", recordLength));
		buffer.append(reader.substring(5, 12));
		buffer.append(String.format("%05d", fieldBasePos));
		buffer.append(reader.substring(17));
		reader = buffer.toString();

		reader = getDefaultReader(reader);

		marcStru.setReader(reader);
	}

	/**
	 * @return 디폴드 리더 데이터를 가져온다.
	 */
	public String getDefaultReader()	{
		return "                        ";
	}
	/**
	 *
	 * @param tagno
	 * @return
	 */
	private  String getDefaultIndicator(int tagno)	{
		String indicator = "  ";

		switch( tagno )
		{
			case  20 :
			case  85 :
				indicator = "  ";
				break;
			case  24 :
			case  41 :
			case  49 :
			case  82 :
			case 100 :
			case 502 :
			case 700 :
			case 765 :
			case 767 :
			case 770 :
			case 772 :
			case 773 :
			case 775 :
			case 776 :
			case 787 :
			case 950 :
				indicator = "0 ";
				break;
			case 50 :
			case 52 :
				indicator = "01";
				break;
			case 246 :
				indicator = "19";
				break;
			case 245 :
			case 440 :
			case 490 :
        	case 500 :
        	case 505 :indicator = "00"; break;
        	case 507 :
        	case 511 :
        	case 600 :
        	case 630 :
        	case 900 :
        		indicator = "00";
				break;
        	case 130 :
        	case 610 :
        	case 611 :
        	case 650 :
        	case 651 :
        	case 730 :
        	case 740 :
        	case 830 :
        	case 911 :
        		indicator = " 0";
				break;
			case 780 :
			case 785 :
				indicator = "02";
				break;
			default :
				indicator = "  ";
				break;
        }

		return indicator;
	}

	/**
	 *
	 * @param subFieldData
	 * @return
	 */
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
			endChar == '+' || endChar == '%' ||
			endChar == '.'  )

		{
			subFieldData = subFieldData.substring(0, subFieldData.length()-1);
		}
		return subFieldData;
	}

	/**
	 *
	 * @param reader
	 * @return
	 */
	private  String getDefaultReader(String reader)	{
		char         temp;
		StringBuffer buffer = new StringBuffer();

		// [0-4]레코드 길이
		buffer.append(reader.substring(0, 5));

		// [5]레코드 상태
		temp = reader.charAt(5);
		if( temp == ' ' )	buffer.append('n');
		else				buffer.append(temp);

		// [6]레코드 형태
		temp = reader.charAt(6);
		if( temp == ' ' )	buffer.append('a');
		else				buffer.append(temp);

		// [7]서지수준
		temp = reader.charAt(7);
		if( temp == ' ' )	buffer.append('m');
		else				buffer.append(temp);

		// [8]제어유형
		temp = reader.charAt(8);
		if( temp == ' ' )	buffer.append(' ');
		else				buffer.append(temp);

		// [9]문자부호체계
		temp = reader.charAt(9);
		if( temp == ' ' )	buffer.append('a');
		else				buffer.append(temp);

		// [10]지시기호 자릿수
		temp = reader.charAt(10);
		if( temp == ' ' )	buffer.append('2');
		else				buffer.append(temp);

		// [11]식별기호 자릿수
		temp = reader.charAt(11);
		if( temp == ' ' )	buffer.append('2');
		else				buffer.append(temp);

		// [12-16]데이터 기본번지
		buffer.append(reader.substring(12, 17));

		// [17]입력수준
		buffer.append(reader.charAt(17));

		// [18]목록기술형식
		temp = reader.charAt(18);
		if( temp == ' ' )	buffer.append('c');
		else				buffer.append(temp);

		// [19]연관레코드 조건
		buffer.append(reader.charAt(19));

		// [20-23]에트리맵
		temp = reader.charAt(20);
		if( temp == ' ' )	buffer.append("4500");
		else				buffer.append(reader.substring(20, 24));

		return buffer.toString();
	}

	/**
	 *
	 * @param data
	 * @param charsetName
	 * @return
	 */
	private int getByteSize(String data, String charsetName)	{
		try
		{
			if( charsetName == null )
			{
				Integer byteSize = data.getBytes().length;

				byteSize = byteSize > Integer.parseInt(message.getMessage("MARC.FIELD_LENGTH_MAX_SIZE")) ? Integer.parseInt(message.getMessage("MARC.FIELD_LENGTH_MAX_SIZE")) : byteSize;

				return byteSize;
			}
			else
			{
				Integer byteSize = data.getBytes(charsetName).length;

				byteSize = byteSize > Integer.parseInt(message.getMessage("MARC.FIELD_LENGTH_MAX_SIZE")) ? Integer.parseInt(message.getMessage("MARC.FIELD_LENGTH_MAX_SIZE")) : byteSize;

				return byteSize;
			}
		}
		catch(UnsupportedEncodingException e)
		{
			return -1;
		}
	}

	/**
	 *
	 * @param data
	 * @return
	 */
	private boolean isNumber(String data){
		for( int i = 0; i < data.length(); i++ )
			if( Character.isDigit(data.charAt(i)) != true )
				return false;

		return true;
	}

	private boolean isPuncMark(String convertChar)
	{
		if(  convertChar.equals(";") == true
		  || convertChar.equals(":") == true
		  || convertChar.equals("/") == true
		  || convertChar.equals("-") == true
		  || convertChar.equals("=") == true
		  || convertChar.equals(",") == true
		  || convertChar.equals("+") == true
		  || convertChar.equals("%") == true
		  || convertChar.equals(".") == true) {
			return true;
		}

		return false;
	}

	/**
	 *
	 * @param fieldData
	 * @return
	 */
	private String convert049FieldData(String fieldData)	{
		int    spos  = 0;
		int    epos  = 0;
		String split = "l";
		String space = "\n     ";

		StringBuffer buffer = new StringBuffer();
		//제일지시기호 , 제이지시기호
		buffer.append(fieldData.substring(0,1)).append(fieldData.substring(1,2));
		//실제 데이터 부부터 계산한다.
		fieldData = fieldData.substring(2);

		spos = fieldData.indexOf(split);
		if( spos == -1 )
			return fieldData;
		else if( spos == 0 )
		{
			epos = fieldData.indexOf(split, spos+1);
			if( epos == -1 )  buffer.append(fieldData);
			else              buffer.append(fieldData.substring(spos, epos));

			spos = epos;
		}
		else
		{
			buffer.append(fieldData.substring(0, spos));
		}

		while( spos != -1 )
		{
			epos = fieldData.indexOf(split, spos+1);
			if( epos == -1 )  buffer.append(space).append(fieldData.substring(spos));
			else              buffer.append(space).append(fieldData.substring(spos, epos));

			spos = epos;
		}

		return buffer.toString();
	}

	private String convert505FieldData(String fieldData)	{
		int    spos  = 0;
		int    epos  = 0;
		String split = "n";
		String space = "\n     ";

		StringBuffer buffer = new StringBuffer();
		//제일지시기호 , 제이지시기호
		buffer.append(fieldData.substring(0,1)).append(fieldData.substring(1,2));
		//실제 데이터 부부터 계산한다.
		fieldData = fieldData.substring(2);

		spos = fieldData.indexOf(split);
		if( spos == -1 )
			return fieldData;
		else if( spos == 0 )
		{
			epos = fieldData.indexOf(split, spos+1);
			if( epos == -1 )  buffer.append(fieldData);
			else              buffer.append(fieldData.substring(spos, epos));

			spos = epos;
		}
		else
		{
			buffer.append(fieldData.substring(0, spos));
		}

		while( spos != -1 )
		{
			epos = fieldData.indexOf(split, spos+1);
			if( epos == -1 )  buffer.append(space).append(fieldData.substring(spos));
			else              buffer.append(space).append(fieldData.substring(spos, epos));

			spos = epos;
		}

		return buffer.toString();
	}
	private String inner_getCurrency_code(String _950_b){

		String returnValue = "";
		if(_950_b == null || _950_b.length() == 0 ) return "";

		for(int i=0 ; i< _950_b.length(); i++)
		{

			if(    _950_b.charAt(i) == '0'
				|| _950_b.charAt(i) == '1'
				|| _950_b.charAt(i) == '2'
				|| _950_b.charAt(i) == '3'
				|| _950_b.charAt(i) == '4'
				|| _950_b.charAt(i) == '5'
				|| _950_b.charAt(i) == '6'
				|| _950_b.charAt(i) == '7'
				|| _950_b.charAt(i) == '8'
				|| _950_b.charAt(i) == '9'
				|| _950_b.charAt(i) == '.'
				|| _950_b.charAt(i) == ','){
				continue;
			}
			returnValue+=String.valueOf(_950_b.charAt(i));
		}
		return returnValue;
	}
	private String inner_getPrice(String _950_b){

		String returnValue = "";
		if(_950_b == null || _950_b.length() == 0 ) return "";

		for(int i=0 ; i< _950_b.length(); i++)
		{
			if(    _950_b.charAt(i) == '0'
				|| _950_b.charAt(i) == '1'
				|| _950_b.charAt(i) == '2'
				|| _950_b.charAt(i) == '3'
				|| _950_b.charAt(i) == '4'
				|| _950_b.charAt(i) == '5'
				|| _950_b.charAt(i) == '6'
				|| _950_b.charAt(i) == '7'
				|| _950_b.charAt(i) == '8'
				|| _950_b.charAt(i) == '9'
				|| _950_b.charAt(i) == '.'
				|| _950_b.charAt(i) == ','){
				returnValue+=String.valueOf(_950_b.charAt(i));
			}

		}
		return returnValue;
	}

	@Override
	public MarcFieldElement getSingleMarcFieldElement(int tagno , MarcStru marcStru){
		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			// 1. search field
			MarcFieldElement field = marcStru.getField(i);
			if( tagno == field.getTagno() )
			{
				return field;
			}
		}
		return null;
	}

	@Override
	public List<MarcFieldElement>  getMultipleMarcFieldElement(int tagno , MarcStru marcStru){
		List<MarcFieldElement> list = new ArrayList<MarcFieldElement>();

		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			// 1. search field
			MarcFieldElement field = marcStru.getField(i);
			if( tagno == field.getTagno() )
			{
				list.add(field);
			}
		}
		return list;
	}

	@Override
	public List<MarcFieldElement> getMultipleMarcFieldElement(List<Integer> tagnoList, MarcStru marcStru){
		List<MarcFieldElement> list = new ArrayList<MarcFieldElement>();

		for( int i = 0; i < marcStru.getFieldCount(); i++ )
		{
			MarcFieldElement field = marcStru.getField(i);

			for(Integer tagNo : tagnoList){
				// 1. search field
				if( field.getTagno()  == tagNo.intValue())
				{
					list.add(field);
					break;
				}
			}

		}
		return list;
	}

	public int removeSetISBN(MarcStru marcStru){
		int count = 0;
		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( MarcFieldElement field : fieldList ){
			if( 20 == field.getTagno().intValue() && field.getFIndicator() == "1" ){
				fieldList.remove(field);
				count++;
			}
		}
		return count;
	}


	//고정장 관련 입력 시작 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getEa_isbn (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(20 ,' ', marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("a".equals(subFieldElement.getSbCode() ) == true ){
				
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getEa_isbn_add_code (MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(20 ,' ', marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("g".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getSet_isbn(MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(20 ,'1', marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("a".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getSet_isbn_add_code(MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(20 ,'1', marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("g".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getTitle         (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("a".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	public String getOrigin_title (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("b".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getParrel_title        (MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("x".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getVol           (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("n".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getVol_title     (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("p".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getAuthor        (MarcStru marcStru){

		String author = null;
		StringBuffer bf = new StringBuffer();
		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("d".equals(subFieldElement.getSbCode() ) == true ){
				bf.append( removePunctuationMark( subFieldElement.getSubFieldData() ) );
			}else if("e".equals(subFieldElement.getSbCode() ) == true ){
				bf.append(" ");
				bf.append( subFieldElement.getSubFieldData() );
			}
		}

		author = bf.toString();

		return author.equals("") == false ? author : null;
	}
	@Override
	public String getEdit     (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(250, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("a".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getPublish_place      (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(260, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("a".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getPublisher     (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(260, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("b".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getPublish_year  (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(260, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("c".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getPage          (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(300, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("a".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	public String getPhysical_property (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(300, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("b".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getBook_size(MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(300, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("c".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getAccomp_mat(MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(300, marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("e".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getSeries_title  (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(490, marcStru);
		if( fieldElement == null ){
			MarcFieldElement fieldElement2 = getSingleFieldElement(440, marcStru);
			if( fieldElement2 == null )
				return null;

			List<MarcSubFieldElement> subFieldList = fieldElement2.getSubFieldList();

			for(MarcSubFieldElement subFieldElement : subFieldList ){
				if("a".equals(subFieldElement.getSbCode() ) == true ){
					return removePunctuationMark(subFieldElement.getSubFieldData());
				}
			}
			return null;
		}else{
			List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

			for(MarcSubFieldElement subFieldElement : subFieldList ){
				if("a".equals(subFieldElement.getSbCode() ) == true ){
					return removePunctuationMark(subFieldElement.getSubFieldData());
				}
			}
			return null;
		}




	}

	@Override
	public String getSeries_vol    (MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(490, marcStru);
		if( fieldElement == null ){
			MarcFieldElement fieldElement2 = getSingleFieldElement(440, marcStru);
			if( fieldElement2 == null )
				return null;
			List<MarcSubFieldElement> subFieldList = fieldElement2.getSubFieldList();

			for(MarcSubFieldElement subFieldElement : subFieldList ){
				if("v".equals(subFieldElement.getSbCode() ) == true ){
					return removePunctuationMark(subFieldElement.getSubFieldData());
				}
			}
			return null;
		}else{
			List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

			for(MarcSubFieldElement subFieldElement : subFieldList ){
				if("v".equals(subFieldElement.getSbCode() ) == true ){
					return removePunctuationMark(subFieldElement.getSubFieldData());
				}
			}
			return null;
		}
	}
	@Override
	public String getPrice_character(MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(950 , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("a".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	@Override
	public String getPrice          (MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(950 , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("b".equals(subFieldElement.getSbCode() ) == true ){
				return inner_getPrice(removePunctuationMark(subFieldElement.getSubFieldData()));
			}
		}
		return null;
	}
	@Override
	public String getCurrency_code(MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(950 , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("b".equals(subFieldElement.getSbCode() ) == true ){
				return inner_getCurrency_code(removePunctuationMark(subFieldElement.getSubFieldData()));
			}
		}
		return null;
	}
	@Override
	public String getPrice_other_info(MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(950 , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("c".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}

	@Override
	public String getSeparate_shelf_code(MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(49 , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("f".equals(subFieldElement.getSbCode() ) == true ){
				return subFieldElement.getSubFieldData();
			}
		}
		return null;
	}
	@Override
	public String getClass_no           (MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(90 , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("a".equals(subFieldElement.getSbCode() ) == true ){
				return subFieldElement.getSubFieldData();
			}
		}
		return null;

	}
	@Override
	public String getBook_code          (MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(90 , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("b".equals(subFieldElement.getSbCode() ) == true ){
				return subFieldElement.getSubFieldData();
			}
		}
		return null;
	}
	@Override
	public String getVol_code           (MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(49 , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("v".equals(subFieldElement.getSbCode() ) == true ){
				return subFieldElement.getSubFieldData();
			}
		}
		return null;
	}
	@Override
	public String getCopy_code          (MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(49 , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("c".equals(subFieldElement.getSbCode() ) == true ){
				return subFieldElement.getSubFieldData();
			}
		}
		return null;
	}
	public String getControl_no          (MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(1 , marcStru);
		if( fieldElement == null )
			return null;

		return fieldElement.getFieldData();

	}
	
	public String getKeyword_first       (MarcStru marcStru) {
		MarcFieldElement fieldElement = getSingleFieldElement(653 , marcStru);
		if( fieldElement == null )
			return null;

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		for(MarcSubFieldElement subFieldElement : subFieldList ){
			if("a".equals(subFieldElement.getSbCode() ) == true ){
				return removePunctuationMark(subFieldElement.getSubFieldData());
			}
		}
		return null;
	}
	
	//고정장 관련 조회 종료 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//고정장 관련 입력 시작 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setEaISBN(String ea_isbn,String ea_isbn_add_code,MarcStru marcStru){

		int tagno = 20;
		int index;
		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( index = 0; index < fieldList.size(); index++ )
		{
			if( tagno < fieldList.get(index).getTagno().intValue() ){
				break;
			}
		}

		StringBuffer fieldData = new StringBuffer();

		if(ea_isbn != null && ea_isbn.equals("") == false){
			fieldData.append(IDENTIFIER).append("a").append(ea_isbn);
		}
		if(ea_isbn_add_code != null && ea_isbn_add_code.equals("") == false){
			fieldData.append(IDENTIFIER).append("g").append(ea_isbn_add_code);
		}
		//가격을 넣은건지 여부 확인 필요

		// 3. insert data
		if( index >= fieldList.size() )
			marcStru.addField(       20, " ", " ", "", fieldData.toString());
		else
			marcStru.addField(index, 20, " ", " ", "", fieldData.toString());

		return ;
	}
	@Override
	public void setSetISBN(String ea_isbn,String ea_isbn_add_code,MarcStru marcStru){

		int tagno = 20;
		int index;
		List<MarcFieldElement> fieldList = marcStru.getFieldList();
		for( index = 0; index < fieldList.size(); index++ )
		{
			if( tagno <= fieldList.get(index).getTagno().intValue() )
				break;
		}

		StringBuffer fieldData = new StringBuffer();

		if(ea_isbn != null && ea_isbn.equals("") == false){

			if(ea_isbn.indexOf("(세트)") == -1){
				ea_isbn = ea_isbn+"(세트)";
			}
			fieldData.append(IDENTIFIER).append("a").append(ea_isbn);
		}
		if(ea_isbn_add_code != null && ea_isbn_add_code.equals("") == false){
			fieldData.append(IDENTIFIER).append("g").append(ea_isbn_add_code);
		}
		//가격을 넣은건지 여부 확인 필요

		// 3. insert data
		if( index >= fieldList.size() )
			marcStru.addField(       20, "1", " ", "", fieldData.toString());
		else
			marcStru.addField(index, 20, "1", " ", "", fieldData.toString());

		return ;
	}
	@Override
	public void setTitle         (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null ){
			//서브필드를 생성하여 필드데이터로 입력
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement ("a" ,data , "")); // 서브필드 리스트 생성
			marcStru.addField(new MarcFieldElement( 245 , "0" , "0" , "" ,subFieldList)); //필드데이터 추가
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("a");
		if (index == -1){
			setSubFieldData(fieldElement , new ArrayList<String>() , new MarcSubFieldElement("a" , data, ""));
		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}

			subFieldList.set(index, new MarcSubFieldElement("a" , data, "") );
		}
		return ;
	}
	@Override
	public void setOrigin_title (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement( "b" , data , ":"));
			marcStru.addField(new MarcFieldElement( 245 , "0" , "0" ,"" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("b");
		if (index == -1){

			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("h");
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("b" , data, ":") );

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}
			subFieldList.set(index, new MarcSubFieldElement("b" , data, ":"));
		}
		return ;
	}
	@Override
	public void setParrel_title        (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("x" , data, "="));
			marcStru.addField(new MarcFieldElement( 245 , "0" , "0" ,"" , subFieldList));
			return;
		}


		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("x");
		if (index == -1){

			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("b");
			orderSbCodeList.add("h");
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("x" , data, "="));

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}

			subFieldList.set(index, new MarcSubFieldElement("x" , data, "="));
		}
		return ;
	}
	@Override
	public void setVol           (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("n" , data, "."));
			marcStru.addField(new MarcFieldElement( 245 , "0" , "0" ,"" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		int p_index = fieldElement.findIndexOfSbCode("p");
		if(p_index !=-1){
			MarcSubFieldElement pSubFieldElement = subFieldList.get(p_index);
			pSubFieldElement.setPunkmark(",");
		}

		int index = fieldElement.findIndexOfSbCode("n");
		if (index == -1){

			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("x");
			orderSbCodeList.add("b");
			orderSbCodeList.add("h");
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("n" , data, "."));

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}

			subFieldList.set(index, new MarcSubFieldElement("n" , data, "."));
		}
		return ;
	}
	@Override
	public void setVol_title     (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("p" , data, "."));
			marcStru.addField(new MarcFieldElement( 245 , "0" , "0" ,"" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();

		int n_index = fieldElement.findIndexOfSbCode("n");
		int index = fieldElement.findIndexOfSbCode("p");
		if (index == -1){
			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("n");
			orderSbCodeList.add("x");
			orderSbCodeList.add("b");
			orderSbCodeList.add("h");
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("p" , data, n_index == -1 ? "." : ","));

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}
			subFieldList.set(index, new MarcSubFieldElement("p" , data, n_index == -1 ? "." : ","));
		}
		return ;
	}

	@Override
	public void setAuthor        (String data, MarcStru marcStru){

		List<MarcSubFieldElement> authorList =  new ArrayList<MarcSubFieldElement>();
		List<Integer> cvsPosList = new ArrayList<Integer>();
		for(int i = 0 ; i< data.length() ; i++){
			if(data.charAt(i) == ',' || data.charAt(i) == ';' ){
				cvsPosList.add(i);
			}
		}

		if(cvsPosList.size() > 0 ){
			for(int i = 0 ; i<= cvsPosList.size() ; i++) {
				MarcSubFieldElement subFieldElement = new MarcSubFieldElement();
				if(i == 0){
					subFieldElement.setSbCode("d");
					subFieldElement.setSubFieldData(data.substring(0, cvsPosList.get(i).intValue()));
					subFieldElement.setPunkmark("/");
				}else if(i < cvsPosList.size()){
					subFieldElement.setSbCode("e");
					subFieldElement.setSubFieldData(data.substring(cvsPosList.get(i-1).intValue()+1, cvsPosList.get(i).intValue()));
					subFieldElement.setPunkmark(data.substring(cvsPosList.get(i-1).intValue(), cvsPosList.get(i-1).intValue()+1));
				}else if(i == cvsPosList.size()){
					subFieldElement.setSbCode("e");
					subFieldElement.setSubFieldData(data.substring(cvsPosList.get(i-1).intValue()+1));
					subFieldElement.setPunkmark(data.substring(cvsPosList.get(i-1).intValue(), cvsPosList.get(i-1).intValue()+1));
				}
				authorList.add(subFieldElement);
			}
		}else{
			MarcSubFieldElement subFieldElement = new MarcSubFieldElement();
			subFieldElement.setSbCode("d");
			subFieldElement.setSubFieldData(data);
			subFieldElement.setPunkmark("/");
			authorList.add(subFieldElement);
		}


		MarcFieldElement fieldElement = getSingleFieldElement(245, marcStru);

		if( fieldElement == null ){

			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			for(MarcSubFieldElement subFieldElement: authorList) {
				subFieldList.add(subFieldElement);
			}

			marcStru.addField(new MarcFieldElement( 245 , "0" , "0" ,"" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		for(int i = 0 ; i < subFieldList.size() ; i++){
			if( subFieldList.get(i).getSbCode().equals("e") == true) {subFieldList.remove(i); i--;}
		}

		int index = fieldElement.findIndexOfSbCode("d");
		if (index == -1){
			// 하위 식별기호중 저자가 존재하지 않는경우 우선순위 순서대로
			// 입력 위치를 찾아서
			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("p");
			orderSbCodeList.add("n");
			orderSbCodeList.add("x");
			orderSbCodeList.add("b");
			orderSbCodeList.add("h");
			orderSbCodeList.add("a");
			setSubFieldDataList(fieldElement , orderSbCodeList , authorList);
		}else{
			for(int i = 0 ; i<  authorList.size() ; i++) {
				if(i == 0){
					subFieldList.set(index, authorList.get(i));
				}else{
					subFieldList.add(index+i, authorList.get(i));
				}
			}
		}

		return ;
	}
	@Override
	public void setEdit     (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(250, marcStru);
		if( fieldElement == null ){

			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("a" , data,""));
			marcStru.addField(new MarcFieldElement( 250 , " " , " " , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("a");
		if (index == -1){
			subFieldList.add(0, new MarcSubFieldElement("a" , data,""));
		}else{
			subFieldList.set(index, new MarcSubFieldElement("a" , data,""));
		}
		return ;
	}
	@Override
	public void setPublish_place      (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(260, marcStru);
		if( fieldElement == null ){

			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("a" , data,""));
			marcStru.addField(new MarcFieldElement( 260 , " " , " " , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("a");
		if (index == -1){

			List<String> orderSbCodeList = new ArrayList<String>();
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("a" , data,""));

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}

			subFieldList.set(index, new MarcSubFieldElement("a" , data,""));
		}
		return ;
	}
	@Override
	public void setPublisher     (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(260, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("b" , data,":"));
			marcStru.addField(new MarcFieldElement(260 , " " , " " , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("b");
		if (index == -1){
			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("b" , data,":"));

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}

			subFieldList.set(index, new MarcSubFieldElement("b" , data,":"));
		}
		return ;
	}
	@Override
	public void setPublish_year  (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(260, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("c" , data,",") );
			marcStru.addField(new MarcFieldElement( 260 , " " , " " , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("c");
		if (index == -1){
			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("b");
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("c" , data,",") );

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}

			subFieldList.set(index, new MarcSubFieldElement("c" , data,",") );
		}
		return ;
	}
	@Override
	public void setPage          (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(300, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("a" , data,"") );
			marcStru.addField(new MarcFieldElement(300 , " " , " " , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("a");
		if (index == -1){
			List<String> orderSbCodeList = new ArrayList<String>();
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("a" , data,""));

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}

			subFieldList.set(index, new MarcSubFieldElement("a" , data,""));
		}
		return ;
	}
	@Override
	public void setPhysical_property (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(300, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("b" , data,":") );
			marcStru.addField(new MarcFieldElement( 300 , " " , " " , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("b");
		if (index == -1){
			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("b" , data,":") );

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}

			subFieldList.set(index, new MarcSubFieldElement("b" , data,":") );
		}
		return ;
	}
	@Override
	public void setBook_size(String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(300, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("c" , data,";") );
			marcStru.addField(new MarcFieldElement(300 , " " , " " , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("c");
		if (index == -1){

			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("b");
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("c" , data,";"));

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}

			subFieldList.set(index, new MarcSubFieldElement("c" , data,";"));
		}
		return ;
	}
	@Override
	public void setAccomp_mat(String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(300, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("e" , data,"+"));
			marcStru.addField(new MarcFieldElement(300 , " " , " " , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("e");
		if (index == -1){
			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("c");
			orderSbCodeList.add("b");
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("e" , data,"+"));

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}
			subFieldList.set(index, new MarcSubFieldElement("e" , data,"+"));
		}
		return ;
	}
	@Override
	public void setSeries_title  (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(490, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("a" , data,""));
			marcStru.addField(new MarcFieldElement(490 , "0" , "0" , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("a");
		if (index == -1){
			List<String> orderSbCodeList = new ArrayList<String>();
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("a" , data,""));

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}

			subFieldList.set(index, new MarcSubFieldElement("a" , data,""));
		}
		return ;
	}
	@Override
	public void setSeries_vol    (String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(490, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("v" , data,";"));
			marcStru.addField(new MarcFieldElement(490 , "0" , "0" , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("v");
		if (index == -1){
			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("s");
			orderSbCodeList.add("p");
			orderSbCodeList.add("n");
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("v" , data,";"));

		}else{
			MarcSubFieldElement old_subFieldElement = subFieldList.get(index);
			if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){
				data = data + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length());
			}
			subFieldList.set(index, new MarcSubFieldElement("v" , data,";"));
		}
		return ;
	}
	@Override
	public void setPrice_character(String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(950, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("a" , data,""));
			marcStru.addField(new MarcFieldElement(950 , "0" , " " , "" , subFieldList));
			return;
		}


		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("a");
		if (index == -1){

			List<String> orderSbCodeList = new ArrayList<String>();
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("a" , data,""));

		}else{
			subFieldList.set(index, new MarcSubFieldElement("a" , data,""));
		}
		return ;
	}
	@Override
	public void setPrice         (String data, MarcStru marcStru){

		MarcFieldElement fieldElement = getSingleFieldElement(950, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("b" , data,""));
			marcStru.addField(new MarcFieldElement(950 , "0" , " " , "" , subFieldList));
			return;
		}


		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("b");
		if (index == -1){
			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("b" , data,""));


		}else{
			subFieldList.set(index, new MarcSubFieldElement("b" , data,""));
		}
		return ;
	}
	@Override
	public void setPrice_other_info(String data, MarcStru marcStru){
		MarcFieldElement fieldElement = getSingleFieldElement(950, marcStru);
		if( fieldElement == null ){
			List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();
			subFieldList.add(new MarcSubFieldElement("c" , data,""));
			marcStru.addField(new MarcFieldElement(950 , "0" , " " , "" , subFieldList));
			return;
		}

		List<MarcSubFieldElement> subFieldList = fieldElement.getSubFieldList();
		int index = fieldElement.findIndexOfSbCode("c");
		if (index == -1){
			List<String> orderSbCodeList = new ArrayList<String>();
			orderSbCodeList.add("b");
			orderSbCodeList.add("a");
			setSubFieldData(fieldElement , orderSbCodeList , new MarcSubFieldElement("c" , data,""));
		}else{
			subFieldList.set(index, new MarcSubFieldElement("c" , data,""));
		}
		return ;
	}


	//고정장 관련 입력 종료 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void setSubFieldData(MarcFieldElement fieldElement , List<String> orderSbCodeList , MarcSubFieldElement insertSubFielElement){
		int index = -1;
		for( String orderSbCode : orderSbCodeList){
			index = fieldElement.findIndexOfSbCode(orderSbCode);
			if(index >= 0 ){

				MarcSubFieldElement old_subFieldElement = fieldElement.getSubFieldList().get(index);
				if( isPuncMark(old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length())) ){

					//앞의 식별기호 데이터에 있던 구두점은 현재 입력하려고 하는 곳으로 이동
					insertSubFielElement.setSubFieldData(insertSubFielElement.getSubFieldData() + old_subFieldElement.getSubFieldData().substring(old_subFieldElement.getSubFieldData().length()-1, old_subFieldElement.getSubFieldData().length() ));

					//앞의 데이터의 구두점 삭제 후 입력되는 데이터의 구두점을 입력한다.
					if(insertSubFielElement.getPunkmark() != null && insertSubFielElement.getPunkmark().equals("") == false){
						old_subFieldElement.setSubFieldData( old_subFieldElement.getSubFieldData().substring(0 , old_subFieldElement.getSubFieldData().length()-1 ) );
						old_subFieldElement.setSubFieldData( old_subFieldElement.getSubFieldData() + insertSubFielElement.getPunkmark() );
					}
				}else{
					if(insertSubFielElement.getPunkmark() != null && insertSubFielElement.getPunkmark().equals("") == false){
						old_subFieldElement.setSubFieldData( old_subFieldElement.getSubFieldData() + insertSubFielElement.getPunkmark() );
					}
				}

				fieldElement.getSubFieldList().add(index+1, insertSubFielElement);
				break;
			}
		}

		if(index == -1){
			MarcSubFieldElement old_subFieldElement = fieldElement.getSubFieldList().get(0);
			if(old_subFieldElement.getPunkmark() != null && old_subFieldElement.getPunkmark().equals("") == false){
				insertSubFielElement.setSubFieldData(insertSubFielElement.getSubFieldData() + old_subFieldElement.getPunkmark());
			}
			fieldElement.getSubFieldList().add(0, insertSubFielElement);
		}
		return;
	}
	private void setSubFieldDataList(MarcFieldElement fieldElement , List<String> orderSbCodeList , List<MarcSubFieldElement> insertSubFielElementList){
		int index = 0;
		for( String orderSbCode : orderSbCodeList){
			index = fieldElement.findIndexOfSbCode(orderSbCode);
			if(index >= 0 ){
				for(int i = 0 ; i<  insertSubFielElementList.size() ; i++) {
					fieldElement.getSubFieldList().add(index+1+i, insertSubFielElementList.get(i));
				}
				break;
			}
		}

		if(index == -1){
			for(int i = 0 ; i<  insertSubFielElementList.size() ; i++) {
				fieldElement.getSubFieldList().add(insertSubFielElementList.get(i));
			}
		}
		return;
	}
	public String getMarcFieldSortData(Integer tagnoData, String fieldData, char crudFlag){
		String temp_fieldData = marcConvertFieldData.getMarcFieldSortData(tagnoData , fieldData , crudFlag);
		return temp_fieldData;
	}

}
