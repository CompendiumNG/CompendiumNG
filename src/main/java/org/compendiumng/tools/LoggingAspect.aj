package org.compendiumng.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * http://weblogs.java.net/blog/ahashim/archive/2008/07/aspectj_please.html
 */
public aspect LoggingAspect {
	
	/** logger for LoggingAspect.class */
	final Logger log = LoggerFactory.getLogger(getClass());
	
	pointcut sysout():call(* java.io.PrintStream.print*(..));
	
	declare warning:sysout():"Don't use SYSOUT";
	
	after():sysout() {
		log.debug(thisJoinPoint.getArgs()[0].toString() + thisJoinPoint.getSourceLocation());
	}
	
	pointcut psexec(): call(* java.sql.PreparedStatement.execute*(..)) || call(* java.sql.PreparedStatement.executeUpdate*(..));
	
	pointcut psprepare():call(* *.prepareStatement(..));
		
	before():psprepare(){
		String location = thisJoinPoint.getSourceLocation().toString();
		String sqlcmd = thisJoinPoint.getArgs()[0].toString();
		log.debug("SQL @({}): {}", location, sqlcmd);
	}
		
}
