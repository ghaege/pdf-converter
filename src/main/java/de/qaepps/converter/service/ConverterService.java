package de.qaepps.converter.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;

public interface ConverterService {

	ByteArrayOutputStream convert(DocumentFormat targetFormat, InputStream inputStream, String inputFileName)
			throws OfficeException;

}
