
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
            // 椭圆曲线加密生成私钥
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            keyGen.initialize(ecSpec);
            // 创建密钥对
            KeyPair kp = keyGen.generateKeyPair();
            PublicKey pub = kp.getPublic();
            PrivateKey pvt = kp.getPrivate();
            // 获取私钥
            ECPrivateKey epvt = (ECPrivateKey) pvt;
            String sepvt = Utils.adjustTo64(epvt.getS().toString(16)).toUpperCase();
//            System.out.println("s[" + sepvt.length() + "]: " + sepvt);
            sepvt = "16AE244817999A8AF58B8B70334EA72CBDA496D9D95F34B593F004033E19B89D";
//            System.out.println("私钥{}" + sepvt);
            // 获取公钥
            ECPublicKey epub = (ECPublicKey) pub;
            ECPoint pt = epub.getW();
            String sx = Utils.adjustTo64(pt.getAffineX().toString(16)).toUpperCase();
            String sy = Utils.adjustTo64(pt.getAffineY().toString(16)).toUpperCase();
            // 公钥
            String bcPub = "04" + sx + sy;
//            System.out.println("公钥{}" + bcPub);
            // sha256
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] s1 = sha.digest(bcPub.getBytes("UTF-8"));
//            System.out.println("sha256后{}" + Utils.byte2Hex(s1).toUpperCase());
            // ripemd160
            RIPEMD160Digest digest = new RIPEMD160Digest();
            digest.update(s1, 0, s1.length);
            byte[] ripemd160Bytes = new byte[digest.getDigestSize()];
            digest.doFinal(ripemd160Bytes, 0);
//            System.out.println("ripemd160加密后{}" + Utils.bytesToHexString(ripemd160Bytes));
            // 添加主网络版本字节
            byte[] networkID = new BigInteger(version, 16).toByteArray();
            byte[] extendedRipemd160Bytes = Utils.add(networkID, ripemd160Bytes);
//            System.out.println("添加NetworkID后{}" + Utils.bytesToHexString(extendedRipemd160Bytes));
            // 重复sha256两次
            byte[] twiceSha256Bytes = Utils.sha256(Utils.sha256(extendedRipemd160Bytes));
//            System.out.println("两次sha256加密后{}" + Utils.bytesToHexString(twiceSha256Bytes));
            // 获取前四个字节当做地址校验和
            byte[] checksum = new byte[4];
            System.arraycopy(twiceSha256Bytes, 0, checksum, 0, 4);
//            System.out.println("checksum{}" + Utils.bytesToHexString(checksum));
            byte[] binaryBitcoinAddressBytes = Utils.add(extendedRipemd160Bytes, checksum);
//            System.out.println("添加checksum之后{}" + Utils.bytesToHexString(binaryBitcoinAddressBytes));
            // 使用base58对地址进行编码
            String ltccoinAddress = Base58.encode(binaryBitcoinAddressBytes);
//            System.out.println("地址{}" + ltccoinAddress);
            Address address = new Address(bcPub, ltccoinAddress);
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}