package com.trans.migration.marc;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component("StringUtil")
public class StringUtil {

    /** HTML 줄바꿈 문자. */
    private final String BR   = "<br>";

    /** 줄바꿈 문자. */
    private final String CRLF = "\r\n";

    /** 특수기호 코드 배열. */
    private char DELETE_SPECIAL_CHAR[][] = null;

    /** 한자를 한글로 변환 UTIL 객체 */
    @Resource(name = "HanjaToHangleInfo")
    private HanjaToHangleInfo hanjaToHangleInfo;

    private static final List<String> rollWordList = new ArrayList<>(Arrays.asList("illustratedby","ilustratedby","...\'[등\']옮김","...\'[등\']공역","...\'[등\']그림","\'[같이\']옮김","\'[같이\']지음","...\'[등\']","\'[옮김_\']","\'[공\']편저","\'[공\']지음","글·그림","\'[공\']옮김","만든이","\'[외\']","\'[편\']","글사진","지은이","by","옮김","지음","엮음","그림","저자","공역","번역","외","글","역","저","강"));


    /**
     * 생성자 [특수기호 코드값 배열 정보를 메모리로올린다.]
     */
    public StringUtil() {
        int INT_DELETE_SPECIAL_CHAR[][] = new int[39][2];
        INT_DELETE_SPECIAL_CHAR[0] [0]= 0x0000; INT_DELETE_SPECIAL_CHAR[0] [1]=0x001F;
        INT_DELETE_SPECIAL_CHAR[1] [0]= 0x0020; INT_DELETE_SPECIAL_CHAR[1] [1]=0x002F;
        INT_DELETE_SPECIAL_CHAR[2] [0]= 0x003A; INT_DELETE_SPECIAL_CHAR[2] [1]=0x0040;
        INT_DELETE_SPECIAL_CHAR[3] [0]= 0x005B; INT_DELETE_SPECIAL_CHAR[3] [1]=0x0060;
        INT_DELETE_SPECIAL_CHAR[4] [0]= 0x007B; INT_DELETE_SPECIAL_CHAR[4] [1]=0x00BF;
        INT_DELETE_SPECIAL_CHAR[5] [0]= 0x0220; INT_DELETE_SPECIAL_CHAR[5] [1]=0x0221;
        INT_DELETE_SPECIAL_CHAR[6] [0]= 0x02B9; INT_DELETE_SPECIAL_CHAR[6] [1]=0x02BF;
        INT_DELETE_SPECIAL_CHAR[7] [0]= 0x02C2; INT_DELETE_SPECIAL_CHAR[7] [1]=0x02DF;
        INT_DELETE_SPECIAL_CHAR[8] [0]= 0x02E5; INT_DELETE_SPECIAL_CHAR[8] [1]=0x02EE;
        INT_DELETE_SPECIAL_CHAR[9] [0]= 0x0300; INT_DELETE_SPECIAL_CHAR[9] [1]=0x036F;
        INT_DELETE_SPECIAL_CHAR[10][0]= 0x2000; INT_DELETE_SPECIAL_CHAR[10][1]=0x206F;
        INT_DELETE_SPECIAL_CHAR[11][0]= 0x2070; INT_DELETE_SPECIAL_CHAR[11][1]=0x209F;
        INT_DELETE_SPECIAL_CHAR[12][0]= 0x20A0; INT_DELETE_SPECIAL_CHAR[12][1]=0x20CF;
        INT_DELETE_SPECIAL_CHAR[13][0]= 0x20D0; INT_DELETE_SPECIAL_CHAR[13][1]=0x20FF;
        INT_DELETE_SPECIAL_CHAR[14][0]= 0x2190; INT_DELETE_SPECIAL_CHAR[14][1]=0x21FF;
        INT_DELETE_SPECIAL_CHAR[15][0]= 0x2200; INT_DELETE_SPECIAL_CHAR[15][1]=0x22FF;
        INT_DELETE_SPECIAL_CHAR[16][0]= 0x2300; INT_DELETE_SPECIAL_CHAR[16][1]=0x23FF;
        INT_DELETE_SPECIAL_CHAR[17][0]= 0x2400; INT_DELETE_SPECIAL_CHAR[17][1]=0x243F;
        INT_DELETE_SPECIAL_CHAR[18][0]= 0x2440; INT_DELETE_SPECIAL_CHAR[18][1]=0x245F;
        INT_DELETE_SPECIAL_CHAR[19][0]= 0x2460; INT_DELETE_SPECIAL_CHAR[19][1]=0x24FF;
        INT_DELETE_SPECIAL_CHAR[20][0]= 0x2500; INT_DELETE_SPECIAL_CHAR[20][1]=0x257F;
        INT_DELETE_SPECIAL_CHAR[21][0]= 0x2580; INT_DELETE_SPECIAL_CHAR[21][1]=0x259F;
        INT_DELETE_SPECIAL_CHAR[22][0]= 0x25A0; INT_DELETE_SPECIAL_CHAR[22][1]=0x25FF;
        INT_DELETE_SPECIAL_CHAR[23][0]= 0x2600; INT_DELETE_SPECIAL_CHAR[23][1]=0x26FF;
        INT_DELETE_SPECIAL_CHAR[24][0]= 0x2700; INT_DELETE_SPECIAL_CHAR[24][1]=0x27CF;
        INT_DELETE_SPECIAL_CHAR[25][0]= 0x2800; INT_DELETE_SPECIAL_CHAR[25][1]=0x28FF;
        INT_DELETE_SPECIAL_CHAR[26][0]= 0x3000; INT_DELETE_SPECIAL_CHAR[26][1]=0x303F;
        INT_DELETE_SPECIAL_CHAR[27][0]= 0x3200; INT_DELETE_SPECIAL_CHAR[27][1]=0x32FF;
        INT_DELETE_SPECIAL_CHAR[28][0]= 0xA000; INT_DELETE_SPECIAL_CHAR[28][1]=0xA48F;
        INT_DELETE_SPECIAL_CHAR[29][0]= 0xA490; INT_DELETE_SPECIAL_CHAR[29][1]=0xA4CF;
        INT_DELETE_SPECIAL_CHAR[30][0]= 0xF000; INT_DELETE_SPECIAL_CHAR[30][1]=0xF0FF;
        INT_DELETE_SPECIAL_CHAR[31][0]= 0xFE20; INT_DELETE_SPECIAL_CHAR[31][1]=0xFE2F;
        INT_DELETE_SPECIAL_CHAR[32][0]= 0xFE30; INT_DELETE_SPECIAL_CHAR[32][1]=0xFE4F;
        INT_DELETE_SPECIAL_CHAR[33][0]= 0xFE50; INT_DELETE_SPECIAL_CHAR[33][1]=0xFE6F;
        INT_DELETE_SPECIAL_CHAR[34][0]= 0xFF00; INT_DELETE_SPECIAL_CHAR[34][1]=0xFF0F;
        INT_DELETE_SPECIAL_CHAR[35][0]= 0xFF1A; INT_DELETE_SPECIAL_CHAR[35][1]=0xFF20;
        INT_DELETE_SPECIAL_CHAR[36][0]= 0xFF3B; INT_DELETE_SPECIAL_CHAR[36][1]=0xFF40;
        INT_DELETE_SPECIAL_CHAR[37][0]= 0xFF5B; INT_DELETE_SPECIAL_CHAR[37][1]=0xFF65;
        INT_DELETE_SPECIAL_CHAR[38][0]= 0xFFE0; INT_DELETE_SPECIAL_CHAR[38][1]=0xFFFF;

        if(DELETE_SPECIAL_CHAR==null){
            DELETE_SPECIAL_CHAR = new char[39][2];
            for( int index = 0; index < 39; index++ )
            {
                DELETE_SPECIAL_CHAR[index][0] = (char)INT_DELETE_SPECIAL_CHAR[index][0];
                DELETE_SPECIAL_CHAR[index][1] = (char)INT_DELETE_SPECIAL_CHAR[index][1];
            }
        }

    }


