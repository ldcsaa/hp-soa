package io.github.hpsocket.soa.framework.util.file.excel;

import java.util.List;

public interface XlsxReaderListener
{
	default void onStartDocumnet(XlsxReader.Parser parser) {}
	default void onEndDocumnet(XlsxReader.Parser parser) {}
	
	default void onStartSheet(XlsxReader.Parser parser, int sheetIndex) {}
	default void onEndSheet(XlsxReader.Parser parser, int sheetIndex) {}	
	
	default void onParseError(XlsxReader.Parser parser, int sheetIndex, int rowNumber,  Exception e, boolean fatal) {}
	
	void onParseRow(XlsxReader.Parser parser, int sheetIndex, int rowNumber, List<String> datas);
}
