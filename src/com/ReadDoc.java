package com;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ReadDoc {
	public static HashMap getMailInfo() {
		String text = ReadFile("/opt/front/doc/mail.json");
        HashMap result=JSON.parseObject(text,HashMap.class);
        return result;
	}
	
	public static HashMap getBitcoinInfo() {
		String text = ReadFile("/opt/front/doc/bitcoin.json");
        HashMap result=JSON.parseObject(text,HashMap.class);
        return result;
	}
	
	public static HashMap getSqlInfo() {
		String text = ReadFile("/opt/front/doc/sql.json");
        HashMap result=JSON.parseObject(text,HashMap.class);
        return result;
	}
	
	public static String ReadFile(String path){
        String text = "";
        try {
            File file = new File(path);
            
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + '\n');
            }
            text = sb.toString();
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return text;
    }
}
