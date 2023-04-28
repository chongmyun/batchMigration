package com.trans.migration.marc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component("HanjaToHangleInfo")
public class HanjaToHangleInfo {
    /** 유니코드 */
    private String code = "";

    /** 한자 레퍼런스 파일을 Load 해놓은 Map객체 */
    private char HANJA_TO_HANGLE_MAP[] =  null;

    /**
     * Returns 유니코드
     *
     * @return 유니코드 값
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets 유니코드
     *
     * @param code 유니코드
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * HanjaToHangleInfo 생성자
     *
     * @param referenceResource 한자 레퍼런스 파일
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public HanjaToHangleInfo(@Value("classpath:reference/hanja_trans/hanjatohangle.xml") Resource referenceResource) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
        if( HANJA_TO_HANGLE_MAP == null ){

//	        FileInputStream readObject = new FileInputStream(referencePath);

            try (FileInputStream readObject = new FileInputStream(referenceResource.getFile().getAbsoluteFile())) {

                //Java XML API의 DocumentBuilder 클래스생성
                DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

                //2. DocumentBuilder 를 이용하여 XML Parsing
                Document doc = docbuilder.parse(readObject);

                //3. DOM 라이브러리 Document 를 이용하여 Element 구조를 NodeList에 Binding
                NodeList propertyList = doc.getDocumentElement().getElementsByTagName("UnicodeMap");

                //4. XML 파일 정보 저장
                if(propertyList != null)
                {

                    //XML 파일을 파싱한 트리구조 객체를 분석한다.
                    for(int i = 0; i < propertyList.getLength(); i++)
                    {
                        Node parentNode = propertyList.item(i);
                        if(parentNode.getNodeType() == Node.ELEMENT_NODE)
                        {
                            NodeList childNodeList = parentNode.getChildNodes();
                            for(int j = 0; j < childNodeList.getLength(); j++)
                            {
                                //NodeList 에서 하나의 Node 추출
                                Node childNodeElement = childNodeList.item(j);

                                if(childNodeElement.getNodeType() == Node.ELEMENT_NODE)
                                {
                                    setCode(childNodeElement.getTextContent().trim());
                                }
                            }
                        }
                    }

                    // 4. 레퍼런스 데이터 편집
                    String buffer = this.getCode();

                    buffer = buffer.replaceAll("\n", "");
                    buffer = buffer.replaceAll("\r", "");

                    String[] codeList = buffer.split(",");

                    // 4. HANJA_TO_HANGLE_MAP 데이터 초기화
                    HANJA_TO_HANGLE_MAP = new char[codeList.length];

                    // 5. 레퍼런스 데이터 입력
                    for( int index = 0; index < codeList.length; index++ )
                    {
                        HANJA_TO_HANGLE_MAP[index] = (char)Integer.decode(codeList[index].trim()).intValue();
                    }
                }

            }
        }
    }

    /** ************************************************************************************
     * public String <b>getHangle(String hanja)</b> throws UnsupportedEncodingException
     * <pre>
     * 한자를 포함한 문자열을 입력받아 한자 데이터부분을 한글로 변환하여 준다.
     * </pre>
     * @param hanja 한자가 포함된 문자열
     * @return      번역된 한글 문자열, 실패시 null을 반환한다.
     */
    public String getHangle(String hanja)
    {
        try
        {
            // 2. 문자열을 UTF-8 인코딩 바이트 배열로 변환
            char unicode = 0x0000;
            byte[] hanjaByte = hanja.getBytes("UTF-8");

            // 3. 3Byte 문자열을 찾아 한자를 한글로 변환
            for( int i = 0; i < hanjaByte.length; )
            {
                if( (hanjaByte[i]&0xFF) < 0x80 )			// 1Byte Character
                {
                    i++;
                    continue;
                }
                else if( (hanjaByte[i]&0xFF) < 0xE0 )		// 2Byte Character
                {
                    i += 2;
                    continue;
                }
                else if( (hanjaByte[i]&0xFF) < 0xF0 )		// 3Byte Character
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
                else {
                    i += 4;
                    continue;
                }
                // 3Byte 문자이면 매핑 테이블을 확인하여 한자이면 한글로 변환
                if( this.HANJA_TO_HANGLE_MAP[unicode] != unicode )
                {
                    unicode = this.HANJA_TO_HANGLE_MAP[unicode];

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
     * 한자.한글 맵핑객체 반환
     *
     * @return 한자.한글 맵핑객체
     */
    public char[] getHanhaToHangleMap()
    {
        return HANJA_TO_HANGLE_MAP;
    }
}
