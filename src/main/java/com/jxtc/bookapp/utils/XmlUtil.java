package com.jxtc.bookapp.utils;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class XmlUtil {

    //map集合转xml字符串
    public static String toXml(Map<String, Object> params) {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version='1.0' encoding='ISO8859-1' standalone='yes' ?><xml>");

        ArrayList<String> arr = new ArrayList<String>();
        for (String key : params.keySet()) {
            if (params.get(key) != null && !params.get(key).equals("")) {
                arr.add(key);
            }
        }
        Collections.sort(arr);
        for (int i = 0; i < arr.size(); i++) {
            String k = arr.get(i);
            if (params.get(k) != null) {
                String v = params.get(k).toString();
                xml.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            }
        }

        xml.append("</xml>");
        String xml2 = "";
        try {
            xml2 = new String(xml.toString().getBytes(), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return xml2;
    }

    /**
     * 解析xml数据
     */
    public static Map<String, Object> parseXml(byte[] xmlBytes, String charset) {
        SAXReader reader = new SAXReader(false);
        InputSource source = new InputSource(new ByteArrayInputStream(xmlBytes));
        source.setEncoding(charset);
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Document doc = reader.read(source);
            Iterator<Element> iter = doc.getRootElement().elementIterator();
            while (iter.hasNext()) {
                Element e = iter.next();
                if (!e.elementIterator().hasNext()) {
                    map.put(e.getName(), e.getTextTrim());
                    continue;
                }
                Iterator<Element> iterator = e.elementIterator();
                Map<String, String> param = new HashMap<String, String>();
                while (iterator.hasNext()) {
                    Element el = iterator.next();
                    param.put(el.getName(), el.getTextTrim());
                }
                map.put(e.getName(), param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, Object> parseXml(String xml) {
        if (StringUtils.isBlank(xml)) {
            return null;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> map = parseXml(xml.getBytes("UTF-8"), "UTF-8");
            for (String key : map.keySet()) {
                Object value = map.get(key);
                result.put(key, String.valueOf(value));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
