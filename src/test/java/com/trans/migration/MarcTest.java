package com.trans.migration;

import com.trans.migration.marc.StringUtil;
import com.trans.migration.marc.marc.struct.MarcStru;
import com.trans.migration.slima.user.domain.SlimUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;

@SpringBootTest
public class MarcTest {

    @Autowired
    StringUtil stringUtil;

    private String getObjectNames(Class c){

        if(c == null) return "";

        String names = "";
        Field[] fields = c.getFields();
        for (Field field : fields) {
            if(field.getName().equals("partitionKey") == false) {
                if (names.equals("")) {
                    names += field.getName();
                } else {
                    names += "," + field.getName();
                }
            }
        }

        return names;
    }
    @Test
    public void test(){
        String marc = "00995nam a2200337 c 4500001001300000005001500013008004100028020002300069020003900092020003900131020003400170020003900204035002500243040002700268041001300295049001700308056001500325090002300340245007100363260003000434300001600464505006000480653003000540700001100570700001100581740001100592740001100603740001100614740002100625950001100646\u001EKMO202303538\u001E20230412120018\u001E070704s2007    ulk           000a  kor  \u001E1 \u001Fa9788937425806(SET)\u001E  \u001Fa9788937425813(v.1)\u001Fg04910:\u001Fc\\30000\u001E  \u001Fa9788937425844(v.4)\u001Fg04910:\u001Fc\\30000\u001E  \u001Fa9788937425820\u001Fg04910:\u001Fc\\30000\u001E  \u001Fa9788937425837(v.3)\u001Fg04910:\u001Fc\\20000\u001E  \u001Fa(111006)KMO200702606\u001E  \u001Fa111006\u001Fc111006\u001Fd129002\u001E1 \u001Fakor\u001Fhchi\u001E0 \u001FlEM1200001650\u001E  \u001Fa912.033\u001F24\u001E  \u001Fa912.033\u001Fbㅈ976ㅈ\u001E20\u001Fa(正史)삼국지.\u001Fn1 - 4/\u001Fd진수陳壽 지음,\u001Fe김원중 옮김\u001E  \u001Fa서울:\u001Fb민음사,\u001Fc2007\u001E  \u001Fa4책;\u001Fc21cm\u001E00\u001Fn1,\u001Ft위서. -\u001Fn2,\u001Ft위서. -\u001Fn3,\u001Ft촉서. -\u001Fn4,\u001Ft오서\u001E  \u001Fa정사\u001Fa삼국지\u001Fa위서\u001E1 \u001Fa진수\u001E1 \u001Fa진수\u001E0 \u001Fa촉서\u001E0 \u001Fa위서\u001E0 \u001Fa오서\u001E0 \u001Fa삼국지 위서\u001E0 \u001Fb\\30000\u001E\u001D";
        String names = getObjectNames(SlimUser.class);
        System.out.println(names);
        MarcStru marcStru = new MarcStru();
        stringUtil.getKeyword("아아아아아너ㅑ애러파ㅣㅓㅋ티ㅏ첲");
    }
}
