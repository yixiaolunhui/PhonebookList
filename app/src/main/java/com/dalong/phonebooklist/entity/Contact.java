package com.dalong.phonebooklist.entity;



/**
 * 通讯录对象
 * @author Administrator
 *
 */
public class Contact {

	//通讯录名字
	private String bookMarkName;
	//通讯录号码
	private String[] bookPhoneNo;
	//城市首字母
	private String NameSort;
	//城市首字
	private String NameSortString;

	private boolean isTitle;

	public boolean isTitle() {
		return isTitle;
	}

	public void setTitle(boolean title) {
		isTitle = title;
	}

	public String getNameSort() {
		return NameSort;
	}

	public void setNameSort(String nameSort) {
		NameSort = nameSort;
	}

	public String getNameSortString() {
		return NameSortString;
	}

	public void setNameSortString(String nameSortString) {
		NameSortString = nameSortString;
	}

	public String getBookMarkName() {
		return bookMarkName;
	}

	public void setBookMarkName(String bookMarkName) {
		this.bookMarkName = bookMarkName;
	}

	public String[] getBookPhoneNo() {
		return bookPhoneNo;
	}

	public void setBookPhoneNo(String[] bookPhoneNo) {
		this.bookPhoneNo = bookPhoneNo;
	}





	@Override
	public String toString() {
		return "Contact [phone_name=" + bookMarkName + ", phone_num=" + bookPhoneNo+"]";
	}
	
}
