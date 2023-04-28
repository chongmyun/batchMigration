/*
 * @author 이태일
 * @Copyright(c) 2017 by (주)이씨오. All rights reserved.
 */

package com.trans.migration.marc.marc.struct;

import java.util.ArrayList;
import java.util.List;


/**
* 마크 데이터 중 한개의 TAG에 대한 필드데이터 정보를 저장하는 데이터 클래스이다.
* @author Written By 이태일 【taeil.lee@eco.co.kr】
*/
final public class MarcFieldElement
{
	/**
	 * 0x1f : 식별기호 - 역삼각형
	 */
	private String IDENTIFIER			= "";
	private Integer	tagno;				// TAG 정보
	private String	fIndicator;			// 제 1지시기호
	private String	sIndicator;			// 제 2지시기호
	private String	repetSymbol;		// 반복기호
	private String	fieldData;			// 필드 데이터
	private List<MarcSubFieldElement> subFieldList = new ArrayList<MarcSubFieldElement>();

	//private char	sbCode;			    // 식별기호

	/**
	 * 기본생성자
	 */
	public MarcFieldElement(){

	}
	/**
	 * 생성자
	 * @param tagno
	 * @param fIndicator
	 * @param sIndicator
	 * @param repetSymbol
	 * @param subFieldList
	 */
	public MarcFieldElement(Integer	tagno  , String	fIndicator , String	sIndicator ,String	repetSymbol , List<MarcSubFieldElement> subFieldList){

		this.tagno       = tagno      ;
		this.fIndicator  = fIndicator ;
		this.sIndicator  = sIndicator ;
		this.repetSymbol = repetSymbol;
		this.setFieldData (subFieldList);
	}
	/**
	 * 생성자
	 * @param tagno
	 * @param fIndicator
	 * @param sIndicator
	 * @param repetSymbol
	 * @param fieldData
	 */
	public MarcFieldElement(Integer	tagno  , String	fIndicator , String	sIndicator ,String	repetSymbol , String  fieldData){
		this.tagno       = tagno      ;
		this.fIndicator  = fIndicator ;
		this.sIndicator  = sIndicator ;
		this.repetSymbol = repetSymbol;
		this.setFieldData (fieldData);
	}




	/**
	 * @return the iDENTIFIER
	 */
	public String getIDENTIFIER() {
		return IDENTIFIER;
	}
	/**
	 * @param iDENTIFIER the iDENTIFIER to set
	 */
	public void setIDENTIFIER(String iDENTIFIER) {
		IDENTIFIER = iDENTIFIER;
	}
	/**
	 * @return the tagno
	 */
	public Integer getTagno() {
		return tagno;
	}
	/**
	 * @param tagno the tagno to set
	 */
	public void setTagno(Integer tagno) {
		this.tagno = tagno;
	}
	/**
	 * @return the fIndicator
	 */
	public String getFIndicator() {
		return fIndicator;
	}
	/**
	 * @param fIndicator the fIndicator to set
	 */
	public void setFIndicator(String fIndicator) {
		this.fIndicator = fIndicator;
	}
	/**
	 * @return the sIndicator
	 */
	public String getSIndicator() {
		return sIndicator;
	}
	/**
	 * @param sIndicator the sIndicator to set
	 */
	public void setSIndicator(String sIndicator) {
		this.sIndicator = sIndicator;
	}
	/**
	 * @return the repetSymbol
	 */
	public String getRepetSymbol() {
		return repetSymbol;
	}
	/**
	 * @param repetSymbol the repetSymbol to set
	 */
	public void setRepetSymbol(String repetSymbol) {
		this.repetSymbol = repetSymbol;
	}
	/**
	 * @return the subFieldList
	 */
	public List<MarcSubFieldElement> getSubFieldList() {
		return subFieldList;
	}
	/**
	 * @param subFieldList the subFieldList to set
	 */
	public void setSubFieldList(List<MarcSubFieldElement> subFieldList) {
		this.subFieldList = subFieldList;
	}
	/**
	 * 맴버변수초기화 함수
	 */
	public void init(){
		tagno       = null;	// TAG 정보
		fIndicator  = null;	// 제 1지시기호
		sIndicator  = null;	// 제 2지시기호
		repetSymbol = null;	// 반복기호
		setFieldData("");	// 필드 데이터
	}

	public String toString(){
		if( tagno < 10 || tagno == 908 ){
			return this.fieldData;
		}

		StringBuffer fieldData = new StringBuffer();
		fieldData.append( fIndicator );
		fieldData.append( sIndicator );
		for(MarcSubFieldElement item : subFieldList ){
			fieldData.append(this.IDENTIFIER).append(item.getSbCode()).append(item.getSubFieldData());
		}
		return fieldData.toString();
	}
	
