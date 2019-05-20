package com.android.ihbut0.seek.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class IntersectionUtil {

	private static byte[] gBytes = {127,-23,39,-100,-74,-7,104,-52,67,63,-8,-12,107,-109,-108,2,
            15,-125,9,84,-43,-93,-66,107,6,113,18,112,14,-111,-106,-87,-90,-4,-89,-128,-46,68,-24,
            -24,-58,-103,74,54,-3,-32,-23,-43,62,-16,95,-20,-110,-113,92,118,-94,88,52,99,-104,-28,
            125,95,-112,123,-56,27,-42,112,-88,36,56,33,-66,-63,-57,84,-51,33,0,109,33,85,-73,-88,
            -43,0,70,-4,100,-126,-67,62,102,-54,45,-31,-9,-73,77,-23,69,-33,-108,62,-20,-27,-26,40,
            9,84,-104,40,-2,124,-112,22,96,-89,26,-71,88,112,40,69,27,-110};

	public static Pairing pairing = PairingFactory.getPairing("assets/mi.properties");
	public static Field Zp = pairing.getZr();
	public static Field G = pairing.getG1();
	private static Element g= G.newElementFromBytes(gBytes).getImmutable();//G的生成元
	private static BigInteger p = G.getOrder();//G的阶

	/**
	 * 通过r0隐藏兴趣集合
	 * @param Qx
	 * @return
	 */
	public static List<byte[]> enCrypt(Element r0, String[] Qx) {
		List<byte[]> res = new ArrayList<>();
		System.out.println("r0:"+r0);
		for (int i = 0; i < Qx.length; i++) {
			String qi = Qx[i];
			BigInteger qInteger = new BigInteger(qi);
			Element qElement = Zp.newElement(qInteger);

			Element cIndex = r0.getImmutable().add(qElement.getImmutable()).getImmutable();
			Element c = g.getImmutable().powZn(cIndex).getImmutable();
			res.add( c.toBytes() );
		}
		return res;
	}

    /**
     * 得到秘密集合C后响应
     * @param C
     * @param Y
     * @return
     */
    public static List<byte[][]> echo(List<byte[]> C, String[] Y) {
        List<byte[][]> res = new ArrayList<>();

        List<String> yList = new ArrayList<>();
        for (int i = 0; i < Y.length; i++) {
            yList.add(Y[i]);
        }
        Element r1 = null;//保存r1

        //for each j from 1 to n循环啦
        int j = 1;
        for (String string : yList) {		//j
            //1. 将String -> 大数
            BigInteger yInteger = new BigInteger(string.getBytes()).abs();
            Element yj = Zp.newElement(yInteger).getImmutable();

            //2. 随机大数
            Element rj = Zp.newRandomElement().getImmutable();

            if (j == 1) {  r1 = rj;  }//给r1赋值
            Element Cyj = null;//累乘保存结果
            //3. 计算累乘
            for(int i = 0 ; i < C.size() ; i++){
                byte[] ciBytes = C.get(i);
                // >1 计算指数rj (yj^i)
                Element tmp1 = yj.pow(new BigInteger(""+i)).getImmutable();
                Element index = tmp1.mul(rj).getImmutable();
                // >2 乘法
                Element ci = G.newElementFromBytes(ciBytes).getImmutable();
                Element cPowed = ci.powZn(index).getImmutable();
                if ( i == 0) {
                    Cyj = cPowed.getImmutable();
                } else {
                    Cyj = cPowed.mul(Cyj).getImmutable();
                }
            }//END FOR i
            // 4. 计算Sj
            Element Sj = null;
            Element gIndex = null;
            if (yj.isOne() ) {
                //C.size() 即 d
                Element tmp1 = yj.mul(new BigInteger(""+C.size())).getImmutable();
                System.err.println(tmp1+"\n"+tmp1.negate());
                gIndex = tmp1.mulZn(r1).getImmutable();
            }else {
                Element one = Zp.newOneElement().getImmutable();
                // yList.size()即n
                Element tmp1 = yj.pow( new BigInteger(""+C.size()) ).getImmutable();//yj^n
                Element tmp2 = tmp1.sub(one).mulZn(rj).getImmutable();//(1-yj^n)*r1
                Element tmp3 = yj.sub(one).getImmutable();//1-yj
                gIndex = tmp2.div(tmp3).getImmutable();
            }
            Sj = g.powZn(gIndex).getImmutable();
            byte[][] elements ={Cyj.toBytes(), Sj.toBytes()};

            res.add(elements);
            j++;
        }// END FOR j
        return res;
    }

    public static int[] decrypt(Element r0, List<Element[]> res) {
        int[] J = new int[res.size()];
        for (int i = 0; i < res.size(); i++) {

            Element Cyj = res.get(i)[0];
            Element Sj = res.get(i)[1].getImmutable();
            Element Sr = Sj.powZn(r0).getImmutable();

            if (Sr.isEqual( Cyj )) {
                J[i] = 1;
            }else {
                J[i] = 0;
            }
            System.err.println(J[i]);
        }
        return J;
    }

    /**
     * 显示byte[]数组内容
     * @param name
     * @param bs
     */
//    public static void showBytes(String name, byte[] bs) {
//        System.out.print("MIJING bytes-"+name+":{");
//        int i = 0;
//        for(byte b:bs){
//            System.out.print(b+",");
//            i++;
//        }
//        System.out.print("} : "+i+"\n");
//    }

}
