package mod.xslt;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * pure java XSLT program
 * 
 * @author weikun_lu (RD-TW)
 *
 */
public class XslTransform {

	private static String HTML_PATH = "./report";
	private static String XSL_PATH = "./report/tmp";
	private static String TEMP_TESTCAST = "testcase_template.xsl";
	private static String TEMP_OVERVIEW = "overview_template.xsl";
	
	/**
	 * common function for generating HTML
	 * @param xslfile HTML template
	 * @param xmlfile data
	 */
	private void genTestcaseReport(File xslfile, File xmlfile) {
		//here just only format output file name of HTML
		String name = "";
		if(xmlfile.getName().indexOf("_") > 0){ //ex: testcase1_pass.xml > testcase1
			name = xmlfile.getName().substring(0, xmlfile.getName().indexOf("_"));
		} else { //ex: overview.xml > overview
			name = xmlfile.getName().substring(0, xmlfile.getName().indexOf("."));
		}
		File fileHTM = new File(HTML_PATH, name + ".html");

		StreamSource xsl = new StreamSource(xslfile);
		StreamSource src = new StreamSource(xmlfile.getAbsolutePath());
		StreamResult res = new StreamResult(fileHTM.getAbsolutePath());
		Transformer trans = null;
		try {
			trans = TransformerFactory.newInstance().newTransformer(xsl);
			trans.transform(src, res);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param procDir process directory
	 */
	private void genCases(File procDir) {
		//list all of XML files under directory which are created by python
		File[] xmlFiles = procDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith("xml"))
					return true;
				return false;
			}
		});

		//generating HTML by XSL template
		File fileXSL = new File(XSL_PATH, TEMP_TESTCAST);
		for (File file : xmlFiles) {
			genTestcaseReport(fileXSL, file);
		}
	}

	/**
	 * 
	 * @param procDir process directory
	 */
	private void genOverView(File procDir) {
		//list all of XML file which under process directory
		File[] xmlFiles = procDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (!name.toLowerCase().startsWith("overview") && name.toLowerCase().endsWith("xml"))
					return true;
				return false;
			}
		});

		//Declare file output location and create content from xmlFiles array variable
		File outFile = new File(XSL_PATH, "overview.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Element rootElement = document.createElement("overview");
			document.appendChild(rootElement);
			for (int i = 0; i < xmlFiles.length; i++) {
				File file = xmlFiles[i];
				String filename = file.getName().substring(0, file.getName().indexOf("_"));
				String status = file.getName().substring(file.getName().indexOf("_")+1, file.getName().lastIndexOf("."));
				Element em = document.createElement("testcase");
				em.appendChild(document.createTextNode(filename));
				em.setAttribute("status", status);
				rootElement.appendChild(em);
			}
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();

			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(outFile);
			DOMSource source = new DOMSource(document);
			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		//generating HTML by XSL template
		File fileXSL = new File(XSL_PATH, TEMP_OVERVIEW);
		genTestcaseReport(fileXSL, outFile);
		
		//remove testcase's and overview's XML files
		xmlFiles = procDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if ((name.toLowerCase().startsWith("testcase") || name.toLowerCase().startsWith("overview")) 
						&& name.toLowerCase().endsWith("xml"))
					return true;
				return false;
			}
		});
		for (File file : xmlFiles) {
			System.out.println(file.getAbsolutePath());
			//file.delete();
		}
	}

	/**
	 * @param args
	 * @return whether continue this program or not.
	 */
	private boolean help(String[] args){
		
		if(args.length==0){
			System.out.println("java -jar xslt.jar <path of scan directory> <overview flag>");
			return false;
		}
		
		File procDir = new File(args[0]);
		if (!procDir.exists() || !procDir.isDirectory()) {
			System.out.println(args[0] + " is a invalid path!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * execute step:
	 * 1. java -jar xslt.jar ./report/tmp
	 * 2. java -jar xslt.jar ./report/tmp 1
	 * 
	 * @param args [0]:template path; [1]:overview flag
	 */
	public static void main(String[] args) {

		long s = System.currentTimeMillis();
		XslTransform xslt = new XslTransform();
		if(xslt.help(args)){
			if (args.length == 2) {
				xslt.genOverView(new File(args[0]));
			} else {
				xslt.genCases(new File(args[0]));
			}
		}
		long e = System.currentTimeMillis();
		System.out.println("xslt was finished: " + (e-s)/1000f);
	}

}
