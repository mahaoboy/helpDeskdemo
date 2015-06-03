package com.velocitydemo.velocityhandler;

import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;

public class LDAPAuthentication {
	private static Logger logger = Logger.getLogger(LDAPAuthentication.class);
	private String URL;
	private String BASEDN;
	private final String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private LdapContext ctx = null;
	private final Control[] connCtls = null;
	private String root;
	private String rootpass;
	private String userNameLDAPField;
	private String userDisplayNameLDAPField;
	public String userMailAddress;

	public LDAPAuthentication(HashMap<String, String> properties) {
		this.URL = "ldap://" + properties.get("URL") + ":"
				+ properties.get("Port") + "/";
		this.BASEDN = properties.get("BASEDN");
		this.root = properties.get("Root");
		this.rootpass = properties.get("RootPassword");
		this.userNameLDAPField = properties.get("UserNameLDAPField");
		this.userDisplayNameLDAPField = properties.get("DisplayNameLDAPField");
		this.userMailAddress = properties.get("MailAddress");
	}

	private void LDAP_connect() {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
		logger.debug(FACTORY);
		env.put(Context.PROVIDER_URL, URL + BASEDN);
		logger.debug(URL + BASEDN);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		env.put(Context.SECURITY_PRINCIPAL, root);
		logger.debug(root);
		env.put(Context.SECURITY_CREDENTIALS, rootpass);
		logger.debug(rootpass);
		// �˴�����ָ���û���������,���Զ�ת��Ϊ������¼
		try {
			ctx = new InitialLdapContext(env, connCtls);
		} catch (javax.naming.AuthenticationException e) {
			System.out.println("��֤ʧ�ܣ�" + e.toString());
			logger.debug("��֤ʧ�ܣ�", e);
		} catch (Exception e) {
			logger.debug("��֤ʧ�ܣ�", e);
		}
	}

	private String getUserDN(String uid) {
		String userDN = "";
		LDAP_connect();
		try {
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> en = ctx.search("",
					userNameLDAPField + "=" + uid, constraints);
			if (en == null || !en.hasMoreElements()) {
				System.out.println("δ�ҵ����û�");
				logger.debug("δ�ҵ����û�");
			}
			// maybe more than one element
			while (en != null && en.hasMoreElements()) {
				Object obj = en.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult si = (SearchResult) obj;
					userDN += si.getName();
					userDN += "," + BASEDN;
				} else {
					System.out.println(obj);
				}
			}
		} catch (Exception e) {
			System.out.println("�����û�ʱ�����쳣��");
			logger.debug("�����û�ʱ�����쳣��", e);
			e.printStackTrace();
		}

		return userDN;
	}

	public boolean authenricate(String UID, String password) {
		boolean valide = false;
		String userDN = getUserDN(UID);
		if(userDN.isEmpty()){
			return valide;
		}

		try {
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(connCtls);
			System.out.println(userDN + " ��֤ͨ��");
			logger.debug(userDN + " ��֤ͨ��");
			valide = true;
		} catch (AuthenticationException e) {
			System.out.println(userDN + " ��֤ʧ��");
			logger.debug(userDN + " ��֤ʧ��" , e);
			System.out.println(e.toString());
			valide = false;
		} catch (NamingException e) {
			System.out.println(userDN + " ��֤ʧ��");
			logger.debug(userDN + " ��֤ʧ��" , e);
			valide = false;
		} catch (Exception e) {
			System.out.println(userDN + " ��֤ʧ��");
			logger.debug(userDN + " ��֤ʧ��" , e);
			valide = false;
		}

		return valide;
	}

	public String getUserDisplayName(String uid) {
		String userDisplayName = "";
		LDAP_connect();
		try {
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> en = ctx.search("",
					userNameLDAPField + "=" + uid, constraints);
			if (en == null || !en.hasMoreElements()) {
				System.out.println("δ�ҵ����û�");
				logger.debug("δ�ҵ����û�");
			}
			// maybe more than one element
			while (en != null && en.hasMoreElements()) {
				Object obj = en.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult si = (SearchResult) obj;
					userDisplayName = si.getAttributes().get(userDisplayNameLDAPField)
							.get().toString();
				} else {
					System.out.println(obj);
					logger.debug(obj);
				}
			}
		} catch (Exception e) {
			System.out.println("�����û�ʱ�����쳣��");
			logger.debug("�����û�ʱ�����쳣��" , e);
			e.printStackTrace();
		}

		return userDisplayName;
	}
	
	public String getUserAttribute(String uid, String searchField) {
		String attrString = "";
		LDAP_connect();
		try {
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> en = ctx.search("",
					userNameLDAPField + "=" + uid, constraints);
			if (en == null || !en.hasMoreElements()) {
				System.out.println("δ�ҵ����û�");
				logger.debug("δ�ҵ����û�");
			}
			// maybe more than one element
			while (en != null && en.hasMoreElements()) {
				Object obj = en.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult si = (SearchResult) obj;
					attrString = si.getAttributes().get(searchField)
							.get().toString();
					logger.debug("Attribute: " + attrString);
				} else {
					System.out.println(obj);
					logger.debug(obj);
				}
			}
		} catch (Exception e) {
			System.out.println("�����û�ʱ�����쳣��");
			logger.debug("�����û�ʱ�����쳣��" , e);
			e.printStackTrace();
		}

		return attrString;
	}
}