	/**
	 * @return 필드 데이터를 반환한다.
	 */
	public String getFieldData() {

		if( tagno < 10 || tagno == 908 ){
			return this.fieldData;
		}

		StringBuffer fieldData = new StringBuffer();
		fieldData.append( fIndicator );
		fieldData.append( sIndicator );

		if( tagno == 245 || tagno == 260 || tagno == 300 ){
			for(int i = 0 ; i < subFieldList.size() ; i++){
				MarcSubFieldElement item = subFieldList.get(i);
				if(i == 0){
					fieldData.append(this.IDENTIFIER);
					fieldData.append(item.getSbCode());
					fieldData.append(item.getSubFieldData());
				}else{
					MarcSubFieldElement prev_item = subFieldList.get(i-1);
					if( isPuncMark(prev_item.getSubFieldData().substring(prev_item.getSubFieldData().length()-1 , prev_item.getSubFieldData().length())) == false
					     && item.getPunkmark() != null
					     && item.getPunkmark().equals("") == false ){
						fieldData.append(item.getPunkmark());
					}
					fieldData.append(this.IDENTIFIER);
					fieldData.append(item.getSbCode());
					fieldData.append(item.getSubFieldData());
				}
			}
		}else{
			for(MarcSubFieldElement item : subFieldList){
				fieldData.append(this.IDENTIFIER).append(item.getSbCode()).append(item.getSubFieldData());
			}
		}

		return fieldData.toString();
	}
	/**
	 * @param fieldData
	 */
	public void setFieldData(String fieldData) {

		this.fieldData = fieldData;

		if(fieldData == null || fieldData.equals("") == true){
			this.subFieldList.clear();
		}else{

			this.subFieldList.clear();
			String t_subFieldDataArray[] = this.fieldData.split(this.IDENTIFIER);
			List<String> subFieldDataArray = new ArrayList<String>();
			for( int i = 0 ; i < t_subFieldDataArray.length ; i++){
				if(t_subFieldDataArray[i].trim().length() > 1){
					subFieldDataArray.add(t_subFieldDataArray[i].trim());
				}
			}


			if(tagno.equals(245) == true || tagno.equals(260) == true  || tagno.equals(300) == true ){
				//기술본체 245, 260, 300 은 구두점을 제대로 관리한다.
//				String check_punkmark = "";

				for( int i = 0 ; i < subFieldDataArray.size() ; i++){

					this.subFieldList.add(new MarcSubFieldElement(subFieldDataArray.get(i).substring(0,1) , subFieldDataArray.get(i).substring(1) , ""));
					//구둣점을 제대로 않쓰는게 정상인건가
//					if(i == 0){
//						this.subFieldList.add(new MarcSubFieldElement(subFieldDataArray.get(i).substring(0,1) , subFieldDataArray.get(i).substring(1) , ""));
//					}else{
//						check_punkmark = subFieldDataArray.get(i).substring(subFieldDataArray.get(i).length()-1, subFieldDataArray.get(i).length());
//						if(isPuncMark( check_punkmark )){
//							this.subFieldList.add(new MarcSubFieldElement(subFieldDataArray.get(i).substring(0,1) , subFieldDataArray.get(i).substring(1 , subFieldDataArray.get(i).length()-1) , check_punkmark));
//						}else{
//							this.subFieldList.add(new MarcSubFieldElement(subFieldDataArray.get(i).substring(0,1) , subFieldDataArray.get(i).substring(1) , ""));
//						}
//					}

//					check_punkmark = "";
				}
			}else{
				for( int i = 0 ; i < subFieldDataArray.size() ; i++){
					this.subFieldList.add(new MarcSubFieldElement( subFieldDataArray.get(i).substring(0,1) , subFieldDataArray.get(i).substring(1),""));
				}
			}
		}

	}

