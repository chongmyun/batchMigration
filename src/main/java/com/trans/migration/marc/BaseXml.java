package com.trans.migration.marc;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Component("BaseXml")
public class BaseXml {

    /** //직렬화 UID 부여 */
    @XStreamOmitField
    public final static long serialVersionUID = 0L;

    /** 문자열 언어셋 */
    @XStreamOmitField
    public final static String CHAR_SET = "UTF-8";

    public BaseXml() {}

    /**
     * 객체를 직렬화하여 XML 형태의 String Type 으로 반환한다
     *
     * @param   xmlObject 직렬화 대상 객체
     * @return  직렬화 결과 데이터 [XML]
     * @throws  FileNotFoundException
     */
    public String toXml(Object xmlObject) throws FileNotFoundException
    {
        XStream xStream = new XStream(new DomDriver(CHAR_SET));
        xStream.allowTypesByWildcard(new String[] {
                "com.trans.migration.marc.**"
        });
        xStream.processAnnotations(xmlObject.getClass());
        xStream.autodetectAnnotations(true);

        String  xmlString = xStream.toXML(xmlObject);

        return xmlString;
    }


    /**
     * XML 형태의 String Type 데이터를 역직렬화 하여 객체로 변환한다.
     *
     * @param   xmlString 역직렬화 대상 데이터
     * @return  역직렬화 결과 객체
     * @throws  FileNotFoundException
     */
    public Object fromXml(String xmlString) throws FileNotFoundException
    {
        XStream xStream = new XStream(new DomDriver(CHAR_SET));
        xStream.allowTypesByWildcard(new String[] {
                "com.trans.migration.marc.**"
        });
        xStream.processAnnotations(getClass());
        xStream.autodetectAnnotations(true);

        Object xmlObject = xStream.fromXML(xmlString);

        return xmlObject;
    }



    /**
     * 객체를 해당 XML 파일경로에 접근하여 직렬화하여 XML 파일을 생성한다.
     *
     * @param   xmlPath XML PATH
     * @throws IOException
     */
    public void toXml(String xmlPath) throws IOException
    {
        XStream xStream= new XStream(new DomDriver(CHAR_SET));
        xStream.allowTypesByWildcard(new String[] {
                "com.trans.migration.marc.**"
        });
        xStream.processAnnotations(getClass());
        xStream.autodetectAnnotations(true);

        try (FileOutputStream outputStream = new FileOutputStream(xmlPath)) {
            xStream.toXML(this, outputStream);
        }
    }


    /**
     * XML 경로에 접근하여 XML 파일을 객체형태로 역직렬화 한다.
     * @param   o 저장대상객체
     * @param   xmlPath XML PATH
     * @return  직렬화하여 XML 파일이 바인딩되는 객체
     * @throws IOException
     */
    public Object fromXml(Object o, String xmlPath) throws IOException {
        XStream xStream = new XStream(new DomDriver(CHAR_SET));
        xStream.allowTypesByWildcard(new String[] {
                "com.trans.migration.marc.**"
        });
        xStream.processAnnotations(o.getClass());
        xStream.autodetectAnnotations(true);

        Object xmlRule = null;
        try (FileInputStream inputStream = new FileInputStream(xmlPath)) {
            xmlRule = xStream.fromXML(inputStream, o);
        }

        return xmlRule;
    }

}
