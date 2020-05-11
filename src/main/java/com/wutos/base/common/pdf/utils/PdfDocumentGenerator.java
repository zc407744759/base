package com.wutos.base.common.pdf.utils;


import com.wutos.base.common.handler.WutosException;
import com.wutos.base.common.pdf.factory.ITextRendererObjectFactory;
import com.wutos.base.common.pdf.freemaker.HtmlGenerator;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * pdf 生成
 *
 * @ClassName: PdfGenerator
 * @Description:pdf 生成
 * @author lihengjun 修改时间： 2013年11月5日 下午3:27:22 修改内容：新建
 */
public class PdfDocumentGenerator {
//	private final static Logger logger = Logger.getLogger(PdfDocumentGenerator.class);

	private final static HtmlGenerator htmlGenerator;
	static {
		htmlGenerator = new HtmlGenerator();
	}

	/**
	 * 使用模板,模板数据,生成pdf
	 *
	 * @Title: generate
	 * @Description: 使用模板,模板数据,生成pdf
	 * @param template
	 *            classpath中路径模板路径
	 * @param outputFile
	 *            生成pdf的路径
	 * @author lihengjun 修改时间： 2013年11月5日 下午1:38:53 修改内容：新建
	 */
	public boolean generate(String template, Map<String,Object> variables,
							String outputFile){
//		Map<String, Object> variables;

		try {
//			variables = documentVo.fillDataMap();
			String htmlContent = htmlGenerator.generate(template,
					variables);
			this.generate(htmlContent, outputFile);


		} catch (Exception e) {

			throw new WutosException("", e);
		}

		return true;
	}

	/**
	 * Output a pdf to the specified outputstream
	 *
	 * @param htmlContent
	 *            the htmlstr
	 * @param outputFile
	 *            the specified output file
	 * @throws Exception
	 */
	public void generate(String htmlContent, String outputFile)
			throws Exception {
		OutputStream out = null;
		ITextRenderer iTextRenderer = null;

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(htmlContent
					.getBytes(StandardCharsets.UTF_8)));
			File f = new File(outputFile);
			if (f != null && !f.getParentFile().exists()) {
				f.getParentFile().mkdir();
			}
			out = new FileOutputStream(outputFile);

			iTextRenderer = (ITextRenderer) ITextRendererObjectFactory
					.getObjectPool().borrowObject();//获取对象池中对象

			try {
				iTextRenderer.setDocument(doc, null);
				iTextRenderer.layout();
				iTextRenderer.createPDF(out);
			} catch (Exception e) {
				ITextRendererObjectFactory.getObjectPool().invalidateObject(
						iTextRenderer);
				iTextRenderer = null;
				throw e;
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (out != null) {
                out.close();
            }

			if (iTextRenderer != null) {
				try {
					ITextRendererObjectFactory.getObjectPool().returnObject(
							iTextRenderer);
				} catch (Exception ex) {
//					logger.error("Cannot return object from pool.", ex);
				}
			}
		}
	}

}