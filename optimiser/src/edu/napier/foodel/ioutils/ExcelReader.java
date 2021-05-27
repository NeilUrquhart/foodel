package edu.napier.foodel.ioutils;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.napier.foodel.geocode.Geocoder;
import edu.napier.foodel.problem.FoodelProblem;
import edu.napier.foodel.problem.FoodelVisit;

public class ExcelReader {

//	private static String[] headers = {
//	"FORENAME",
//	"SURNAME",
//	"PREFERRED_PHONE_NUMBER",
//	"ADDRESS_NUMBER",
//	"STREET_NAME",
//	"POSTCODE",
//	"LOCALITY",
//	"ONGOING_FOOD_SERVICE",
//	"FOOD_ALLERGY_DETAILS",
//	"FOOD_ASSISTANCE_REQUIRED",
//	"BABY_FOOD_DETAILS",
//	"SANITARY_PRODUCT_DETAILS",
//	"PET_FOOD_DETAILS",
//	"Delivery date",
//	"Delivery Notes",
//	"EVOC Notes"};
//	
//	private static int[] cols={
//		1,
//		2,
//		4,
//		10,
//		11,
//		12,
//		13,
//		14,
//		16,
//		17,
//		18,
//		19,
//		20,
//		26,
//		27,
//		25};
	
	public class ColNode{
		String header;
		int col;
	}
	private static ArrayList<ColNode>headers = new ArrayList<ColNode>();
	

	private  void initialise(File dataDir) {
		 try {
			 
			 File cols = new File(dataDir.getAbsolutePath() +"/cols.csv");
			
			BufferedReader  b = new BufferedReader(new FileReader(cols));
			String readLine = "";
			b.readLine();//skip header
			while ((readLine = b.readLine()) != null) {
				String[] data =  readLine.split(",");
				if (data.length >1) {
					ColNode n = new ColNode();
					n.header = data[0];
					n.col = Integer.parseInt(data[1]);
					headers.add(n);
				}
			}

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
	}
	public FoodelProblem readExcel(String fName, FoodelProblem myProb, File dataDir) throws FileNotFoundException, IOException,Exception {
		initialise(dataDir);
		File myFile = new File(fName); 
		FileInputStream fis = new FileInputStream(myFile); // Finds the workbook instance for XLSX file 
		XSSFWorkbook myWorkBook = new XSSFWorkbook (fis); // Return first sheet from the XLSX workbook 
		XSSFSheet mySheet = myWorkBook.getSheetAt(0); // Get iterator to all the rows in current sheet 
		Iterator<Row> rowIterator = mySheet.iterator(); // Traversing over each row of XLSX file 
		
		//1. get header row
		Row header = rowIterator.next(); // For each row, iterate through each columns 
		//done header
		while (rowIterator.hasNext()) { 
			Cell[] cells = rowToArray(rowIterator.next());
			//Now process the row...

			String name = getValue("FORENAME",cells).trim() +" "+getValue("SURNAME",cells).trim();
			if (!name.equals(" ")) {
			  //Now create a new FoodVisit
			  String postcode = 	getValue("POSTCODE",cells);
			  
			  Point2D loc = Geocoder.find(postcode);
			  
			  if (loc==null)
				  throw new Exception("Postcode "+ postcode +" not found.");
			  
			  FoodelVisit v = new FoodelVisit(name, loc.getX(),loc.getY(),1);
			  v.setPostCode(postcode);
			  v.setAddress(getValue("ADDRESS_NUMBER",cells) +" "+ getValue("STREET_NAME",cells) + " Tel:" +getValue("PREFERRED_PHONE_NUMBER",cells));
			  v.setLocality(getValue("LOCALITY",cells));
			  v.setAllergyDetails(getValue("FOOD_ALLERGY_DETAILS",cells));
			  v.setdeliveryNote(getValue("Delivery Notes",cells));
			  String order = "FOOD ASSISTANCE REQUIRED: " + getValue("FOOD_ASSISTANCE_REQUIRED",cells) +
						"BABY FOOD DETAILS: "+ getValue("BABY_FOOD_DETAILS",cells) +
						"SANITARY PRODUCT DETAILS: "+getValue("SANITARY_PRODUCT_DETAILS",cells) +
						"PET FOOD DETAILS: " + getValue("PET_FOOD_DETAILS",cells);
			  v.setOrder(order);
			  v.setShippingDate(getValue("Delivery date",cells));
			  myProb.addVisit(v);
			 System.out.println(name + " : Added" );
			}
		}
		return myProb;
	}

	private static String getValue(String key, Cell[] cells) {
		int idx=-1;

		for(ColNode n : headers) {
			if (n.header.equals(key)) {
				idx = n.col;
				break;
			}
		}
		if (idx == -1) return "";
		
		if (idx >= cells.length) return "";
		
		Cell cellValue = cells[idx];
		String value="";
		if (cellValue != null) {
			if (cellValue.getCellType()==Cell.CELL_TYPE_NUMERIC)
			  value = ""+cellValue.getNumericCellValue();
			else
			  value =cellValue.getStringCellValue();
		}
		return value;
	}

	private static Cell[] rowToArray(Row row) {
		int noOfCols = row.getLastCellNum()+1;
	
		Cell[] res = new Cell[noOfCols];
		
		Iterator<Cell> cellIterator = row.cellIterator(); 
		while (cellIterator.hasNext()) { 
			Cell c = cellIterator.next();
			
			res[c.getColumnIndex()] = c;
		}
		return res;
	}
	
}


