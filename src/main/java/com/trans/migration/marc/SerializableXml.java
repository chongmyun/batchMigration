package com.trans.migration.marc;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface SerializableXml {
    /**
     * 객체를 해당 XML 경로에 접근하여 직렬화 한다.
     *
     * @param xmlPath the xml path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void toXml(String xmlPath) throws FileNotFoundException, IOException;


    /**
     * 해당 경로에 접근하여 XML 파일을 역직렬화 하여 객체로 생성한다.
     *
     * @param o the object
     * @param xmlPath the xml path
     * @return SerializableXml 구현객체
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Object fromXml(Object o, String xmlPath)
            throws FileNotFoundException, IOException;


    /**
     * 객체를 직렬화하여 XML 형태의 String Type 으로 반환한다.
     *
     * @param xmlObject the xml object
     * @return 직렬화 결과 데이터[XML형식]
     * @throws FileNotFoundException
     */
    public String toXml(Object xmlObject) throws FileNotFoundException;


    /**
     * XML 형태의 String Type 데이터를 역직렬화 하여 객체로 변환한다.
     *
     * @param xmlString the xml string
     * @return 역직렬화 결과 객체
     * @throws FileNotFoundException
     */
    public Object fromXml(String xmlString) throws FileNotFoundException;
}
