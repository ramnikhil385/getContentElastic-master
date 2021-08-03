package com.dms.doc360.rest.getcontent.dfc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * DFC Session Manager to provide session for given repository and user id.
 * 
 * @author Sudheer Raganaboina, Tarun Verma
 *
 */
@Slf4j
@Service
public class SUManager {

	private IDfSessionManager adminSessionManager = null;
	private Map<String, IDfSession> sessionMap = new HashMap<>();

	@Autowired
	private RepositoryCredentials repositoryCredentials;

	/**
	 * Load the repositories during initialization, and setup Admin Session
	 * Manager.
	 * 
	 * @throws ConfigurationException
	 * @throws DfException
	 */
	// @PostConstruct
	public void loadReporistories() throws ConfigurationException, DfException {

		// create new client for this session
		IDfClientX clientx = new DfClientX();
		IDfClient client = clientx.getLocalClient();
		adminSessionManager = client.newSessionManager();

		// setup the user credentials on session manager
		IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
		// provide user ID
		loginInfoObj.setUser(repositoryCredentials.getSuperUser());
		// provide password
		loginInfoObj.setPassword(repositoryCredentials.getPwd());
		loginInfoObj.setDomain("");

		// provide docbase name, and set login credentials
		adminSessionManager.setIdentity(repositoryCredentials.getName(), loginInfoObj);
		// authenticate the user, no session handle is returned
		adminSessionManager.authenticate(repositoryCredentials.getName());
		log.info("Successfully authenticated user {} for repository {}", repositoryCredentials.getSuperUser(),
				repositoryCredentials.getName());
		IDfSession session = adminSessionManager.getSession(repositoryCredentials.getName());
		this.sessionMap.put(repositoryCredentials.getName(), session);
	}

	/**
	 * Dispense the login ticket for the given repository, user id and timeout.
	 * 
	 * @param repository
	 * @param userName
	 * @param timeout
	 * @return String Login Ticket value
	 * @throws DfException
	 */
	public synchronized String dispenseLoginTicket(String repository, String userName, int timeout) throws DfException {

		IDfSession session = null;
		IDfClientX clientx = null;
		IDfClient client = null;
		try {
			synchronized (SUManager.class) {
				// get the session from the map
				session = this.sessionMap.get(repository);
				// create new session if not present or not connected
				if (session == null || !session.isConnected()) {
					clientx = new DfClientX();
					client = clientx.getLocalClient();
					adminSessionManager = client.newSessionManager();
					session = adminSessionManager.getSession(repository);
					this.sessionMap.put(repository, session);
				}
				// get the login ticket for the given user using admin session
				String ticket = session.getLoginTicketEx(userName, "global", timeout, false, null);
				log.info("Successfully created login ticket for repository {} and user {}", repository, userName);
				return ticket;
			}
		} catch (DfAuthenticationException dfae) {
			log.error("Invalid userid; Unable to get Login Ticket! UserId: " + userName + ", Repository: " + repository,
					dfae);
			throw dfae;
		} catch (DfException dfe) {
			log.error("Error while getting Login Ticket using Admin Session Manager. UserId: " + userName
					+ ", Repository: " + repository, dfe);
			throw dfe;
		} finally {
			// release the admin session
			// release(session);
		}
	}

	/**
	 * Get the DFC session for the given repository.
	 * 
	 * @param repository
	 * @return IDfSession
	 * @throws DfException
	 */
	public IDfSession getSession(String repository) throws DfException {

		IDfSession session = null;
		String sessionId = null;
		try {
			// ensure admin session manager is configured
			if (adminSessionManager == null) {
				throw new DfException("Admin session manager was not configured during service initialization.");
			}
			session = adminSessionManager.getSession(repository);
			sessionId = session.getSessionId();
			log.info("Successfully received session with session id: {}", sessionId);
			return session;
		} catch (DfAuthenticationException dfae) {
			log.error("Invalid userid and password! UserID: " + ", Repository: " + repository, dfae);
			throw dfae;
		} catch (DfException dfe) {
			log.error("Error during authentication and setting up DFC session." + ", Repository: " + repository, dfe);
			throw dfe;
		}
	}

	/**
	 * Release the DFC session and underlying resources.
	 * 
	 * @param idfSession
	 */
	public void releaseSession(IDfSession idfSession) {
		if (adminSessionManager != null && idfSession != null) {
			if (!idfSession.isConnected()) {
				log.info("Session is already released for user");
				return;
			}
			try {
				adminSessionManager.release(idfSession);
				log.info("Ended session for user");
			} catch (Exception th) {
				log.error("Ignoring the error while releasing the session ", th);
			}
		}
	}
}
