package com.android.ihbut0.seek.utils;
 
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
/**
 *  eg:abcd的全排列结果分别为：a,b,c,d,ab,ac,ad,bc,bd,cd,abc,abd,acd,bcd,abcd
 *   
 * @author Z-H
 *
 */
 
public class Combine {
	/**
	 * @param sourceData 原始数据
	 * @param workSpace	   自定义一个临时空间，用来存储每次符合条件的值
	 * @param k		C(n,k)中的k
	 */
	List<BigInteger> sourceData;
	BigInteger[] coefResult;
	public void setData(List<BigInteger> data)
	{
		this.sourceData=data;
		coefResult=new BigInteger[data.size()+1];
	}
	public String[] putResult()
	{
		String[] res = new String[coefResult.length];
		for ( int i = 0, j = coefResult.length-1 ; i < coefResult.length ; i++,j-- ){
			res[i] = coefResult[j].toString();
		}
		return res;
	}
	public void combinerSelect(List<BigInteger> data, List<BigInteger> workSpace, int n, int k) {
		List<BigInteger> copyData;
		List<BigInteger> copyWorkSpace;
		if(workSpace.size() == k) {
			BigInteger coefTemp= BigInteger.ONE;
			for(BigInteger c : workSpace){
				coefTemp = coefTemp.multiply(c);
			}
			if (coefResult[k] == null) {
				coefResult[k] = BigInteger.ZERO;
			}
			coefResult[k] = coefTemp.add(coefResult[k]);
		}
		
		for(int i = 0; i < data.size(); i++) {
			copyData = new ArrayList<BigInteger>(data);
			copyWorkSpace = new ArrayList<BigInteger>(workSpace);
			
			copyWorkSpace.add(copyData.get(i));
			for(int j = i; j >=  0; j--)
				copyData.remove(j);
			combinerSelect(copyData, copyWorkSpace, n, k);
		}
		
	}
	public void calculateCoefs()
	{
		if(sourceData.size()>0)
		{
			for(int i = 0; i <= sourceData.size(); i++)
				combinerSelect(sourceData, new ArrayList<BigInteger>(), sourceData.size(), i);
		}
	}
}
