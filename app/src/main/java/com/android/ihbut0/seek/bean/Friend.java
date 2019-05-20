package com.android.ihbut0.seek.bean;


import com.android.ihbut0.seek.dao.UserDAO;


public class Friend extends User{

    private String label;

    public Friend(){}

//    public Friend(String friendAccount, String label, float index){
//        User user = UserDAO.getUser(friendAccount);
//        super.setAccount(user.getAccount());
//        super.setNickname(user.getNickname());
//        super.setAddress(user.getAddress());
//        super.setBirthday(user.getBirthday());
//        super.setEducation(user.getEducation());
//        super.setHeadImg(user.getHeadImg());
//        super.setSex(user.getSex());
//        super.setSign(user.getSign());
//
//        this.label = label;
//        this.index = index;
//    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
