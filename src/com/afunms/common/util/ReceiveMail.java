package com.afunms.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

//This function can be used to send the mail
// with the parameters given to it
//U have to specify the smtp server through
//which u have to send the mail
//since i was trying with a homenetmail
//account i directly sent the mail its server
//For sending this mail u need a mail server
//which lets u to relay the messages
//Try this thing for sending to a
//www.homenetmail.com account because it lets
//u send
//mails to the accounts like example try
//sending it to a "abc@homenetmail.com"
//account.Create the mail account in homenet
//mail first. If u get any other server which
//supports relaying u can try this on that
//also.

//Use this function in ur Servlet to send
//mail by calling the function with the
//parameters

public class ReceiveMail {

	private MimeMessage mimeMessage = null;
	private String saveAttachPath = "";// �������غ�Ĵ��Ŀ¼
	private StringBuffer bodytext = new StringBuffer(); // ����ʼ����ݵ�StringBuffer����

	private String dateformat = "yy-MM-ddHH:mm"; // Ĭ�ϵ���ǰ��ʾ��ʽ

	/**
	 * ���캯��,��ʼ��һ��MimeMessage����
	 */
	public ReceiveMail() {
	}

	public ReceiveMail(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	/**
	 * ����ø������·����
	 */

	public String getAttachPath() {
		return saveAttachPath;
	}

	/**
	 * ����ʼ���������
	 */

	public String getBodyText() {
		return bodytext.toString();
	}

	/**
	 * ��÷����˵ĵ�ַ������
	 */
	public String getFrom() throws Exception {
		InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
		String from = address[0].getAddress();
		if (from == null) {
			from = "";
		}
		String personal = address[0].getPersonal();
		if (personal == null) {
			personal = "";
		}
		String fromaddr = personal + "<" + from + ">";
		return fromaddr;
	}

	/**
	 * ����ʼ����ռ��ˣ����ͣ������͵ĵ�ַ�����������������ݵĲ����Ĳ�ͬ "to"----�ռ���"cc"---�����˵�ַ"bcc"---�����˵�ַ
	 */

	public String getMailAddress(String type) throws Exception {
		String mailaddr = "";
		String addtype = type.toUpperCase();
		InternetAddress[] address = null;
		if (addtype.equals("TO") || addtype.equals("CC") || addtype.equals("BCC")) {
			if (addtype.equals("TO")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
			} else if (addtype.equals("CC")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
			} else {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
			}
			if (address != null) {
				for (int i = 0; i < address.length; i++) {
					String email = address[i].getAddress();
					if (email == null) {
						email = "";
					} else {
						email = MimeUtility.decodeText(email);
					}
					String personal = address[i].getPersonal();
					if (personal == null) {
						personal = "";
					} else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + email + ">";
					mailaddr += "," + compositeto;
				}
				mailaddr = mailaddr.substring(1);
			}
		} else {
			throw new Exception("Error emailaddr type!");
		}
		return mailaddr;
	}

	/**
	 * �����ʼ����ѵõ����ʼ����ݱ��浽һ��StringBuffer�����У������ʼ� ��Ҫ�Ǹ���MimeType���͵Ĳ�ִͬ�в�ͬ�Ĳ�����һ��һ���Ľ���
	 */

	public void getMailContent(Part part) throws Exception {
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;
		if (nameindex != -1) {
			conname = true;
		}
		System.out.println("CONTENTTYPE:" + contenttype);
		if (part.isMimeType("text/plain") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("text/html") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				getMailContent(multipart.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent());
		} else {
		}
	}

	/**
	 * ��ô��ʼ���Message-ID
	 */
	public String getMessageId() throws MessagingException {
		return mimeMessage.getMessageID();
	}

	/**
	 * PraseMimeMessage�����
	 */
	public boolean GetReceieveMail(String host, String username, String password) {
		try {
			Properties props = new Properties();
			// �洢�����ʼ�������ʹ�õ�Э��
			props.setProperty("mail.store.protocol", "pop3");
			// ���ý����ʼ��������ĵ�ַ
			props.setProperty("mail.pop3.host", host);
			Session session = Session.getDefaultInstance(props);
			Store store = session.getStore("pop3");

			store.connect(host, username, password);
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
			if (folder.isOpen()) {
				folder.close(false);
				store.close();
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * �жϴ��ʼ��Ƿ���Ҫ��ִ�������Ҫ��ִ����"true",���򷵻�"false"
	 */
	public boolean getReplySign() throws MessagingException {
		boolean replysign = false;
		String needreply[] = mimeMessage.getHeader("Disposition-Notification-To");
		if (needreply != null) {
			replysign = true;
		}
		return replysign;
	}

	/**
	 * ����ʼ���������
	 */

	public String getSentDate() throws Exception {
		Date sentdate = mimeMessage.getSentDate();
		SimpleDateFormat format = new SimpleDateFormat(dateformat);
		return format.format(sentdate);
	}

	/**
	 * ����ʼ�����
	 */

	public String getSubject() throws MessagingException {
		String subject = "";
		try {
			subject = MimeUtility.decodeText(mimeMessage.getSubject());
			if (subject == null) {
				subject = "";
			}
		} catch (Exception exce) {
		}
		return subject;
	}

	/**
	 * �жϴ��ʼ��Ƿ��������
	 */
	public boolean isContainAttach(Part part) throws Exception {
		boolean attachflag = false;
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					attachflag = true;
				} else if (mpart.isMimeType("multipart/*")) {
					attachflag = isContainAttach(mpart);
				} else {
					String contype = mpart.getContentType();
					if (contype.toLowerCase().indexOf("application") != -1) {
						attachflag = true;
					}
					if (contype.toLowerCase().indexOf("name") != -1) {
						attachflag = true;
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			attachflag = isContainAttach((Part) part.getContent());
		}
		return attachflag;
	}

	/**
	 * ���жϴ��ʼ��Ƿ��Ѷ������δ�����ط���false,��֮����true��
	 */
	public boolean isNew() throws MessagingException {
		boolean isnew = false;
		Flags flags = ((Message) mimeMessage).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isnew = true;
				break;
			}
		}
		return isnew;
	}

	/**
	 * �����渽����
	 */

	public void saveAttachMent(Part part) throws Exception {
		String fileName = "";
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();
					if (fileName.toLowerCase().indexOf("gb2312") != -1) {
						fileName = MimeUtility.decodeText(fileName);
					}
					saveFile(fileName, mpart.getInputStream());
				} else if (mpart.isMimeType("multipart/*")) {
					saveAttachMent(mpart);
				} else {
					fileName = mpart.getFileName();
					if ((fileName != null) && (fileName.toLowerCase().indexOf("GB2312") != -1)) {
						fileName = MimeUtility.decodeText(fileName);
						saveFile(fileName, mpart.getInputStream());
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachMent((Part) part.getContent());
		}
	}

	/**
	 * �������ı��渽����ָ��Ŀ¼�
	 */

	private void saveFile(String fileName, InputStream in) throws Exception {
		String osName = System.getProperty("os.name");
		String storedir = getAttachPath();
		String separator = "";
		if (osName == null) {
			osName = "";
		}
		if (osName.toLowerCase().indexOf("win") != -1) {
			separator = "\\";
			if (storedir == null || storedir.equals("")) {
				storedir = "c:\\tmp";
			}
		} else {
			separator = "/";
			storedir = "/tmp";
		}
		File storefile = new File(storedir + separator + fileName);
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(storefile));
			bis = new BufferedInputStream(in);
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("�ļ�����ʧ��!");
		} finally {
			bos.close();
			bis.close();
		}
	}

	/**
	 * �����ø������·����
	 */

	public void setAttachPath(String attachpath) {
		this.saveAttachPath = attachpath;
	}

	/**
	 * ������������ʾ��ʽ��
	 */

	public void setDateFormat(String format) throws Exception {
		this.dateformat = format;
	}

	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

}