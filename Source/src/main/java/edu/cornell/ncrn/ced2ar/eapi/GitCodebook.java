package edu.cornell.ncrn.ced2ar.eapi;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

public class GitCodebook implements Serializable,Cloneable {
	
	private static final long serialVersionUID = 2761494133779544168L;
	
	public static final String STATUS_EXISTS = "EXISTS";
	public static final String STATUS_DOES_NOT_EXIST = "DOES_NOT_EXIST";
	public static final String STATUS_LOCAL_NON_EXISTANT = "LOCAL_DOES_NOT_EXIST";
	public static final String STATUS_REMOTE_NON_EXISTANT = "REMOTE_DOES_NOT_EXIST";
	
	public static final String STATUS_LOCAL_AHEAD = "LOCAL_AHEAD";
	public static final String STATUS_LOCAL_BEHIND = "LOCAL_BEHIND";
	public static final String STATUS_INVALID_REMOTE = "INVALID_REMOTE";
	public static final String STATUS_CONFLICT = "CONFLICT";
	public static final String STATUS_UPTODATE = "UP_TO_DATE";
	public static final String STATUS_UNKNOWN = "UNKNOWN";
	
	private String codebookName;
	private long lastLocalUpdateTime;
	private long lastRemoteUpdateTime;
	private String lastLocalCommitHash;
	private String lastRemoteCommitHash;
	private String lastLocalMessage;
	private String lastRemoteMessage;
	private String lastLocalAuthor;
	private String lastRemoteAuthor;
	private String status;
	
	private String baseXExistanceStatus;	
	private String localGitExistanceStatus;
	private String remoteGitExistanceStatus;
	
	public String getFormattedLastLocalAuthor() {
		return getFormattedAuthor(getLastLocalAuthor());
	}

	public String getFormattedLastRemoteAuthor() {
		return getFormattedAuthor(getLastRemoteAuthor());
	}
	
	public String getFormattedRemoteMessage() {
		return getFormattedMessage(getLastRemoteMessage());
	}
	
	public String getFormattedLocalMessage() {
		return getFormattedMessage(getLastLocalMessage());
	}
	
	private String getFormattedMessage(String message) {
		String formattedMessage = message;
		if(!StringUtils.isEmpty(message)) {
			String msg = message;
			String[] messageParts = msg.split(",");
		    if(messageParts.length==4) {
		    	formattedMessage = "Change to " +messageParts[2] +  " " +messageParts[3].substring(0,messageParts[3].length()-1);
		    }
		}
		return formattedMessage;
	}

	private String getFormattedAuthor(String author) {
		String formattedAuthor = author;
		if(!StringUtils.isEmpty(author) && author.startsWith("tomcat")) {
			formattedAuthor = "system";
		}
		return formattedAuthor;
	}

	public String getCodebookBaseHandle() {
		String baseHandle="";
		String codebookNameNoExt = getCodebookNameNoExt();
		String splits[] = codebookNameNoExt.split("\\.");
		if(splits.length>=1) {
			baseHandle = splits[0];
		}
		else {
			baseHandle = codebookNameNoExt;
		}
		
		return baseHandle;
	}
	public String getCodebookVersion(){
		String version ="";
		String codebookNameNoExt = getCodebookNameNoExt();
		String splits[] = codebookNameNoExt.split("\\.");
		if(splits.length >= 2) {
			version = splits[1];
		}
		return version;
	}
	
	private String getCodebookNameNoExt() {
		return codebookName.substring(0,(codebookName.length()-4));
	}

	public String getFormattedLastLocalUpdateTime() {
		if(this.getLastLocalUpdateTime() == 0 ) return ""; 
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
	    Date resultDate = new Date(this.getLastLocalUpdateTime() * 1000);
		return sdf.format(resultDate);
	}
	
	public String getFormattedLastRemoteUpdateTime() {
		if(this.getLastRemoteUpdateTime() == 0 ) return "";
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
	    Date resultDate = new Date(this.getLastRemoteUpdateTime() * 1000);
	    return sdf.format(resultDate);
	}


	public String getBaseXExistanceStatus() {
		if(StringUtils.isEmpty(baseXExistanceStatus)) {
			return this.STATUS_DOES_NOT_EXIST;
		}
		else {
			return baseXExistanceStatus;
		}
	}

	public void setBaseXExistanceStatus(String baseXExistanceStatus) {
		this.baseXExistanceStatus = baseXExistanceStatus;
	}

