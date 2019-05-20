package com.android.ihbut0.seek.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class User{

    private String account;
    private String password;
    private int headImg;
    private String background;
    private String sign;
	private String nickname;
    private String birthday;
    private int sex;
    private String address;
    private String education;
    private String registerTime;
    
    //用于URL传参和取参时的key
    public static String PHONE = "phone";
    public static String PASSWORD = "password";
    public static String NICKNAME = "nickname";

    public User(){}
    
  //构造函数
    public User(String account,String password,String nickname){
        this.account = account;
        this.password = password;
        this.nickname = nickname;
    }

	public int getHeadImg() {
		return headImg;
	}

	public void setHeadImg(int headImg) {
		this.headImg = headImg;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
    
	public String getAccount() {
//		String result = account.substring(0,3);
//		result += " "+ account.substring(3,7);
//		result += " "+ account.substring(7,11);
		return this.account;
	}

	public void setAccount(String phone) {
		this.account = phone;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	@Override
	public String toString() {
		return "("+account+"):"+nickname+" "+sign+" "+birthday+" "+address+" "+education;
	}
}