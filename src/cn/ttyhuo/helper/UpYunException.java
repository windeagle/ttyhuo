/** * com.upyun.api * UpYunException.java */package cn.ttyhuo.helper;/** * UpYunException.java * 异常类。 * 通常只有code和message信息。 * 但在上传时候还会传递其他错误信息，供开发者使用。 * @author vincent chen * @since 2012 Jun 26, 2012 4:20:36 PM */public class UpYunException extends Exception{	private static final long serialVersionUID = 3854772125385537971L;		public int code;	public String message;	public String url;	public long time;	public String signString;	public boolean isSigned;		/**	 * @param code	 * @param message	 */	public UpYunException(int code, String message) {		this.code = code;		this.message = message;	}	/* (non-Javadoc)	 * @see java.lang.Object#toString()	 */	@Override	public String toString() {		return "UpYunException [code=" + code + ", " + (message != null ? "message=" + message + ", " : "")				+ (url != null ? "url=" + url + ", " : "") + "time=" + time + ", "				+ (signString != null ? "signString=" + signString + ", " : "") + "isSigned=" + isSigned + "]";	}}