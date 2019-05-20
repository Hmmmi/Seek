package com.android.ihbut0.seek.utils;

import java.math.BigInteger;

public class Matrix {

	private BigInteger[][] A;
	private BigInteger[] B;
	private int n;

	public Matrix(BigInteger[][] A, BigInteger[] B) {
		this.A = A;
		this.B = B;
		this.n = B.length;
	}

	/**
	 * 求解未知数,即多项式的系数
	 * @return
	 */
	public BigInteger[] calculateQ(){
		BigInteger[] Q = new BigInteger[n];

		for( int k = 0 ; k < n ; k++ ){
			getColPrimer(k);
			//计算高斯消元比
			for( int i = k+1; i < n ; i++ ){
				BigInteger head = A[i][k];
				BigInteger bottom = A[k][k];
				for( int j = k+1; j < n ; j++ ){
					BigInteger tmp = A[k][j].multiply(head).divide(bottom);
					A[i][j] = A[i][j].subtract(tmp);
				}
				BigInteger tmp = B[k].multiply(head).divide(bottom);
				B[i] = B[i].subtract(tmp);
			}
		}
		//回代求解
		for( int i = n-1 ; i >= 0 ; i-- ){
			Q[i] = B[i];
			for ( int j = i + 1; j < n; j++ ){
				BigInteger tmp = A[i][j].multiply(Q[j]);
				Q[i] = Q[i].subtract(tmp);
			}
			Q[i] = Q[i].divide(A[i][i]);
		}

		return Q;
	}

	/**
	 * 第k列主元素
	 * @param k
	 */
	public void getColPrimer(int k) {
		int max_row = k;
		for (int i = k+1; i < n; i++) {
			if (A[i][k].abs().compareTo(A[k][k].abs()) == 1) {		//A[i][k] > A[k][k]
				max_row = i;
			}
		}
		if (max_row != k) {
			changeRow(max_row, k);
		}
	}

	/**
	 * 交换增广矩阵的第i行跟第k行
	 * @param i
	 * @param k
	 */
	public void changeRow(int i, int k) {
		BigInteger[] tmp = A[i];
		A[i] = A[k];
		A[k] = tmp;

		BigInteger tmp2 = B[i];
		B[i] = B[k];
		B[k] = tmp2;
	}

	/**
	 * 打印增广矩阵
	 */
	public void printMatrix() {
		System.out.println("------------------------------------------");
		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				System.out.print(A[row][col]+"\t");
			}
			System.out.print("\t|\t"+B[row]+"\n");
		}
		System.out.println("------------------------------------------");
	}

}