    /**
     *
     * 문자열 NULL 검사.
     *
     * @param targetStr NULL 체크 대상 문자열
     * @return [NULL: TRUE NOT_NULL: FALSE]
     *
     */
    public boolean isNull(String targetStr)
    {
        if(targetStr == null || targetStr.trim().length() == 0)
        {
            return true;
        }
        return false;
    }


    /**
     * *
     * * 문자열에 숫자가 하나라도 존재하는지 확인한다.
     *
     * @param targetStr 숫자가포함된 문자열 체크 대상 문자열
     * @return [有: TRUE 無: FALSE]
     *         **
     *
     */
    public boolean isContainsNumber(String targetStr)
    {
        if(isNull(targetStr) == true)
        {
            return false;
        }
        else
        {
            for(int idx = 0; idx <  targetStr.trim().length(); idx++)
            {
                if(Character.isDigit(targetStr.charAt(idx)) == true)
                    return true;
            }
            return false;
        }
    }

    /**
     *
     * 파라미터가 숫자로된 문자열인인지 확인.
     *
     * @param targetStr 숫자로된 문자열 체크 대상 문자열
     * @return [有: TRUE 無: FALSE]
     *
     *
     */
    public boolean isNumber(String targetStr)

    {
        if(isNull(targetStr) == true)
        {
            return false;
        }
        else
        {
            for( int idx = 0; idx <  targetStr.length(); idx++)
            {
                if(Character.isDigit(targetStr.charAt(idx)) == true)
                {
                    continue;
                }
                else
                {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 문자열 내에서 숫자만 추출한다.
     * @param targetStr
     * @return 숫자로이루어진 문자열
     */
    public String extractNumber(String targetStr){
        String destStr = "";
        if(isNull(targetStr) == true)
        {
            return destStr;
        }
        else
        {
            for( int idx = 0; idx <  targetStr.length(); idx++)
            {
                if(Character.isDigit(targetStr.charAt(idx)) == true)
                {
                    destStr+=String.valueOf(targetStr.charAt(idx));
                }
                else
                {
                    continue;
                }
            }
            return destStr;
        }
    }

    /**
     *
     * 문자열에 숫자문자열이 있는지 확인하고 삭제한다.
     *
     * @param targetStr 숫자 존재유무 체크 대상 문자열
     * @return 성공: 제거완료 문자열 실패: NULL
     *
     */
    public String RemoveNumber(String targetStr)
    {
        String resultData = "";

        if(isNull(targetStr) == true)
        {
            return null;
        }
        else
        {
            resultData = targetStr;

            for(int i=0x30; i<= 0x39 ; i++ ){

                resultData = resultData.replaceAll(""+((char)i), "");
            }
            for(int i=0xFF10 ; i<= 0xFF19 ; i++){

                resultData = resultData.replaceAll(""+((char)i), "");
            }


        }
        return resultData;
    }

    /**
     *
     * 문자열에 알파벳이 하나라도 존재하는지 확인한다.
     *
     * @param targetStr 알파벳 존재유무 체크 대상 문자열
     * @return [有: TRUE 無: FALSE]
     *
     *
     */
    public boolean isContainsAlpha(String targetStr)
    {
        if(isNull(targetStr) == true)
        {
            return false;
        }
        else
        {
            for(int idx = 0; idx < targetStr.trim().length(); idx++)
            {
                char tempAlpha = 0;
                tempAlpha = targetStr.charAt(idx);

                if((tempAlpha > 0x40 && tempAlpha < 0x5B ) ||
                        (tempAlpha > 0x60 && tempAlpha < 0x7B))
                {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     *
     * 문자열에 알파벳을 모두 제거한 나머지 데이터를 반환한다.
     *
     * @param targetStr
     *            the target str
     * @return 문자열에 알파벳을 모두 제거한 나머지 데이터
     *
     */
    public String removeAlpha(String targetStr)
    {
        StringBuffer resultBuffer = new StringBuffer();

        if(isNull(targetStr) == true)
        {
            return null;
        }
        for(int idx = 0; idx < targetStr.trim().length(); idx++)
        {
            char tempAlpha = 0;
            tempAlpha = targetStr.charAt(idx);

            if((tempAlpha > 0x41 && tempAlpha < 0x5a ) ||
                    (tempAlpha > 0x61 && tempAlpha < 0x7a))
            {
                continue;
            }
            resultBuffer.append(tempAlpha);
        }
        return resultBuffer.toString();
    }


    /**
     *
     *  전각문자를 반각문자로 변경한다. 반각문자는 전각문자보다 65248(0xfee0)이 작다.
     *
     * @param zenKaKuChar
     *            the zen ka ku char
     * @return 성공: 반각문자열 실패: NULL
     *
     */
    public String zenKaKuToHanKaKuFrom(String zenKaKuChar)
    {
        StringBuffer hanKaKuChar = new StringBuffer();

        char zenKaKuCharAt = 0;
        int  nSrcLength    = 0;

        if(zenKaKuChar != null && zenKaKuChar.trim().equals("") == false)
        {
            nSrcLength = zenKaKuChar.length();

            //0x80 보다 작으면 반각
            for (int i = 0; i < nSrcLength; i++)
            {
                zenKaKuCharAt = zenKaKuChar.charAt(i);

                //'！' ~ '～' 범위의 전각문자인 경우
                if (zenKaKuCharAt >= 0xff01 && zenKaKuCharAt <= 0xff5d)
                {
                    zenKaKuCharAt -= 0xfee0;
                }
                else if (zenKaKuCharAt == 0x3000)
                {
                    zenKaKuCharAt = 0x20;
                }
                else
                {
                    continue;
                }
                hanKaKuChar.append(zenKaKuCharAt);
            }
        }
        else
        {
            return "";
        }
        return hanKaKuChar.toString();
    }


    /**
     *
     *  반각문자를 전각문자로 변경한다. 전각문자는 반각문자보다 65248(0xfee0)이 크다
     *
     * @param hanKaKuChar
     *            the han ka ku char
     * @return 성공: 전각문자열 실패: NULL
     *
     */
    public String hanKaKuToZenKaKuFrom(String hanKaKuChar)
    {
        StringBuffer zenKaKuChar = new StringBuffer();

        char hanKaKuCharAt = 0;
        int  nSrcLength    = 0;

        if(hanKaKuChar != null && hanKaKuChar.trim().equals("") == false)
        {
            nSrcLength = hanKaKuChar.length();

            //전각문자로 변환
            for (int i = 0; i < nSrcLength; i++)
            {
                hanKaKuCharAt = hanKaKuChar.charAt(i);

                //'!' ~ '~' 범위의 반각문자인 경우
                if (hanKaKuCharAt >= 0x21 && hanKaKuCharAt <= 0x7e)
                {
                    hanKaKuCharAt += 0xfee0;
                }
                else if (hanKaKuCharAt == 0x20)
                {
                    hanKaKuCharAt = 0x3000;
                }
                else
                {
                    continue;
                }
                zenKaKuChar.append(hanKaKuCharAt);
            }
        }
        else
        {
            return "";
        }
        return zenKaKuChar.toString();
    }


    /**
     * *
     *  문자열에 전각문자가 존재하는지 CHECK 한다.(예: 12テABCD テ-->전각) CHECK 대상:
     * 특수문자,숫자,알파벳 소.대문자, 카타카나(히라가나는 반각없음)
     *
     * @param targetStr
     *            the target str
     * @return 전각(O): TRUE 전각(X): FALSE
     *         **
     *         **
     */
    public boolean isZenkakuCheck(String targetStr)
    {
        if(targetStr != null && targetStr.equals("") == false)
        {
            for(int i = 0;  i < targetStr.length(); i++)
            {
                int zenKaku = targetStr.charAt(i);

                if(zenKaku < 0x100 || (zenKaku >= 0xff61 && zenKaku <= 0xff9f))
                {
                    continue;
                }
                else
                {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * *
     *  문자열에 반각 가타카나 존재여부 판단(CHECK 대상: 반각 가타카나).
     *
     * @param targetStr
     *            the target str
     * @return 반각 가타카나(有): TRUE 반각 가타카나(無): FALSE
     *         *
     *
     */
    public boolean isHanKaKuKanaCheck(String targetStr)
    {
        if (targetStr != null && targetStr.length() != 0)
        {
            StringBuffer valueBuf = new StringBuffer(targetStr);

            for (int i = 0; i < valueBuf.length(); i++)
            {
                int hanKaKuKana = targetStr.charAt(i);

                if (hanKaKuKana >= 0xFF61 && hanKaKuKana <= 0xFF9F)
                {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     *
     * &lt;br&gt;을 엔터값(CRLF)으로 변경.
     *
     * @param targetStr
     *            the target str
     * @return 성공: CRLF 실패: NULL
     *
     *
     */
    public String BrToCRLF(String targetStr)
    {
        if(isNull(targetStr) == true)
        {
            return null;
        }
        else
        {
            String value = targetStr.replaceAll(BR, CRLF);
            return value;
        }
    }


    /**
     * 엔터값(CR/LF)을 &lt;br&gt; 로 변경.
     *
     * @param targetStr
     *            the target str
     * @return 성공: &lt;br&gt; 실패: NULL
     *
     *
     */
    public String CRLFToBr(String targetStr)
    {
        if(isNull(targetStr) == true)
        {
            return null;
        }
        else
        {
            String value = targetStr.replaceAll(CRLF, BR);
            return value;
        }
    }


    /**
     * String[] 타입의 문자열 데이터 중복조사 후 중복된 데이터를 제거.
     *
     * @param targetStrs
     *            the target strs
     * @return 성공: 중복조사 제거완료 데이터(구분자 Blank) 실패: NULL
     *
     *
     */
    public String overlapDataRemove(String[]targetStrs)
    {
        boolean isFirst = true;
        String resultData = "";

        LinkedList<String> overlapRomoveList = new LinkedList<String>();

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


    /**
     * 문자열에서 특수기호를 제거한다.
     *
     * @param input 특수기호 제거 대상 문자열
     * @return 특수기호가 제거된 문자열 , 문제가 존재한다면 null
     */
    public String getRemoveSpecialChar(String input){
        boolean specialChar = false;
        char    tempChar;
        String  output      = "";

        // 1. 입력 데이터 확인
        if( input == null )
            return null;

        // 2. DEL_SPECIAL_CHAR 초기화 상태확인
        if( DELETE_SPECIAL_CHAR == null )
            return null;

        // 3. 입력 문자열 확인
        for( int i = 0; i < input.length(); i++ )
        {
            tempChar = input.charAt(i);
            for( int j = 0; j < DELETE_SPECIAL_CHAR.length; j++ )
            {
                // 4. 특수기호를 찾음
                specialChar = false;
                if( ( tempChar >= DELETE_SPECIAL_CHAR[j][0] ) &&
                        ( tempChar <= DELETE_SPECIAL_CHAR[j][1] ) )
                {
                    specialChar = true;
                    break;
                }
            }
            // 5. 특수기호에 해당하지 않을 경우 결과로 저장
            if( specialChar == false )
                output += tempChar;
        }

        return output;
    }
    /**
     * ' '를 제외한 특수기호를 제거한다.
     *
     * @param input 특수기호 제거 대상 문자열
     * @return 특수기호가 제거된 문자열 , 문제가 존재한다면 null
     */
    public String getRemoveSpecialCharNoRemoveSpace(String input){
        boolean specialChar = false;
        char    tempChar;
        StringBuffer output = new StringBuffer();

        // 1. 입력 데이터 확인
        if( input == null )
            return null;

        // 2. DEL_SPECIAL_CHAR 초기화 상태확인
        if( DELETE_SPECIAL_CHAR == null )
            return null;

        // 3. 입력 문자열 확인
        for( int i = 0; i < input.length(); i++ )
        {
            tempChar = input.charAt(i);
            if(tempChar != ' ') {
                for( int j = 0; j < DELETE_SPECIAL_CHAR.length; j++ )
                {
                    // 4. 특수기호를 찾음
                    specialChar = false;
                    if( ( tempChar >= DELETE_SPECIAL_CHAR[j][0] ) &&
                            ( tempChar <= DELETE_SPECIAL_CHAR[j][1] ) )
                    {
                        specialChar = true;
                        break;
                    }
                }
                // 5. 특수기호에 해당하지 않을 경우 결과로 저장
                if( specialChar == false )
                    output.append(tempChar);
            }else{
                output.append(tempChar);
            }
        }
        return output.toString();
    }

    /**
     * 한자를 한글로 변환 한다.
     * @param hanja 변환 대상 문자열
     * @return 한자가 제거된 문자열
     */
    public String convertHanjaToHangul(String hanja )
    {
        try
        {
            // 1. HANJA_TO_HANGLE_MAP 초기화
            char HANJA_TO_HANGLE_MAP[] = hanjaToHangleInfo.getHanhaToHangleMap();

            // 2. 문자열을 UTF-8 인코딩 바이트 배열로 변환
            char unicode = 0x0000;
            byte[] hanjaByte = hanja.getBytes("UTF-8");

            // 3. 3Byte 문자열을 찾아 한자를 한글로 변환
            for( int i = 0; i < hanjaByte.length; )
            {
                if( (hanjaByte[i]&0xFF) < 0x80 )            // 1Byte Character
                {
                    i++;
                    continue;
                }
                else if( (hanjaByte[i]&0xFF) < 0xE0 )       // 2Byte Character
                {
                    i += 2;
                    continue;
                }
                else if( (hanjaByte[i]&0xFF) < 0xF0 )       // 3Byte Character
                {
                    unicode = (char)(hanjaByte[i] & 0x0f);
                    i++;
                    unicode = (char)(unicode << 6);
                    unicode = (char)(unicode | (hanjaByte[i] & 0x3f));
                    i++;
                    unicode = (char)(unicode << 6);
                    unicode = (char)(unicode | (hanjaByte[i] & 0x3f));
                    i++;
                }


                // 3Byte 문자이면 매핑 테이블을 확인하여 한자이면 한글로 변환
                if( HANJA_TO_HANGLE_MAP[unicode] != unicode )
                {
                    unicode = HANJA_TO_HANGLE_MAP[unicode];

                    hanjaByte[i-1] = (byte)((unicode & 0x3f) | 0x80);
                    hanjaByte[i-2] = (byte)(((unicode << 2) & 0x3f00 | 0x8000) >> 8);
                    hanjaByte[i-3] = (byte)(((unicode << 4) & 0x3f0000 | 0xe00000) >> 16);

                    continue;
                }
            }

            return (new String(hanjaByte, "UTF-8"));
        }
        catch(UnsupportedEncodingException e)
        {
            return null;
        }
    }


    /**
     * 검색용 색인어를 생성한다.
     *
     * @param item   검색 색인어 생성 대상 문자열
     * @return 특수기호 , 한자가 제거된 문자열
     * @see #getRemoveSpecialChar(String)
     * <pre>
     * 함수 내부에서 특수기호 제거 함수를 호출 한다.
     *  item = getRemoveSpecialChar(item);
     * 함수 내부에서 한자 제거 함수를 호출 한다.
     *  item = convertHanjaToHangul(item );
     * </pre>
     */
    public String getKeyword(String item ){

        if( item == null) return null;

        if( item.equals("")) return null;

        item = getRemoveSpecialChar(item);
        if( item == null) return null;

        item = convertHanjaToHangul(item);
        if( item == null) return null;

        item = item.toUpperCase();

        if(item.length()>20) item = item.substring(0,20);

        return item;
    }

    public String getKeyword_author(String item ){

        if( item == null) return null;

        for(String rollWord : rollWordList){
            item = item.replaceAll(" "+rollWord , "");
            if(rollWord.length()>2){
                item = item.replaceAll(rollWord , "");
            }
        }

        item = getRemoveSpecialChar(item);
        if( item == null) return null;

        item = convertHanjaToHangul(item);
        if( item == null) return null;

        item = item.toUpperCase();

        if(item.length()>20) item = item.substring(0,20);

        return item;
    }


    /**
     * 키워드를 생성할때 뛰어쓰기를 삭제 하지 않고 키워드를 생성한다.
     * @param item
     * @return 키워드
     */
    public String getKeywordNoRemoveSpace(String item ){

        if( item == null) return null;

        item = getRemoveSpecialCharNoRemoveSpace(item);
        if( item == null) return null;

        item = convertHanjaToHangul(item);
        if( item == null) return null;

        item = item.toUpperCase();

        if(item.length()>20) item = item.substring(0,20);

        return item;
    }


    /**
     * 문자열이 모두 숫자로 구성된 문자열인지 확인한다.
     * @param  targetStr 검사 문자열
     * @return ALL NUMBER: TRUE NOT ALL NUMBER: FALSE
     */
    public boolean isAllNumber(String targetStr)
    {
        if(isNull(targetStr) == true)
        {
            return false;
        }
        else
        {
            for( int idx = 0; idx <  targetStr.length(); idx++)
            {
                if(Character.isDigit(targetStr.charAt(idx)) == true)
                {
                    continue;
                }
                else
                {
                    return false;
                }
            }
            return true;
        }
    }


    /**
     * 문자열의 바이트 사이즈 구한다.
     * @param   str 검사 문자열
     * @return  정상: ByteSize 비정상: -1
     */
    public int isByteSize(String str)
    {
        if(isNull(str))
        {
            return -1;
        }
        else
        {
            byte[] strByte = str.getBytes();
            return strByte.length;
        }
    }


    /**
     * 문자열에 알파벳이 하나라도 존재하는지 확인한다.
     * @param   targetStr 검사 문자열
     * @return  [有: TRUE 無: FALSE]
     */
    public boolean isAlpha(String targetStr)
    {
        if(isNull(targetStr) == true)
        {
            return false;
        }
        else
        {
            for(int idx = 0; idx < targetStr.trim().length(); idx++)
            {
                char tempAlpha = 0;
                tempAlpha = targetStr.charAt(idx);

                if((tempAlpha >= 0x41 && tempAlpha <= 0x5a ) ||
                        (tempAlpha >= 0x61 && tempAlpha <= 0x7a))
                {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * 저자명이 [영어 , .]으로 이루어져있는지 체크
     * @param targetStr
     * @return ture  = 저자명이 [영어 , .] , false != 저자명이 [영어 , .]이외의 문자가 존재함
     */
    public boolean isEngAuthor(String targetStr)
    {
        boolean flag = true;
        if(isNull(targetStr) == true)
        {
            return false;
        }
        else
        {
            for(int idx = 0; idx < targetStr.trim().length(); idx++)
            {
                char tempAlpha = 0;
                tempAlpha = targetStr.charAt(idx);

                //0x2C	,
                //0x2E	.
                //0x41 A ~ 0x5a Z
                //0x61 a ~ 0x7a z
                if((  (tempAlpha==0x2c) ||(tempAlpha==0x2e)|| tempAlpha > 0x41 && tempAlpha < 0x5a ) || (tempAlpha > 0x61 && tempAlpha < 0x7a) )
                {
                    flag = true;
                }else{
                    flag = false;
                    break;
                }
            }
        }
        return flag;

    }

    /**
     * 문자열에 알파벳이 있는지 확인하고 삭제한다.
     * @param  targetStr 삭제 대상문자열
     * @return 성공: 제거완료 문자열 실패: NULL
     */
    public String RemoveAlpha(String targetStr)
    {
        String resultData = "";

        if(isNull(targetStr) == true)
        {
            return null;
        }
        else
        {
            for(int i = 0;  i < targetStr.length(); i++)
            {
                char charStr = targetStr.charAt(i);

                if((charStr > 0x41 && charStr < 0x5a ) || (charStr > 0x61 && charStr < 0x7a))
                {
                    resultData = targetStr.replaceAll((""+charStr), "");
                }
            }
        }
        return resultData;
    }

    /**
     * 코라스의 괄호안의 내용을 제거하고 문자열을 추출하는 함수 복사
     * @param checkValue
     * @return StringCheckObject
     */
    public StringCheckObject RemoveAllParenthesis(String checkValue){

        StringCheckObject returnValue = new StringCheckObject();

        String strReturn = "";
        int nCount = checkValue.length();
        char findTch = '(';
        boolean bFind = false;

        for (int i = 0; i < nCount; i++)
        {
            char tch = checkValue.charAt(i);
            if (tch == findTch)
            {
                bFind = true;

                if(findTch == ')'){
                    findTch = '(';
                }else{
                    findTch = ')';
                }

                continue;
            }

            if (findTch != ')')
            {
                strReturn += tch;
            }
        }

        if (bFind)
            returnValue.setFunction_value(1);
        else
            returnValue.setFunction_value(0);

        returnValue.setValue(strReturn);


        return returnValue;
    }

    /**
     * '('로 시작하는 문자열의 ()내용을 삭제하고 문자열을 생성한다.
     * @param strSrc 체크 문자열
     * @return ()내용을 삭제한 문자열
     */
    public StringCheckObject RemoveOneParenthesis(String strSrc)
    {
        StringCheckObject returnValue = new StringCheckObject();

        String strReturn = "";

        int nCount = strSrc.length();
        char findTch = '(';
        boolean bFind = false;

        for (int i = 0; i < nCount; i++)
        {
            char tch = strSrc.charAt(i);
            if (!bFind && tch == findTch)
            {

                if(findTch == ')'){
                    findTch = '(';
                }else{
                    findTch = ')';
                }
                continue;
            }


            if (findTch != ')')
            {
                bFind = true;
                strReturn += tch;
            }
        }

        if (bFind)
            returnValue.setFunction_value(1);
        else
            returnValue.setFunction_value(0);

        returnValue.setValue(strReturn);

        return returnValue;

    }
    /**
     * '('로 시작하는 문자열의 ()내용을 삭제하고 문자열을 생성한다.
     * @param strSrc 체크 문자열
     * @return ()내용을 삭제한 문자열
     */
    public String RemoveOneParenthesisReturnString(String strSrc)
    {
        String strReturn = "";

        int nCount = strSrc.length();
        char findTch = '(';
        boolean bFind = false;

        for (int i = 0; i < nCount; i++)
        {
            char tch = strSrc.charAt(i);
            if (!bFind && tch == findTch)
            {

                if(findTch == ')'){
                    findTch = '(';
                }else{
                    findTch = ')';
                }
                continue;
            }

            if (findTch != ')')
            {
                bFind = true;
                strReturn += tch;
            }
        }

        return strReturn;

    }

    /**
     * '('로 시작하는 () 삭제하고 문자열을 생성한다.
     * @param strSrc 체크 문자열
     * @return ()내용을 삭제한 문자열
     */
    public String RemoveOnlyOneParenthesisReturnString(String strSrc)
    {

        int firstParenthesisIdx = -1;
        firstParenthesisIdx = strSrc.indexOf("(");
        if(firstParenthesisIdx != 0) {
            return strSrc;
        }

        strSrc = strSrc.replaceFirst("\\(", " ");
        strSrc = strSrc.replaceFirst("\\)", " ");
        strSrc = strSrc.replaceAll("  ", " ");

        return strSrc;

    }

    /**
     * 색인 데이터 생성시 본표제에 대한 색인 문자열을 생성한다.
     * @param strIndexItem
     * @return ALLITEM용 색인어 생성
     */
    public String MakeFullIndexForTitle(String strIndexItem){

        StringBuffer strAllItem = new StringBuffer();
        String strProcessItem   = "";
        String strTempItem       = "";

        int nPos = 0;
        strProcessItem = strIndexItem;
        nPos = strProcessItem.indexOf(")");
        if(nPos == -1) {
            nPos = strProcessItem.indexOf(" ");
        }

        strTempItem = strProcessItem;

        //공백 제거
        strTempItem = strTempItem.replaceAll(" ", "");

        strAllItem.append(strTempItem);
        while(0 < nPos )
        {
            strTempItem = strProcessItem.substring(0,nPos);
            if(strTempItem.substring(0,1).equals("(") == true) strTempItem = strTempItem.substring(1);

            strProcessItem = strProcessItem.substring(nPos+1);
            strProcessItem = strProcessItem.trim();

            strAllItem.append(" " + strTempItem);
            strTempItem = strProcessItem;
            strTempItem = RemoveSpace(strTempItem);
            strAllItem.append(" " + strTempItem);

            nPos = strProcessItem.indexOf(")");
            if(nPos == -1) {
                nPos = strProcessItem.indexOf(" ");
            }

        }

        return strAllItem.toString();
    }

    /**
     * 색인 데이터 생성시 본표제를 제외한 대한 색인 문자열을 생성한다.
     * @param strIndexItem
     * @return 뛰어쓰기가 제거된 문자
     */
    public String MakeFullIndex( String strIndexItem )
    {
        return RemoveSpace(strIndexItem);
    }

    /**
     * 색인어 생성
     * @param strSrc 색인생성 대상문자열
     * @return 색인문자열
     */
    public String MakeIndex(String strSrc)
    {
        if(strSrc == null) return null;
        strSrc = strSrc.toUpperCase();
        strSrc = strSrc.replaceAll(" ", "");
        strSrc = getRemoveSpecialChar(strSrc);
        strSrc = convertHanjaToHangul(strSrc);
        strSrc = strSrc.trim();
        return strSrc;

    }

    /**
     * 공백을 제거한다.
     * @param strIndexItem 공백제거대상 문자열
     * @return 공백이 제거된 문자열
     */
    public String RemoveSpace(String strIndexItem){
        if(strIndexItem == null) return null;
        strIndexItem = strIndexItem.replaceAll(" ", "");
        return strIndexItem;
    }

    /**
     * 문자열에서 지정된 위치의 글씨를 삭제한다.
     * @param str 문자열
     * @param startIndex 삭제하려는 문자열의 시작위치
     * @param deleteLength 삭제하려는 글씨 길이
     * @return 처리완료 문자열
     */
    public String DeleteString(String str ,int startIndex , int deleteLength){
        StringBuffer temp = new StringBuffer();
        for(int i=0; i < str.length() ; i++){

            //문자열 삭제
            if(i >= startIndex && i <= (startIndex + deleteLength) ){
                continue;
            }

            temp.append(str.charAt(i));

        }
        return temp.toString();

    }

    /**
     * 화면구분을 구한다.
     * @param _950_b 마크 950테그의 식별기호 b
     * @return 화폐구분 문자
     */
    public String getCurrency_code(String _950_b){

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
    /**
     * 가격을 구한다.
     * @param _950_b 마크 950테그의 식별기호 b
     * @return 가격
     */
    public String getPrice(String _950_b){

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
    /**
     * 문자열이 NULL 또는 길이가 0인지 확인
     * @param data 데이터
     * @return if 문자열이 NULL 또는 길이가 0인경우 true, else false
     */
    public boolean isEmpty(String data) {
        if (data == null || data.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

}
