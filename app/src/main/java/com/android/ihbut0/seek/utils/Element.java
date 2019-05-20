package com.android.ihbut0.seek.utils;

import java.math.BigInteger;

public class Element {

	BigInteger c1;
	BigInteger c2;
	
	/**
	 * Construction methods
	 * @param x
	 * @param y
	 */
	public Element(BigInteger c1, BigInteger c2) {
		this.c1 = c1;
		this.c2 = c2;
	}
	
	/**
	 * No-parameter construction method
	 */
	public Element() {
		c1 = c2 = BigInteger.ZERO;
	}

	/**
	 * @param e
	 * @param p
	 * @return
	 */
	public Element multiply(Element e, BigInteger p) {
		BigInteger newX = getC1().multiply(e.getC1()).mod(p);
		BigInteger newY = getC2().multiply(e.getC2()).mod(p);
		return new Element(newX, newY);
	}
	
	/**
	 * ���ĳ˷�
	 * @param index
	 * @param p
	 * @return
	 */
	public Element pow(BigInteger index, BigInteger p) {
		BigInteger newX = getC1().modPow(index, p);
		BigInteger newY = getC2().modPow(index, p);
		return new Element(newX, newY);
	}
	
	/*
	 * getter and setter
	 */
	public BigInteger getC1() {
		return c1;
	}

	public void setC1(BigInteger c1) {
		this.c1 = c1;
	}

	public BigInteger getC2() {
		return c2;
	}

	public void setC2(BigInteger c2) {
		this.c2 = c2;
	}

	@Override
	public String toString() {
		return "------\nX:"+getC1()+"\nY:"+getC2()+"\n------";
	}
	
}

