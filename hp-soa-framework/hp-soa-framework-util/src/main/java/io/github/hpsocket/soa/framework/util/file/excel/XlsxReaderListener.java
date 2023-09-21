package io.github.hpsocket.soa.framework.util.file.excel;

import java.util.List;

/** <b>Excel 文件读取监听器</b><br>
 * 处理 Excel 文件读取过程中的各个事件 
 */
public interface XlsxReaderListener
{
	/** 开始读取 Excel 文件 */
	default void onStartDocumnet(XlsxReader.Parser parser) {}
	/** 结束读取 Excel 文件 */
	default void onEndDocumnet(XlsxReader.Parser parser) {}
	/** 开始读取一个 Sheet */
	default void onStartSheet(XlsxReader.Parser parser, int sheetIndex) {}
	/** 结束读取一个 Sheet */
	default void onEndSheet(XlsxReader.Parser parser, int sheetIndex) {}
	/** 读取某行失败，其中 fatal 为 true 会终止操作 */
	default void onParseError(XlsxReader.Parser parser, int sheetIndex, int rowNumber,  Exception e, boolean fatal) {}
	/** 成功读取某项，其中 datas 为读取到的内容 */
	void onParseRow(XlsxReader.Parser parser, int sheetIndex, int rowNumber, List<String> datas);
}