	/**
	 * 하위 서브필드를 입력하여 필드데이터 값을 입력한다.
	 * @param t_subFieldList
	 */
	public void setFieldData(List<MarcSubFieldElement> t_subFieldList ){

		this.subFieldList.clear();

		for(MarcSubFieldElement subFieldElement: t_subFieldList){
			this.subFieldList.add(subFieldElement);
		}

		StringBuffer fieldData = new StringBuffer();
		if( tagno == 245 || tagno == 260 || tagno == 300 ){
			for(int i = 0 ; i < subFieldList.size() ; i++){
				MarcSubFieldElement item = subFieldList.get(i);
				if(i == 0){
					fieldData.append(this.IDENTIFIER);
					fieldData.append(item.getSbCode());
					fieldData.append(item.getSubFieldData());
				}else{
					MarcSubFieldElement prev_item = subFieldList.get(i-1);
					if( isPuncMark(prev_item.getSubFieldData().substring(prev_item.getSubFieldData().length()-1 , prev_item.getSubFieldData().length())) == false
					     && item.getPunkmark() != null
					     && item.getPunkmark().equals("") == false ){
						fieldData.append(item.getPunkmark());
					}
					fieldData.append(this.IDENTIFIER);
					fieldData.append(item.getSbCode());
					fieldData.append(item.getSubFieldData());
				}
			}
		}else{
			for(MarcSubFieldElement item : subFieldList){
				fieldData.append(this.IDENTIFIER).append(item.getSbCode()).append(item.getSubFieldData());
			}
		}

		this.fieldData = fieldData.toString();
	}
	/**
	 * 하위 서브필드 배열중 찾고자 하는 배열요소만 모아서 배열로 전해줌
	 * @param sbcode
	 * @return 서브필드 데이터 배열
	 */
	public String[] getSubFieldList(String sbcode){

		List<String> list = new ArrayList<String>();
		for(MarcSubFieldElement item : subFieldList){

			if(item.getSbCode().equals(sbcode) == true){
				list.add(item.getSubFieldData());
			}
		}
		String[] templist = new String[list.size()];
		for(int i = 0 ; i < templist.length ; i++){

			templist[i] = list.get(i);
		}

		return  templist;

	}

	/**
	 * 하위 서브필드 리스트에 서브필드를 추가한다.
	 * @param subFieldElement
	 */
	public void addSubFieldList(MarcSubFieldElement subFieldElement){
		subFieldList.add(subFieldElement);
	}

	/**
	 * 하위 서브필드 리스트에 서브필드를 추가한다.
	 * @param sbCode
	 * @param subFieldData
	 * @param punkmark
	 */
	public void addSubFieldList(String sbCode, String subFieldData, String punkmark){
		MarcSubFieldElement subFieldElement = new MarcSubFieldElement();

		subFieldElement.setSbCode(sbCode);
		subFieldElement.setSubFieldData(subFieldData);
		subFieldElement.setPunkmark(punkmark);

		subFieldList.add(subFieldElement);
	}

	/**
	 * 원하는 index에 하위 서브필드 리스트에 서브필드를 추가한다.
	 * @param index
	 * @param sbCode
	 * @param subFieldData
	 */
	public void addSubFieldList(int index , String sbCode, String subFieldData){
		MarcSubFieldElement subFieldElement = new MarcSubFieldElement();

		subFieldElement.setSbCode(sbCode);
		subFieldElement.setSubFieldData(subFieldData);

		subFieldList.add(index, subFieldElement);
	}

	/**
	 * 원하는 index에 하위 서브필드 리스트에 서브필드를 추가한다.
	 * @param index
	 * @param sbCode
	 * @param subFieldData
	 * @param punkmark
	 */
	public void addSubFieldList(int index , String sbCode, String subFieldData, String punkmark){
		MarcSubFieldElement subFieldElement = new MarcSubFieldElement();

		subFieldElement.setSbCode(sbCode);
		subFieldElement.setSubFieldData(subFieldData);
		subFieldElement.setPunkmark(punkmark);

		subFieldList.add(index, subFieldElement);
	}
	/**
	 * 입력된 식별기호로 되어있는 서브필드의 위치를 찾는다.
	 * @param sbCode
	 * @return 서브필드위치
	 */
	public int findIndexOfSbCode(String sbCode){

		for(int i = 0 ; i < subFieldList.size() ; i ++){
			if(subFieldList.get(i).getSbCode().equals(sbCode) == true){
				return i;
			}
		}

		return -1;
	}
	/**
	 * 입력된 식별기호의 처음 데이터를 리턴한다.
	 * @param sbCode
	 * @return 서브필드
	 */
	public MarcSubFieldElement getSubFieldElement(String sbCode){

		for(int i = 0 ; i < subFieldList.size() ; i ++){
			if(subFieldList.get(i).getSbCode().equals(sbCode) == true){
				return subFieldList.get(i);
			}
		}
		return null;
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
		  || convertChar.equals(".") == true
		  || convertChar.equals(",") == true ) {
			return true;
		}

		return false;
	}
	
	/**
	 * 입력된 식별기호의 마지막 위치를 찾는다
	 * @param sbCode
	 * @return
	 */
	public int last_findIndexOfSbCode(String sbCode){
		
		int result = 0;
		
		for(int i = 0 ; i < subFieldList.size() ; i ++){
			if(subFieldList.get(i).getSbCode().equals(sbCode) == true){
				if(result == 0) {
					result = i;
				}else {
					result = result+1;
				}
			}
		}

		if(result == 0) {			
			return -1;
		}else {
			return result;
		}
	}
	
	public int last_findIndexOfSbCode2(String sbCode){
		
		for(int i = 0 ; i < subFieldList.size() ; i ++){
			if(subFieldList.get(i).getSbCode().equals(sbCode) == true){
				return i;
			}
		}

		return -1;

	}
}
