package de.qaepps.converter.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.springframework.stereotype.Service;

@Service
public class ConverterServiceImpl implements ConverterService {
	
	private final OfficeManager officeManager;

	// constructor injection
	public ConverterServiceImpl(OfficeManager officeManager) {
		this.officeManager = officeManager;
	}
	
	@Override
	public ByteArrayOutputStream convert(DocumentFormat targetFormat, InputStream inputStream, String inputFileName) 
			throws OfficeException {
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DocumentConverter converter = LocalConverter.builder().officeManager(officeManager).build();
		
		DocumentFormat inputFormat = guessEncoding(inputStream, inputFileName);
		if (inputFormat != null) {
			converter.convert(inputStream).as(inputFormat).to(outputStream).as(targetFormat).execute();
		} else {
			converter.convert(inputStream).to(outputStream).as(targetFormat).execute();
		}
		
		return outputStream;
	}
	
	/**
	 * <pre>
	 * for all text input files not containing encoding read inputStream and guess encoding
	 * 
	 * see https://github.com/unicode-org/icu/tree/master/icu4j
	 * see https://stackoverflow.com/questions/499010/java-how-to-determine-the-correct-charset-encoding-of-a-stream
	 * 
	 * usage:
	 * DocumentFormat inputFormat = guessEncoding(inputStream, inputFileName);
	 * converter.convert(inputStream).as(inputFormat).to(outputStream).as(targetFormat).execute();
	 * </pre>
	 * 
	 * @param inputStream
	 * @param inputFileName
	 * @return
	 */
	private DocumentFormat guessEncoding(InputStream inputStream, String inputFileName) {
		if (inputFileName == null) {
			return null;
		}
		
		// simple solution: for now set encoding to UTF-8 for extensions txt, json, xml
		if (FilenameUtils.isExtension(inputFileName.toLowerCase(), "txt", "json", "xml")) {
			return getDocumentFormat("utf8");
		}
		
		return null;
	}

	/**
	 * USE utf8 since UTF-8 doesn't work
	 * 
	 * @param encoding
	 * @return
	 */
	private DocumentFormat getDocumentFormat(String encoding) {
		return DocumentFormat.builder()
				.from(DefaultDocumentFormatRegistry.TXT)
				.loadProperty("FilterOptions", encoding)
				.build();
	}
}
