package com.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excel.writer.ExcelCreator;

/**
 * Servlet implementation class Hello
 */
@WebServlet("/getSpeciesExcel")
public class getSpeciesExcel extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private List<String> columnsNames;
	private Map<String, String> columnsTypes;
	private Map<String, List<String>> columnsDataContent;
	private String serviceUrl;
	private ExcelCreator excelCreator;

	/**
	 * Default constructor.
	 */
	public getSpeciesExcel() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			serviceUrl = "http://species.vamdc.org/species_database/v1/species";
			this.buildInternalObjectModel();
			this.getInformationFromService();
			this.convertIntoExcel();

			DateFormat dateFormat = new SimpleDateFormat("HH-mm yyyy-MM-dd");
			Date date = new Date();
			String currentDate = dateFormat.format(date);

			OutputStream out = response.getOutputStream();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition",
					"attachement; filename=SpeciesDBContentOn" + currentDate
							+ ".xls");
			excelCreator.getWorkBook().write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private void buildInternalObjectModel() {
		this.columnsNames = new ArrayList<String>();
		columnsNames.add("Node identifier"); // 0
		columnsNames.add("InChIKey"); // 1
		columnsNames.add("name"); // 2
		columnsNames.add("massNumber"); // 3
		columnsNames.add("did"); // 4
		columnsNames.add("stoichiometricFormula"); // 5
		columnsNames.add("speciesType"); // 6
		columnsNames.add("charge"); // 7
		columnsNames.add("InChI"); // 8
		columnsNames.add("formula"); // 9

		this.columnsTypes = new HashMap<String, String>();
		columnsTypes.put(columnsNames.get(0), "String");
		columnsTypes.put(columnsNames.get(1), "String");
		columnsTypes.put(columnsNames.get(2), "String");
		columnsTypes.put(columnsNames.get(3), "number");
		columnsTypes.put(columnsNames.get(4), "String");
		columnsTypes.put(columnsNames.get(5), "String");
		columnsTypes.put(columnsNames.get(6), "String");
		columnsTypes.put(columnsNames.get(7), "number");
		columnsTypes.put(columnsNames.get(8), "String");
		columnsTypes.put(columnsNames.get(9), "String");

		this.columnsDataContent = new HashMap<String, List<String>>();
		for (int i = 0; i < columnsNames.size(); i++) {
			List<String> currentList = new ArrayList<String>();
			columnsDataContent.put(columnsNames.get(i), currentList);
		}
	}

	private void getInformationFromService() throws IOException {
		URL url = new URL(serviceUrl);
		try (InputStream is = url.openStream();
				JsonReader rdr = Json.createReader(is)) {

			JsonObject obj = rdr.readObject();
			for (Entry<String, JsonValue> e : obj.entrySet()) {
				String nodeId = e.getKey();
				JsonArray array = (JsonArray) e.getValue();
				for (int i = 0; i < array.size(); i++) {
					columnsDataContent.get(columnsNames.get(0)).add(nodeId);

					for (int k = 1; k < columnsNames.size(); k++) {
						String fieldName = columnsNames.get(k);
						String fieldValue;
						try {
							String fieldType = columnsTypes.get(fieldName);
							if (fieldType.equalsIgnoreCase("number")) {
								fieldValue = array.getJsonObject(i)
										.getJsonNumber(fieldName).toString();
							} else {
								fieldValue = array.getJsonObject(i)
										.getJsonString(fieldName).toString();
								fieldValue = fieldValue.replace("\"", "");
							}
						} catch (ClassCastException castException) {
							fieldValue = "";
						}
						columnsDataContent.get(fieldName).add(fieldValue);
					}
				}
			}
		}
	}

	private void convertIntoExcel() {
		DateFormat dateFormat = new SimpleDateFormat("HH-mm yyyy-MM-dd");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		excelCreator = new ExcelCreator("speciesList", columnsNames,
				columnsTypes, columnsDataContent, false);
	}

}
