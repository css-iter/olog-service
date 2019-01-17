/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;

public class JCRUtil extends OlogContextListener {
    private static Session session;
    private static List<Session> allSessions = new ArrayList<Session>();
    private static final String WEBINF = "WEB-INF";

    /**
     * Create an instance of JCRUtil
     */
    public JCRUtil() {
        try {
            URL url = JCRUtil.class.getResource("JCRUtil.class");
            String className = url.getFile();
            String filePath = className.substring(0,className.indexOf(WEBINF) + WEBINF.length());
            String xml;
            Boolean jcrInDb = Boolean.FALSE;
            try {
                Context initCtx = new InitialContext();
                jcrInDb = (Boolean) initCtx.lookup("java:/comp/env/JCR_IN_DB");
            } catch (NamingException e ) {
            }
            System.out.println("JCR in DB (java:/comp/env/JCR_IN_DB): "+jcrInDb);
            if (Boolean.TRUE.equals(jcrInDb)) {
                xml = filePath + "/repository_db.xml";
            } else {
                xml = filePath + "/repository.xml";
            }
            String dir = null;
            try {
                Context initCtx = new InitialContext();
                dir = (String) initCtx.lookup("java:/comp/env/JCR_REPO_PATH");
            } catch (NamingException e ) {
            }
            if (dir==null) {
                dir = "jackrabbit";
            }
            System.out.println("JCR in Path (java:/comp/env/JCR_REPO_PATH): "+dir);
            RepositoryConfig config = RepositoryConfig.create(xml, dir);
            Repository repository = RepositoryImpl.create(config);

            
            SimpleCredentials adminCred = new 
            SimpleCredentials("admin", new char[0]); 
            session = repository.login(adminCred);
            allSessions.add(session);
                        
            // Connect to additional FDW schemas
            try {
                Context initCtx = new InitialContext();
                String fdwSchemas = (String) initCtx.lookup("java:/comp/env/JCR_FDW_SCHEMAS");                
    			String xmlContent = new String(Files.readAllBytes(Paths.get(xml)), StandardCharsets.UTF_8);
                
                for(String schema : fdwSchemas.split(",")) {
                	schema = schema.trim();
                	if(!schema.equals("")) {
                        System.out.println("Configuring additonal JCR to FDW schema '"+schema+"'");                
        				File tmp = File.createTempFile(schema, "repository_db.xml");
        				PrintWriter pw = new PrintWriter(tmp);    				
                        // Replace table prefix to use Views created on FWD schema tables for JCR
                        // We use views because JCR does not allow a schema prefix (with dot) in table name prefix
        				String replaced = xmlContent.replaceAll("jcr_", schema+"_jcr_");
        				// Set schemaCheckEnabled to FALSE (we don't have permission to create tables in any FWD schema)
        				replaced = replaced.replaceAll("</FileSystem>", "\t<param name=\"schemaCheckEnabled\" value=\"false\" />\n\t</FileSystem>");
        				replaced = replaced.replaceAll("</DataStore>", "\t<param name=\"schemaCheckEnabled\" value=\"false\" />\n\t</DataStore>");
        				replaced = replaced.replaceAll("</PersistenceManager>", "\t<param name=\"schemaCheckEnabled\" value=\"false\" />\n\t</PersistenceManager>");
        				pw.write(replaced);
        				pw.close();
        				try {
        					allSessions.add(RepositoryImpl.create(RepositoryConfig.create(tmp.getAbsolutePath(), dir+"_"+schema)).login(adminCred));
        				} catch (RepositoryException e) {
        					Logger.getLogger(JCRUtil.class.getName()).log(Level.WARNING, "Failed to initialize FDW repository for "+schema, e);
						}
                	}
                }
            } catch (NamingException e ) {
            	Logger.getLogger(JCRUtil.class.getName()).log(Level.INFO, "JCR_FDW_SCHEMAS not configured");
            }
            

            
		} catch (IOException e) {
			Logger.getLogger(JCRUtil.class.getName()).log(Level.SEVERE, "Error configuring JCR", e);
        } catch (RepositoryException ex) {
            Logger.getLogger(JCRUtil.class.getName()).log(Level.SEVERE, "RepositoryException configuring JCR", ex);
        }
    }
    
    public static Session getSession() {

        return session;
    }
    
    /**
     * Returns a List of Session objects, the own session plus one for each configured Foreign Data Wrapper schema
     * @return List<Session>
     */
    public static List<Session> getAllSessions() {
    	return allSessions;
    }
    
    /**
     * Disconnects from all Repositories
     */
    public static void shutdown() {
    	for(Session session: allSessions) {
    		((RepositoryImpl)session.getRepository()).shutdown();
    	}
    }

}