	public String getLocalGitExistanceStatus() {
		if(StringUtils.isEmpty(localGitExistanceStatus)) {
			return this.STATUS_DOES_NOT_EXIST;
		}
		else {
			return localGitExistanceStatus;
		}
	}

	public void setLocalGitExistanceStatus(String localGitExistanceStatus) {
		this.localGitExistanceStatus = localGitExistanceStatus;
	}

	public String getRemoteGitExistanceStatus() {
		if(StringUtils.isEmpty(remoteGitExistanceStatus)) {
			return this.STATUS_DOES_NOT_EXIST;
		}
		else {
			return remoteGitExistanceStatus;
		}
	}

	public void setRemoteGitExistanceStatus(String remoteGitExistanceStatus) {
		this.remoteGitExistanceStatus = remoteGitExistanceStatus;
	}

	public long getLastLocalUpdateTime() {
		return lastLocalUpdateTime;
	}
	public void setLastLocalUpdateTime(long lastLocalUpdateTime) {
		this.lastLocalUpdateTime = lastLocalUpdateTime;
	}
	public long getLastRemoteUpdateTime() {
		return lastRemoteUpdateTime;
	}
	public void setLastRemoteUpdateTime(long lastRemoteUpdateTime) {
		this.lastRemoteUpdateTime = lastRemoteUpdateTime;
	}
	public String getCodebookName() {
		return codebookName;
	}
	public void setCodebookName(String codebookName) {
		this.codebookName = codebookName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLastLocalMessage() {
		return lastLocalMessage;
	}
	public void setLastLocalMessage(String lastLocalMessage) {
		this.lastLocalMessage = lastLocalMessage;
	}
	public String getLastRemoteMessage() {
		return lastRemoteMessage;
	}
	public void setLastRemoteMessage(String lastRemoteMessage) {
		this.lastRemoteMessage = lastRemoteMessage;
	}
	public String getLastLocalAuthor() {
		return lastLocalAuthor;
	}
	public void setLastLocalAuthor(String lastLocalAuthor) {
		this.lastLocalAuthor = lastLocalAuthor;
	}
	public String getLastRemoteAuthor() {
		return lastRemoteAuthor;
	}
	public void setLastRemoteAuthor(String lastRemoteAuthor) {
		this.lastRemoteAuthor = lastRemoteAuthor;
	}
	public String getLastLocalCommitHash() {
		return lastLocalCommitHash;
	}
	public void setLastLocalCommitHash(String lastLocalCommitHash) {
		this.lastLocalCommitHash = lastLocalCommitHash;
	}
	public String getLastRemoteCommitHash() {
		return lastRemoteCommitHash;
	}
	public void setLastRemoteCommitHash(String lastRemoteCommitHash) {
		this.lastRemoteCommitHash = lastRemoteCommitHash;
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
	
	
	
	@Override
	public String toString() {
		return "GitCodebook [codebookName=" + codebookName
				+ ", lastLocalUpdateTime=" + lastLocalUpdateTime
				+ ", lastRemoteUpdateTime=" + lastRemoteUpdateTime
				+ ", lastLocalCommitHash=" + lastLocalCommitHash
				+ ", lastRemoteCommitHash=" + lastRemoteCommitHash
				+ ", lastLocalMessage=" + lastLocalMessage
				+ ", lastRemoteMessage=" + lastRemoteMessage
				+ ", lastLocalAuthor=" + lastLocalAuthor
				+ ", lastRemoteAuthor=" + lastRemoteAuthor + ", status="
				+ status + ", baseXExistanceStatus=" + baseXExistanceStatus
				+ ", localGitExistanceStatus=" + localGitExistanceStatus
				+ ", remoteGitExistanceStatus=" + remoteGitExistanceStatus
				+ "]";
	}

	public String toStrings() {
		return "GitCodebook [codebookName=" + codebookName + ", status="
				+ status + ", lastLocalUpdateTime=" + lastLocalUpdateTime
				+ ", lastRemoteUpdateTime=" + getFormattedLastRemoteUpdateTime()
				+ ", lastLocalCommitHash=" + getFormattedLastLocalUpdateTime()
				+ ", lastRemoteCommitHash=" + lastRemoteCommitHash
				+ ", lastLocalMessage=" + lastLocalMessage
				+ ", lastRemoteMessage=" + lastRemoteMessage
				+ ", lastLocalAuthor=" + lastLocalAuthor
				+ ", lastRemoteAuthor=" + lastRemoteAuthor + "]";
	}
}