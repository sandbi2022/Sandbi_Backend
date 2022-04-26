
package com.address;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;

import org.bitcoinj.core.Base58;
import org.spongycastle.crypto.digests.RIPEMD160Digest;

public class Address {
	private String publicKey;
	private String address;
	
	public Address(String publicKey, String address) {
		this.publicKey = publicKey;
		this.address = address;
	}
	
	public String getPublicKey() {
		return this.publicKey;
	}
	
	public String getAddressy() {
		return this.address;
	}
	
	public static Address parent(String version) {
        try {
            // ��Բ���߼�������˽Կ
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            keyGen.initialize(ecSpec);
            // ������Կ��
            KeyPair kp = keyGen.generateKeyPair();
            PublicKey pub = kp.getPublic();
            PrivateKey pvt = kp.getPrivate();
            // ��ȡ˽Կ
            ECPrivateKey epvt = (ECPrivateKey) pvt;
            String sepvt = Utils.adjustTo64(epvt.getS().toString(16)).toUpperCase();
//            System.out.println("s[" + sepvt.length() + "]: " + sepvt);
            sepvt = "16AE244817999A8AF58B8B70334EA72CBDA496D9D95F34B593F004033E19B89D";
//            System.out.println("˽Կ{}" + sepvt);
            // ��ȡ��Կ
            ECPublicKey epub = (ECPublicKey) pub;
            ECPoint pt = epub.getW();
            String sx = Utils.adjustTo64(pt.getAffineX().toString(16)).toUpperCase();
            String sy = Utils.adjustTo64(pt.getAffineY().toString(16)).toUpperCase();
            // ��Կ
            String bcPub = "04" + sx + sy;
//            System.out.println("��Կ{}" + bcPub);
            // sha256
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] s1 = sha.digest(bcPub.getBytes("UTF-8"));
//            System.out.println("sha256��{}" + Utils.byte2Hex(s1).toUpperCase());
            // ripemd160
            RIPEMD160Digest digest = new RIPEMD160Digest();
            digest.update(s1, 0, s1.length);
            byte[] ripemd160Bytes = new byte[digest.getDigestSize()];
            digest.doFinal(ripemd160Bytes, 0);
//            System.out.println("ripemd160���ܺ�{}" + Utils.bytesToHexString(ripemd160Bytes));
            // ���������汾�ֽ�
            byte[] networkID = new BigInteger(version, 16).toByteArray();
            byte[] extendedRipemd160Bytes = Utils.add(networkID, ripemd160Bytes);
//            System.out.println("���NetworkID��{}" + Utils.bytesToHexString(extendedRipemd160Bytes));
            // �ظ�sha256����
            byte[] twiceSha256Bytes = Utils.sha256(Utils.sha256(extendedRipemd160Bytes));
//            System.out.println("����sha256���ܺ�{}" + Utils.bytesToHexString(twiceSha256Bytes));
            // ��ȡǰ�ĸ��ֽڵ�����ַУ���
            byte[] checksum = new byte[4];
            System.arraycopy(twiceSha256Bytes, 0, checksum, 0, 4);
//            System.out.println("checksum{}" + Utils.bytesToHexString(checksum));
            byte[] binaryBitcoinAddressBytes = Utils.add(extendedRipemd160Bytes, checksum);
//            System.out.println("���checksum֮��{}" + Utils.bytesToHexString(binaryBitcoinAddressBytes));
            // ʹ��base58�Ե�ַ���б���
            String ltccoinAddress = Base58.encode(binaryBitcoinAddressBytes);
//            System.out.println("��ַ{}" + ltccoinAddress);
            Address address = new Address(bcPub, ltccoinAddress);
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}