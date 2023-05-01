package com.trans.migration.slima.book.chunk;

import com.trans.migration.code.*;
import com.trans.migration.exception.BookException;
import com.trans.migration.marc.StringUtil;
import com.trans.migration.marc.index.service.MarcIndexService;
import com.trans.migration.marc.marc.struct.MarcStru;
import com.trans.migration.marc.marc.struct.service.MarcStructService;
import com.trans.migration.slima.book.domain.*;
import jakarta.annotation.Resource;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BookItemProcessor implements ItemProcessor<SlimSpecies, BoSpecies> {

    @Resource(name="MarcStructService")
    private MarcStructService marcStructService;

    @Resource(name="MarcIndexService")
    private MarcIndexService marcIndexService;

    @Resource(name="StringUtil")
    private StringUtil stringUtil;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    public BookItemProcessor(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public BoSpecies process(SlimSpecies item) throws Exception {


        MarcStru marcStru = new MarcStru();
        Map<String,String> marcResult = new HashMap<>();

        String marc = item.getMarc();
        String firstWork = "";

        marcResult = marcStructService.readMarcStruFromStreamMarc(marc, marcStru);

        BoSpecies boSpecies = new BoSpecies();
        if(marcResult.get("status").equals("SUCCESS") == false ) {
            item.setErrorMsg(marcResult.get("statusDescription").toString());
            throw new BookException(marcResult.get("statusDescription").toString());
        }

        List<SlimBook> slimBooks = getSlimBook(item.getSlimSpeciesKey());
        List<SlimAppendix> slimAppendices = getSlimAppendix(item.getSlimSpeciesKey());
        /*
        * 0. 마크에서 값 추출
        * */

        //TODO classNo,classNoType 값 마크에서 꺼내서 입력할 지 아님 책정보에서 꺼내서 입력할지 확인 필요
        String workCode = "";
        String classNoType = "";
        String classNo = "";
        String kdcClassNo = "";
        String ddcClassNo = "";
        String etcClassNo = "";

        classNo	    =  marcStructService.getSubFieldData(90, 'a', marcStru);
        kdcClassNo  =  marcStructService.getSubFieldData(56, 'a', marcStru);
        ddcClassNo  =  marcStructService.getSubFieldData(82, 'a', marcStru);
        etcClassNo  =  marcStructService.getSubFieldData(85, 'a', marcStru);

        if(classNo == null) classNo = "";
        if(kdcClassNo == null) kdcClassNo = "";
        if(ddcClassNo == null) ddcClassNo = "";
        if(etcClassNo == null) etcClassNo = "";

        if(classNo.equals("") == false){
            if(kdcClassNo.equals("") == false && classNo.equals(kdcClassNo) == true )		{ classNoType = "1";}
            else if(ddcClassNo.equals("") == false && classNo.equals(ddcClassNo) == true )	{ classNoType = "2";}
            else if(etcClassNo.equals("") == false && classNo.equals(etcClassNo) == true )	{ classNoType = "7";}
        }

        String kdcVer = marcStructService.getSubFieldData(56, '2', marcStru);
        String ddcVer = marcStructService.getSubFieldData(82, '2', marcStru);

        String pubYear          = marcStructService.getPublish_year(marcStru);
        String author           = marcStructService.getAuthor(marcStru);
        String vol              = marcStructService.getVol(marcStru);
        String volTitle         = marcStructService.getVol_title(marcStru);
        String page             = marcStructService.getPage(marcStru);
        String bookSize         = marcStructService.getBook_size(marcStru);
        String eaIsbn           = marcStructService.getEa_isbn(marcStru);
        String eaIsbnAddCode    = marcStructService.getEa_isbn_add_code(marcStru);
        String bookCode         = marcStructService.getBook_code(marcStru);
        String priceOtherInfo   = marcStructService.getPrice_other_info(marcStru);
        String physicalProperty = marcStructService.getPhysical_property(marcStru);
        String accompMat        = marcStructService.getAccomp_mat(marcStru);
        String priceCharacter   = marcStructService.getPrice_character(marcStru);
        String price            = marcStructService.getPrice(marcStru);

        /*
        * 1. 종정보 입력
        * */
        /**종 고정값
         * 0.AcqCode        : 수입구분      -> 책정보
         * 1.ClassNoType    : 분류표구분    -> 종정보
         * 2.ControlMatCode : 제어자료구분  -> 종정보
         * 3.FormCode       : 형식구분      -> 종정보
         * 4.MatCode        : 자료구분      -> 종정보
         * 5.MediaCode      : 매체구분       -> 종정보,책정보
         * 6.SubRegCode     : 보조등록구분    -> 종정보
         * 7.UseLimitCode   : 이용제한구분    -> 종정보,책정보
         * 8.UseObjCode     : 이용대상구분    -> 종정보,책정보
         * */

        //종정보 색인정보 공통값
        String matCode = FormCode.BK.getCode();
        String formCode = MatCode.GM.getCode();
        String mediaCode = MediaCode.PR.getCode();
        String subRegCode = SubRegCode.EL.getCode();
        String useLimitCode = UseLimitCode.GM.getCode();
        String useObjCode = UseObjCode.PU.getCode();

        String controlMatCode = ControlMatCode.KMO.getCode();

        boSpecies.setRecKey(item.getSlimSpeciesKey());
        boSpecies.setControlMatCode(controlMatCode); // -> 단행본 기본은 KMO
        boSpecies.setFormCode(formCode); // 기본값은 도서 어떤값 넣을지 확인필요 (SLIM 에 정보 없음)
        boSpecies.setMatCode(matCode); // 기본값은 일반도서 어떤값 넣을지 확인필요 (SLIM 에 정보 없음)
        boSpecies.setMediaCode(mediaCode); //기본값은 인쇄자료형 , 비도서일 경우 ,SLIM 종 정보에따라 값 변동
        boSpecies.setSubRegCode(subRegCode); //동양 서양 구분 ,SLIM 종 정보에따라 값 변동
        boSpecies.setUseLimitCode(useLimitCode); //기본값은 일반, SLIM 종이나 책 정보에따라 값 변동
        boSpecies.setUseObjCode(useObjCode); //기본값은 일반 ,SLIM 종이나 책 정보에따라 값 변동 (SLIM 에 정보 없음)

        String manageCode = "";
        boSpecies.setManageCode(manageCode); //SLIM 책 정보에 LIB_CODE 에 따라 변동

        //마크작업 후 입력
        boSpecies.setClassNoType(classNoType); //마크에서 090,056,082,085 정보에서 꺼내서 입력
        boSpecies.setSubjectCode(classNo.equals("") ? null : classNo.substring(0,1)); // 090 앞자리
        boSpecies.setKdcClassNo(kdcClassNo);
        boSpecies.setKdcClassNoVer(kdcVer);
        boSpecies.setDdcClassNo(ddcClassNo);
        boSpecies.setDdcClassNoVer(ddcVer);
        boSpecies.setEctClassNo(etcClassNo);
        boSpecies.setWorkCode(workCode);


        boSpecies.setPriorityYn("N");
        boSpecies.setCatalogStatus(""); //목록작업상태 확인 (0:정리안됨,1:정리됨)
        boSpecies.setWorker("ALPAS");
        boSpecies.setFirstWork(firstWork);

        boSpecies.setAcqYear(""); // 구입정보의 수입년도에 따라 변동

        String controlNo = getControlNo();
        boSpecies.setControlNo(controlNo);

        /*
         * 2. 색인정보 입력
         * */
        IdxBo idxBo = new IdxBo();
        idxBo.setRecKey(item.getSlimSpeciesKey());
        idxBo.setFormCode(formCode);
        idxBo.setMatCode(matCode);
        idxBo.setMediaCode(mediaCode);
        idxBo.setSubRegCode(subRegCode);
        idxBo.setUseLimitCode(useLimitCode);
        idxBo.setUseObjCode(useObjCode);
        idxBo.setWorkCode(workCode);
        idxBo.setManageCode(manageCode);
        idxBo.setIdxTitle(marcIndexService.makeIDX_TITLE(marcStru));
        idxBo.setIdxDupTitle(marcIndexService.makeIDX_DUP_TITLE(marcStru));
        idxBo.setIdxKeyword(marcIndexService.makeIDX_KEYWORD(marcStru));
        idxBo.setIdxAuthor(marcIndexService.makeIDX_AUTHOR(marcStru));
        idxBo.setIdxPublisher(marcIndexService.makeIDX_PUBLISHER(marcStru));
        idxBo.setIdxAllItem(marcIndexService.makeIDX_ALL_ITEM(marcStru));
        idxBo.setIdxVol(marcIndexService.makeIDX_VOL(marcStru));
        idxBo.setIdxIcs(marcIndexService.makeIDX_ICS(marcStru));
        idxBo.setIdxStandardno(marcIndexService.makeIDX_STANDARDNO(marcStru));
        idxBo.setIdxItitle(marcIndexService.makeIDX_ITITLE(marcStru));
        idxBo.setIdxIpublisher(marcIndexService.makeIDX_IPUBLISHER(marcStru));
        idxBo.setIdxIpubYear(marcIndexService.makeIDX_IPUB_YEAR(marcStru));
        idxBo.setTitleInfo(marcIndexService.makeTITLE_INFO(marcStru));
        idxBo.setAuthorInfo(marcIndexService.makeAUTHOR_INFO(marcStru));
        idxBo.setPubInfo(marcIndexService.makePUB_INFO(marcStru));
        idxBo.setPubYearInfo(marcIndexService.makePUB_YEAR_INFO(marcStru));
        idxBo.setEditInfo(marcIndexService.makeEDIT_INFO(marcStru));
        idxBo.setIcsInfo(marcIndexService.makeICS_INFO(marcStru));
        idxBo.setStandardnoInfo(marcIndexService.makeSTANDARDNO_INFO(marcStru));
        idxBo.setTitle(marcIndexService.makeTITLE(marcStru));
        idxBo.setAuthor(marcIndexService.makeAUTHOR(marcStru));
        idxBo.setPublisher(marcIndexService.makePUBLISHER(marcStru));
        idxBo.setPubYearInfo(marcIndexService.makePUB_YEAR_INFO(marcStru));
        idxBo.setStCode(marcIndexService.makeST_CODE(marcStru));
        idxBo.setStIssn(marcIndexService.makeST_ISSN(marcStru));
        idxBo.setStStrn(marcIndexService.makeST_STRN(marcStru));
        idxBo.setStRnstrn(marcIndexService.makeST_RNSTRN(marcStru));
        idxBo.setStCbn(marcIndexService.makeST_CBN(marcStru));
        idxBo.setStCan(marcIndexService.makeST_CAN(marcStru));
        idxBo.setClassNo(marcIndexService.makeCLASS_NO(marcStru));
        idxBo.setBookCode(marcIndexService.makeBOOK_CODE(marcStru));
        idxBo.setVolCode(marcIndexService.makeVOL_CODE(marcStru));
        idxBo.setVolCodeDisp(marcIndexService.makeVOL_CODE_DISP(marcStru));
        idxBo.setKdcpClass(marcIndexService.makeKDCP_CLASS(marcStru));
        idxBo.setKdcClass(marcIndexService.makeKDC_CLASS(marcStru));
        idxBo.setDdcClass(marcIndexService.makeDDC_CLASS(marcStru));
        idxBo.setCecClass(marcIndexService.makeCEC_CLASS(marcStru));
        idxBo.setCwcClass(marcIndexService.makeCWC_CLASS(marcStru));
        idxBo.setCocClass(marcIndexService.makeCOC_CLASS(marcStru));
        idxBo.setUdcClass(marcIndexService.makeUDC_CLASS(marcStru));
        idxBo.setNdcClass(marcIndexService.makeNDC_CLASS(marcStru));
        idxBo.setLcClass(marcIndexService.makeLC_CLASS(marcStru));
        idxBo.setEtcClass(marcIndexService.makeETC_CLASS(marcStru));
        idxBo.setUniCode(marcIndexService.makeUNI_CODE(marcStru));
        idxBo.setGovCode(marcIndexService.makeGOV_CODE(marcStru));
        idxBo.setDegCode(marcIndexService.makeDEG_CODE(marcStru));
        idxBo.setEdit(marcIndexService.makeEDIT(marcStru));
        idxBo.setTextLang(marcIndexService.makeTEXT_LANG(marcStru));
        idxBo.setSumLang(marcIndexService.makeSUM_LANG(marcStru));
        idxBo.setPublishCountryCode(marcIndexService.makePUBLISH_COUNTRY_CODE(marcStru));
        idxBo.setEditNo(marcIndexService.makeEdit_no(marcStru));
        idxBo.setSeriesTitle( marcStructService.getSeries_title(marcStru));
        idxBo.setSeriesVol(marcStructService.getSeries_vol(marcStru));
        idxBo.setCurrencySign(marcStructService.getCurrency_code(marcStru));
        boSpecies.setIdxbo(idxBo);

        //마크 재생성 할것들
        String setIsbn          = marcStructService.getSet_isbn(marcStru);
        String setIsbnAddCode = marcStructService.getSet_isbn_add_code(marcStru);
        if(setIsbn == null) setIsbn = "";
        if(setIsbnAddCode == null) setIsbnAddCode = "";

        if(setIsbn.equals("") == false){
            marcStructService.removeTagNo(20, marcStru);
            marcStructService.setSetISBN(setIsbn, setIsbnAddCode, marcStru);
        }

        int count = 0 ;
        String errorMsg = "";
        boolean isError = false;
        List<SlimBook> errorBooks = new ArrayList<>();
        for (SlimBook slimBook : slimBooks) {
            BoBook boBook = new BoBook();
            boBook.setRecKey(slimBook.getSlimBookKey());
            boBook.setAcqKey(null); //구입정보에서 어떻게 가져올지 확인
            boBook.setSpeciesKey(item.getSlimSpeciesKey());
            boBook.setAccessionRecKey(null); //원부정보에서 키값

            String regDateStr      = slimBook.getRegDate();
            String shelfDateStr    = slimBook.getShelfDate();

            try{
                Date regDate = formatter.parse(regDateStr);
                boBook.setRegDate(regDate);
                boBook.setInputDate(regDate);
                if(count == 0) boSpecies.setInputDate(regDate);
            }catch(ParseException e){
                isError = true;
                if(errorMsg.equals("")) errorMsg += "등록일 포멧 오류";
                else errorMsg += ","+"등록일 포멧 오류";
            }   
            try{
                Date shelfDate = formatter.parse(shelfDateStr);
                boBook.setShelfDate(shelfDate);
            }catch(ParseException e){
                isError = true;
                if(errorMsg.equals("")) errorMsg += "배가일 포멧 오류";
                else errorMsg += ","+"배가일 포멧 오류";
            }

            if(count == 0) boBook.setRepresentBookYn("Y");
            boBook.setBookAppendixFlag("B");

            //TODO 자료상태값 어떻게 지정하는지 확인필요
            String workingStatus = slimBook.getBookStatus();
            if(workingStatus.equals("")){
                boBook.setWorkingStatus("BOL112N");
            }
            
            //TODO 등록번호 변환해서 입력
            String regNo    = slimBook.getRegNo();
            String regCode  = slimBook.getRegCode();
            boBook.setRegNo(regNo); //등록번호 길이 변경해서 입력
            boBook.setRegCode(regCode);
            
            //TODO 수입구분 코드값 어떻게 넣을지 확인필요
            boBook.setAcqCode(null); //구입정보에서 가져와서 입력
            boBook.setAcqYear(null); //구입정보에서 가져와서 입력

            boBook.setManageCode(manageCode);
            boBook.setMediaCode(mediaCode);
            boBook.setUseLimitCode(useLimitCode);
            boBook.setUseObjectCode(useObjCode);
            boBook.setVolSortNo(10);
            boBook.setAuthor(slimBook.getAuthor());
            boBook.setVol(slimBook.getVol());
            boBook.setVolIndex(slimBook.getVolIndex());
            boBook.setVolTitle(slimBook.getVolTitle());
            boBook.setVolTitleIndex(slimBook.getVolTitleIndex());

            if(slimBook.getPublishYear() == null ) boBook.setPublishYear(pubYear);
            else boBook.setPublishYear(slimBook.getPublishYear());

            String publishDate = slimBook.getPublishDate();
            try{
                Date date = formatter.parse(publishDate);
                boBook.setPublishDate(date);
            }catch(ParseException e){
                isError = true;
                if(errorMsg.equals("")) errorMsg += "출판일 포멧 오류";
                else errorMsg += ","+"출판일 포멧 오류";
            }

            if(slimBook.getPage() == null ) boBook.setPage(page);
            else boBook.setPage(slimBook.getPage());

            if(slimBook.getPhysicalProperty() == null )boBook.setPhysicalProperty(physicalProperty);
            else boBook.setPhysicalProperty(slimBook.getPhysicalProperty());

            if(slimBook.getAccompMat() == null )boBook.setAccompMat(physicalProperty);
            else boBook.setAccompMat(slimBook.getPhysicalProperty());

            try{
                if(slimBook.getPrice() == null )boBook.setPrice(Integer.parseInt(price));
                else boBook.setPrice(Integer.parseInt(slimBook.getPrice()));
            }catch(NumberFormatException e){
                isError = true;
                if(errorMsg.equals("")) errorMsg += "가격 포멧 오류";
                else errorMsg += ","+"가격 포멧 오류";
            }

            if(slimBook.getPriceCharacter() == null )boBook.setPriceCharacter(priceCharacter);
            else boBook.setPriceCharacter(slimBook.getPriceCharacter());

            if(slimBook.getPriceOtherInfo() == null )boBook.setPriceOtherInfo(priceOtherInfo);
            else boBook.setPriceOtherInfo(slimBook.getPriceOtherInfo());

            if(slimBook.getEaIsbn() == null )boBook.setEaIsbn(eaIsbn);
            else boBook.setEaIsbn(slimBook.getEaIsbn());

            boBook.setEaAddCode(eaIsbnAddCode);

            boBook.setSeparateShelfCode(slimBook.getSeparateShelfCode());

            if(slimBook.getClassNoType() == null )boBook.setClassNoType(classNoType);
            else boBook.setClassNoType(slimBook.getClassNoType());

            if(slimBook.getClassNo() == null )boBook.setClassNo(classNo);
            else boBook.setClassNo(slimBook.getClassNo());

            if(slimBook.getBookCode() == null )boBook.setBookCode(bookCode);
            else boBook.setBookCode(slimBook.getBookCode());

            boBook.setVolCode(slimBook.getVolNo());
            boBook.setCopyCode(slimBook.getCopyNo());

            //TODO 값 어떻게 넣을지 확인 필요
            boBook.setCatTransferDate(null);
            boBook.setCatCompleteDate(null);
            boBook.setLocTransferDate(null);

            //TODO 자료실값 어떻게 넣을지 확인필요
            boBook.setShelfLocCode(null);
            if(count == 0)  boSpecies.setShelfLocCode(null);

            boBook.setFirstWork(null);
            boBook.setWorkno(slimBook.getWorkNo());

            //TODO 부록정보 입력
            for (SlimAppendix slimAppendix : slimAppendices) {
                String bookRegNo = slimAppendix.getBookRegNo();
                if(regNo.equals(bookRegNo)){
                    BoBook appendix = new BoBook();
                    appendix.setRecKey(slimAppendix.getAppendixKey());
                }
            }

            boSpecies.getBoBookList().add(boBook);
            if(count == 0) count++;
            if(isError == true) {
                slimBook.setErrorMsg(errorMsg);
                errorBooks.add(slimBook);
            }
        }


        if(errorBooks.size() > 0){
            item.setErrorBooks(errorBooks);
            throw new BookException("책 데이터변환 오류 발생");
        }

        return boSpecies;
    }

    private List<SlimBook> getSlimBook(Long speciesKey){
        String sql = "SELECT * FROM SLIM_BOOK WHERE SLIM_SPECIES_KEY = :speciesKey ORDER BY REG_NO";
        Map<String,Object> params = new HashMap<>();
        params.put("speciesKey",speciesKey);
        BeanPropertyRowMapper<SlimBook> slimBookBeanPropertyRowMapper = BeanPropertyRowMapper.newInstance(SlimBook.class);
        List<SlimBook> slimBooks = namedParameterJdbcTemplate.query(sql, params, slimBookBeanPropertyRowMapper);

        return slimBooks;
    }

    private List<SlimAppendix> getSlimAppendix(Long speciesKey){
        String sql = "SELECT * FROM SLIM_APPENDIX WHERE SLIM_SPECIES_KEY = :speciesKey ORDER BY REG_NO";
        Map<String,Object> params = new HashMap<>();
        params.put("speciesKey",speciesKey);
        BeanPropertyRowMapper<SlimAppendix> slimBookBeanPropertyRowMapper = BeanPropertyRowMapper.newInstance(SlimAppendix.class);
        List<SlimAppendix> slimAppendices = namedParameterJdbcTemplate.query(sql, params, slimBookBeanPropertyRowMapper);

        return slimAppendices;
    }

    //제어번호 어떻게 넣을지 고민해봐야함
    private String getControlNo(){

        return "";
    }



}
