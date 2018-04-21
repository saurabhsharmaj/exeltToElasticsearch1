import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.elasticsearch.client.Client;

public class ExcelReader {

	static ESManager esManager = new ESManager();
	static Client client = esManager.getClient("localhost", 9300).get();
	static IngestService ingestService = new IngestService(client);

	static Map<String,String> header = new HashMap<String,String>();
	public static final String SAMPLE_XLSX_FILE_PATH = "./git.xlsx";

	public static void main(String[] args) throws IOException, InvalidFormatException {
		Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));
		
		workbook.forEach(sheet -> {
			sheet.forEach(row -> {
				if(row.getRowNum()==0){
					header = getHeaderRowData(row);
					return;
				}
				Map<String, String> rowData = getRowData(row);				
				System.out.println(rowData);
				ingestService.ingest("git","student",rowData);
			});
		});		
		workbook.close();
	}

	private static Map<String, String> getRowData(Row row) {
		Map<String,String> map = new HashMap<String,String>();
		for (Cell cell : row) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			map.put(header.get(CellReference.convertNumToColString(cell.getColumnIndex())),getCellValue(cell));
		}
		
		return map;
		
	}
	
	private static Map<String, String> getHeaderRowData(Row row) {
		Map<String,String> map = new HashMap<String,String>();
		for (Cell cell : row) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			map.put(CellReference.convertNumToColString(cell.getColumnIndex()), getCellValue(cell));
		}
		return map;
	}
	
	private static String getCellValue(Cell cell) {
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
		case STRING:
		case NUMERIC:
		case FORMULA:
			return cell.getStringCellValue();
		case BLANK:
			return "";
		default:
			return "";
		}
	}
}
