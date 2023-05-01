package com.trans.migration.slima.user.chunk;

import com.trans.migration.exception.UserException;
import com.trans.migration.slima.user.domain.CoLoanUser;
import com.trans.migration.slima.user.domain.SlimUser;
import org.springframework.batch.item.ItemProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserItemProcessor implements ItemProcessor<SlimUser, CoLoanUser> {

    private SimpleDateFormat birthDayFormatter = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
    private SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public CoLoanUser process(SlimUser item) throws Exception {
        String errorMsg = "";
        boolean isError = false;

        CoLoanUser coLoanUser = new CoLoanUser();
        coLoanUser.setUserId(item.getUserId());
        coLoanUser.setUserNo(item.getUserNo());
        //TODO WEBID 확인필요
        coLoanUser.setPassword(item.getPassword());
        coLoanUser.setWorkno(item.getRfid());
        coLoanUser.setCardPassword(item.getRfidPassword());
        coLoanUser.setName(item.getUsername());

        try{
            String birthday = item.getBirthday();
            if(birthday.equals("") == false){
                coLoanUser.setBirthday(birthDayFormatter.parse(birthday));
            }
        }catch(ParseException e){
            errorMsg += ",생일 포멧 오류";
            isError = true;
        }

        String sex = item.getSex();
        if(sex.equals("1") || sex.equals("3")) coLoanUser.setGpinSex("0");
        else if(sex.equals("2") || sex.equals("4")) coLoanUser.setGpinSex("1");
        else coLoanUser.setGpinSex("");

        try{
            String applyDate = item.getApplyDate();
            if(applyDate.equals("") == false){
                coLoanUser.setRegDate(formatter.parse(applyDate));
            }
        }catch(ParseException e){
            errorMsg += ",신청일 포멧 오류";

            isError = true;
        }

        //TODO 소속기관 확인필요 (lib_code 인지 아님 다른 값인지)
        String libCode = item.getLibCode();
        coLoanUser.setManageCode("");
        coLoanUser.setUserClassCode(item.getUserClass());
        coLoanUser.setUserPositionCode(item.getUserPosition());
        ///////////////////////////////
        coLoanUser.setSmsUseYn(item.getSmsYN());
        coLoanUser.setMailingUseYn(item.getEmailYN());
        coLoanUser.setGrade(item.getGrade());
        coLoanUser.setHPhone(item.getNumber());
        coLoanUser.setHandphone(item.getPhoneNumber());
        coLoanUser.setEMail(item.getEmail());
        coLoanUser.setHZipcode(item.getZipCode());

        //TODO 주소가 두개인데 집이랑 회사인지 아니면 합쳐서 사용해야하는지 확인필요
        coLoanUser.setHAddr1(item.getAddressOne());
        coLoanUser.setWAddr1(item.getAddressTwo());
        ///////////////////////////////

        try{
            String loanStopDate = item.getLoanStopDate();
            if(loanStopDate.equals("") == false){
                coLoanUser.setLoanStopDate(formatter.parse(loanStopDate));
            }
        }catch(ParseException e){
            errorMsg += ",대출정지일 포멧 오류";
            isError = true;
        }

        //TODO 이용자 사진 받아서 경로 찾아서 BYTE로 데이터 입력
        String userPicturePath = item.getUserPicturePath();
        try{
            if(userPicturePath.equals("") == false){
                String ext = userPicturePath.substring(userPicturePath.lastIndexOf(".") + 1).toUpperCase();
                Path path = Paths.get(userPicturePath);
                byte[] data = Files.readAllBytes(path);
                coLoanUser.setUserPicture(data);
                coLoanUser.setUserPictureType(ext);
            }
        }catch (IOException e){
            errorMsg += ",이용자이미지 파일 I/O 오류";
            isError = true;
        }

        coLoanUser.setRemark(item.getRemark());

        //TODO 본인인증 관련 확인필요

        //TODO 팝업비고, 보안등급(1~5) ,승인일자 ,승인담당자 확인필요
        ///////////////////////////////
        try{
            String stopDate = item.getStopDate();
            if(stopDate.equals("") == false){
                coLoanUser.setRemoveDate(formatter.parse(stopDate));
            }
        }catch(ParseException e){
            errorMsg += ",제적인 포멧 오류";
            isError = true;
        }


        //유효일자 -> 개인정보 보유 만기일, 개인정보 파기예정일
        //약관동의일 -> 개인정보확인일
        //만기일 확인 후 개인정보확인 필요여부 체크
        String privacyExpireDateStr = item.getPrivacyExpireDate();
        Date privacyExpireDate = null;
        try{
            if(privacyExpireDateStr.equals("") == false){
                privacyExpireDate = formatter.parse(privacyExpireDateStr);
                coLoanUser.setPrivacyExpireDate(privacyExpireDate);
                coLoanUser.setPrivacyDestroyDate(privacyExpireDate);
            }
        }catch(ParseException e){
            errorMsg += "유효일자(약관만기일) 포멧 오류";
            isError = true;
        }

        String privacyDateStr = item.getPrivacyDate();
        Date privacyDate = null;
        try{
            if(privacyDateStr.equals("") == false){
                privacyDate = formatter2.parse(privacyDateStr);
                coLoanUser.setPrivacyConfirmDate(privacyDate);
            }
        }catch(ParseException e){
            errorMsg += ",약관동의일 포멧 오류";
            isError = true;
        }

        //TODO 오픈일에 맞춰서 날짜 변경할 예정
        Date today = new Date();
        if(privacyExpireDate != null){
            int result = today.compareTo(privacyExpireDate);
            if(result > 0) coLoanUser.setPrivacyConfirmYn("Y");
            else coLoanUser.setPrivacyConfirmYn("N");
        }else{
            coLoanUser.setPrivacyConfirmYn("Y");
        }

        if(isError == true){
            item.setErrorMsg(errorMsg);
            throw new UserException("오류발생");
        }

        return coLoanUser;
    }
}
