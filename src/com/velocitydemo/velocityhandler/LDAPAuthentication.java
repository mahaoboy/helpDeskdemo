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

public class LDAPAuthentication {
	private String URL;
	private String BASEDN;
	private final String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private LdapContext ctx = null;
	private final Control[] connCtls = null;
	private String root;
	private String rootpass;

	public LDAPAuthentication(HashMap<String, String> properties) {
		this.URL = "ldap://" + properties.get("URL") + ":"
				+ properties.get("Port") + "/";
		this.BASEDN = properties.get("BASEDN");
		this.root = properties.get("Root");
		this.rootpass = properties.get("RootPassword");
	}

	private void LDAP_connect() {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
		env.put(Context.PROVIDER_URL, URL + BASEDN);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		env.put(Context.SECURITY_PRINCIPAL, root);
		env.put(Context.SECURITY_CREDENTIALS, rootpass);
		// �˴�����ָ���û���������,���Զ�ת��Ϊ������¼
		try {
			ctx = new InitialLdapContext(env, connCtls);
		} catch (javax.naming.AuthenticationException e) {
			System.out.println("��֤ʧ�ܣ�" + e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getUserDN(String uid) {
		String userDN = "";
		LDAP_connect();
		try {
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> en = ctx.search("", "uid=" + uid,
					constraints);
			if (en == null || !en.hasMoreElements()) {
				System.out.println("δ�ҵ����û�");
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
			e.printStackTrace();
		}

		return userDN;
	}

	public boolean authenricate(String UID, String password) {
		boolean valide = false;
		String userDN = getUserDN(UID);

		try {
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(connCtls);
			System.out.println(userDN + " ��֤ͨ��");
			valide = true;
		} catch (AuthenticationException e) {
			System.out.println(userDN + " ��֤ʧ��");
			System.out.println(e.toString());
			valide = false;
		} catch (NamingException e) {
			System.out.println(userDN + " ��֤ʧ��");
			valide = false;
		}

		return valide;
	}
}
