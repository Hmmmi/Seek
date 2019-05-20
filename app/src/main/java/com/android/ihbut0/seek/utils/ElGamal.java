package com.android.ihbut0.seek.utils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;

/**
 * Security of the ElGamal algorithm depends on the difficulty of computing
 * discrete logs in a large prime modulus
 * <p>
 * - Theorem 1 : a in [Z/Z[p]] then a^(p-1) [p] = 1 - Theorem 2 : the order of
 * an element split the order group
 */
public final class ElGamal {

    public static BigInteger TWO = new BigInteger("2");

    /**
     * @return
     */
    public static Map<String, BigInteger> KeyGeneration() {
        return KeyGeneration(200);
    }

    /**
     * Generate the public key and the secret key for the ElGamal encryption.
     *
     * @param n key size
     */
    public static Map<String, BigInteger> KeyGeneration(int n) {
        // (a) take a random prime p with getPrime() function. p = 2 * p' + 1 with
        // prime(p') = true
        BigInteger p = getPrime(n, 40, new Random());
        // (b) take a random element in [Z/Z[p]]* (p' order)
        BigInteger g = randNum(p, new Random());
        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(ElGamal.TWO);

        while (!g.modPow(pPrime, p).equals(BigInteger.ONE)) {
            if (g.modPow(pPrime.multiply(ElGamal.TWO), p).equals(BigInteger.ONE))
                g = g.modPow(TWO, p);
            else
                g = randNum(p, new Random());
        }

        Map<String, BigInteger> map = new HashMap<String, BigInteger>();

        // (c) take x random in [0, p' - 1]
        BigInteger x = randNum(pPrime.subtract(BigInteger.ONE), new Random());
        BigInteger h = g.modPow(x, p);
        // secret key is (p, x) and public key is (p, g, h)
        map.put("pk_p", p);
        map.put("pk_g", g);
        map.put("pk_h", h);

        map.put("sk_s", p);
        map.put("sk_x", x);
        return map;
    }

    /**
     * @param p
     * @param g
     * @param h
     * @param m
     * @return
     */
    @SuppressWarnings("unused")
    private static Element normalEncrypt(BigInteger p, BigInteger g, BigInteger h, BigInteger m) {
        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(ElGamal.TWO);
        // [0, N -1] or [1, N-1] ?
        BigInteger r = randNum(pPrime, new Random());
        // encrypt couple (g^r, m * h^r)
        BigInteger x = g.modPow(r, p);
        BigInteger y = m.multiply(h.modPow(r, p));
        return new Element(x, y);
    }

    /**
     * Homomorphic encrypt m by public key
     *
     * @param pk
     * @param m
     * @return
     */
    public static Element encrypt(Map<String, BigInteger> pk, BigInteger m) {
        BigInteger p = pk.get("pk_p");
        BigInteger g = pk.get("pk_g");
        BigInteger h = pk.get("pk_h");
        return encryptHomomorphic(p, g, h, m);
    }

    /**
     * Homomorphic encrypt m by detail number
     *
     * @param p
     * @param g
     * @param h
     * @param message
     * @return
     */
    private static Element encryptHomomorphic(BigInteger p, BigInteger g, BigInteger h, BigInteger message) {
        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(ElGamal.TWO);
        //  [0, N -1] or [1, N-1] ?
        BigInteger r = randNum(pPrime, new Random());
        // encrypt couple (g^r, h^r * g^m)
        BigInteger hr = h.modPow(r, p);
        BigInteger gm = g.modPow(message, p);

        BigInteger c1 = g.modPow(r, p);
        BigInteger c2 = hr.multiply(gm).mod(p);
        return new Element(c1, c2);
    }

    /**
     * @param p
     * @param x
     * @param element
     * @return
     */
    @SuppressWarnings("unused")
    private static BigInteger normalDecrypt(BigInteger p, BigInteger x, Element element) {
        BigInteger gr = element.getC1();
        BigInteger mhr = element.getC2();
        BigInteger hr = gr.modPow(x, p);
        return mhr.multiply(hr.modInverse(p)).mod(p);
    }


    public static BigInteger decrypt(Map<String, BigInteger> keyPair, Element c){
        BigInteger[] gn = genGn(keyPair, 2);
        BigInteger p = keyPair.get("sk_s");
        BigInteger x = keyPair.get("sk_x");
        BigInteger g = keyPair.get("pk_g");
        return decryptPre(p, x, g, c, gn);
    }

    /**
     * Homomorphic decrypt cipher by key pair
     *
     * @param keyPair
     * @param c
     * @return
     */
    public static BigInteger decrypt(Map<String, BigInteger> keyPair, Element c, BigInteger[] gn) {
        BigInteger p = keyPair.get("sk_s");
        BigInteger x = keyPair.get("sk_x");
        BigInteger g = keyPair.get("pk_g");
        return decryptPre(p, x, g, c, gn);
    }

    /**
     * @param keyPair
     * @param n
     * @return
     */
    public static BigInteger[] genGn(Map<String, BigInteger> keyPair, int n) {
        BigInteger[] res = new BigInteger[n];
        BigInteger g = keyPair.get("pk_g");
        BigInteger p = keyPair.get("pk_p");
        for (int i = 0; i < n; i++) {
            res[i] = g.modPow(new BigInteger("" + i), p);
        }
        return res;
    }

    private static BigInteger decryptPre(BigInteger p, BigInteger x, BigInteger g, Element element, BigInteger[] gn) {
        BigInteger hr = element.getC1().modPow(x, p);
        BigInteger gm = element.getC2().multiply(hr.modInverse(p)).mod(p);

        for (int i = 0; i < gn.length; i++) {
            if (gm.equals(gn[i])) {
                return new BigInteger("" + i);
            }
        }
        return new BigInteger("-1");
    }

    /**
     * Homomorphic decrypt cipher by detail number
     *
     * @param p
     * @param x
     * @param g
     * @param element
     * @return
     */
    @SuppressWarnings("unused")
    private static BigInteger decryptHomomorphic(BigInteger p, BigInteger x, BigInteger g, Element element) {
        BigInteger hr = element.getC1().modPow(x, p);
        BigInteger gm = element.getC2().multiply(hr.modInverse(p)).mod(p);

        BigInteger m = BigInteger.ZERO;
        BigInteger gm_prime = g.modPow(m, p);

        while (!gm_prime.equals(gm)) {
            m = m.add(BigInteger.ONE);
            gm_prime = g.modPow(m, p);
        }
        return m;
    }


    /**
     * Return a prime p = 2 * p' + 1
     *
     * @param nb_bits
     * @param certainty
     * @param prg
     * @return
     */
    private static BigInteger getPrime(int nb_bits, int certainty, Random prg) {
        BigInteger pPrime = new BigInteger(nb_bits, certainty, prg);
        // p = 2 * pPrime + 1
        BigInteger p = pPrime.multiply(TWO).add(BigInteger.ONE);

        while (!p.isProbablePrime(certainty)) {
            pPrime = new BigInteger(nb_bits, certainty, prg);
            p = pPrime.multiply(TWO).add(BigInteger.ONE);
        }
        return p;
    }

    /**
     * Return a random integer in [0, N - 1]
     *
     * @param N
     * @param prg
     * @return
     */
    private static BigInteger randNum(BigInteger N, Random prg) {
        return new BigInteger(N.bitLength() + 100, prg).mod(N);
    }

}