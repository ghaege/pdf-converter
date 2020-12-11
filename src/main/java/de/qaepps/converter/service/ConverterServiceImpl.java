package de.qaepps.converter.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.jodconverter.core.DocumentConverter;
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
		converter.convert(inputStream).to(outputStream).as(targetFormat).execute();
		
		return outputStream;
	}
}
