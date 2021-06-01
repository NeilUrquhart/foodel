package edu.napier.foodel.server;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HTMLpage {
	private  StringBuilder head = new StringBuilder();
	private  StringBuilder body = new StringBuilder();
	private static String headtemplate= null;
	private static String bodytemplate= null;
	private static String footertemplate = null;
	
	public HTMLpage(String title) {
		try {
			if (headtemplate==null)
			  headtemplate = Files.readString(Path.of("./config/" +ServerProperties.getInstance().get("htmlheader")));
			
			if (bodytemplate==null)
				bodytemplate = Files.readString(Path.of("./config/" +ServerProperties.getInstance().get("htmlbody")));
		
			if (footertemplate==null)
				footertemplate = Files.readString(Path.of("./config/" +ServerProperties.getInstance().get("htmlfooter")));
		
			
			head.append("<title>"+title+"</title>\n");
		} catch (IOException e) {
			//Logger
			e.printStackTrace();
		}
	}
	
	public void addToHeader(String line) {
		head.append( line);
	}
	
	public void addToBody(String line) {
		body.append( line);
	}
	
	public String html() {
		return "<!DOCTYPE html>\n"
				+ "<html>\n"
				+ "  <head>\n"
				+  headtemplate
				+ head.toString()
				+ "  </head>\n"
				+ "  <body>\n"
				+  bodytemplate
				+ body.toString()
				+ footertemplate
				+ "  </body>\n"
				+ "</html>";
	}
	
}
